package com.odoo.rxshop.activity.product

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.odoo.odoorx.rxshop.R
import com.odoo.rxshop.utils.DialogUtils
import com.odoo.odoorx.core.base.orm.OValues
import com.odoo.odoorx.core.base.orm.RelValues
import com.odoo.odoorx.rxshop.base.support.OdooCompatActivity
import com.odoo.odoorx.core.base.utils.BitmapUtils
import com.odoo.odoorx.core.data.db.Columns
import com.odoo.odoorx.core.data.dto.Product
import com.odoo.odoorx.core.data.dto.ProductImage
import com.odoo.odoorx.core.data.viewmodel.ProductViewModel
import kotlinx.android.synthetic.main.layout_product_website.*


class WebsiteFragment(val product: Product, val ovProduct: OValues, val ovProductTemplate: OValues, val model: ProductViewModel) : Fragment() {

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        public var TAKE_ANOTHER_PICTURE = 3
        public var BROWSE_MORE_IMAGES = 4

    }
    lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_product_website, container, false)
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
        product.productTemplate.websiteImages.forEach {
            addImageUI(it)
        }
    }

    private fun listeners() {
        ib_add_more_images.setOnClickListener { onAddMoreImages() }
    }

    private fun getImage(productImage: ProductImage, removeImage: View.OnClickListener): FrameLayout {
        val tinySize = 30
        val margin = 20

        val parentLayoutParams = FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val imageFrame = FrameLayout(activity)
        imageFrame.layoutParams = parentLayoutParams

        val closeBtnLayoutParams = FrameLayout.LayoutParams(tinySize, tinySize)
        closeBtnLayoutParams.gravity = Gravity.TOP or Gravity.RIGHT
        closeBtnLayoutParams.rightMargin = margin
        closeBtnLayoutParams.topMargin = margin
        val closeButton = ImageButton(activity)
        closeButton.layoutParams = closeBtnLayoutParams
        closeButton.setImageResource(R.drawable.ic_close)
        closeButton.setOnClickListener { removeImage.onClick(imageFrame) }

        val correctBtnLayoutParams = FrameLayout.LayoutParams(tinySize, tinySize)
        correctBtnLayoutParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
        correctBtnLayoutParams.rightMargin = margin
        correctBtnLayoutParams.bottomMargin = margin
        val correctButton = ImageButton(activity)
        correctButton.layoutParams = correctBtnLayoutParams
        correctButton.setImageResource(R.drawable.ic_check_circle_black_24dp)

        val imageViewLayoutParams = FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val imageView = ImageView(activity)
        imageViewLayoutParams.rightMargin = 10
        imageView.layoutParams = imageViewLayoutParams
        imageView.layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
        imageView.layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
        imageView.setImageBitmap(productImage.image)

        imageFrame.addView(closeButton)
        imageFrame.addView(correctButton)
        imageFrame.addView(imageView)
        imageFrame.bringChildToFront(closeButton)
        imageFrame.bringChildToFront(correctButton)
        return imageFrame

    }

    private fun addImageUI(productImage: ProductImage) {
        val onClick = View.OnClickListener {
            ll_more_images.removeView(it)
            onRemoveWebsiteImage(productImage)
        };
        val productImageView = getImage(productImage, onClick)
        ll_more_images.addView(productImageView)
        val relValues = RelValues()
        relValues.append(productImage)
    }

    //Images
    private fun onAddMoreImages() {
        DialogUtils.showChooseImageDialog(activity) {
            if (it.id == R.id.ll_take_picture) {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent.resolveActivity(activity?.packageManager) != null) {
                    startActivityForResult(takePictureIntent, TAKE_ANOTHER_PICTURE)
                }
            }

            if (it.id == R.id.ll_browse_image) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                val fragment = this
                fragment.startActivityForResult(Intent.createChooser(intent, "Select Pictures"), BROWSE_MORE_IMAGES)
            }
        }
    }

    private fun onRemoveWebsiteImage(productImage: ProductImage) {
        val relValues = RelValues()
        relValues.delete(productImage.id)
        ovProductTemplate.put(Columns.ProductTemplateCol.product_image_ids, relValues)
    }

    private fun onAddWebsiteImage(productImage: ProductImage){
        val imageValue = RelValues()
        imageValue.append(productImage.toOValues())
        ovProductTemplate.put(Columns.ProductTemplateCol.product_image_ids, imageValue)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == OdooCompatActivity.RESULT_OK) {
            if (requestCode == BROWSE_MORE_IMAGES) {
                val uri = data?.data
                if (uri != null) {
                    val imageBitmap = BitmapUtils.uriToBitmap(activity!!.contentResolver,uri)
                    val productImage = ProductImage(0, 0, imageBitmap)
                    onAddWebsiteImage(productImage)
                    addImageUI(productImage)
                }
            }
            if (requestCode == TAKE_ANOTHER_PICTURE) {
                val extras = data!!.extras
                val imageBitmap = extras!!.get("data") as Bitmap
                val productImage = ProductImage(0, 0, imageBitmap)
                onAddWebsiteImage(productImage)
                addImageUI(productImage)
            }
        }
    }
}