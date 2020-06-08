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
import com.ehealthinformatics.core.support.OUser

import com.ehealthinformatics.core.orm.fields.OColumn.RelationType
import com.ehealthinformatics.core.orm.fields.types.*
import com.ehealthinformatics.core.rpc.helper.ODomain
import com.ehealthinformatics.data.LazyList
import com.ehealthinformatics.data.db.Columns
import com.ehealthinformatics.data.db.ModelNames
import kotlin.collections.ArrayList

class PosOrderDao(context: Context?, user: OUser?) : OModel(context, ModelNames.POS_ORDER, user) {

    internal var name = OColumn("Name", OVarchar::class.java).setSize(100).setRequired()
    internal var sequence_number = OColumn("Sequence Number", OInteger::class.java)
    internal var session_id = OColumn("Session", PosSessionDao::class.java, RelationType.ManyToOne)
    internal var partner_id = OColumn("Partner Id", ResPartner::class.java, RelationType.ManyToOne)
    internal var date_order = OColumn("Order Date", ODateTime::class.java)
    internal var currency_rate = OColumn("Currency Rate", OFloat::class.java)
    internal var pricelist_id = OColumn("Price List", PriceListDao::class.java, RelationType.ManyToOne)
    internal var state = OColumn("Vendor Reference", OSelection::class.java)
            .addSelection("draft", "New")
            .addSelection("cancel", "Cancelled")
            .addSelection("paid", "Paid")
            .addSelection("done", "Posted")
            .addSelection("invoiced", "Invoiced")
    internal var company_id = OColumn("Company", ResCompany::class.java, RelationType.ManyToOne)
    internal var user_id = OColumn(null, ResUsers::class.java, RelationType.ManyToOne)

    internal var amount_tax = OColumn("Taxes", OFloat::class.java)
    internal var amount_total = OColumn("Total Amount", OFloat::class.java)
    internal var amount_paid = OColumn("Amount Paid", OFloat::class.java)
    internal var amount_return = OColumn("Amount Returned", OFloat::class.java)

    internal var invoice_group = OColumn("Invoice Group", OBoolean::class.java)
    internal var invoice_id = OColumn("Account", OInteger::class.java)//Account
    internal var account_move = OColumn("Journal Entry", OInteger::class.java)//Journal
    internal var picking_id = OColumn("Picking", OInteger::class.java)//Picking ID
    internal var picking_type_id = OColumn("Operation Type", OInteger::class.java)//Picking ID
    internal var location_id = OColumn("Location", OInteger::class.java)//Location
    internal var lines = OColumn("Order Lines", PosOrderLineDao::class.java, RelationType.OneToMany)
            .setRelatedColumn("order_id")

    lateinit var companyDao : ResCompany
    lateinit var partnerDao : ResPartner
    lateinit var sessionDao : PosSessionDao
    lateinit var currencyDao : ResCurrency
    lateinit var userDao : ResUsers
    lateinit var priceListDao: PriceListDao

    private var SEQUENCE_NO = 1;

    override fun defaultDomain(): ODomain {
        val oDomain = super.defaultDomain()
//        oDomain.add("date_order", ">=", "2019-10-25 12:43:26")
        return oDomain
    }

    override fun initDaos() {
        companyDao = App.getDao(ResCompany::class.java)
        partnerDao = App.getDao(ResPartner::class.java)
        sessionDao = App.getDao(PosSessionDao::class.java)
        currencyDao = App.getDao(ResCurrency::class.java)
        userDao = App.getDao(ResUsers::class.java)
        priceListDao = App.getDao(PriceListDao::class.java)
    }


    override fun fromRow(row: ODataRow, qf: QueryFields): PosOrder {
        populateRelatedColumns(relationColumns.map{it.name}.toTypedArray(), row)
        val id : Int? = if(qf.contains(Columns.PosOrder.id))row.getInt(Columns.id) else null
        val serverId : Int? = if(qf.contains(Columns.PosOrder.server_id)) row.getInt(Columns.PosSession.server_id) else null
        val name : String? =  if(qf.contains(Columns.PosOrder.name)) row.getString(Columns.PosSession.name) else null
        val seqNo : Int?  = if(qf.contains(Columns.PosOrder.sequence_no)) row.getInt(Columns.PosOrder.sequence_no) else null
        val amountTaxed : Float? = if(qf.contains(Columns.PosOrder.amount_tax)) row.getFloat(Columns.PosOrder.amount_tax) else null
        val amountTotal : Float? = if(qf.contains(Columns.PosOrder.amount_total)) row.getFloat(Columns.PosOrder.amount_total) else null
        val amountPaid : Float? = if(qf.contains(Columns.PosOrder.amount_paid))  row.getFloat(Columns.PosOrder.amount_paid) else null
        val amountReturn : Float? = if(qf.contains(Columns.PosOrder.amount_return))  row.getFloat(Columns.PosOrder.amount_return) else null
        val session = if(qf.contains(Columns.PosOrder.session_id)) sessionDao.fromRow(row.getM2ORecord(Columns.PosOrder.session_id).browse(), qf.childField(Columns.PosOrder.session_id)) else null
        val partner =  if(qf.contains(Columns.PosOrder.partner_id)) Customer(partnerDao.fromRow(row.getM2ORecord(Columns.PosOrder.partner_id ).browse(), qf.childField(Columns.PosOrder.partner_id))) else null
        val priceList =  if(qf.contains(Columns.PosOrder.pricelist_id)) priceListDao.fromRow(row.getM2ORecord(Columns.PosOrder.pricelist_id).browse(), qf.childField(Columns.PosOrder.session_id)) else null
        val company = if(qf.contains(Columns.PosOrder.company_id)) companyDao.fromRow(row.getM2ORecord(Columns.PosOrder.company_id).browse(), qf.childField(Columns.PosOrder.company_id)) else null
        val orderDate = if(qf.contains(Columns.PosOrder.order_date)) DateUtils.parseFromDB(row.getString(Columns.PosOrder.order_date)) else null
        val currencyRate = if(qf.contains(Columns.PosOrder.currency_rate)) row.getFloat(Columns.PosOrder.currency_rate) else null
        val state = if(qf.contains(Columns.PosOrder.state)) row.getString(Columns.PosOrder.state) else null
        val user = if(qf.contains(Columns.PosOrder.user_id)) userDao.fromRow(row.getM2ORecord(Columns.PosOrder.user_id).browse(), qf.childField(Columns.PosOrder.user_id)) else null
        val posOrder = PosOrder(id, serverId, name, company, session, user, partner, priceList, currencyRate, amountTaxed, amountTotal, amountPaid,
                amountReturn, seqNo, state, orderDate)
        return posOrder
    }

    init {
        setHasMailChatter(true)
    }

    fun selectAll(qt: QueryFields): List<PosOrder>{
        val oDataRows = select(null, null, null, "id DESC")
        val posOrders = ArrayList<PosOrder>();
        for(oDataRow in oDataRows){
            posOrders.add(fromRow(oDataRow, qt))
        }
        return posOrders
    }

    fun selectAllLazyily(qt: QueryFields): LazyList<PosOrder>{
        val oDataRows = select(arrayOf(Columns.id), null, null, "id DESC")
        return LazyList(getProductCreator(qt), oDataRows);
    }


    private fun getProductCreator(qt: QueryFields): LazyList.ItemFactory<PosOrder> {
        return object : LazyList.ItemFactory<PosOrder> {
            override fun create(id: Int): PosOrder {
                return get(id, qt)
            }
        }
    }

    fun selectByState(state: String, qt: QueryFields ): List<PosOrder>{
        this.state.selectionMap.containsKey(state);
        val oDataRows = select(null, "state = ?", arrayOf(state), "id DESC")
        val posOrders = ArrayList<PosOrder>()
        for(oDataRow in oDataRows){
            posOrders.add(fromRow(oDataRow, qt))
        }
        return posOrders
    }

    fun searchFilter(filterText: String , qt: QueryFields): List<PosOrder>{
        val oDataRows = select(null, "name like ?", arrayOf("$filterText%"), "id DESC")
        val posOrders = ArrayList<PosOrder>();
        for(oDataRow in oDataRows){
            posOrders.add(fromRow(oDataRow, qt))
        }
        return posOrders
    }

    fun totalPayments(sessionId: Int ): Float{
        val oDataRows = select(arrayOf(Columns.PosOrder.amount_total), Columns.PosOrder.session_id + " = ?", arrayOf("$sessionId%"), null)
        var totalSum = 0F
        for(oDataRow in oDataRows){
            totalSum += oDataRow.getFloat(Columns.PosOrder.amount_total)
        }
        return totalSum
    }

    fun newOrder() : PosOrder {
        val company = companyDao[companyDao.selectRowId(user.companyId), QueryFields.all()]
        val session = sessionDao[sessionDao.selectRowId(user.posSessionId), QueryFields.all()]
        val priceList = session.config?.priceList
        val currencyRate = company.currency?.rate!!
        val user = userDao[userDao.selectRowId(user.userId), QueryFields.all()]
        val customer = Customer(user.partner)
        val posOrder = PosOrder("RX1", company, session, user,
                    customer, priceList, currencyRate,  SEQUENCE_NO++)
            posOrder.id = insert(posOrder.toOValues())
        val createdPosOrder = browse(posOrder.id!!)
        return fromRow(createdPosOrder, QueryFields.all())
    }

    fun neeSMSOrder(phoneNumber: String, orderString: String, qt: QueryFields) : PosOrder {


        val orderLinesString = orderString.split("&")

        val company = companyDao[companyDao.selectRowId(user.companyId), QueryFields.id()]
        val session = sessionDao[sessionDao.selectRowId(user.posSessionId), QueryFields.id()]
        val priceList = session.config?.priceList
        val currency = company.currency?.rate!!
        val user = userDao[userDao.selectRowId(user.userId), qt.childField(Columns.PosOrder.user_id)]
        val customer = Customer(user.partner)
        val posOrder = PosOrder(phoneNumber, company, session, user,
                customer, priceList, currency,  SEQUENCE_NO++)
        posOrder.id = insert(posOrder.toOValues())

        val posOrderLineDao = App.getDao<PosOrderLineDao>(PosOrderLineDao::class.java)
        orderLinesString.forEach {
            var orderLineString = it.split("|")
            val id = orderLineString[0].toInt()
            val price = orderLineString[1].toFloat()
            val qty = orderLineString[2].toFloat()
            val orderLine = makeOrderLine(id, price, qty, posOrder)
            orderLine.id = posOrderLineDao.insert(orderLine.toOValues())
            posOrder.lines.add(orderLine)
        }

        return posOrder
    }


    fun makeOrderLine(serverId: Int, price: Float, qty: Float, order: PosOrder): PosOrderLine {
        var productDao = App.getDao<ProductDao>(ProductDao::class.java)
        val product = productDao[productDao.selectRowId(serverId), QueryFields.all()]
        val total = product.price * qty
        return PosOrderLine(0, product.name, order.company, product,
                "SMS", product.price, qty, total, total, 0F,
                order)
    }


    override fun get(id: Int, qt: QueryFields) : PosOrder
    {
        val oDataRow = browse(id)
        return fromRow(oDataRow, qt)
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

    override fun allowDeleteRecordInLocal(): Boolean {
        return false
    }

    companion object {
         @JvmField
         var AUTHORITY: String = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.pos_order"
         val TAG = PosOrderDao::class.java.simpleName
         //var AUTHORITY = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.pos_order"
    }

}
