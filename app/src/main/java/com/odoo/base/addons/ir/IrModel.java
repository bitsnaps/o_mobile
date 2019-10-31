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
package com.odoo.base.addons.ir;

import android.content.Context;
import android.util.Log;

import com.odoo.base.addons.abirex.dto.SyncModel;
import com.odoo.base.addons.abirex.util.DateUtils;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;
import com.odoo.core.utils.ODateUtils;
import com.odoo.data.abirex.Columns;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class IrModel extends OModel {
    public static final String TAG = IrModel.class.getSimpleName();
    OColumn name = new OColumn("Model Description", OVarchar.class).setSize(100);
    OColumn model = new OColumn("Model", OVarchar.class).setSize(100);
    OColumn server_count = new OColumn("Count on Server", OInteger.class).setSize(64);
    OColumn count = new OColumn("Count ", OInteger.class).setSize(64);
    OColumn status = new OColumn("Status", OVarchar.class).setSize(64);
    OColumn state = new OColumn("State", OVarchar.class).setSize(64);
    OColumn status_detail = new OColumn("Status Detail", OVarchar.class).setSize(200);

    OColumn last_synced = new OColumn("Last Synced on ", ODateTime.class)
            .setLocalColumn();

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

    public void updateSyncModel(SyncModel syncModel) {
        Log.i(TAG, "Model Sync Update : " + syncModel.getModel());
        OValues values = syncModel.toOValues();
        Date last_sync = ODateUtils.createDateObject(ODateUtils.getUTCDate(), ODateUtils.DEFAULT_FORMAT, true);
        Calendar cal = Calendar.getInstance();
        cal.setTime(last_sync);
        /*
                Fixed for Postgres SQL
                It stores milliseconds so comparing date wrong.
             */
        cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) + 2);
        last_sync = cal.getTime();
        values.put("last_synced", ODateUtils.getDate(last_sync, ODateUtils.DEFAULT_FORMAT));
        insertOrUpdate("model = ?", new String[]{syncModel.getModel()}, values);
    }

    public void updateStatusDetail(SyncModel syncModel,String statusDetail) {
        syncModel.setStatusDetail(statusDetail);
        updateSyncModel(syncModel);
    }

    public SyncModel getSyncModel(String modelName){
        List<ODataRow> oDataRow = select(null, "name = ?", new String[]{modelName});
        if(oDataRow.size() > 0){
            return fromRow(oDataRow.get(0));
        }
        return null;
    }

    public SyncModel fromRow(ODataRow row){

        SyncModel syncModel;
        Date lastSyncTime;
        try {
            lastSyncTime =DateUtils.parseFromDB(row.getString(Columns.SyncModel.last_synced));
        } catch (ParseException e) {
            lastSyncTime = DateUtils.beginningOfTime();
        }
        syncModel = new SyncModel(row.getInt(Columns.SyncModel.id), row.getString(Columns.SyncModel.model), row.getString(Columns.SyncModel.authority),
                row.getInt(Columns.SyncModel.sync_limit), true, row.getInt(Columns.SyncModel.server_count), row.getInt(Columns.SyncModel.count),
                row.getString(Columns.SyncModel.status), row.getString(Columns.SyncModel.status_detail), lastSyncTime);
        return syncModel;
    }

    public List<SyncModel> selectDTOs() {
        List<SyncModel> syncModelList = new ArrayList<>();
        List<ODataRow> oDataRows = select();
        for(ODataRow row: oDataRows){
            syncModelList.add(fromRow(row));
        }
        return syncModelList;
    }

}
