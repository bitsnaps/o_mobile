package com.odoo.base.addons.abirex.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.support.v4.content.Loader
import android.util.Log
import com.odoo.BuildConfig

import com.odoo.base.addons.abirex.Utils
import com.odoo.base.addons.abirex.model.*
import com.odoo.base.addons.res.ResCompany
import com.odoo.base.addons.res.ResCurrency
import com.odoo.base.addons.res.ResPartner
import com.odoo.core.orm.ODataRow
import com.odoo.core.orm.OModel
import com.odoo.core.orm.fields.OColumn
import com.odoo.core.orm.fields.OColumn.ROW_ID
import com.odoo.core.support.OUser
import com.odoo.data.DataLoader
import com.odoo.data.LazyList

import com.odoo.core.orm.fields.OColumn.RelationType
import com.odoo.core.orm.fields.types.*
import com.odoo.core.utils.OCursorUtils

class PosOrderDao(context: Context, user: OUser?) : OModel(context, "pos.order", user) {

    internal var name = OColumn("Name", OVarchar::class.java).setSize(100).setRequired()
    internal var sequence_number = OColumn("Sequence Number", OInteger::class.java)
    internal var session_id = OColumn("Session", PosSessionDao::class.java, RelationType.ManyToOne)
    internal var partner_id = OColumn("Partner Id", ResPartner::class.java, RelationType.ManyToOne)
    internal var date_order = OColumn("Order Date", ODateTime::class.java)
    internal var currency_id = OColumn("Currency", ResCurrency::class.java, RelationType.ManyToOne)
    internal var pricelist_id = OColumn("Price List", PriceListDao::class.java, RelationType.ManyToOne)
    internal var state = OColumn("Vendor Reference", OSelection::class.java)
            .addSelection("draft", "RFQ")
            .addSelection("sent", "RFQ Sent")
            .addSelection("to approve", "To Approve")
            .addSelection("purchase", "PurchaseList Order")
            .addSelection("done", "Locked")
            .addSelection("cancel", "Cancelled")
    internal var company_id = OColumn("Company", ResCompany::class.java, RelationType.ManyToOne)
    internal var user_id = OColumn(null, UserDao::class.java, RelationType.ManyToOne)

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

    //TODO: Move all Daos into a container than can be loaded when app starts
    var companyDao = CompanyDao(context, user)
    var partnerDao = ResPartner(context, user)
    var sessionDao = PosSessionDao(context, user)
    var currencyDao = CurrencyDao(context, user)
    var userDao = UserDao(context, user)
    var priceListDao = PriceListDao(context, user)
    var posOrderLineDao = PosOrderLineDao(context, user)

    fun  posOrderCreator(): LazyList.ItemFactory<PosOrder> {
        return object : LazyList.ItemFactory<PosOrder> {
            override fun create(cursor: Cursor, index: Int): PosOrder {
                cursor.moveToPosition(index);
                var row = OCursorUtils.toDatarow(cursor)
                return fromRow(row)
            }
        }
    }

    public fun fromRow(row: ODataRow): PosOrder {
        prepareFields()

        populateRelatedColumns(relationColumns.map{it.name}.toTypedArray(), row)
        val id = row.getInt(ROW_ID)
        val seqNo = row.getInt(sequence_number.name)
        val name = row.getString(name.name)
        val session = sessionDao.get(row.getInt(session_id.name))
        val customer = partnerDao.getCustomer(row.getInt(partner_id.name )) as Customer
        val priceList = priceListDao.fromRow(row.getM2ORecord(pricelist_id.name).browse())
        val company = companyDao.fromRow(row.getM2ORecord(company_id.name ).browse())
        val orderDate = Utils.dateFromString(row.getString(date_order.name))
        val currency = null// currencyDao.fromDataRow(row.getM2ORecord(currency_id.name).browse())
        val state = row.getString(state.name)
        val user = userDao.get(row.getInt(user_id.name))
        val amountTaxed = row.getFloat(amount_tax.name)
        val amountTotal = row.getFloat(amount_total.name)
        val amountPaid = row.getFloat(amount_paid.name)
        val amountReturn = row.getFloat(amount_return.name)
        val posOrder = PosOrder(id, name, session, customer, amountTaxed, amountTotal, amountPaid,
                amountReturn, seqNo, priceList)
        return posOrder
    }

    internal fun getOrderLines(id: Int): List<OrderLine>{
        return posOrderLineDao.fromPosOrder(id)
    }

    fun ci(cursor: Cursor, columnName: String): Int{
        return cursor.getColumnIndex(columnName)
    }

    init {
        setHasMailChatter(true)
    }

    fun selectAll(): Loader<*>{
        return DataLoader<LazyList<Product>>(context, uri(), null, null, null, null, posOrderCreator())
    }

    fun get(id: Int): PosOrder{
        var row = browse(id)
        return fromRow(row)
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

     companion object {
         @JvmField
         var AUTHORITY: String = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.pos_order"
         val TAG = PosOrderDao::class.java.simpleName
         //var AUTHORITY = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.pos_order"
    }
}
