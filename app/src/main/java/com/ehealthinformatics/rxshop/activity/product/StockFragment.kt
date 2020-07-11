package com.ehealthinformatics.rxshop.activity.product

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ehealthinformatics.odoorx.rxshop.R
import com.ehealthinformatics.odoorx.core.base.orm.OValues
import com.ehealthinformatics.odoorx.core.data.db.Columns
import com.ehealthinformatics.odoorx.core.data.dto.Product
import com.ehealthinformatics.odoorx.core.data.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.layout_product_stock.*

class StockFragment(val product: Product, val ovProduct: OValues,val ovProductTemplate: OValues, val model: ProductViewModel) : Fragment() {
    lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.layout_product_stock, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(rootView: View) {
        toUI()
        listeners()
    }

    private fun toUI() {
        et_product_qty.setText("%.2f".format(product.qtyAvailable))
    }

    private fun listeners() {
        et_product_qty.addTextChangedListener(onQtyChange());
        sc_product_activate_stock.setOnCheckedChangeListener { _, b ->
                et_product_qty.isFocusableInTouchMode = b
                et_product_qty.isFocusable = b
        }
    }

    private fun onQtyChange() : TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                try {
                    ovProduct.put(Columns.ProductCol.qty_available, s.toString().toFloat())
                } catch (nfe: NumberFormatException) {
                    ovProduct.put(Columns.ProductCol.qty_available, 0.00)
                }
            }
        }
    }
}
