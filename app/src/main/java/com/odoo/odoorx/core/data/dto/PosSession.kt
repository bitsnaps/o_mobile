package com.odoo.odoorx.core.data.dto

import com.odoo.odoorx.core.base.orm.OValues
import com.odoo.odoorx.core.data.db.Columns
import java.util.*

class PosSession(var id: Int?, var serverId: Int?, var name: String?, var user: User?, var config: PosConfig?,
                 var startAt: Date?, var stopAt: Date?,var state: String?,var sequenceNo: Int?,var loginNo: Int?,var rescue: Boolean?,
                 var statements: List<AccountBankStatement>) : DTO

{

    override fun toOValues(): OValues {
        val oValues = OValues()
        oValues.put(Columns.server_id, serverId)
        oValues.put(Columns.name, name)
        oValues.put(Columns.PosSession.user_id, user?.id)
        oValues.put(Columns.PosSession.config_id, config?.id)
        oValues.put(Columns.PosSession.start_at, startAt)
        oValues.put(Columns.PosSession.stop_at, stopAt)
        oValues.put(Columns.PosSession.state, state)
        oValues.put(Columns.PosSession.sequence_no, sequenceNo)
        oValues.put(Columns.PosSession.login_no, loginNo)
        oValues.put(Columns.PosSession.rescue, rescue)
        return oValues
    }

    public override fun toString(): String {
        return "id: {$id}"
    }

}