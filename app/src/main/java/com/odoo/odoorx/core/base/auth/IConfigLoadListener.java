package com.odoo.odoorx.core.base.auth;

import com.odoo.odoorx.core.base.rpc.listeners.OdooError;

public interface IConfigLoadListener {
    ISyncConfig onStartConfigLoad();
    void onConfigLoadError(OdooError e);
    void onConfigLoadSuccess(ISyncConfig syncConfig);
}
