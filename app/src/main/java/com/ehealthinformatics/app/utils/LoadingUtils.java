package com.ehealthinformatics.app.utils;

import android.content.Context;

import com.ehealthinformatics.App;
import com.ehealthinformatics.core.auth.ServerDefaultsService;
import com.ehealthinformatics.core.support.OUser;
import com.ehealthinformatics.data.dao.ProductDao;
import com.ehealthinformatics.data.dao.QueryFields;
import com.ehealthinformatics.data.dto.AccountBankStatement;
import com.ehealthinformatics.data.dto.PosSession;
import com.ehealthinformatics.data.dto.SyncConfig;
import com.ehealthinformatics.data.dto.User;

import java.util.List;

public class LoadingUtils {





    public static class ArtifactsLoader {

        private OUser oUser;
        private Context context;

        public ArtifactsLoader(Context context, final OUser oUser) {
            this.oUser = oUser;
            this.context = context;
        }

        public SyncConfig init() {
            User user;
            PosSession posSession;
            List<AccountBankStatement> accountBankStatements;
            App.initDaos(oUser.getUsername());
            ServerDefaultsService serverDefaultsService = new ServerDefaultsService(App.getContext(), oUser);
            user = serverDefaultsService.syncUser(oUser.getUserId());
            if (user != null) {
                posSession = serverDefaultsService.syncCurrentOpenSession(user);
                if (posSession != null) {
                    oUser.setPosSessionId(posSession.getId());
                    accountBankStatements = serverDefaultsService.syncSessionStatement(posSession);
                    if (accountBankStatements != null) {
                        return new SyncConfig(user, posSession);
                    }
                }
            }
            return null;
        }

        public void loadProductsToCache() {
            ProductDao productDao = App.getDao(ProductDao.class);
            productDao.lazySelectAll(QueryFields.all());
        }

    }


}
