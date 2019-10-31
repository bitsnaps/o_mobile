package com.odoo.base.addons.abirex.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.support.v4.content.Loader
import android.util.Log
import com.odoo.App
import com.odoo.BuildConfig

import com.odoo.base.addons.abirex.dto.*
import com.odoo.base.addons.abirex.util.DateUtils
import com.odoo.base.addons.res.ResCompany
import com.odoo.base.addons.res.ResCurrency
import com.odoo.base.addons.res.ResPartner
import com.odoo.base.addons.res.ResUsers
import com.odoo.core.orm.ODataRow
import com.odoo.core.orm.OModel
import com.odoo.core.orm.fields.OColumn
import com.odoo.core.orm.fields.OColumn.ROW_ID
import com.odoo.core.support.OUser
import com.odoo.data.DataLoader
import com.odoo.data.LazyList

import com.odoo.core.orm.fields.OColumn.RelationType
import com.odoo.core.orm.fields.types.*
import com.odoo.core.rpc.helper.ODomain
import com.odoo.core.utils.OCursorUtils
import com.odoo.data.abirex.Columns
import com.odoo.data.abirex.ModelNames
import java.util.*

class PosOrderDao(context: Context?, user: OUser?) : OModel(context, ModelNames.POS_ORDER, user) {

    internal var name = OColumn("Name", OVarchar::class.java).setSize(100).setRequired()
    internal var sequence_number = OColumn("Sequence Number", OInteger::class.java)
    internal var session_id = OColumn("Session", PosSessionDao::class.java, RelationType.ManyToOne)
    internal var partner_id = OColumn("Partner Id", ResPartner::class.java, RelationType.ManyToOne)
    internal var date_order = OColumn("Order Date", ODateTime::class.java)
    internal var currency_rate = OColumn("Currency Rate", OFloat::class.java)
    internal var pricelist_id = OColumn("Price List", PriceListDao::class.java, RelationType.ManyToOne)
    internal var state = OColumn("Vendor Reference", OSelection::class.java)
            .addSelection("draft", "Draft")
            .addSelection("cancel", "RFQ Sent")
            .addSelection("paid", "To Approve")
            .addSelection("done", "PurchaseList Order")
            .addSelection("invoiced", "Locked")
            .addSelection("cancel", "Cancelled")
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
        oDomain.add("date_order", ">=", "2019-08-10 12:43:26")
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

    fun  posOrderCreator(): LazyList.ItemFactory<PosOrder> {
        return object : LazyList.ItemFactory<PosOrder> {
            override fun create(cursor: Cursor, index: Int): PosOrder {
                cursor.moveToPosition(index);
                var row = OCursorUtils.toDatarow(cursor)
                return fromRow(row)
            }
        }
    }

    override fun fromRow(row: ODataRow): PosOrder {
        populateRelatedColumns(relationColumns.map{it.name}.toTypedArray(), row)
        val id = row.getInt(ROW_ID)
        val serverId = row.getInt(Columns.server_id)
        val seqNo = row.getInt(sequence_number.name)
        val name = row.getString(name.name)
        val session = sessionDao.fromRow(row.getM2ORecord(session_id.name).browse())
        val partner =  partnerDao.fromRow(row.getM2ORecord(partner_id.name ).browse())
        val priceList = priceListDao.fromRow(row.getM2ORecord(pricelist_id.name).browse())
        val company = companyDao.fromRow(row.getM2ORecord(company_id.name ).browse())
        val orderDate = DateUtils.parseFromDB(row.getString(date_order.name))
        val currencyRate = row.getFloat(currency_rate.name)
        val state = row.getString(state.name)
        val user = userDao.fromRow(row.getM2ORecord(user_id.name).browse())
        val amountTaxed = row.getFloat(amount_tax.name)
        val amountTotal = row.getFloat(amount_total.name)
        val amountPaid = row.getFloat(amount_paid.name)
        val amountReturn = row.getFloat(amount_return.name)
        val posOrder = PosOrder(id, serverId, name, company, session, user, partner, priceList, currencyRate, amountTaxed, amountTotal, amountPaid,
                amountReturn, seqNo, state, orderDate)
        return posOrder
    }

    init {
        setHasMailChatter(true)
    }

    fun selectAll(from : Date, to: Date): Loader<*>{
        return DataLoader<LazyList<PosOrder>>(context, uri(), null, null, null, "id ASC LIMIT 10", posOrderCreator())
    }

    fun newOrder() : PosOrder {
        val company = companyDao[companyDao.selectRowId(user.companyId)]
        val session = sessionDao[sessionDao.selectRowId(user.posSessionId)]
        val priceList = priceListDao[priceListDao.selectRowId(user.priceListId)]
        val currency = currencyDao[currencyDao.selectRowId(user.currencyId)].rate
        val user = userDao[userDao.selectRowId(user.userId)]
        val customer = Customer(user.partner)
        val posOrder = PosOrder("Abirex Phone", company, session, user,
                    customer.partner, priceList, currency,  SEQUENCE_NO++)
            posOrder.id = insert(posOrder.toOValues())
        val createdPosOrder = browse(posOrder.id)
        return fromRow(createdPosOrder)
    }

    fun neeSMSOrder(phoneNumber: String, orderString: String) : PosOrder {


        val orderLinesString = orderString.split("&")

        val company = companyDao[companyDao.selectRowId(user.companyId)]
        val session = sessionDao[sessionDao.selectRowId(user.posSessionId)]
        val priceList = priceListDao[priceListDao.selectRowId(user.priceListId)]
        val currency = currencyDao[currencyDao.selectRowId(user.currencyId)].rate
        val user = userDao[userDao.selectRowId(user.userId)]
        val customer = Customer(user.partner)
        val posOrder = PosOrder(phoneNumber, company, session, user,
                customer.partner, priceList, currency,  SEQUENCE_NO++)
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
        val product = productDao[productDao.selectRowId(serverId)]
        val total = product.price * qty
        return PosOrderLine(0, product.name, order.company, product,
                "SMS", product.price, qty, total, total, 0F,
                order)
    }


    override fun get(id: Int) : PosOrder
    {
        val oDataRow = browse(id)
        return fromRow(oDataRow)
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
         val TAG = PosOrderDao::class.java.simpleName
         //var AUTHORITY = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.pos_order"
    }

}
