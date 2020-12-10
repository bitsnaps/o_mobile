package com.odoo.odoorx.core.base.auth;

import com.odoo.odoorx.core.base.support.OUser;

public interface ISyncConfig {
    boolean isValid();
    boolean updateUserConfig(OUser oUser);
}
