package com.odoo.base.addons.abirex.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build

import com.odoo.base.addons.abirex.dto.PosSession
import com.odoo.base.addons.abirex.dto.User
import com.odoo.base.addons.res.ResUsers
import com.odoo.core.orm.ODataRow
import com.odoo.core.orm.OModel
import com.odoo.core.orm.fields.OColumn
import com.odoo.core.support.OUser
import com.odoo.data.LazyList

import com.odoo.core.orm.fields.OColumn.RelationType
import com.odoo.core.orm.fields.types.*
import com.odoo.core.utils.OCursorUtils
import android.os.Build.MANUFACTURER
import android.text.TextUtils
import com.odoo.App
import com.odoo.base.addons.abirex.util.DateUtils
import com.odoo.data.abirex.Columns
import com.odoo.data.abirex.ModelNames


class PosSessionDao(context: Context, user: OUser?) : OModel(context, ModelNames.POS_SESSION, user) {

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

    lateinit  var userDao : ResUsers
    lateinit var posConfigDao: PosConfigDao
    var userr = user


    override fun initDaos() {
        posConfigDao = App.getDao(PosConfigDao::class.java)
        userDao = App.getDao(ResUsers::class.java)
    }

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

    override fun get(id: Int): PosSession {
        val oDataRow = browse(id)
        return fromRow(oDataRow)
    }

     override fun fromRow(row: ODataRow): PosSession {
         super.fromRow(row)
         val id = row.getInt(Columns.id)
         val serverId = row.getInt(Columns.server_id)
        val posConfig = posConfigDao.fromDataRow(row.getM2ORecord(config_id.name).browse())
        val name = row.getString(name.name)
        val user = userDao.fromRow(row.getM2ORecord(user_id.name).browse())
         //TODO: use state to make start and stop date null if session hasn't being started
        val startTime = if(row.getString(start_at.name) != "false")  DateUtils.parseToYYDDMM(row.getString(start_at.name)) else  DateUtils.now()
        val stopTime =  if(row.getString(stop_at.name) != "false")  DateUtils.parseToYYDDMM(row.getString(stop_at.name)) else  DateUtils.now()
        val state = row.getString(state.name)
         val sequenceNo = row.getInt(sequence_number.name)
        val loginNo = row.getInt(login_number.name)
//      val cashJournalId = row.getInt(cash_journal_id.name)
//      val cashRegisterId = row.getBoolean(cash_register_id.name)
        val rescue = row.getBoolean(rescue.name)
        return PosSession(id, serverId, name, user, posConfig, startTime , stopTime,
                    state, sequenceNo, loginNo, rescue)

    }
//
//    override fun uri(): Uri {
//
//        return buildURI(AUTHORITY)
//    }

    override fun onModelUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    fun new (user: User) : PosSession {
        val now = DateUtils.now()
        val posSession = PosSession(0, 0, getDeviceName() + " - " + now , user, posConfigDao[1]
                , now,  now, "opened", 1, 1, false)
        posSession.id = insert(posSession.toOValues())
        return posSession
    }


    fun getDeviceName(): String {
        val manufacturer = MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else capitalize(manufacturer) + " " + model
    }

    private fun capitalize(str: String): String {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true

        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c))
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }

        return phrase.toString()
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
