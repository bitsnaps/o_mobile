package com.ehealthinformatics.data.viewmodel;


import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ehealthinformatics.App;
import com.ehealthinformatics.core.utils.DateUtils;
import com.ehealthinformatics.data.dao.PosOrderDao;
import com.ehealthinformatics.data.dao.PosSessionDao;
import com.ehealthinformatics.data.dao.ProductDao;
import com.ehealthinformatics.data.dto.Dashboard;
import com.ehealthinformatics.data.dto.PosSession;
import com.ehealthinformatics.data.dao.ResPartner;

public class DashboardViewModel extends ViewModel {
    private ProductDao productDao;
    private PosOrderDao posOrderDao;
    private ResPartner partnerDao;
    private PosSessionDao posSessionDao;
    private final MutableLiveData<Dashboard> selected = new MutableLiveData<>();

    public DashboardViewModel() {
        this.posOrderDao = App.getDao(PosOrderDao.class);
        this.partnerDao = App.getDao(ResPartner.class);
        this.posSessionDao = App.getDao(PosSessionDao.class);
        this.productDao = App.getDao(ProductDao.class);
    }

    public void loadDashboardData() {
        new AsyncTask<Void,Void, Dashboard>() {
            @Override
            protected Dashboard doInBackground(Void... voids) {
                String noOfCustomers = partnerDao.count(null, null) + "";
                String noOfOrders = posOrderDao.count(null, null) + "";
                String noOfSessions = posSessionDao.count(null, null) + "";
                String noOfProducts = productDao.count(null, null) + "";
                PosSession currentSession = posSessionDao.current();
                String totalPayment = posOrderDao.totalPayments(currentSession.getId()) + "";
                String startAt = currentSession.getStartAt() == null ? "Not Started" : DateUtils.formatToYYDDMMHHMMSS(currentSession.getStartAt());
                return new Dashboard(posSessionDao.getUser().toDTO(posOrderDao.userDao), noOfCustomers, noOfOrders, noOfSessions,noOfProducts, totalPayment,
                      currentSession,"200,000", startAt, "200,000", "200,000");
            }
            @Override
            protected void onPostExecute(Dashboard data) {
                selected.setValue(data);
            }
        }.execute();
    }

    public LiveData<Dashboard> getDashboardData() {
        return selected;
    }


}