package com.odoo.odoorx.core.data.viewmodel;

import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.odoo.odoorx.core.base.rpc.helper.ODomain;
import com.odoo.odoorx.core.data.dao.DaoRepoBase;
import com.odoo.odoorx.core.data.dao.PosOrderDao;
import com.odoo.odoorx.core.data.dao.QueryFields;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.dto.PosOrder;

import java.util.HashMap;
import java.util.List;

public class PosOrderListViewModel extends ViewModel {
    private final HashMap<String, List<PosOrder>> cache = new HashMap<>();
    private final MutableLiveData<List<PosOrder>> posOrderLiveData =
            new MutableLiveData<>();
    private PosOrderDao posOrderDao;

    public PosOrderListViewModel()
    {
        DaoRepoBase daoRepo = DaoRepoBase.getInstance();
        posOrderDao = daoRepo.getDao(PosOrderDao.class);
        loadData();
    }

    public void loadData() {
        if(cache.get("all") != null){
            posOrderLiveData.setValue(cache.get("all"));
        }
        new AsyncTask<Void,Void,List<PosOrder>>() {
            @Override
            protected List<PosOrder> doInBackground(Void... voids) {
                List<PosOrder> posOrders = posOrderDao.selectAll(QueryFields.root());
                return posOrders;
            }
            @Override
            protected void onPostExecute(List<PosOrder> data) {
                cache.put("all", data);
                posOrderLiveData.setValue(data);
            }
        }.execute();
    }

    public void onStateChange(final String state) {
        if(state.equalsIgnoreCase("all")){
            loadData();
        }else {
            if(cache.get(state) != null){
                posOrderLiveData.setValue(cache.get(state));
            }
            new AsyncTask<Void,Void,List<PosOrder>>() {
                @Override
                protected List<PosOrder> doInBackground(Void... voids) {
                    List<PosOrder> posOrders = posOrderDao.selectByState(state, QueryFields.all());
                    return posOrders;
                }
                @Override
                protected void onPostExecute(List<PosOrder> data) {
                    cache.put("state", data);
                    posOrderLiveData.setValue(data);
                }
            }.execute();
        }

    }

    public void quickSync(final PosOrder posOrder){

        new AsyncTask<PosOrder, Void, Integer>(){
            @Override
            protected Integer doInBackground(PosOrder... voids) {
                ODomain oDomain = new ODomain();
                oDomain.add(Columns.id, "=", posOrder.getId());
                posOrderDao.quickCreateRecord(posOrder.toOValues().toDataRow());
                return 0;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                loadData();
            }
        }.execute();
    }

    public void searchFilter(final String filterText){

        new AsyncTask<String, Void, List<PosOrder>>(){
            @Override
            protected List<PosOrder> doInBackground(String... searchText) {
//                ArrayList<PosOrder> filtered = new ArrayList<>();
//                for(PosOrder posOrder: posOrderLiveData.getValue()){
//                    if(posOrder.getName().startsWith(filterText)) {
//                        filtered.add(posOrder);
//                    }
//                }
                return posOrderDao.searchFilter(filterText, QueryFields.all());
            }

            @Override
            protected void onPostExecute(List<PosOrder> posOrderList) {
                super.onPostExecute(posOrderList);
                posOrderLiveData.setValue(posOrderList);
            }
        }.execute();
    }

    public MutableLiveData<List<PosOrder>> getData(){
        return posOrderLiveData;
    }

}