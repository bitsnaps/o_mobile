package com.odoo.base.addons.abirex.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.support.v4.content.Loader
import android.util.Log
import com.odoo.App

import com.odoo.BuildConfig
import com.odoo.base.addons.abirex.model.PosOrder
import com.odoo.base.addons.abirex.model.PosSession
import com.odoo.base.addons.abirex.model.PurchaseOrder
import com.odoo.base.addons.res.ResCompany
import com.odoo.base.addons.res.ResCurrency
import com.odoo.base.addons.res.ResUsers
import com.odoo.core.orm.ODataRow
import com.odoo.core.orm.OModel
import com.odoo.core.orm.fields.OColumn
import com.odoo.core.support.OUser
import com.odoo.data.LazyList

import com.odoo.core.orm.fields.OColumn.RelationType
import com.odoo.core.orm.fields.types.*
import com.odoo.core.utils.OCursorUtils

class PosSessionDao(context: Context, user: OUser?) : OModel(context, "pos.session", user) {

    internal var config_id = OColumn("Pos Config", PosConfigDao::class.java, RelationType.ManyToOne).setRequired()
    internal var name = OColumn("Session ID", OInteger::class.java)
    internal var user_id = OColumn("User", ResUsers::class.java,RelationType.ManyToOne)
    internal var start_at = OColumn("Start Date", ODateTime::class.java)
    internal var stop_at = OColumn("Stop Date", ODateTime::class.java)
    internal var state = OColumn("State", OSelection::class.java)
            .addSelection("opening_control", "Opening Control")
            .addSelection("opened", "In Progress")
            .addSelection("closing_control", "Closing Control")
            .addSelection("closed", "Closed & Posted")
    internal var sequence_number = OColumn("Order Sequence Number", OInteger::class.java)
    internal var login_number = OColumn("Login Number", OInteger::class.java)
//    internal var cash_journal_id = OColumn("Cash Journal", OVarchar::class.java)
//    internal var cash_register_id = OColumn("Company", ResCompany::class.java, RelationType.ManyToOne)
    internal var rescue = OColumn("Rescue", OBoolean::class.java)

    internal var userDao = ResUsers(context, user)

    fun  posSessionCreator(): LazyList.ItemFactory<PosSession> {
        return object : LazyList.ItemFactory<PosSession> {
            override fun create(cursor: Cursor, index: Int): PosSession {
                cursor.moveToPosition(index);
                var row = OCursorUtils.toDatarow(cursor)
                return fromRow(row)
            }
        }
    }

    init {
        setHasMailChatter(true)
    }

    operator fun get(id: Int): PosSession? {
        val oDataRow = browse(id)
        populateRelatedColumns(relationColumns.map{it.name}.toTypedArray(), oDataRow)
        return fromRow(oDataRow)
    }

    fun fromRow(row: ODataRow): PosSession{
        val id = row.getInt(OColumn.ROW_ID)
        val configId = row.getInt(config_id.name)
        val name = row.getString(name.name)
        val user = userDao.fromRow(row.getM2ORecord(user_id.name).browse())
        val startTime = row.getString(start_at.name)
        val stopTime = row.getString(stop_at.name)
        val state = row.getString(state.name)
        val sequenceNo = row.getInt(sequence_number.name)
        val loginNo = row.getInt(login_number.name)
//        val cashJournalId = row.getInt(cash_journal_id.name)
//        val cashRegisterId = row.getBoolean(cash_register_id.name)
        val rescue = row.getBoolean(rescue.name)
        return PosSession(id, name, user)
    }
//
//    override fun uri(): Uri {
//
//        return buildURI(AUTHORITY)
//    }

    override fun onModelUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
//
//    override fun onSyncStarted() {
//        Log.e(TAG, "PosSessionDao->onSyncStarted")
//    }
//
//    override fun onSyncFinished() {
//        Log.e(TAG, "PosSessionDao->onSyncFinished")
//    }

//    companion object {
//
//        val TAG = PosSessionDao::class.java.simpleName
//        @kotlin.jvm.JvmField
//        var AUTHORITY = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.pos_session"
//    }


    override fun allowCreateRecordOnServer(): Boolean {
        return false
    }

    override fun allowUpdateRecordOnServer(): Boolean {
        return false
    }

    override fun allowDeleteRecordInLocal(): Boolean {
        return false
    }
}
