package com.ehealthinformatics.data.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup

import com.ehealthinformatics.R
import com.ehealthinformatics.app.viewholder.ImageViewHolder

class AdvertismentListAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<ImageViewHolder>() {

    var advertismentList: List<Bitmap> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.image_button, null)
        return ImageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val advertisement = advertismentList[position]
        holder.image.setImageBitmap(advertisement)
    }

    override fun getItemCount(): Int {
        return  advertismentList.size
    }

    fun resetList(advertismentList: List<Bitmap>){
        this.advertismentList = advertismentList
        notifyDataSetChanged()
    }

}
