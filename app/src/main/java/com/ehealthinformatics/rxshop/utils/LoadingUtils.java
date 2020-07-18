package com.ehealthinformatics.rxshop.utils;


import com.ehealthinformatics.RxShop;
import com.ehealthinformatics.odoorx.core.base.support.OUser;
import com.ehealthinformatics.odoorx.core.base.auth.OUserAccount;
import com.ehealthinformatics.odoorx.core.base.auth.ServerDefaultsService;
import com.ehealthinformatics.odoorx.core.data.dao.ProductDao;
import com.ehealthinformatics.odoorx.core.data.dao.QueryFields;
import com.ehealthinformatics.odoorx.core.data.dto.AccountBankStatement;
import com.ehealthinformatics.odoorx.core.data.dto.PosSession;
import com.ehealthinformatics.odoorx.core.data.dto.User;
import com.ehealthinformatics.odoorx.rxshop.dto.SyncConfig;

import java.util.List;

public class LoadingUtils  {

    public static class ArtifactsLoader {

        private OUserAccount oUserAccount;
        private OUser oUser;

        public ArtifactsLoader(OUserAccount oUserAccount) {
            this.oUserAccount = oUserAccount;
            this.oUser = oUserAccount.getOUser();
        }

        public SyncConfig load() {
            User user;
            PosSession posSession;
            List<AccountBankStatement> accountBankStatements;
            ServerDefaultsService serverDefaultsService = new ServerDefaultsService(this.oUserAccount);
            user = serverDefaultsService.syncUser(oUser.getUserId());
            if (user != null) {
                posSession = serverDefaultsService.syncCurrentOpenSession(user);
                if (posSession != null) { oUser.setPosSessionId(posSession.getId());
                    accountBankStatements = serverDefaultsService.syncSessionStatement(posSession);
                    if (accountBankStatements != null) {
                        return new SyncConfig(user, posSession);
                    }
                }
            }
            return null;
        }

        public void loadProductsToCache() {
            ProductDao productDao = RxShop.getDao(ProductDao.class);
            productDao.lazySelectAll(QueryFields.all());
        }
    }

}
