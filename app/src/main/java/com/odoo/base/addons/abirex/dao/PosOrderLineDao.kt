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
import com.odoo.base.addons.res.ResCompany
import com.odoo.core.orm.ODataRow
import com.odoo.core.orm.OModel
import com.odoo.core.orm.fields.OColumn
import com.odoo.core.orm.fields.types.OFloat
import com.odoo.core.orm.fields.types.OVarchar
import com.odoo.core.support.OUser
import com.odoo.data.DataLoader
import com.odoo.data.LazyList
import com.odoo.core.orm.fields.OColumn.RelationType
import com.odoo.core.utils.OCursorUtils
import com.odoo.data.LazyList.ItemFactory
import com.odoo.data.abirex.Columns
import com.odoo.data.abirex.ModelNames

class PosOrderLineDao(context: Context?, user: OUser?) : OModel(context, ModelNames.POS_ORDER_LINE, user) {

    internal var company_id = OColumn("Company", ResCompany::class.java, RelationType.ManyToOne)
    internal var name = OColumn("Name", OVarchar::class.java).setSize(100).setRequired()
    internal var notice = OColumn("Notice", OVarchar::class.java)
    internal var product_id = OColumn("Product", ProductDao::class.java, RelationType.ManyToOne)
    internal var price_unit = OColumn("Unit Price", OFloat::class.java)
    internal var qty = OColumn("Quantity", OFloat::class.java)
    internal var price_subtotal = OColumn("Subtotal w/o Tax", OFloat::class.java)
    internal var price_subtotal_incl = OColumn("Subtotal with Tax", OFloat::class.java)
    internal var discount = OColumn("Discount", OFloat::class.java)
    internal var order_id = OColumn("Order", PosOrderDao::class.java, RelationType.ManyToOne)
    lateinit var companyDao: ResCompany
    lateinit var productDao: ProductDao
    lateinit var posOrderDao: PosOrderDao

   fun  posOrderLineCreator(posOrder: PosOrder): ItemFactory<PosOrderLine> {
       return object : ItemFactory<PosOrderLine> {
           override fun create(cursor: Cursor, index: Int): PosOrderLine {
               cursor.moveToPosition(index)
               val row = OCursorUtils.toDatarow(cursor)
               val orderLine = fromRow(row, posOrder)
                return orderLine
           }
       }
   }

    override fun initDaos() {
        companyDao = App.getDao(ResCompany::class.java)
        productDao = App.getDao(ProductDao::class.java)
        posOrderDao = App.getDao(PosOrderDao::class.java)
    }

    init {
        setHasMailChatter(true)
    }

    fun fromPosOrder(posOrder: PosOrder): List<PosOrderLine> {
        return select(null, "order_id = ?",  arrayOf("${posOrder.id}")).map { fromRow(it, posOrder) }
    }

    fun searchProduct(name: String): Loader<*>{
        return productDao.searchByName(name)
    }

    fun fromRow(row: ODataRow, order: PosOrder?): PosOrderLine{
        val id = row.getInt(Columns.id)
        val name = row.getString(Columns.name)
        val notice = row.getString(Columns.PosOrderLine.notice)
        val companyId = row.getInt(Columns.PosOrderLine.company_id)
        val company = companyDao.get(companyId)
        val productId = row.getInt(Columns.PosOrderLine.product_id)
        val product = productDao.get(productId)
        val unitPrice = row.getFloat(Columns.PosOrderLine.price_unit)
        val quantity = row.getFloat(Columns.PosOrderLine.qty)
        val subTotal = row.getFloat(Columns.PosOrderLine.price_subtotal)
        val subTotalIncl = row.getFloat(Columns.PosOrderLine.price_subtotal_incl)
        val discount = row.getFloat("price_unit")
        val realPosOrder = if (order?.id == null) { posOrderDao.get(row.getInt(Columns.PosOrderLine.order_id)) } else {  order }
        return PosOrderLine(id, name, company, product, notice, unitPrice, quantity,
                subTotal, subTotalIncl, discount, realPosOrder)
    }

    override fun uri(): Uri {
        return buildURI(AUTHORITY)
    }

    fun selectAll(context: Context, uri: Uri, projection: Array<String>, selection: String,
                                    selectionArgs: Array<String>, sortOrder: String, order: PosOrder): Loader<LazyList<PosOrderLine>> {
        return DataLoader(context, uri, projection, selection, selectionArgs, sortOrder, posOrderLineCreator(order))

    }

    override fun onModelUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    override fun onSyncStarted() {
        Log.e(TAG, "PosOrderLineDao->onSyncStarted")
    }

    override fun onSyncFinished() {
        Log.e(TAG, "PosOrderLineDao->onSyncFinished")
    }

    companion object {
        val TAG = PosOrderLineDao::class.java.simpleName
        @JvmField
        var AUTHORITY = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.pos_order_line"
    }
}
