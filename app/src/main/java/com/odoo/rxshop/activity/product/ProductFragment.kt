package com.odoo.rxshop.activity.product

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.odoo.odoorx.rxshop.R
import com.odoo.rxshop.activity.utils.ListUtils
import com.odoo.rxshop.listeners.OnItemClickListener
import com.odoo.rxshop.utils.DialogUtils
import com.odoo.rxshop.utils.Tools
import com.odoo.rxshop.utils.ViewAnimation
import com.odoo.odoorx.core.base.orm.OValues
import com.odoo.odoorx.rxshop.base.support.OdooCompatActivity
import com.odoo.odoorx.core.base.utils.BitmapUtils
import com.odoo.odoorx.core.base.utils.StringUtils
import com.odoo.odoorx.rxshop.data.adapter.SimpleListAdapter
import com.odoo.odoorx.core.data.db.Columns
import com.odoo.odoorx.core.data.dto.Product
import com.odoo.odoorx.core.data.dto.SimpleItem
import com.odoo.odoorx.core.data.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.layout_product_basic.*
import java.text.DecimalFormat


class ProductFragment(val product: Product, val ovProduct: OValues,val ovProductTemplate: OValues, val model: ProductViewModel) : Fragment() {

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val CURRENCY_FORMAT = "₦ %.2f";

        public var TAKE_PICTURE = 1
        public var BROWSE_IMAGE = 2
        public var TAKE_ANOTHER_PICTURE = 3
        public var BROWSE_MORE_IMAGES = 4
        private val CANCEL_TAG = "cancel"
        private val UPLOAD_TAG = "upload"
        private val currencySign = "₦"
    }

    lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.layout_product_basic, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    fun init(rootView: View) {
        toUI()
        listeners()
    }

    fun toUI() {
        tv_product_name.text = StringUtils.trimString(product.productTemplate.name!!,20)
        tv_product_price.text = ("$currencySign %.2f").format(product.cost)
        actv_product_name.setText(product.productTemplate.name)
        actv_product_price.setText("$currencySign %.2f".format(product.price))
        actv_product_category.setText(product.productTemplate.category!!.name)
        actv_product_description.setText(StringUtils.strip(product.productTemplate.description))
        actv_product_code.setText(StringUtils.strip(product.code))
        actv_product_cost_price.setText(currencySign + "%.2f".format(product.cost))
        actv_product_uom.setText(product.productTemplate.uom!!.name)
        sc_product_active.isChecked = product.productTemplate.active!!
        if(product.imageSmall != null) {
            iv_product_image.setImageBitmap(product.imageSmall)
            et_product_image_text.visibility = View.GONE
            iv_upload_or_cancel.setImageResource(R.drawable.ic_close_grey)
            iv_upload_or_cancel.tag = CANCEL_TAG
        } else {
            et_product_image_text.visibility = View.VISIBLE
            iv_upload_or_cancel.setImageResource(R.drawable.ic_image_black_24dp)

        }
    }

    fun listeners() {
        v_product_color.setOnClickListener { onProductColorClick() }
        iv_upload_or_cancel.setOnClickListener { onUploadOrCancelClick() }
        actv_product_name.addTextChangedListener (onTextChange() )
        actv_product_price.addTextChangedListener(onTextChange())
        actv_product_category.setOnClickListener { onCategoryClick() }
        actv_product_description.setOnClickListener { onDescriptionClick() }
        actv_product_code.setOnFocusChangeListener { v, focus -> if (!focus) onCodeChange() }
        actv_product_cost_price.setOnFocusChangeListener { v, focus -> if (!focus) onCostChange() }
        actv_product_uom.setOnClickListener { onUOMClick() }
        sc_product_active.setOnCheckedChangeListener { buttonView, isChecked -> onActiveChange(isChecked) }
        bt_toggle_info.setOnClickListener {  toggleSectionInfo(it); }
        bt_hide_info.setOnClickListener {  toggleSectionInfo(bt_toggle_info);  };
        btn_product_submit.setOnClickListener { onSubmit() }
    }

    private fun onProductColorClick(){
        DialogUtils.showColorsDialog(activity, v_product_color, ll_summary_in_image, et_product_image_text)
    }
    var decim: DecimalFormat = DecimalFormat("#,###.##")
    private fun onTextChange() : TextWatcher {
        return object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(s: Editable) {
                if (s.hashCode() == actv_product_price.text.hashCode()) {
                    actv_product_price.removeTextChangedListener(this)
                    if (s.toString() != current) {
                    val productPrice = s.removePrefix(currencySign)
                            .toString()
                            .replace(".", "")
                            .toFloat() / 100
                    val editTextString = "%.2f".format(productPrice);
                    actv_product_price.setText("$currencySign $editTextString")
                    actv_product_price.setSelection(editTextString.length+2)
                    ovProduct.put(Columns.ProductCol.lst_price, productPrice)
                    }
                    actv_product_price.addTextChangedListener(this);
                }
                else if (s.hashCode() == actv_product_name.text.hashCode()) {
                    ovProduct.put(Columns.name, actv_product_name.text.toString() )
                    ovProductTemplate.put(Columns.name, actv_product_name.text.toString() )
                }
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
    private fun toggleArrow(view: View): Boolean {
        if (view.rotation == 0F) {
            view.animate().setDuration(200).rotation(180F);
            return true
        } else {
            view.animate().setDuration(200).rotation(0F);
            return false
        }
    }
    private fun onCategoryClick() {
        val simpleListAdapter = SimpleListAdapter(activity as Activity, ListUtils.toSimpleItems(model.selected.value!!.categories), OnItemClickListener<SimpleItem> { v, simpleItem, pos ->
             actv_product_category.setText(simpleItem.label)
            ovProductTemplate.put(Columns.ProductTemplateCol.category_id, simpleItem.value)
        })
        DialogUtils.showChooseItemDialog(activity, simpleListAdapter)
    }
    private fun onCodeChange() {
        ovProduct.put(Columns.ProductCol.code, actv_product_code.text.toString())
    }
    private fun onCostChange() {
        ovProduct.put(Columns.ProductCol.cost_price, actv_product_cost_price.text.toString().toFloat())
    }
    private fun onUOMClick() {
        val simpleListAdapter = SimpleListAdapter(activity as Activity, ListUtils.toSimpleItems2(model.selected.value!!.uoms), OnItemClickListener<SimpleItem> { v, simpleItem, pos ->
            actv_product_uom.setText(simpleItem.label)
            ovProductTemplate.put(Columns.ProductTemplateCol.uom_id, simpleItem.value.toInt())
        })
        DialogUtils.showChooseItemDialog(activity, simpleListAdapter)
    }
    private fun onActiveChange(isActive: Boolean) {
        ovProduct.put(Columns.ProductCol.active, isActive)
        ovProductTemplate.put(Columns.ProductTemplateCol.active, isActive)
    }
    private fun onUploadOrCancelClick(){
        if (iv_upload_or_cancel.tag == UPLOAD_TAG) {
            DialogUtils.showChooseImageDialog(activity, View.OnClickListener {
                if (it.id == R.id.ll_take_picture) {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if(takePictureIntent.resolveActivity(activity!!.packageManager) != null){
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
            removeProductImage()
            iv_product_image.visibility = View.GONE
            et_product_image_text.visibility = View.VISIBLE
            iv_upload_or_cancel.tag = UPLOAD_TAG
            iv_upload_or_cancel.setImageResource(R.drawable.ic_image_black_24dp)
        }
    }

    private fun addProductImage(bitmap: Bitmap) { val values =  OValues()
        val bitmapSmall = BitmapUtils.toBase64(BitmapUtils.resizeImage(72, 72, bitmap))
        val bitmapMedium = BitmapUtils.toBase64(BitmapUtils.resizeImage(144, 144, bitmap))
        val bitmapLarge = BitmapUtils.toBase64(BitmapUtils.resizeImage(480, 800, bitmap))
        ovProduct.put("image_small", bitmapSmall)
        ovProduct.put("image_medium", bitmapMedium)
        ovProduct.put("image", bitmapLarge)
    }

    private fun removeProductImage() {
        ovProduct.put("image_small", "false")
        ovProduct.put("image_medium", "false")
        ovProduct.put("image", "false")
    }

    private fun onDescriptionClick() {
        DialogUtils.showEnterTextDialog(activity, actv_product_description.text.toString()) {
            val text = (it as EditText).text.toString()
            actv_product_description.setText(text)
            ovProductTemplate.put(Columns.description,  text)
        }
    }
    private fun onMedicineChange(isChecked: Boolean) {
        ovProduct.put(Columns.ProductTemplateCol.is_medicine, isChecked)
    }
    private fun onSubmit() {
        if (ovProduct.keys().size > 0 || ovProductTemplate.keys().size > 0)
            model.save(ovProduct, ovProductTemplate)
        else
            Toast.makeText(context, "No changes to save", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == OdooCompatActivity.RESULT_OK) {
            if (requestCode == BROWSE_IMAGE) {
                val uri = data?.data
                if (uri != null) {
                    val imageBitmap = BitmapUtils.uriToBitmap(activity!!.contentResolver, uri)
                    iv_product_image.setImageBitmap(imageBitmap)
                    iv_product_image.visibility = View.VISIBLE
                    et_product_image_text.visibility = View.GONE
                    iv_upload_or_cancel.tag = CANCEL_TAG
                    iv_upload_or_cancel.setImageResource(R.drawable.ic_close_grey)
                    addProductImage(imageBitmap)
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
                addProductImage(imageBitmap)
            }
        }
    }
}