
package com.ehealthinformatics.app.activity.product

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.ehealthinformatics.App

import com.ehealthinformatics.R
import com.ehealthinformatics.data.dto.*
import com.ehealthinformatics.core.support.OdooCompatActivity
import com.ehealthinformatics.data.dao.ProductTemplateDao
import com.ehealthinformatics.data.db.Columns
import com.ehealthinformatics.data.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.activity_product_edit.*
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.ehealthinformatics.core.utils.IntentUtils
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.layout_product_basic.*
import kotlinx.android.synthetic.main.layout_product_basic.sc_product_active
import kotlinx.android.synthetic.main.layout_product_stock.*
import com.ehealthinformatics.app.utils.Tools
import com.ehealthinformatics.app.utils.ViewAnimation




class ProductEdit : OdooCompatActivity() {

    private lateinit var product : Product
    private lateinit var model : ProductViewModel
    private lateinit var mMenu: Menu

    private var productId = 0
    private var editMode = true

    private lateinit var productTemplateDao: ProductTemplateDao

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productTemplateDao = App.getDao(ProductTemplateDao::class.java)
        setContentView(R.layout.activity_product_edit)
        productId =  intent.extras.getInt(Columns.id)
        editMode = intent.extras.getBoolean(IntentUtils.IntentParams.EDIT_MODE)
        initDataLoad()
    }

    private  fun initToolbar() {
        setSupportActionBar(tb_product)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = if(productId > 0) product.name else "New product"
    }

    private fun initDataLoad() {
        model = ViewModelProviders.of(this).get(ProductViewModel::class.java)
        model.get(productId)
        model.selected.observe(this, Observer<Product> { product ->
            this@ProductEdit.product = product
            initToolbar()
            initComponent()
            toUI()
        })
    }

    private fun initComponent() {
        val vp_products_ = findViewById<ViewPager>(R.id.vp_products)
        setupViewPager(vp_products_)
        val tab_layout = findViewById<TabLayout>(R.id.tl_product)
        tab_layout.setupWithViewPager(vp_products_)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = PlaceholderFragment.SectionsPagerAdapter(supportFragmentManager)
        val productFragment = PlaceholderFragment.newInstance(1)
        adapter.addFragment(productFragment, "PRODUCT")
        adapter.addFragment(PlaceholderFragment.newInstance(2), "STOCK")
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object: OnPageChangeListener {
            override fun  onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun  onPageSelected(position: Int) { initFragmentListeners(position) }
        })
    }

    private fun toUI() {
        actv_product_name.setText(product.productTemplate.name)
        actv_product_price.setText("%.2f".format(product.price))
        actv_product_category.setText(product.productTemplate.category!!.name)
        actv_product_code.setText(product.code)
        actv_product_cost_price.setText("%.2f".format(product.cost))
        actv_product_uom.setText(product.productTemplate.uom!!.name)
        sc_product_active.isChecked = product.productTemplate.active!!
        actv_product_description.setText(product.productTemplate.description)
        et_product_qty.setText("%.2f".format(product.qtyAvailable))
        sc_product_is_medicine.isChecked = product.productTemplate.isMedicine!!
    }

    private fun onNameChange(name: String) {
        product.productTemplate.name = name
    }

    private fun onPriceChange(price: Float) {
        product.price = price
    }

    private fun onCategoryClick() {
    }

    private fun onCodeChange(code: String) {
        product.code = code
    }

    private fun onCostChange(qty: Float) {
        product.cost = qty
    }

    private fun onUOMClick() {
        product.productTemplate.uom!!.name = ""
    }

    private fun onActiveChange(active: Boolean) {
        product.productTemplate.active = active
    }

    private fun onDescriptionChange(desription: String) {
        product.productTemplate.description = desription
    }

    private fun onQtyChange(qty: Float) {
        product.qtyAvailable = qty
    }

    private fun onMedicineChange(isChecked: Boolean) {
        product.productTemplate.isMedicine = isChecked
    }

    fun initFragmentListeners (position: Int) {
        if (position == 0) {
            actv_product_name.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    onNameChange((v as AutoCompleteTextView).text.toString())
            }
            actv_product_price.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    onPriceChange(floatFromUI(v))
            }
            actv_product_uom.setOnClickListener { onUOMClick() }
            actv_product_code.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus)
                    onCodeChange((v as AutoCompleteTextView).text.toString())
            }
            actv_product_cost_price.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) onCostChange(floatFromUI(v))
            }
            actv_product_uom.setOnClickListener { onUOMClick() }

            sc_product_active.setOnCheckedChangeListener { buttonView, isChecked ->
                onActiveChange(isChecked)
            }

            bt_toggle_info.setOnClickListener {  toggleSectionInfo(it); }

            bt_hide_info.setOnClickListener {  toggleSectionInfo(bt_toggle_info);  };
            btn_product_submit.setOnClickListener {

            }
        } else if (position == 1) {
            actv_product_description.setOnFocusChangeListener { v, hasFocus ->
                if(!hasFocus)
                    onDescriptionChange((v as AutoCompleteTextView).text.toString())
            }
            et_product_qty.setOnFocusChangeListener { v, hasFocus ->
                if(!hasFocus)
                    onQtyChange(floatFromUI(v))
            }
            sc_product_is_medicine.setOnCheckedChangeListener { buttonView, isChecked ->
                onMedicineChange(isChecked)
            }
        }


    }

    private fun toggleSectionInfo(view: View) {
        val show = toggleArrow(view)
        if (show) {
            ViewAnimation.expand(lyt_expand_info) { Tools.nestedScrollTo(nested_scroll_view, lyt_expand_info) }
        } else {
            ViewAnimation.collapse(lyt_expand_info)
        }
    }



    fun toggleArrow(view: View ): Boolean {
        if (view.rotation == 0F) {
            view.animate().setDuration(200).rotation(180F);
            return true
        } else {
            view.animate().setDuration(200).rotation(0F);
            return false
        }
    }

    fun floatFromUI(view: View): Float {
        if (view is AutoCompleteTextView) {
            return (view).text.toString().toFloat()
        } else if (view is EditText) {
            return (view).text.toString().toFloat()
        }
        return 0F

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mMenu = menu
        menuInflater.inflate(R.menu.menu_product_detail, menu)
        return true
    }

}