
package com.ehealthinformatics.app.activity.product

import android.Manifest
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import com.ehealthinformatics.R
import com.ehealthinformatics.data.dto.*
import com.ehealthinformatics.core.support.OdooCompatActivity
import com.ehealthinformatics.data.db.Columns.ProductCol.*
import com.ehealthinformatics.data.db.Columns.ProductTemplateCol.*
import com.ehealthinformatics.data.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.activity_product_edit.*
import androidx.viewpager.widget.ViewPager
import com.ehealthinformatics.core.utils.IntentUtils
import android.content.pm.PackageManager
import android.view.MenuItem
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ehealthinformatics.core.orm.OValues
import com.ehealthinformatics.core.utils.StringUtils
import com.ehealthinformatics.data.adapter.SimpleListAdapter
import com.ehealthinformatics.data.db.Columns
import java.util.ArrayList

class ProductEdit : OdooCompatActivity() {

    private lateinit var product : Product
    private lateinit var model : ProductViewModel
    private lateinit var mMenu: Menu
    private var productId = 0
    private var editMode = true
    private var simpleItem = SimpleItem("None", "None")
    private var ovProduct = OValues()
    private var ovProductTemplate = OValues()
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_edit)
        productId =  intent.extras.getInt(IntentUtils.IntentParams.ID)
        editMode = intent.extras.getBoolean(IntentUtils.IntentParams.EDIT_MODE)
        initDataLoad()
        permissions()
    }
    private  fun initToolbar() {
        setSupportActionBar(tb_product)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = if(productId > 0) StringUtils.trimString(product.name, 20) else "New product"
    }
    private fun initDataLoad() {
        model = ViewModelProviders.of(this).get(ProductViewModel::class.java)
        model.loadData(productId)
        model.selected.observe(this, Observer<ProductViewModel.ProductViewClass> { pvc ->
            this@ProductEdit.product = pvc.product
            initToolbar()
            initFragments()
        })
        model.saveStatus.observe(this, Observer<Boolean> { saved ->
            if (saved){
                Toast.makeText(this, "Save Product Success", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "Failed Saving Product", Toast.LENGTH_SHORT).show()
            }
        })
        model.syncStatus.observe(this, Observer<Boolean> { synced ->
            if (synced){
                Toast.makeText(this, "Product Synced successfully...", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "Failed Syncing Product...", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mMenu = menu
        menuInflater.inflate(R.menu.menu_product_detail_2, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem):  Boolean
    {
        when(item.itemId) {
            android.R.id.home -> finish()
            R.id.menu_product_sync -> model.sync()
        }
        return true
    }
    private fun initFragments() {
        setupViewPager(vp_products)
        tl_product.setupWithViewPager(vp_products)
    }
    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = SectionsPagerAdapter(supportFragmentManager)
        adapter.addFragment(ProductFragment(product, ovProduct, ovProductTemplate, model), "PRODUCT")
        adapter.addFragment(StockFragment(product, ovProduct, ovProductTemplate, model), "STOCK")
        adapter.addFragment(WebsiteFragment(product, ovProduct, ovProductTemplate, model), "WEBSITE")
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 3
    }

    class SectionsPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    private fun permissions() {
        if (!checkIfAlreadyhavePermission()) {
            requestForSpecificPermission()
        }
    }

    private fun checkIfAlreadyhavePermission(): Boolean {
        val result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        return result1 == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE), 101)
    }

}