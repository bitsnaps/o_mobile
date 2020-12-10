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
 * Created on 1/1/15 3:17 PM
 */
package com.odoo.odoorx.core.base.service;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import com.odoo.odoorx.core.base.orm.OdooServerException;
import com.odoo.odoorx.core.data.dao.DaoRepoBase;
import com.odoo.odoorx.core.data.dao.IrModel;
import com.odoo.odoorx.core.data.dao.QueryFields;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.dto.SyncModel;
import com.odoo.odoorx.core.data.dto.SyncProcess;
import com.odoo.odoorx.core.data.dao.ResCompany;
import com.odoo.odoorx.core.base.auth.OdooAccountManager;
import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.OValues;
import com.odoo.odoorx.core.base.orm.fields.OColumn;
import com.odoo.odoorx.core.base.rpc.Odoo;
import com.odoo.odoorx.core.base.rpc.handler.OdooVersionException;
import com.odoo.odoorx.core.base.rpc.helper.ODomain;
import com.odoo.odoorx.core.base.rpc.helper.ORecordValues;
import com.odoo.odoorx.core.base.rpc.helper.OdooFields;
import com.odoo.odoorx.core.base.rpc.helper.utils.gson.OdooRecord;
import com.odoo.odoorx.core.base.rpc.helper.utils.gson.OdooResult;
import com.odoo.odoorx.core.base.support.OUser;
import com.odoo.odoorx.core.base.utils.ODateUtils;
import com.odoo.odoorx.core.base.utils.OPreferenceManager;
import com.odoo.odoorx.core.base.utils.OResource;
import com.odoo.odoorx.core.base.utils.OdooRecordUtils;
import com.odoo.odoorx.core.base.utils.logger.OLog;
import com.odoo.odoorx.core.config.OConstants;
import com.odoo.odoorx.rxshop.R;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class OSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = OSyncAdapter.class.getSimpleName();
    public static final String SYNC_MODE = "sync_mode";
    public final static String DEVELOPER_MODE = "developer_mode";
    private Context mContext;
    private DaoRepoBase daoRepo = null;
    private Odoo mOdoo;
    private OModel mModel;
    private OSyncService mService;
    private OPreferenceManager preferenceManager;
    private Class<? extends OModel> mModelClass;
    private HashMap<String, ODomain> mDomain = new HashMap<>();
    private HashMap<String, ISyncFinishListener> mSyncFinishListeners = new HashMap<>();

    public OSyncAdapter(Context context, Class<? extends OModel> model, OSyncService service,
                        boolean autoInitialize) {
        super(context, autoInitialize);
        init(context, model, service);
    }

    public OSyncAdapter(Context context, Class<? extends OModel> model, OSyncService service,
                        boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        init(context, model, service);
    }

    private void init(Context context, Class<? extends OModel> model, OSyncService service) {
        mContext = context;
        mModelClass = model;
        mService = service;
        preferenceManager = new OPreferenceManager(mContext);
        daoRepo = DaoRepoBase.init(context);
    }

    public OSyncAdapter setDomain(ODomain domain) {
        mDomain.put(mModel.getModelName(), domain);
        return this;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        OUser user  = OdooAccountManager.getDetails(mContext, account.name);
        if(!daoRepo.isDaosinitialized(user.getUsername()))
        {
            daoRepo.initDaos(user.getUsername());}

        mModel =  daoRepo.getDao(mModelClass);
        OUser mUser = mModel.getUser();

        if (OdooAccountManager.isValidUserObj(mContext, mUser)) {
            // Creating Odoo instance
            mOdoo = createOdooInstance(mContext, mUser);
            if (mOdoo != null) {
                Log.i(TAG, "User        : " + mModel.getUser().getAndroidName());
                Log.i(TAG, "Model       : " + mModel.getModelName());
                Log.i(TAG, "Database    : " + mModel.getDatabaseName());
                Log.i(TAG, "Odoo Version: " + mUser.getOdooVersion().getServerSerie());
                // Calling service callback
                if (mService != null)
                    mService.performDataSync(this, extras, mUser);

                //Creating domain
                ODomain domain = (mDomain.containsKey(mModel.getModelName())) ?
                        mDomain.get(mModel.getModelName()) : new ODomain();
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                // Ready for sync data from server
                //TODO: use the domain here on sync process
                String syncMode = extras.getString(SYNC_MODE) != null ?  extras.getString(SYNC_MODE) : Columns.SyncModel.Mode.SYSTEM_TRIGGERED.name();
                syncData(mModel, domain, syncResult, Columns.SyncModel.Mode.valueOf(syncMode));
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(daoRepo.getContext(), "Unable to connect with Server", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e(TAG, "Unable to connect with Odoo Server.");
            }
        }
    }

//if mode set heck for datalimit
    private void syncData(OModel oModel, ODomain domain_filter,
                          SyncResult result, Columns.SyncModel.Mode syncMode) {
        //Create Sync Process Object here

        OSyncDataUtils dataUtils = new OSyncDataUtils(mContext, mOdoo);

        IrModel syncModelDao = daoRepo.getDao(IrModel.class);
        SyncModel  syncModel = syncModelDao.getOrCreateTrigger(oModel.getModelName(), syncMode, OConstants.getSyncDepth(oModel.getModelName()), QueryFields.all());

        SyncProcess syncProcess = OSyncDataUtils
                .getSyncProcess(syncModel, domain_filter, dataUtils, result);

        syncData(syncProcess);
    }

    private void syncData(SyncProcess syncProcess) {
        //Set variables locally
        OModel oModel = syncProcess.getOModel();
        String modelName = syncProcess.getOModel().getModelName();
        OUser user = syncProcess.getOUser();
        ODomain oDomain = syncProcess.getODomain();
        String msg = "Sync for " + modelName + " Started at " + ODateUtils.getDate();
        syncProcess.update(SyncStatus.STARTED, msg);
        //Log.v(TAG, msg);
        try {
            //STEP 1: Getting list data from server API
            syncProcess.update(SyncStatus.API_CALLED, "Calling server now at " + ODateUtils.getDate());
          ///  ODomain idFilterDommain = new ODomain();             idFilterDommain.add("id", "in",  new ArrayList(syncProcess.getSyncModel().getPullToDeviceIds()));
            OdooResult response = mOdoo
                    .withRetryPolicy(OConstants.RPC_REQUEST_TIME_OUT, OConstants.RPC_REQUEST_RETRIES)
                    .searchRead(oModel.getModelName(), getFields(oModel)
                            , oDomain, 0, 20, "create_date DESC ");
            msg = "Sync api call response received for " + modelName + " at " + ODateUtils.getDate();
            syncProcess.update(SyncStatus.SERVER_RESPONSE_RECEIVED, msg);
            //Step 1a: Null response.
            //TODO: Retry logic may be useful here cos of funny failures
            if (response == null) {
              handleNullResponse(syncProcess);
              return;
            }
            syncProcess.setSyncResponse(response);

            //Step 1b: Error response,
            if (response.containsKey("error")) {
               handleError(syncProcess);
               return;
            }

            syncProcess.update(SyncStatus.VALID_RESPONSE, "Received valid response from server");

            msg = "Received valid response  from server ";
            int count = response.getRecords().size();
            msg += "  Count (" + count  + " records)";
            //Log.v(TAG, "Now processing " + count + " records");
            syncProcess.setServerCount(count);
            syncProcess.update(SyncStatus.PRE_PROCESSING, msg);

            //Step 2: Handle the results
            syncProcess.getDataUtils().handleResult(syncProcess);
            syncProcess.update(SyncStatus.SAVED_RECORDS, "Received " + count + " records from API");

            // Updating records on server if local are latest updated.
            // if modelName allowed update record to server
            //Step 2: Update server records
            //TODO: Use SyncProcess
            if (syncProcess.getOModel().allowUpdateRecordOnServer()) {
                syncProcess.update(SyncStatus.SERVER_UPDATE, "Will now update server records of locally dirty");
                syncProcess.getDataUtils().updateRecordsOnServer(syncProcess);
                syncProcess.update(SyncStatus.SERVER_UPDATED, "Updated locally dirty records.");
            }

            // Creating or updating relation records
            handleRelationRecords(syncProcess);

            // If modelName allowed to create record on server
            if (syncProcess.getOModel().allowCreateRecordOnServer()) {
                createRecordsOnServer(oModel);
            }

            // If modelName allowed to delete record on server
            if (oModel.allowDeleteRecordOnServer()) {
                removeRecordOnServer(oModel);
            }

            // If modelName allowed to delete server removed record from local database
            if (oModel.allowDeleteRecordInLocal()) {
                removeNonExistRecordFromLocal(oModel);
            }

            //Log.v(TAG, "Sync for (" + oModel.getModelName() + ") finished at " + ODateUtils.getDate());

            oModel.onSyncFinished();

        } catch (Exception e) {
            e.printStackTrace();
            oModel.onSyncFailed();
        }
        // Performing next sync if any in service
        //TODO: Keep track
        if (mSyncFinishListeners.containsKey(oModel.getModelName())) {
            OSyncAdapter adapter = mSyncFinishListeners.get(oModel.getModelName())
                    .performNextSync(user, syncProcess.getSyncResult());
            mSyncFinishListeners.remove(oModel.getModelName());
            if (adapter != null) {
                SyncResult syncResult = new SyncResult();
                OModel syncModel = oModel.createInstance(adapter.getModelClass());
                ContentProviderClient contentProviderClient =
                        mContext.getContentResolver().acquireContentProviderClient(syncModel.authority());
                adapter.onPerformSync(user.getAccount(), null, syncModel.authority(),
                        contentProviderClient, syncResult);
            }
        }
        oModel.close();
    }

    private void handleError(SyncProcess syncProcess){
        daoRepo.setOdoo(null, syncProcess.getOUser());
        OPreferenceManager pref = new OPreferenceManager(mContext);
        OdooResult error = syncProcess.getSyncResponse().getMap("error");
        if (pref.getBoolean(DEVELOPER_MODE, false)) {
            OLog.log("ERROR_RESPONSE ERROR_RESPONSE :(" + error);
        }
        String msg = "Received error response from server " + error;
        syncProcess.update(SyncStatus.ERROR_RESPONSE, msg);
        return;
    }
    //TODO: Remove LOG FROM HERE, USE SYNC PROCESS
    private void handleNullResponse(SyncProcess syncProcess){
        // FIXME: Check in library. May be timeout issue with slow network.
        String msg = "Received null response from server or network.";
        Log.w(TAG, msg);
        syncProcess.update(SyncStatus.NULL_RESONSE, msg);
        return;
    }

    private void handleRelationRecords(SyncProcess parentSyncProcess) {

        parentSyncProcess.update(SyncStatus.PROCESSING, TAG + " HandleRelationRecords for " + parentSyncProcess.getChildrenNames() + " relations");
        Iterator<SyncProcess> childProcesses = parentSyncProcess.getChildren();
        while (childProcesses.hasNext()) {
            SyncProcess childSyncProcess = childProcesses.next();
            Set<Integer> relationModelIds = childSyncProcess.getSyncModel().getPullToDeviceIds();
            parentSyncProcess.update(SyncStatus.PROCESSING, TAG + " HandleRelationRecord for " + childSyncProcess.getModelName() + "  relation");
            //Log.id(TAG, "handleRelationRecords (" + childSyncProcess.getModelName() + ")" );
            OModel rel_model = childSyncProcess.getOModel();
                    //daoRepo.getDao((Class<? extends OModel>) record.getRelationModel());
            //SyncModel relatedSyncModel = oModel.getSyncModel();

            // Skipping blank sync request if there is no any ids to sync.
            if (!relationModelIds.isEmpty()) {
                parentSyncProcess.update(SyncStatus.CHILD_SYNC_CREATED, TAG + " Creating new sync process for " + rel_model.getModelName() + "  relation");
                syncData(childSyncProcess);
                parentSyncProcess.update(SyncStatus.CHILD_SYNC_FINISHED, TAG + " Finished sync process for " + rel_model.getModelName() + "  relation");
            }
            // Updating manyToOne record with their relation record row_id
//            switch (record.getRelationType()) {
//                case ManyToOne:
//                    // Nothing to do. Already added link with record relation
//                    break;
//                case OneToMany:
//                    // Update related_column with base id's row_id for each of record ids
//                    String related_column = record.getRelatedColumn();
//                    for (Integer id : record.getUniqueIds()) {
//                        OValues values = new OValues();
//                        ODataRow rec = rel_model.browse(rel_model.selectRowId(id));
//                        values.put(related_column, rec.getInt(related_column));
//                        values.put("_is_dirty", "false");
//                        rel_model.update(rel_model.selectRowId(id), values);
//                    }
//                    break;
//                case ManyToMany:
//                    // Nothing to do. Already added relation records links
//                    break;
//            }

            for (Integer id : relationModelIds) {
                OValues values = new OValues();
                //TODO: Update related column
                values.put("_is_dirty", "false");
                rel_model.update(rel_model.selectRowId(id), values);
            }
            rel_model.close();
        }

        parentSyncProcess.update(SyncStatus.RELATION_RECORDS_UPDATE, TAG + " Finished syncing process for all children relations");
        parentSyncProcess.update(SyncStatus.COMPLETED, TAG + " Finished syncing process for " + parentSyncProcess.getModelName() + " and all children relations");
    }

    public static Odoo createOdooInstance(final Context context, final OUser user) {
        DaoRepoBase daoRepo = DaoRepoBase.getInstance();
        Odoo odoo = daoRepo.getOdoo(user);
        boolean hostNotAvailable;
        try { hostNotAvailable = !Odoo.isHostAvailable(user.getHost(), 3000);
            if(hostNotAvailable){
                return null;
            }
            if (odoo == null) {
                odoo = Odoo.createQuickInstance(context, user.getHost());
                OUser mUser = odoo
                        .withRetryPolicy(OConstants.RPC_REQUEST_TIME_OUT, OConstants.RPC_REQUEST_RETRIES)
                        .authenticate(user.getUsername(), user.getPassword(), user.getDatabase());
                daoRepo.setOdoo(odoo, user);
                if (mUser != null) {
                    ResCompany company = new ResCompany(context, user);
                    if (company.count("id = ? ", new String[]{user.getCompanyId() + ""}) <= 0) {
                        ODataRow company_details = new ODataRow();
                        company_details.put(Columns.server_id, user.getCompanyId());
                        company.quickCreateRecord(company_details);
                    }
                } else {
                    // FIXME: Unable to get user object or may be due session destroyed with Odoo Saas (single connection support only)
                    Log.e(TAG, OResource.string(context, R.string.toast_something_gone_wrong));
                }
            }
        } catch (OdooVersionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return odoo;
    }

    private OdooFields getFields(OModel oModel) {
        OdooFields fields = new OdooFields();
        List<String> names = new ArrayList<>();
        for (OColumn column : oModel.getColumns(false)) {
            names.add(column.getSyncColumn());
        }
        fields.addAll(names.toArray(new String[names.size()]));
        return fields;
    }
    //TODO: After Resync, records are created again
    /**
     * Creates locally created record on server (id with zero)
     *
     * @param oModel modelName object
     */
    private void createRecordsOnServer(OModel oModel) throws OdooServerException {
        List<ODataRow> records = oModel.select(null,
                "(id = ? or id = ?)", new String[]{"0", "false"});
        int counter = 0;
        for (ODataRow record : records) {
            if (validateRelationRecords(oModel, record)) {
                /*
                 * Need to check server id for record,
                 * it is possible that record created on server by validating main record.
                 */
                if (oModel.selectServerId(record.getInt(OColumn.ROW_ID)) == 0) {
                    int id = createOnServer(oModel, OdooRecordUtils.createRecordValues(oModel, record));
                    if (id != OModel.INVALID_ROW_ID) {
                        OValues values = new OValues();
                        values.put(Columns.server_id, id);
                        values.put("_is_dirty", "false");
                        values.put("_write_date", ODateUtils.getUTCDate());
                        oModel.update(record.getInt(OColumn.ROW_ID), values);
                        counter++;
                    } else {
                        Log.e(TAG, "Unable to create record on server.");
                    }
                }
            }
        }
        if (counter == records.size()) {
            //Log.i(TAG, counter + " records created on server.");
        }
    }

    /**
     * Validate relation record for the record. And if relation record not created on server.
     * It will be created on server before syncing original record
     *
     * @param oModel
     * @param row
     * @return updatedRow
     */
    public static boolean validateRelationRecords(OModel oModel, ODataRow row) throws OdooServerException {
        Log.d(TAG, "Validating relation records for record");
        // Check for relation local record
        for (OColumn column : oModel.getRelationColumns()) {
            OModel relModel = oModel.createInstance(column.getType());
            switch (column.getRelationType()) {
                case ManyToOne:
                    if (!row.getString(column.getName()).equals("false")) {
                        ODataRow m2oRec = row.getM2ORecord(column.getName()).browse();
                        if (m2oRec != null && m2oRec.getInt(Columns.server_id) == 0) {
                            int new_id = relModel.getServerDataHelper().createOnServer(
                                    OdooRecordUtils.createRecordValues(relModel, m2oRec));
                            updateRecordServerId(relModel, m2oRec.getInt(OColumn.ROW_ID), new_id);
                        }
                    }
                    break;
                case ManyToMany:
                    List<ODataRow> m2mRecs = row.getM2MRecord(column.getName()).browseEach();
                    if (!m2mRecs.isEmpty()) {
                        for (ODataRow m2mRec : m2mRecs) {
                            if (m2mRec.getInt(Columns.server_id) == 0) {
                                int new_id = relModel.getServerDataHelper().createOnServer(
                                        OdooRecordUtils.createRecordValues(relModel, m2mRec));
                                updateRecordServerId(relModel, m2mRec.getInt(OColumn.ROW_ID), new_id);
                            }
                        }
                    }
                    break;
                case OneToMany:
                    List<ODataRow> o2mRecs = row.getO2MRecord(column.getName()).browseEach();
                    if (!o2mRecs.isEmpty()) {
                        for (ODataRow o2mRec : o2mRecs) {
                            if (o2mRec.getInt(Columns.server_id) == 0) {
                                int new_id = relModel.getServerDataHelper().createOnServer(
                                        OdooRecordUtils.createRecordValues(relModel, o2mRec));
                                updateRecordServerId(relModel, o2mRec.getInt(OColumn.ROW_ID), new_id);
                            }
                        }
                    }
                    break;
            }
        }
        return true;
    }

    /**
     * Updating local record with server id
     *
     * @param oModel
     * @param row_id
     * @param server_id
     */
    private static void updateRecordServerId(OModel oModel, int row_id, int server_id) {
        OValues values = new OValues();
        values.put(Columns.server_id, server_id);
        values.put("_is_dirty", "false");
        oModel.update(row_id, values);
    }

    private int createOnServer(OModel oModel, ORecordValues values) {
        int id = OModel.INVALID_ROW_ID;
        try {
            if (values != null) {
                OdooResult result = mOdoo.createRecord(oModel.getModelName(), values);
                if (result.hasError()) {
                    Log.e(TAG, "Error occurred creating model (" + oModel.getModelName() + ") : " + result.getString("error"));
                }
                id = result.getInt("result");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    /**
     * Removes record on server if local record is not active
     *
     * @param oModel
     */
    private void removeRecordOnServer(OModel oModel) {
        List<ODataRow> records = oModel.select(new String[]{},
                "id != ? and _is_active = ?", new String[]{"0", "false"});
        List<Integer> serverIds = new ArrayList<>();
        for (ODataRow record : records) {
            serverIds.add(record.getInt(Columns.server_id));
        }
        if (serverIds.size() > 0) {
            if (removeRecordsFromServer(oModel, serverIds)) {
                int counter = oModel.deleteRecords(serverIds, true);
                Log.i(TAG, counter + " records removed from server and local database");
            } else {
                Log.e(TAG, "Unable to remove records from server");
            }
        }
    }

    private boolean removeRecordsFromServer(OModel oModel, List<Integer> serverIds) {
        try {
            mOdoo.unlinkRecord(oModel.getModelName(), serverIds);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Removes non exist record from local database
     *
     * @param oModel
     */
    private void removeNonExistRecordFromLocal(OModel oModel) {
        List<Integer> ids = oModel.getServerIds();
        try {
            ODomain domain = new ODomain();
            domain.add(Columns.server_id, "in", ids);
            OdooFields fields = new OdooFields();
            fields.addAll(new String[]{Columns.server_id});
            OdooResult result = mOdoo.searchRead(oModel.getModelName(), fields, domain, 0, 0, null);
            List<OdooRecord> records = result.getRecords();
            if (!records.isEmpty()) {
                for (OdooRecord record : records) {
                    ids.remove(ids.indexOf(record.getInt(Columns.server_id)));
                }
            }
            int removedCounter = 0;
            if (ids.size() > 0) {
                removedCounter = oModel.deleteRecords(ids, true);
            }
            Log.i(TAG, removedCounter + " Records removed from local database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Class<? extends OModel> getModelClass() {
        return mModelClass;
    }

    public void setModel(OModel model){
        this.mModel = model;
    }

    public OSyncAdapter onSyncFinish(ISyncFinishListener syncFinish) {
        mSyncFinishListeners.put(mModel.getModelName(), syncFinish);
        return this;
    }
}
