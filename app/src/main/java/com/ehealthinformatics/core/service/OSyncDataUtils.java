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
 * Created on 2/1/15 4:15 PM
 */
package com.ehealthinformatics.core.service;

import android.content.Context;
import android.content.SyncResult;
import android.util.Log;

import com.ehealthinformatics.App;
import com.ehealthinformatics.core.rpc.helper.ORecordValues;
import com.ehealthinformatics.data.dao.IrModel;
import com.ehealthinformatics.data.dto.SyncModel;
import com.ehealthinformatics.data.dto.SyncProcess;
import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.OValues;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.rpc.Odoo;
import com.ehealthinformatics.core.rpc.helper.ODomain;
import com.ehealthinformatics.core.rpc.helper.OdooFields;
import com.ehealthinformatics.core.rpc.helper.utils.gson.OdooRecord;
import com.ehealthinformatics.core.rpc.helper.utils.gson.OdooResult;
import com.ehealthinformatics.core.support.OUser;
import com.ehealthinformatics.core.utils.ODateUtils;
import com.ehealthinformatics.core.utils.OListUtils;
import com.ehealthinformatics.core.utils.OdooRecordUtils;
import com.ehealthinformatics.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class OSyncDataUtils {
    public static final String TAG = OSyncDataUtils.class.getSimpleName();
    private Context mContext;
    private OModel mModel;
    private OUser mUser;
    private OdooResult response;
    private HashSet<String> recordsId = new HashSet<>();
    private List<String> recentSyncIds = new ArrayList<>();
    //private HashMap<String, SyncRelationRecords> relationRecordsHashMap = new HashMap<>();
    private Odoo mOdoo;
    //private HashMap<String, List<Integer>> updateToServerRecords = new HashMap<>();

    private String SERVER_ID_FIELD = "id";
    private String SERVER_NAME_FIELD = "name";
    private SyncProcess syncProcess;

    public OSyncDataUtils(Context context, Odoo odoo) {
        mContext = context;
        mOdoo = odoo;
    }

    public void handleResult(SyncProcess syncProcess) {
        syncProcess.update(SyncStatus.PROCESSING, " Now processing response result");
        mModel = syncProcess.getOModel();
        mUser = syncProcess.getOUser();
        this.response = syncProcess.getSyncResponse();
        this.syncProcess = syncProcess;
        List<OdooRecord> updateInLocal = checkLocalUpdatedRecords();
        handleResult(updateInLocal);
    }

    /**
     * This method filters out the records that have been modified locally,
     * and compares if it's most recent relative to the corresponding server record
     * so that they aren't overwritten by server docs
     * @return List of records to be created or updated on device.
     */
    private List<OdooRecord> checkLocalUpdatedRecords() {

        syncProcess.update(SyncStatus.PROCESSING, " Checking new or to-be-updated(dirty) records");
        // Array of records which are new or need to update in local
        List<OdooRecord> finalRecords = new ArrayList<>();
        try {
            // Getting list of ids which are present in local database
            List<Integer> serverIds = new ArrayList<>();
            HashMap<String, OdooRecord> serverIdRecords = new HashMap<>();
            List<OdooRecord> records = response.getRecords();
            syncProcess.update(SyncStatus.PROCESSING, " Iterating response records to filter ");
            for (OdooRecord record : records) {
                //if (mModel.hasServerRecord(record.getInt(SERVER_ID_FIELD))) {
                    if (mModel.isServerRecordDirty(record.getInt(SERVER_ID_FIELD))) {
                        int server_id = record.getInt(SERVER_ID_FIELD);
                        serverIds.add(server_id);
                        serverIdRecords.put("key_" + server_id, record);
                    } else {
                        finalRecords.add(record);
                    }
                //}

            }
            String updateMessage = "Found "+ serverIds.size() +" records modified on device";
            syncProcess.update(SyncStatus.PROCESSING, TAG + updateMessage);

            // getting local dirty records if server records length = 0
            syncProcess.update(SyncStatus.PROCESSING, " Querying db and iterating result for new records without serverId");
            int localDirty = 0;
            for (ODataRow row : mModel.select(new String[]{}, "_is_dirty = ? and _is_active = ? and id != ?",
                    new String[]{"true", "true", "0"})) {
                serverIds.add(row.getInt(SERVER_ID_FIELD));
                localDirty++;
            }
            syncProcess.update(SyncStatus.PROCESSING, " Found " + localDirty + " local-dirty(new and modified) records to update on server");
            // Comparing dirty (updated) record
            Set<Integer> updateToServerIds = new HashSet<>();
            syncProcess.update(SyncStatus.PROCESSING, " Comparing server-dirty & local-dirty records to find latest to update");
            if (serverIds.size() > 0) {
                HashMap<String, String> write_dates = getWriteDate(mModel, serverIds);
                for (Integer server_id : serverIds) {
                    String key = "key_" + server_id;
                    String write_date = write_dates.get(key);
                    ODataRow record = mModel.browse(new String[]{"_write_date"}, "id = ?",
                            new String[]{server_id + ""});
                    if (record != null) {
                        Date write_date_obj = ODateUtils.createDateObject(write_date,
                                ODateUtils.DEFAULT_FORMAT, false);
                        Date _write_date_obj = ODateUtils.createDateObject(record.getString("_write_date"),
                                ODateUtils.DEFAULT_FORMAT, false);
                        if (_write_date_obj.compareTo(write_date_obj) > 0) {
                            // Local record is latest
                            updateToServerIds.add(server_id);
                        } else {
                            if (serverIdRecords.containsKey(key)) {
                                finalRecords.add(serverIdRecords.get(key));
                            }
                        }
                    }
                }
            }
            if (updateToServerIds.size() > 0) {
                syncProcess.getSyncModel().setPushToServerIds(updateToServerIds);
            }
            syncProcess.update(SyncStatus.PROCESSING, " Found " + updateToServerIds.size() + "records to update on server");
        } catch (Exception e) {
            e.printStackTrace();
        }

        syncProcess.update(SyncStatus.PROCESSING, " Filtered " + finalRecords.size() + " records to create locally");
        return finalRecords;
    }

    private HashMap<String, String> getWriteDate(OModel model, List<Integer> ids) {
        HashMap<String, String> map = new HashMap<>();
        try {
            List<OdooRecord> result;
            if (model.getColumn("write_date") != null) {
                OdooFields fields = new OdooFields("write_date");
                ODomain domain = new ODomain();
                domain.add(SERVER_ID_FIELD, "in", ids);
                OdooResult response =
                        mOdoo.searchRead(model.getModelName(), fields, domain, 0, 0, null);
                result = response.getRecords();
            } else {
                Log.i(TAG, "Perm Read hidden fields for write_date and create_date : (Only in Odoo 7.0) for " + ids);
                OdooResult response = mOdoo.permRead(model.getModelName(), ids);
                result = response.getArray("result");
            }

            if (!result.isEmpty()) {
                for (OdooRecord record : result) {
                    map.put("key_" + record.getInt(SERVER_ID_FIELD), record.getString("write_date"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private void handleResult(List<OdooRecord> records) {
        Boolean mCreateRelationRecords = syncProcess.getSyncModel().getSyncDepth() > 0;
        //SQLiteDatabase db = mModel.getWritableDatabase();

        syncProcess.update(SyncStatus.PROCESSING, " About to start saving records");
        try {
            recordsId.clear();
            int counter = 0;
            int size = records.size();
            List<OColumn> columns = mModel.getColumns(false);
            columns.addAll(mModel.getFunctionalColumns());
            //Log.id(TAG, "Syncing modelName "+ mModel.getModelName() + ", Saved  " + records.size() + " records into DB");
            syncProcess.update(SyncStatus.PROCESSING, " Saving "+ size +" records into db");
            //db.beginTransaction();
            for (OdooRecord record : records) {
                syncProcess.update(SyncStatus.PROCESSING, "Processing " + counter + " of " + size);
                if (!recentSyncIds.contains(mModel.getModelName() + ":" + record.getInt(SERVER_ID_FIELD))) {
                    OValues values = new OValues();
                    recordsId.add(mModel.getModelName() + "_" + record.getInt(SERVER_ID_FIELD));
                    for (OColumn column : columns) {
                        String name = column.getSyncColumn();
                        String lName = column.getName();
                        if (column.getRelationType() == null) {
                            // checks for functional store fields
                            if (column.isFunctionalColumn() && column.canFunctionalStore()) {
                                List<String> depends = column.getFunctionalStoreDepends();
                                OValues dependValues = new OValues();
                                if (!column.isLocal())
                                    dependValues.put(column.getName(), record.get(column.getName()));
                                for (String depend : depends) {
                                    if (record.containsKey(depend)) {
                                        dependValues.put(depend, record.get(depend));
                                    }
                                }
                                Object value = mModel.getFunctionalMethodValue(column, dependValues);
                                values.put(lName, value);
                            } else {
                                // Normal Columns
                                values.put(lName, record.get(name));
                            }
                        } else {
                            // Relation Columns
                            if (!record.getString(name).equals("false")) {
                                switch (column.getRelationType()) {
                                    case ManyToOne:
                                        OdooRecord m2oData = record.getM20(name);
                                        OModel m2o_model = mModel.createInstance(column.getType());
                                        String recKey = m2o_model.getModelName() + "_" + m2oData.getInt(SERVER_ID_FIELD);
                                        int m2oRowId;
                                        if (!recordsId.contains(recKey)) {
                                            OValues m2oValue = new OValues();
                                            m2oValue.put(SERVER_ID_FIELD, m2oData.getInt(SERVER_ID_FIELD));
                                            m2oValue.put(m2o_model.getDefaultNameColumn(), m2oData.getString(SERVER_NAME_FIELD));
                                            m2oValue.put("_is_dirty", "false");
                                            m2oRowId = m2o_model.insertOrUpdate(m2oData.getInt(SERVER_ID_FIELD),
                                                    m2oValue);
                                        } else {
                                            m2oRowId = m2o_model.selectRowId(m2oData.getInt(SERVER_ID_FIELD));
                                        }

                                        values.put(lName, m2oRowId);
                                        if (mCreateRelationRecords) {
                                            // Add id to sync if modelName contains more than (id,name) columns
                                            if (m2o_model.getColumns(false).size() > 2
                                                    || (m2o_model.getColumns(false).size() > 4
                                                    && mModel.getOdooVersion().getVersionNumber() > 7)) {
                                                syncProcess.getChildModel(m2o_model.getModelName())
                                                        .getPullToDeviceIds().add(m2oData.getInt(SERVER_ID_FIELD));
                                            }
                                        }
                                        m2o_model.close();
                                        break;
                                    case ManyToMany:
                                        OModel m2mModel = mModel.createInstance(column.getType());
                                        List<Integer> m2mIds = OListUtils.doubleToIntList(record.getM2M(name));
                                        if (mCreateRelationRecords) {
                                            syncProcess.getChildModel(m2mModel.getModelName())
                                                    .getPullToDeviceIds().addAll(m2mIds);
                                        }
                                        List<Integer> m2mRowIds = new ArrayList<>();
                                        for (Integer id : m2mIds) {
                                            recKey = m2mModel.getModelName() + "_" + id;
                                            int r_id;
                                            if (!recordsId.contains(recKey)) {
                                                OValues m2mValues = new OValues();
                                                m2mValues.put(SERVER_ID_FIELD, id);
                                                m2mValues.put("_is_dirty", "false");
                                                r_id = m2mModel.insertOrUpdate(id, m2mValues);
                                            } else {
                                                r_id = m2mModel.selectRowId(id);
                                            }
                                            m2mRowIds.add(r_id);
                                        }
                                        if (m2mRowIds.size() > 0) {
                                            // Putting many to many related ids
                                            // (generated _id for each of server ids)
                                            values.put(lName, m2mRowIds);
                                        }
                                        m2mModel.close();
                                        break;
                                    case OneToMany:
                                        if (mCreateRelationRecords) {
                                            OModel o2mModel = mModel.createInstance(column.getType());
                                            List<Integer> o2mIds = OListUtils.doubleToIntList(record.getO2M(name));
                                            syncProcess.getChildModel(o2mModel.getModelName())
                                                    .getPullToDeviceIds().addAll(o2mIds);
//                                            addUpdateRelationRecord(mModel, o2mModel.getTableName(),
//                                                    column.getType(), name, column.getRelatedColumn(),
//                                                    column.getRelationType(),
//                                                    (column.getRecordSyncLimit() > 0) ?
//                                                            o2mIds.subList(0, column.getRecordSyncLimit()) : o2mIds);
                                            o2mModel.close();
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    // Some default values
                    values.put(SERVER_ID_FIELD, record.getInt(SERVER_ID_FIELD));
                    values.put("_write_date", ODateUtils.getUTCDate());
                    values.put("_is_active", "true");
                    values.put("_is_dirty", "false");
                    mModel.insertOrUpdate(record.getInt(SERVER_ID_FIELD), values);

                    // Fixed issue of multiple time sync same record. Performance improved
                    // Adding to recent sync list for avoiding duplicate process for record
                    recentSyncIds.add(mModel.getModelName() + ":" + record.getInt(SERVER_ID_FIELD));
                    syncProcess.getSyncResult().stats.numEntries++;
                    counter++;
                    syncProcess.savedRecord(counter, size);
                    syncProcess.update(SyncStatus.PROCESSING, " Saved "+ counter + " of " + size + " records");
                    syncProcess.setSavedCount(counter);
                }

            }
//            db.setTransactionSuccessful();
//            db.endTransaction();
            syncProcess.update(SyncStatus.PROCESSING, counter + " records affected");
        } catch (Exception e) {
            syncProcess.getSyncResult().stats.numParseExceptions++;
            syncProcess.update(SyncStatus.ERROR_PROCESSING, TAG + e.getMessage() + " ( " + syncProcess.getSyncResult().stats.numParseExceptions + ") ");
            e.printStackTrace();
        }
    }


    private List<String> toStrings(Set<Integer> numbers){
        ArrayList<String> numberList = new ArrayList<>();
        for(Integer number : numbers) {
            numberList.add(number.toString());
        }
        return numberList;
    }


    public boolean updateRecordsOnServer(SyncProcess syncProcess) {
        OModel model = syncProcess.getOModel();
        try {
            // Use key (modal name) from updateToServerRecords
            // use updateToServerRecords ids
            int counter = 0;
            //for (String key : updateToServerRecords.keySet()) {
                //OModel modelName = OModel.get(mContext, key, mUser.getAndroidName());
            Set<Integer> ids = syncProcess.getSyncModel().getPushToServerIds();
            counter += ids.size();
            for (ODataRow record : model.select(null,
                        "id IN ( " + StringUtils.repeat("?, ", ids.size() - 1) + " ?)",
                         toStrings(ids).toArray(new String[ids.size()]))) {

                if (OSyncAdapter.validateRelationRecords(model, record)) {
                    String modelName = model.getModelName();
                    ORecordValues oRecordValues = OdooRecordUtils.createRecordValues(model, record);
                    Integer serverID = record.getInt(SERVER_ID_FIELD);
                    mOdoo.updateRecord(modelName, oRecordValues, serverID);
                    OValues value = new OValues();
                    value.put("_is_dirty", "false");
                    value.put("_write_date", ODateUtils.getUTCDate());
                    model.update(record.getInt(OColumn.ROW_ID), value);
                    model.close();
                }
            }
            //}
            Log.i(TAG, counter + " records updated on server");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//    private void addUpdateRelationRecord(SyncProcess syncProcess, OModel baseModel, String relTable, Class<?> modelName,
//                                         String column, String relatedColumn,
//                                         OColumn.RelationType type, List<Integer> ids) {
//        String key = relTable + "_" + column;
//
//        if (relationRecordsHashMap.containsKey(key)) {
//            SyncRelationRecords data = relationRecordsHashMap.get(key);
//            data.updateIds(ids);
//            relationRecordsHashMap.put(key, data);
//        } else {
//            relationRecordsHashMap.put(key,
//                    new SyncRelationRecords(baseModel, modelName, column, relatedColumn, type, ids));
//        }
//    }

//    public HashMap<String, SyncRelationRecords> getRelationRecordsHashMap() {
//        if (mCreateRelationRecords)
//            return relationRecordsHashMap;
//        return new HashMap<>();
//    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (mModel != null)
            mModel.close();
    }

    public static class SyncRelationRecords {
        private OModel baseModel;
        private Class<?> relationModel;
        private String relationColumn;
        private String relatedColumn;
        private OColumn.RelationType relationType;
        private List<Integer> serverIds = new ArrayList<>();

        public SyncRelationRecords(OModel baseModel, Class<?> relationModel, String relationColumn, String relatedColumn,
                                   OColumn.RelationType relationType, List<Integer> serverIds) {
            this.baseModel = baseModel;
            this.relationModel = relationModel;
            this.relationColumn = relationColumn;
            this.relatedColumn = relatedColumn;
            this.relationType = relationType;
            this.serverIds.addAll(serverIds);
        }

        public OModel getBaseModel() {
            return baseModel;
        }

        public void setBaseModel(OModel baseModel) {
            this.baseModel = baseModel;
        }

        public Class<?> getRelationModel() {
            return relationModel;
        }

        public void setRelationModel(Class<?> relationModel) {
            this.relationModel = relationModel;
        }

        public String getRelationColumn() {
            return relationColumn;
        }

        public void setRelationColumn(String relationColumn) {
            this.relationColumn = relationColumn;
        }


        public String getRelatedColumn() {
            return relatedColumn;
        }

        public void setRelatedColumn(String relatedColumn) {
            this.relatedColumn = relatedColumn;
        }

        public OColumn.RelationType getRelationType() {
            return relationType;
        }

        public void setRelationType(OColumn.RelationType relationType) {
            this.relationType = relationType;
        }

        public List<Integer> getServerIds() {
            return serverIds;
        }

        public void setServerIds(List<Integer> serverIds) {
            this.serverIds.clear();
            this.serverIds.addAll(serverIds);
        }

        public void updateIds(List<Integer> ids) {
            this.serverIds.addAll(ids);
        }

        public List<Integer> getUniqueIds() {
            List<Integer> ids = new ArrayList<>();
            HashSet<Integer> uIds = new HashSet<>(serverIds);
            ids.addAll(uIds);
            return ids;
        }
    }

    //TODO: Change syncmode to enum
    public static SyncProcess getSyncProcess(SyncModel syncModel, ODomain domainFilter,
                                              OSyncDataUtils dataUtils, SyncResult result) {

        //Create Sync Process Object here
        IrModel syncModelDao = App.getDao(IrModel.class);
        OModel oModel = App.getDao(syncModel.getModelName());
        OUser user = oModel.getUser();
        SyncProcess syncProcess = new SyncProcess(null, oModel, syncModelDao, user,  syncModel,
                 dataUtils, domainFilter, result);
            Iterator<SyncModel> syncModelIterator = syncProcess.getSyncModel().getChildren();
            while (syncModelIterator.hasNext()) {
                SyncModel childSyncModel = syncModelIterator.next();
                SyncProcess childSyncProcess = getSyncProcess(childSyncModel, new ODomain(),
                        dataUtils, result);
                syncProcess.addChild(childSyncProcess);
            }
        return syncProcess;
    }


}
