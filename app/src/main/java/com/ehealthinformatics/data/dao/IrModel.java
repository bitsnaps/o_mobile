/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 2/1/15 3:18 PM
 */
package com.ehealthinformatics.data.dao;

import android.content.Context;
import android.util.Log;

import com.ehealthinformatics.App;
import com.ehealthinformatics.config.OConstants;
import com.ehealthinformatics.core.service.SyncStatus;
import com.ehealthinformatics.data.db.ModelNames;
import com.ehealthinformatics.data.dto.SyncModel;
import com.ehealthinformatics.core.utils.DateUtils;
import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.OValues;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.orm.fields.types.ODateTime;
import com.ehealthinformatics.core.orm.fields.types.OInteger;
import com.ehealthinformatics.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.core.support.OUser;
import com.ehealthinformatics.data.db.Columns;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IrModel extends OModel {
    public static final String TAG = IrModel.class.getSimpleName();
    OColumn name = new OColumn("Model Description", OVarchar.class).setSize(100);
    OColumn model_name = new OColumn("Model", OVarchar.class).setSize(100);
    OColumn server_count = new OColumn("Count on Server", OInteger.class).setSize(64);
    OColumn local_count = new OColumn("Local Count ", OInteger.class).setSize(64);
    OColumn percentage_synced = new OColumn("Percentage Synced ", OInteger.class).setSize(64);
    OColumn sync_depth = new OColumn("Sync Depth ", OInteger.class).setSize(64);
    OColumn sync_limit = new OColumn("Sync Limit ", OInteger.class).setSize(64);
    OColumn must_sync_relations = new OColumn("Sync Limit ", OInteger.class).setSize(64);
    OColumn sync_mode = new OColumn("Sync Mode ", OInteger.class).setSize(64);
    OColumn status = new OColumn("Status", OVarchar.class).setSize(64);
    OColumn state = new OColumn("State", OVarchar.class).setSize(64);
    OColumn status_detail = new OColumn("Status Detail", OVarchar.class).setSize(200);
    OColumn authority = new OColumn("Authority", OVarchar.class).setSize(200);
    OColumn last_synced = new OColumn("Last Synced on ", ODateTime.class)
            .setLocalColumn();
    OColumn process_updated = new OColumn("Last Updated on ", ODateTime.class)
            .setLocalColumn();
    //TODO: Enforce constraint
    OColumn parent_id = new OColumn("Parent", OInteger.class);
    OColumn pull_to_device_ids = new OColumn("Model Description", OVarchar.class).setSize(50000);
    OColumn push_to_server_ids = new OColumn("Model Description", OVarchar.class).setSize(50000);

    public IrModel(Context context, OUser user) {
        super(context, "ir.model", user);
    }

    @Override
    public boolean checkForCreateDate() {
        return false;
    }

    @Override
    public boolean checkForWriteDate() {
        return false;
    }

    public SyncModel getBy(String modelName, Integer parentId, Columns.SyncModel.Mode syncMode, QueryFields qf){
        List<ODataRow> oDataRow = select(null, "model_name = ? and parent_id = ? and sync_mode = ?", new String[]{modelName, "false", syncMode.toString()}, "id DESC");
        if(oDataRow.size() > 0) {
            return fromRow(oDataRow.get(0), qf);
        }
        return null;
    }

    public SyncModel getOrCreateTrigger(String modelName, Columns.SyncModel.Mode syncMode, Integer syncDepth, QueryFields qf) {
        SyncModel syncModel = getBy(modelName, 0, syncMode, qf);
        if(syncModel == null) {
            syncModel = create(modelName, null, syncMode, syncDepth);
        }
        //TODO: Resume SyncProcess
        //syncModel.requeue();
        return syncModel;
    }

    //New methods
    private SyncModel create(String modelName, SyncModel parent, Columns.SyncModel.Mode syncMode, Integer syncDepth) {
        SyncModel syncModel;
        OModel syncDao = App.getDao(modelName);
        Log.v(TAG, "Creating SyncModel  : " + modelName +
                (parent == null ? "" : "(" + parent.toString() + ")"));

        syncModel = new SyncModel(0, modelName, ModelNames.getAuthority(modelName), OConstants.getSyncLimit(modelName),
                syncDepth, syncMode, syncDepth > 1, 0, 0, SyncStatus.QUEUED, "Queued for Syncing",
                DateUtils.now(), parent, new ArrayList<SyncModel>(),  new HashSet<Integer>(),
                new HashSet<Integer>());
        syncModel.setId(insert(syncModel.toOValues()));
        ArrayList<String> siblingModels = new ArrayList();
        if(syncModel.getSyncDepth() > 0){
            List<OColumn> childColumns = syncDao.getRelationColumns();
            for(OColumn childModelColumn: childColumns){
                //TODO: This next line may be seriously hacky
                //TODO: instead of creating diff Syncmodels, use same and add ids
                String childModelName = ((OModel)App.getDao((Class<? extends OModel>)childModelColumn.getType())).getModelName();
                if(!siblingModels.contains(childModelName)){
                    SyncModel childModel = create(childModelName, syncModel, Columns.SyncModel.Mode.PARENT_TRIGGERED , syncModel.getSyncDepth() - 1);
                    syncModel.addChild(childModel);
                    siblingModels.add(childModelName);
                }
            }
        }
        return syncModel;
    }

    @Override
    public int insert(OValues oValues){
        List<ODataRow> oDataRows =select(null, "model_name = ? and status = ?", new String[]{oValues.getString(Columns.SyncModel.model_name), oValues.getString(Columns.SyncModel.sync_mode)});
        if(oDataRows.size() < 1) {
           return super.insert(oValues);
        }
        return oDataRows.get(0).getInt(Columns.id);
    }

    @Override
    public SyncModel get(int id, QueryFields queryFields){
        return fromRow(browse(id), queryFields);
    }

    public List<SyncModel> getChildren(int parentId){
        ArrayList<SyncModel> children = new ArrayList<>();
        List<ODataRow> childrenRows = select(null, "parent_id = ?", new String[]{parentId+""});
        for (ODataRow oDataRow: childrenRows) {
            children.add(fromRow(oDataRow, QueryFields.all()));
        }
        return children;
    }

    public SyncModel fromRow(ODataRow row, QueryFields qt) {
        Integer _id = null, syncLimit =  null, syncDepth= null, localCount = null, serverCount = null;
        String authority = null, modelName = null, status = null, statusDetail = null;
        SyncModel syncModel;
        Columns.SyncModel.Mode syncMode = null;
        Date lastSyncTime = null, processUpdated = null;
        Boolean mustSyncRelations = null;
        if(qt.contains(Columns.id))_id = row.getInt(Columns.SyncModel.id);
        if(qt.contains(Columns.SyncModel.last_synced)) lastSyncTime =  DateUtils.parseFromDB(row.getString(Columns.SyncModel.last_synced));
        if(qt.contains(Columns.SyncModel.process_updated)) processUpdated = DateUtils.parseFromDB(row.getString(Columns.SyncModel.process_updated));
        if(qt.contains(Columns.SyncModel.authority)) authority = row.getString(Columns.SyncModel.authority);
        if(qt.contains(Columns.SyncModel.sync_limit)) syncLimit = row.getInt(Columns.SyncModel.sync_limit);
        if(qt.contains(Columns.SyncModel.sync_depth))  syncDepth = row.getInt(Columns.SyncModel.sync_depth);
        if(qt.contains(Columns.SyncModel.sync_mode)) syncMode = Columns.SyncModel.Mode.valueOf(row.getString(Columns.SyncModel.sync_mode));
        if(qt.contains(Columns.SyncModel.model_name)) modelName = row.getString(Columns.SyncModel.model_name);
        if(qt.contains(Columns.SyncModel.must_sync_relations)) mustSyncRelations = row.getBoolean(Columns.SyncModel.must_sync_relations);
        if(qt.contains(Columns.SyncModel.local_count))  localCount = row.getInt(Columns.SyncModel.local_count);
        if(qt.contains(Columns.SyncModel.server_count)) serverCount = row.getInt(Columns.SyncModel.server_count);
        if(qt.contains(Columns.SyncModel.status)) status = row.getString(Columns.SyncModel.status);
        if(qt.contains(Columns.SyncModel.status_detail)) statusDetail = row.getString(Columns.SyncModel.status_detail);
        Set<Integer> pushToServerIds = toInts(row.getString(Columns.SyncModel.push_to_server_ids).split(","));
        Set<Integer> pullToDeviceIds = toInts(row.getString(Columns.SyncModel.pull_to_device_ids).split(","));
        ArrayList<SyncModel> children = (ArrayList) getChildren(_id);
        syncModel = new SyncModel(_id, modelName, authority,  syncLimit, syncDepth, syncMode, mustSyncRelations, serverCount, localCount,
                status, statusDetail, lastSyncTime, null, children, pullToDeviceIds, pushToServerIds);
        syncModel.setProcessUpdated(processUpdated);
        for(SyncModel child: children){
            child.setParent(syncModel);
        }
        return syncModel;
    }

    private Set<Integer> toInts(String[] numbers){
        HashSet<Integer> numberList = new HashSet<>();
        for(String number : numbers) {
            if(!number.trim().isEmpty()){
                numberList.add(Integer.parseInt(number));
            }
        }
        return numberList;
    }

    public List<SyncModel> selectDTOs(Columns.SyncModel.Mode syncMode, QueryFields qt) {
        List<SyncModel> syncModelList = new ArrayList<>();
        List<ODataRow> oDataRows = select(null, "sync_mode = ?", new String[]{syncMode.toString()});
        for (ODataRow row: oDataRows) {
            syncModelList.add(fromRow(row, qt));
        }
        return syncModelList;
    }

}
