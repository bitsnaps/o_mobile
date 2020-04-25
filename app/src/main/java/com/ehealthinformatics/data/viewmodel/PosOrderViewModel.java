package com.ehealthinformatics.data.viewmodel;


import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ehealthinformatics.App;
import com.ehealthinformatics.data.dao.AccountBankStatementDao;
import com.ehealthinformatics.data.dao.AccountBankStatementLineDao;
import com.ehealthinformatics.data.dao.PosOrderDao;
import com.ehealthinformatics.data.dao.PosOrderLineDao;
import com.ehealthinformatics.data.dao.QueryFields;
import com.ehealthinformatics.data.dto.AccountBankStatement;
import com.ehealthinformatics.data.dto.AccountBankStatementLine;
import com.ehealthinformatics.data.dto.PosOrder;

import java.util.List;

public class PosOrderViewModel extends ViewModel {

    private PosOrderDao posOrderDao;
    private PosOrderLineDao posOrderLineDao;
    private final MutableLiveData<PosOrder> selected = new MutableLiveData<>();
    AccountBankStatementDao accountBankStatementDao;
    AccountBankStatementLineDao accountBankStatementLineDao;

    public PosOrderViewModel(){
        this.posOrderDao = App.getDao(PosOrderDao.class);
        this.posOrderLineDao = App.getDao(PosOrderLineDao.class);
        this.accountBankStatementDao = App.getDao(AccountBankStatementDao.class);
        this.accountBankStatementLineDao = App.getDao(AccountBankStatementLineDao.class);
    }

    public void loadData(final int id) {
        new AsyncTask<Void,Void, PosOrder>() {
            @Override
            protected PosOrder doInBackground(Void... voids) {
                PosOrder posOrder;
                if(id > 0){
                    posOrder = posOrderDao.get(id, QueryFields.all());
                    posOrder.getLines().addAll(posOrderLineDao.fromPosOrder(posOrder, QueryFields.all()));
                    posOrder.getAccountBankStatements().addAll(accountBankStatementDao.posSessionStatements(posOrder.getSession()));
                    for(AccountBankStatement statement: posOrder.getAccountBankStatements()) {
                        statement.getStatements().addAll(accountBankStatementLineDao.forStatement(posOrder, statement, QueryFields.all()));
                    }
                } else {
                    posOrder = posOrderDao.newOrder();
                }
                return posOrder;
            }
            @Override
            protected void onPostExecute(PosOrder data) {
                selected.setValue(data);
            }
        }.execute();
    }

    public LiveData<PosOrder> getSelected() {
        return selected;
    }

}