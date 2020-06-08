package com.ehealthinformatics.app.activity.product

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.ehealthinformatics.R
import com.ehealthinformatics.app.utils.DialogUtils
import com.ehealthinformatics.core.orm.OValues
import com.ehealthinformatics.core.orm.RelValues
import com.ehealthinformatics.core.support.OdooCompatActivity
import com.ehealthinformatics.data.db.Columns
import com.ehealthinformatics.data.dto.Product
import com.ehealthinformatics.data.dto.ProductImage
import com.ehealthinformatics.data.viewmodel.ProductViewModel
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
            addImage(it)
        }
    }

    private fun listeners() {
        ib_add_more_images.setOnClickListener { addMoreImagesClick() }
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

    private fun addImage(productImage: ProductImage) {
        val onClick = View.OnClickListener {
            ll_more_images.removeView(it)
            onRemoveWebsiteImage(productImage)
        };
        val productImageView = getImage(productImage, onClick)
        ll_more_images.addView(productImageView)
        val relValues = RelValues()
        relValues.append(productImage)
        ovProductTemplate.put(Columns.ProductTemplateCol.product_image_ids, productImage)
    }

    //Images
    private fun addMoreImagesClick() {
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

    private fun onRemoveWebsiteImage(image: ProductImage) {
        val relValues = RelValues()
        relValues.delete(image.id)
        ovProductTemplate.put(Columns.ProductTemplateCol.product_image_ids, image)
    }


    private fun uriToBitmap(uri: Uri): Bitmap {
        return MediaStore.Images.Media.getBitmap(activity!!.contentResolver, uri)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == OdooCompatActivity.RESULT_OK) {
            if (requestCode == BROWSE_MORE_IMAGES) {
                val uri = data?.data
                if (uri != null) {
                    val imageBitmap = uriToBitmap(uri)
                    val productImage = ProductImage(0, 0, imageBitmap)
                    addImage(productImage)
                }
            }
            if (requestCode == TAKE_ANOTHER_PICTURE) {
                val extras = data!!.extras
                val imageBitmap = extras!!.get("data") as Bitmap
                val productImage = ProductImage(0, 0, imageBitmap)
                addImage(productImage)
            }
        }
    }
}