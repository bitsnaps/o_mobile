package com.ehealthinformatics.app.viewholder

import android.view.View
import android.widget.*

import com.ehealthinformatics.R

class ImageViewHolder internal constructor(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    public var image: ImageButton = itemView.findViewById(R.id.ib_image_button) as ImageButton


}