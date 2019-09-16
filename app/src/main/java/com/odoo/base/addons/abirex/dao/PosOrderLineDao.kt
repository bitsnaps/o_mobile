package com.odoo.base.addons.abirex.dao

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.support.v4.content.Loader
import android.util.Log

import com.odoo.BuildConfig
import com.odoo.base.addons.abirex.model.OrderLine
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

class PosOrderLineDao(context: Context?, user: OUser?) : OModel(context, "pos.order.line", user) {

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

    var companyDao: ResCompany = ResCompany(context, user)
    var productDao: ProductDao = ProductDao(context, user)
    //var posOrderDao: PosOrderDao = PosOrderDao(context, user)

   fun  posOrderLineCreator(): ItemFactory<OrderLine> {
       return object : ItemFactory<OrderLine> {
           override fun create(cursor: Cursor, index: Int): OrderLine {
               cursor.moveToPosition(index)
               val row = OCursorUtils.toDatarow(cursor)
               val orderLine = fromRow(row)
                return orderLine
           }
       }
   }

    init {
        setHasMailChatter(true)
    }

    fun fromPosOrder(posOrderId: Int): List<OrderLine> {
        return select(null, "order_id = ?",  arrayOf("$posOrderId")).map { fromRow(it) }
    }

    fun fromRow(row: ODataRow): OrderLine{
        val id = row.getInt("id")
        val name = row.getString("name")
        val notice = row.getString("notice")
        val companyId = row.getInt("company_id")
        val company = companyDao.get(companyId)
        val productId = row.getInt("product_id")
        val product = productDao.get(productId)
        val unitPrice = row.getFloat("price_unit")
        val quantity = row.getFloat("qty")
        val subTotal = row.getFloat("price_subtotal")
        val subTotalIncl = row.getFloat("price_subtotal_incl")
        val discount = row.getFloat("price_unit")
        val orderId = ""//row.getInt("order_id")

        val posOrderLine = OrderLine(id, company, product, name, notice, unitPrice, quantity, subTotal, subTotalIncl, discount, orderId)
        return posOrderLine
    }

    override fun uri(): Uri {

        return buildURI(AUTHORITY)
    }

    fun selectAll(context: Context, uri: Uri, projection: Array<String>, selection: String,
                                    selectionArgs: Array<String>, sortOrder: String): Loader<LazyList<OrderLine>> {
        return DataLoader(context, uri, null, selection, selectionArgs, sortOrder, posOrderLineCreator())

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
