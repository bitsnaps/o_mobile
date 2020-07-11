package com.ehealthinformatics.rxshop.listeners;

import android.view.MenuItem;
import android.view.View;

import com.ehealthinformatics.odoorx.core.data.dto.PosOrder;

public interface OnMoreButtonClickListener {
    void onItemClick(View view , PosOrder posOrder, MenuItem menuItem);
}