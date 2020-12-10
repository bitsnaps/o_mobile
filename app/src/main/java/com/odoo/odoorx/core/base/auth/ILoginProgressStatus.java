package com.odoo.odoorx.core.base.auth;

import com.odoo.odoorx.core.base.rpc.listeners.OdooError;
import com.odoo.odoorx.core.base.support.OUser;

import java.util.List;

public interface ILoginProgressStatus {

    void onConnect(List<String> databases);
    void onLoginSuccess(OUser oUser);
    void onConnectionError(OdooError error);

}
