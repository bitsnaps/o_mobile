package com.odoo.odoorx.rxshop.dto

import com.odoo.odoorx.core.base.auth.ISyncConfig
import com.odoo.odoorx.core.base.support.OUser
import com.odoo.odoorx.core.data.dto.PosSession
import com.odoo.odoorx.core.data.dto.User

data class SyncConfig(var user: User?, var posSession: PosSession?): ISyncConfig
{
    override fun isValid(): Boolean {
        return user == null || posSession == null
    }

    override fun updateUserConfig(oUser: OUser?): Boolean {
        return true;
    }

    public override fun toString(): String {
        return "{ { user: " + user.toString() + "}, { posSession: " + posSession.toString() + "}";
    }
}

