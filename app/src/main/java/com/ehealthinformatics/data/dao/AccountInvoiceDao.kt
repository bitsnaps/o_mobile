package com.ehealthinformatics.data.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log
import com.ehealthinformatics.App
import com.ehealthinformatics.BuildConfig

import com.ehealthinformatics.data.dto.*
import com.ehealthinformatics.core.utils.DateUtils
import com.ehealthinformatics.core.orm.ODataRow
import com.ehealthinformatics.core.orm.OModel
import com.ehealthinformatics.core.orm.fields.OColumn
import com.ehealthinformatics.core.orm.fields.OColumn.ROW_ID
import com.ehealthinformatics.core.support.OUser

import com.ehealthinformatics.core.orm.fields.OColumn.RelationType
import com.ehealthinformatics.core.orm.fields.types.*
import com.ehealthinformatics.core.rpc.helper.ODomain
import com.ehealthinformatics.data.LazyList
import com.ehealthinformatics.data.db.Columns
import com.ehealthinformatics.data.db.ModelNames
import com.ehealthinformatics.data.dto.Currency
import java.util.*
import kotlin.collections.ArrayList

class AccountInvoiceDao(context: Context?, user: OUser?) : OModel(context, ModelNames.POS_ORDER, user) {

    internal var name = OColumn("Name", OVarchar::class.java).setSize(100).setRequired()
    internal var origin = OColumn("Sequence Number", OVarchar::class.java)
    internal var type = OColumn("Session", OVarchar::class.java)
    internal var reference = OColumn("Reference", OVarchar::class.java)
    internal var number = OColumn("Number", OVarchar::class.java)
    internal var partner_id = OColumn("Partner Id", ResPartner::class.java, RelationType.ManyToOne)
    internal var company_id = OColumn("Company", ResCompany::class.java, RelationType.ManyToOne)
    internal var user_id = OColumn(null, ResUsers::class.java, RelationType.ManyToOne)
    internal var account_id = OColumn("Account", Account::class.java, RelationType.ManyToOne)
    internal var currency_id = OColumn("Currency", ResCurrency::class.java)
    internal var sent = OColumn("Sent", OBoolean::class.java)
    internal var amount_tax = OColumn("Taxes", OFloat::class.java)
    internal var amount_total = OColumn("Total Amount", OFloat::class.java)
    internal var amount_paid = OColumn("Amount Paid", OFloat::class.java)
    internal var amount_return = OColumn("Amount Returned", OFloat::class.java)
    internal var state = OColumn("State", OSelection::class.java)
            .addSelection("draft", "Draft")
            .addSelection("cancel", "RFQ Sent")
            .addSelection("paid", "To Approve")
            .addSelection("done", "PurchaseList Order")
            .addSelection("invoiced", "Locked")
            .addSelection("cancel", "Cancelled")
    internal var date_invoice = OColumn("Invoice Date", ODate::class.java)
    internal var date_due = OColumn("Date Bue", ODate::class.java)
//    internal var account_move = OColumn("Journal Entry", OInteger::class.java)//Journal
//    internal var picking_id = OColumn("Picking", OInteger::class.java)//Picking ID
//    internal var picking_type_id = OColumn("Operation Type", OInteger::class.java)//Picking ID
//    internal var location_id = OColumn("Location", OInteger::class.java)//Location
    internal var lines = OColumn("Order Lines", AccountInvoiceLine::class.java, RelationType.ManyToMany)
            .setRelatedColumn("invoice_id")

    lateinit var companyDao : ResCompany
    lateinit var partnerDao : ResPartner
    lateinit var accountDao : AccountDao
    lateinit var currencyDao : ResCurrency
    lateinit var userDao : ResUsers

    private var SEQUENCE_NO = 1;

    override fun initDaos() {
        companyDao = App.getDao(ResCompany::class.java)
        partnerDao = App.getDao(ResPartner::class.java)
        accountDao = App.getDao(AccountDao::class.java)
        currencyDao = App.getDao(ResCurrency::class.java)
        userDao = App.getDao(ResUsers::class.java)
    }


    override fun fromRow(row: ODataRow, qf: QueryFields): AccountInvoice {
        populateRelatedColumns(relationColumns.map{it.name}.toTypedArray(), row)
        val id : Int? = if(qf.contains(Columns.AccountInvoice.id))row.getInt(Columns.id) else null
        val serverId : Int? = if(qf.contains(Columns.AccountInvoice.server_id)) row.getInt(Columns.PosSession.server_id) else null
        val name : String? =  if(qf.contains(Columns.AccountInvoice.name)) row.getString(Columns.PosSession.name) else null
        val origin =  if(qf.contains(Columns.AccountInvoice.origin)) row.getString(Columns.AccountInvoice.origin) else null
        val type = if(qf.contains(Columns.AccountInvoice.type)) row.getString(Columns.AccountInvoice.type) else null
        val reference = if(qf.contains(Columns.AccountInvoice.origin)) row.getString(Columns.AccountInvoice.reference) else null
        val number = if(qf.contains(Columns.AccountInvoice.number)) row.getString(Columns.AccountInvoice.number) else null
        val partner =  if(qf.contains(Columns.AccountInvoice.partner_id)) Customer(partnerDao.fromRow(row.getM2ORecord(Columns.AccountInvoice.partner_id ).browse(), qf.childField(Columns.AccountInvoice.partner_id ))) else null
        val company = if(qf.contains(Columns.AccountInvoice.company_id)) companyDao.fromRow(row.getM2ORecord(Columns.AccountInvoice.company_id).browse(), qf.childField(Columns.AccountInvoice.company_id)) else null
        val account = if(qf.contains(Columns.AccountInvoice.account_id)) accountDao.fromRow(row.getM2ORecord(Columns.AccountInvoice.account_id).browse(), qf.childField(Columns.AccountInvoice.account_id)) else null
        val user = if(qf.contains(Columns.AccountInvoice.user_id)) userDao.fromRow(row.getM2ORecord(Columns.AccountInvoice.user_id).browse(), qf.childField(Columns.AccountInvoice.user_id) ) else null
        val currency =  if(qf.contains(Columns.AccountInvoice.currency_id)) currencyDao.fromRow(row.getM2ORecord(Columns.AccountInvoice.currency_id).browse(), qf.childField(Columns.AccountInvoice.currency_id)) else null
        val sent = if(qf.contains(Columns.AccountInvoice.state)) row.getBoolean(Columns.AccountInvoice.state) else null
        val invoiceDate = if(qf.contains(Columns.AccountInvoice.invoice_date))  DateUtils.parseFromDB(row.getString(Columns.AccountInvoice.invoice_date)) else null
        val dueDate = if(qf.contains(Columns.AccountInvoice.due_date)) DateUtils.parseFromDB(row.getString(Columns.AccountInvoice.due_date)) else null
        val state = if(qf.contains(Columns.AccountInvoice.state)) row.getString(Columns.AccountInvoice.state) else null
        val amountTaxed = if(qf.contains(Columns.AccountInvoice.amount_tax)) row.getFloat(Columns.AccountInvoice.amount_tax) else null
        val amountTotal = if(qf.contains(Columns.AccountInvoice.amount_total)) row.getFloat(Columns.AccountInvoice.amount_total) else null
        val amountPaid = if(qf.contains(Columns.AccountInvoice.amount_paid)) row.getFloat(Columns.AccountInvoice.amount_paid) else null
        val amountReturn = if(qf.contains(Columns.AccountInvoice.amount_return)) row.getFloat(Columns.AccountInvoice.amount_return) else null
        val accountInvoice = AccountInvoice(id, serverId, name, origin, type, reference , number, partner, company, user, account,
                currency, sent, amountTaxed, amountTotal, amountPaid, amountReturn, state, invoiceDate, dueDate)

        return accountInvoice
    }

    init {
        setHasMailChatter(true)
    }

    fun selectAll(queryFields: QueryFields): List<AccountInvoice>{
        val oDataRows = select(null, null, null, "id DESC")
        val accountInvoices = ArrayList<AccountInvoice>()
        for(oDataRow in oDataRows){
            accountInvoices.add(fromRow(oDataRow, queryFields))
        }
        return accountInvoices
    }

    fun selectAllLazyily(from : Date, to: Date, queryFields: QueryFields): LazyList<AccountInvoice>{
        val oDataRows = select(arrayOf(Columns.id), null, null, "id DESC")
        return LazyList(getAccountInvoiceCreator(queryFields), oDataRows);
    }


    private fun getAccountInvoiceCreator(queryFields: QueryFields): LazyList.ItemFactory<AccountInvoice> {
        return object : LazyList.ItemFactory<AccountInvoice> {
            override fun create(id: Int): AccountInvoice {
                return get(id,queryFields)
            }
        }
    }


    fun selectByState(state: String, queryFields: QueryFields): List<AccountInvoice>{
        this.state.selectionMap.containsKey(state);
        val oDataRows = select(null, "state = ?", arrayOf(state), "id DESC")
        val accountInvoices = ArrayList<AccountInvoice>();
        for(oDataRow in oDataRows){
            accountInvoices.add(fromRow(oDataRow, queryFields))
        }
        return accountInvoices
    }

    fun searchFilter(filterText: String,queryFields: QueryFields ): List<AccountInvoice>{
        val oDataRows = select(null, "name like ?", arrayOf("$filterText%"), "id DESC")
        val accountInvoices = ArrayList<AccountInvoice>();
        for(oDataRow in oDataRows){
            accountInvoices.add(fromRow(oDataRow,queryFields))
        }
        return accountInvoices
    }

    fun totalPayments(sessionId: Int ): Float{
        val oDataRows = select(arrayOf(Columns.PosOrder.amount_total), Columns.PosOrder.session_id + " = ?", arrayOf("$sessionId%"), null)
        var totalSum = 0F
        for(oDataRow in oDataRows){
            totalSum += oDataRow.getFloat(Columns.PosOrder.amount_total)
        }
        return totalSum
    }

//    fun newOrder() : AccountInvoice {
//        val company = companyDao[companyDao.selectRowId(user.companyId)]
//        val account = accountDao[accountDao.selectRowId(user.)]
//        val priceList = session.config.priceList
//        val currencyRate = company.currency.rate
//        val user = userDao[userDao.selectRowId(user.userId)]
//        val customer = Customer(user.partner)
//        val posOrder = PosOrder("RX1", company, session, user,
//                    customer, priceList, currencyRate,  SEQUENCE_NO++)
//            posOrder.id = insert(posOrder.toOValues())
//        val createdPosOrder = browse(posOrder.id)
//        return fromRow(createdPosOrder)
//    }

//    fun neeSMSOrder(phoneNumber: String, orderString: String) : PosOrder {
//
//
//        val orderLinesString = orderString.split("&")
//
//        val company = companyDao[companyDao.selectRowId(user.companyId)]
//        val session = sessionDao[sessionDao.selectRowId(user.posSessionId)]
//        val priceList = session.config.priceList
//        val currency = company.currency.rate
//        val user = userDao[userDao.selectRowId(user.userId)]
//        val customer = Customer(user.partner)
//        val posOrder = PosOrder(phoneNumber, company, session, user,
//                customer, priceList, currency,  SEQUENCE_NO++)
//        posOrder.id = insert(posOrder.toOValues())
//
//        val posOrderLineDao = App.getDao<PosOrderLineDao>(PosOrderLineDao::class.java)
//        orderLinesString.forEach {
//            var orderLineString = it.split("|")
//            val id = orderLineString[0].toInt()
//            val price = orderLineString[1].toFloat()
//            val qty = orderLineString[2].toFloat()
//            val orderLine = makeOrderLine(id, price, qty, posOrder)
//            orderLine.id = posOrderLineDao.insert(orderLine.toOValues())
//            posOrder.lines.add(orderLine)
//        }
//
//        return posOrder
//    }


    fun makeOrderLine(serverId: Int, price: Float, qty: Float, order: AccountInvoice): AccountInvoiceLine {
        var productDao = App.getDao<ProductDao>(ProductDao::class.java)
        val product = productDao[productDao.selectRowId(serverId), QueryFields.all()]
        val total = product.price * qty
        return AccountInvoiceLine(0, product.name, order.company, product,
                "SMS", product.price, qty, total, total, 0F,
                order)
    }


    override fun get(id: Int, queryFields: QueryFields) : AccountInvoice
    {
        val oDataRow = browse(id)
        return fromRow(oDataRow,queryFields)
    }

        override fun uri(): Uri {
        return buildURI(AUTHORITY)
    }

    override fun onModelUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    override fun onSyncStarted() {
        Log.e(TAG, "PosOrderDao->onSyncStarted")
    }

    override fun onSyncFinished() {
        Log.e(TAG, "PosOrderDao->onSyncFinished")
    }


    override fun allowCreateRecordOnServer(): Boolean {
        return true
    }

    companion object {
         @JvmField
         var AUTHORITY: String = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.pos_order"
         val TAG = AccountInvoiceDao::class.java.simpleName
         //var AUTHORITY = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.pos_order"
    }

}
