
package com.ehealthinformatics.app.activity.product

import android.os.Bundle
import android.view.Menu
import android.view.View
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
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.ehealthinformatics.app.utils.DialogUtils
import com.ehealthinformatics.core.utils.IntentUtils
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.layout_product_basic.*
import kotlinx.android.synthetic.main.layout_product_basic.sc_product_active
import kotlinx.android.synthetic.main.layout_product_stock.*
import com.ehealthinformatics.app.utils.Tools
import com.ehealthinformatics.app.utils.ViewAnimation
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.*
import com.ehealthinformatics.app.listeners.OnItemClickListener
import com.ehealthinformatics.core.orm.OValues
import com.ehealthinformatics.data.adapter.SimpleListAdapter
import com.ehealthinformatics.data.db.Columns
import java.io.File
import java.util.ArrayList


class ProductEdit : OdooCompatActivity() {

    private lateinit var product : Product
    private lateinit var model : ProductViewModel
    private lateinit var mMenu: Menu

    private var productId = 0
    private var editMode = true
    private var TAKE_PICTURE = 1
    private var BROWSE_IMAGE = 2
    private var TAKE_ANOTHER_PICTURE = 3
    private var BROWSE_MORE_IMAGES = 4
    private val currencySign = "₦"
    private val UPLOAD_TAG = "upload"
    private val CANCEL_TAG = "cancel"

    private var simpleItem = SimpleItem("None", "None")
    private var ovProduct = OValues()
    private var ovProductTemplate = OValues()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_edit)
        productId =  intent.extras.getInt(Columns.id)
        editMode = intent.extras.getBoolean(IntentUtils.IntentParams.EDIT_MODE)
        initDataLoad()
    }

    private  fun initToolbar() {
        setSupportActionBar(tb_product)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = if(productId > 0) trimString(product.name, 20) else "New product"
    }

    private fun trimString(text: String, length: Int): String {
        if (text.length > length) {
            return text.substring(0, length-3).plus("...")
        }
        return text
    }

    private fun initDataLoad() {
        model = ViewModelProviders.of(this).get(ProductViewModel::class.java)
        model.loadData(productId)
        model.selected.observe(this, Observer<ProductViewModel.ProductViewClass> { pvc ->
            this@ProductEdit.product = pvc.product
            initToolbar()
            initComponent()
            toUI()
        })

        model.saveStatus.observe(this, Observer<Boolean> { saved ->
            if (saved){
                Toast.makeText(this, "Save Product Success", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "Failed Saving Product", Toast.LENGTH_SHORT).show()
            }
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
        tv_product_name.text = trimString(product.productTemplate.name!!,20)
        tv_product_price.text = "%.2f".format(product.cost)
        actv_product_name.setText(product.productTemplate.name)
        actv_product_price.setText("%.2f".format(product.price))
        actv_product_category.setText(product.productTemplate.category!!.name)
        actv_product_code.setText(product.code)
        actv_product_cost_price.setText("%.2f".format(product.cost))
        actv_product_uom.setText(strip(product.productTemplate.uom!!.name))
        sc_product_active.isChecked = product.productTemplate.active!!
        actv_product_description.setText(strip(product.productTemplate.description))
        et_product_qty.setText("%.2f".format(product.qtyAvailable))
        sc_product_is_medicine.isChecked = product.productTemplate.isMedicine!!
        if(product.imageSmall != null) {
            iv_product_image.setImageBitmap(product.imageSmall)
            et_product_image_text.visibility = View.GONE
            iv_upload_or_cancel.setImageResource(R.drawable.ic_close_grey)
            iv_upload_or_cancel.tag = CANCEL_TAG
        } else {
            et_product_image_text.visibility = View.VISIBLE
            iv_upload_or_cancel.setImageResource(R.drawable.ic_image_black_24dp)
            iv_upload_or_cancel.tag = UPLOAD_TAG
        }
    }

    private fun strip(string: String?): String {
        return if(string == "false") return "" else string!!
    }


    private fun toSimpleItems (categories: List<Category>): List<SimpleItem> {
        val simpleItems = ArrayList<SimpleItem>()
        var index = categories.size
        while (index-- > 0){
            val category = categories.get(index)
            simpleItems.add(SimpleItem(category.id.toString(), category.name))
        }
        return simpleItems;
    }



    private fun toSimpleItems2 (uoms: List<Uom>): List<SimpleItem> {
        val simpleItems = ArrayList<SimpleItem>()
        var index = uoms.size
        while (index-- > 0){
            val uom = uoms.get(index)
            simpleItems.add(SimpleItem(uom.id.toString(), uom.name))
        }
        return simpleItems
    }

    private fun onProductColorClick(){
        DialogUtils.showColorsDialog(this, v_product_color, ll_summary_in_image, et_product_image_text)
    }
    private fun onNameChange() {
        ovProduct.put(Columns.name, actv_product_name )
        ovProductTemplate.put(Columns.name, actv_product_name.text.toString())
    }
    private fun onPriceChange() : TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable) {
                actv_product_price.removeTextChangedListener(this)
                val productPrice = s.removePrefix(currencySign).toString()
                        .replace(",", "")
                        .replace(".", "")
                        .toFloat()
                "₦ %.2f".format(productPrice)
                actv_product_price.addTextChangedListener(this)
                ovProduct.put(lst_price, productPrice)
            }
        }
    }
    private fun onCategoryClick() {
        val simpleListAdapter = SimpleListAdapter(this, toSimpleItems(model.selected.value!!.categories), OnItemClickListener<SimpleItem> { v, item, pos ->
            simpleItem = item
            actv_product_category.setText(simpleItem.label)
            ovProductTemplate.put(category_id, simpleItem.value)
        })
        DialogUtils.showChooseItemDialog(this, simpleListAdapter)
    }
    private fun onCodeChange() {
        ovProduct.put(Columns.ProductCol.code, actv_product_code.text.toString())
    }
    private fun onCostChange() {
        ovProduct.put(cost_price, actv_product_cost_price.text.toString().toFloat())
    }
    private fun onUOMClick() {
        val simpleListAdapter = SimpleListAdapter(this, toSimpleItems2(model.selected.value!!.uoms), OnItemClickListener<SimpleItem> { v, item, pos ->
            simpleItem = item
            actv_product_uom.setText(simpleItem.label)
            ovProductTemplate.put(Columns.ProductTemplateCol.uom_id, simpleItem.value.toInt())
        })
        DialogUtils.showChooseItemDialog(this, simpleListAdapter)
    }
    private fun onActiveChange(isActive: Boolean) =
            ovProduct.put(Columns.ProductCol.active, isActive)
    private fun onUploadOrCancelClick(){
        if (iv_upload_or_cancel.tag == UPLOAD_TAG) {
            DialogUtils.showChooseImageDialog(this, View.OnClickListener {
                if (it.id == R.id.ll_take_picture) {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if(takePictureIntent.resolveActivity(getPackageManager()) != null){
                        startActivityForResult(takePictureIntent, TAKE_PICTURE)
                    }
                }

                if (it.id  == R.id.ll_browse_image) {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), BROWSE_IMAGE)
                }
            } )


        } else if (iv_upload_or_cancel.tag == CANCEL_TAG) {
            product.imageSmall = null
            product.imageMedium = null
            product.imageLarge = null
            iv_product_image.setImageBitmap(null)
            iv_product_image.visibility = View.GONE
            et_product_image_text.visibility = View.VISIBLE
            iv_upload_or_cancel.tag = UPLOAD_TAG
            iv_upload_or_cancel.setImageResource(R.drawable.ic_image_black_24dp)
        }
    }

    //Second Tab
    private fun onDescriptionClick() {
        DialogUtils.showEnterTextDialog(this) {
            ovProduct.put(Columns.description,  (it as EditText).text)
        }
    }
    private fun onQtyChange() {
        ovProduct.put(qty_available, et_product_qty.text.toString().toFloat())
    }
    private fun onMedicineChange(isChecked: Boolean) {
        ovProduct.put(is_medicine, isChecked)
    }

    //Images
    private fun addMoreImagesClick() {
        DialogUtils.showChooseImageDialog(this) {
            if (it.id == R.id.ll_take_picture) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if(takePictureIntent.resolveActivity(packageManager) != null){
                    startActivityForResult(takePictureIntent, TAKE_ANOTHER_PICTURE)
                }
            }

            if (it.id  == R.id.ll_browse_image) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Pictures"), BROWSE_MORE_IMAGES)
            }
        }
    }
    private fun onSubmit() {
        model.save(ovProduct, ovProductTemplate)
    }

    fun initFragmentListeners (position: Int) {
        if (position == 0) {
            v_product_color.setOnClickListener { onProductColorClick() }
            iv_upload_or_cancel.setOnClickListener { onUploadOrCancelClick() }
            actv_product_name.setOnFocusChangeListener { v, focus -> if (!focus) onNameChange() }
            actv_product_price.addTextChangedListener(onPriceChange())
            actv_product_category.setOnClickListener { onCategoryClick() }
            actv_product_description.setOnClickListener {  }
            actv_product_code.setOnFocusChangeListener { v, focus -> if (!focus) onCodeChange() }
            actv_product_cost_price.setOnFocusChangeListener { v, focus -> if (!focus) onCostChange() }
            actv_product_uom.setOnClickListener { onUOMClick() }
            sc_product_active.setOnCheckedChangeListener { buttonView, isChecked -> onActiveChange(isChecked) }
            bt_toggle_info.setOnClickListener {  toggleSectionInfo(it); }
            bt_hide_info.setOnClickListener {  toggleSectionInfo(bt_toggle_info);  };
            btn_product_submit.setOnClickListener { onSubmit() }
        }
        else if (position == 1) {
            actv_product_description.setOnClickListener { onDescriptionClick() }
            et_product_qty.setOnFocusChangeListener { _, focused -> if(focused) onQtyChange()}
            sc_product_is_medicine.setOnCheckedChangeListener { buttonView, isChecked ->
                onMedicineChange(isChecked)
            }
            ib_add_more_images.setOnClickListener { addMoreImagesClick() }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mMenu = menu
        menuInflater.inflate(R.menu.menu_product_detail_2, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int,  resultCode: Int, data: Intent?)
    {

        if(resultCode == RESULT_OK) {
            if (requestCode == BROWSE_IMAGE) {
                val uri = data?.data
//            if (uri != null) {
//                val imageFile = uriToImageFile(uri)
//
//            }
                if (uri != null) {
                    val imageBitmap = uriToBitmap(uri)
                    iv_product_image.setImageBitmap(imageBitmap)
                    iv_product_image.visibility = View.VISIBLE
                    et_product_image_text.visibility = View.GONE
                    iv_upload_or_cancel.tag = CANCEL_TAG
                    iv_upload_or_cancel.setImageResource(R.drawable.ic_close_grey)
                }
            }

            if (requestCode == TAKE_PICTURE) {
                val extras = data!!.extras
                val imageBitmap = extras!!.get("data") as Bitmap
                iv_product_image.setImageBitmap(imageBitmap)
                iv_product_image.visibility = View.VISIBLE
                et_product_image_text.visibility = View.GONE
                iv_upload_or_cancel.tag = CANCEL_TAG
                iv_upload_or_cancel.setImageResource(R.drawable.ic_close_grey)
            }


            if (requestCode == TAKE_ANOTHER_PICTURE) {
                val extras = data!!.extras
                val imageBitmap = extras!!.get("data") as Bitmap
                iv_product_image.setImageBitmap(imageBitmap)
                iv_product_image.visibility = View.VISIBLE
                et_product_image_text.visibility = View.GONE
                iv_upload_or_cancel.tag = CANCEL_TAG
                iv_upload_or_cancel.setImageResource(R.drawable.ic_close_grey)
                val imageView = ImageView(this)
                imageView.layoutParams.height = 120
                imageView.layoutParams.width = 120
                imageView.setImageBitmap(imageBitmap)
                ll_more_images.addView(imageView)
            }

            if (requestCode == BROWSE_MORE_IMAGES) {
                val uri = data?.data
//            if (uri != null) {
//                val imageFile = uriToImageFile(uri)
//
//            }
                if (uri != null) {
                    val imageBitmap = uriToBitmap(uri)
                    iv_product_image.setImageBitmap(imageBitmap)
                    iv_product_image.visibility = View.VISIBLE
                    et_product_image_text.visibility = View.GONE
                    iv_upload_or_cancel.tag = CANCEL_TAG
                    iv_upload_or_cancel.setImageResource(R.drawable.ic_close_grey)
                    ll_more_images.addView(getImage(imageBitmap, View.OnClickListener {
                        ll_more_images.removeView(it)
                    }))
                }
            }
        }

    }

    private fun uriToImageFile(uri: Uri): File? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                val filePath = cursor.getString(columnIndex)
                cursor.close()
                return File(filePath)
            }
            cursor.close()
        }
        return null
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        return MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
    }

    private fun getImage(bitmap: Bitmap, removeImage: View.OnClickListener) : FrameLayout{
        val tinySize = 30
        val rightMargin = 10

        val parentLayoutParams = FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val imageFrame = FrameLayout(this)
        imageFrame.layoutParams = parentLayoutParams

        val closeBtnLayoutParams = FrameLayout.LayoutParams(tinySize, tinySize)
        closeBtnLayoutParams.gravity = Gravity.TOP or Gravity.RIGHT
        closeBtnLayoutParams.rightMargin = rightMargin
        val closeButton = ImageButton(this)
        closeButton.layoutParams = closeBtnLayoutParams
        closeButton.setImageResource(R.drawable.ic_close)
        closeButton.setOnClickListener { removeImage.onClick(imageFrame) }

        val correctBtnLayoutParams = FrameLayout.LayoutParams(tinySize, tinySize)
        correctBtnLayoutParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
        correctBtnLayoutParams.rightMargin = rightMargin
        val correctButton = ImageButton(this)
        correctButton.layoutParams = correctBtnLayoutParams
        correctButton.setImageResource(R.drawable.ic_check_circle_black_24dp)

        val imageViewLayoutParams = FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val imageView = ImageView(this)
        imageViewLayoutParams.rightMargin = rightMargin
        imageView.layoutParams = imageViewLayoutParams
        imageView.layoutParams.height = ib_add_more_images.height
        imageView.layoutParams.width =  ib_add_more_images.width
        imageView.setImageBitmap(bitmap)

        imageFrame.addView(closeButton)
        imageFrame.addView(correctButton)
        imageFrame.addView(imageView)
        imageFrame.bringChildToFront(closeButton)
        imageFrame.bringChildToFront(correctButton)
        return imageFrame

    }

}