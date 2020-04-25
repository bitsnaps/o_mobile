package com.ehealthinformatics.data.dao

import android.content.Context
import android.os.Build

import com.ehealthinformatics.data.dto.PosSession
import com.ehealthinformatics.data.dto.User
import com.ehealthinformatics.core.orm.ODataRow
import com.ehealthinformatics.core.orm.OModel
import com.ehealthinformatics.core.orm.fields.OColumn
import com.ehealthinformatics.core.support.OUser
import com.ehealthinformatics.data.LazyList

import com.ehealthinformatics.core.orm.fields.OColumn.RelationType
import com.ehealthinformatics.core.orm.fields.types.*
import android.os.Build.MANUFACTURER
import com.ehealthinformatics.App
import com.ehealthinformatics.core.utils.DateUtils
import com.ehealthinformatics.core.orm.OValues
import com.ehealthinformatics.core.rpc.helper.ODomain
import com.ehealthinformatics.core.utils.StringUtils
import com.ehealthinformatics.data.db.Columns
import com.ehealthinformatics.data.db.ModelNames
import com.ehealthinformatics.data.dto.PosConfig
import java.util.*
import kotlin.collections.ArrayList


class PosSessionDao(context: Context, user: OUser?) : OModel(context, ModelNames.POS_SESSION, user) {

    val c = Columns.PosSession()
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
//    internal var statement_ids = OColumn("Statements", AccountBankStatementDao::class.java,RelationType.OneToMany)
//            .setRelatedColumn("pos_session_id")

    lateinit  var userDao : ResUsers
    lateinit var posConfigDao: PosConfigDao
    lateinit var accountBankStatementDao: AccountBankStatementDao
    var userr = user

    override fun insert(values: OValues?): Int {
        return super.insert(values)
    }

    override fun initDaos() {
        posConfigDao = App.getDao(PosConfigDao::class.java)
        accountBankStatementDao = App.getDao(AccountBankStatementDao::class.java)
        userDao = App.getDao(ResUsers::class.java)
    }

    fun  posSessionCreator(): LazyList.ItemFactory<PosSession> {
        return object : LazyList.ItemFactory<PosSession> {
            override fun create(id: Int): PosSession {
                return get(id, QueryFields.id())
            }
        }
    }

    init {
        setHasMailChatter(true)
    }

    override fun get(id: Int, qf: QueryFields): PosSession {
        val oDataRow = browse(id)
        return fromRow(oDataRow, qf)
    }

     override fun fromRow(row: ODataRow, qf: QueryFields): PosSession {
         super.fromRow(row, qf)
         val id : Int? = if(qf.contains(Columns.PosSession.id))row.getInt(Columns.id) else null
         val serverId : Int? = if(qf.contains(Columns.PosSession.server_id)) row.getInt(Columns.PosSession.server_id) else null
         val name : String? =  if(qf.contains(Columns.PosSession.name)) row.getString(Columns.PosSession.name) else null
         val startTime: Date? = if(qf.contains(Columns.PosSession.start_at)) DateUtils.parseFromDB(row.getString(Columns.PosSession.start_at)) else null
         val stopTime: Date? = if(qf.contains(Columns.PosSession.stop_at)) DateUtils.parseFromDB(row.getString(Columns.PosSession.stop_at)) else null
         val state: String? = if(qf.contains(Columns.PosSession.state)) row.getString(Columns.PosSession.state) else null
         val loginNo : Int? = if(qf.contains(Columns.PosSession.login_no)) row.getInt(Columns.PosSession.login_no) else null
         val sequenceNo : Int? = if(qf.contains(Columns.PosSession.sequence_no))  row.getInt(Columns.PosSession.sequence_no) else null
         val rescue : Boolean? = if(qf.contains(Columns.PosSession.rescue)) row.getBoolean(Columns.PosSession.rescue) else null
         val user: User? = if(qf.contains(Columns.PosSession.user_id)) userDao.fromRow(row.getM2ORecord(Columns.PosSession.user_id).browse(), qf.childField(Columns.PosSession.server_id)) else null
         val posConfig: PosConfig? = if(qf.contains(Columns.PosSession.config_id)) posConfigDao.fromDataRow(row.getM2ORecord(Columns.PosSession.config_id).browse(), qf.childField(Columns.PosSession.config_id)) else null
//       val cashJournalId = row.getInt(cash_journal_id.name)
//       val cashRegisterId = row.getBoolean(cash_register_id.name)
         val posSession = PosSession(id, serverId, name, user, posConfig, startTime , stopTime, state, sequenceNo, loginNo, rescue, ArrayList())
         posSession.statements = accountBankStatementDao.posSessionStatements(posSession)
         return posSession
    }

    fun new (user: User) : PosSession {
        val now = DateUtils.now()
        val posSession = PosSession(0, 0, "M/POS/${getDeviceName()}/$now", user, posConfigDao[1,null!!]
                , now,  now, "opening_control", 1, 1, false, ArrayList())
        posSession.id = insert(posSession.toOValues())
        return posSession
    }

    //TODO: Use projection to load lazy
    fun current () : PosSession? {
        val oDataRows = select(null, "state = ? or state = ?", arrayOf(Columns.PosSession.State.opened.name, Columns.PosSession.State.opening_control.name))
        return if (oDataRows.size > 0) fromRow(oDataRows[0], QueryFields.all()) else null
    }

    fun getDeviceName(): String {
        val manufacturer = MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
           StringUtils.capitalizeString(model)
        } else StringUtils.capitalizeString(manufacturer) + " " + model
    }

    fun sessionIsStillOpen(posSession: PosSession) :  Boolean {
        val oDomain = ODomain()
        oDomain.add(Columns.id, "=", posSession.id)
        quickSyncRecords(oDomain)
        val oDataRow = select(arrayOf(Columns.PosSession.state), "id = ?", arrayOf(posSession.id.toString()))
        if(oDataRow != null){
            val state = oDataRow[0].getString(Columns.PosSession.state)
            return state.equals(Columns.PosSession.State.opened) ||
                    state.equals(Columns.PosSession.State.opening_control)
        }
        return false
    }
}
