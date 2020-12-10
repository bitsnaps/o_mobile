package com.odoo.rxshop.listeners;

import android.view.MenuItem;
import android.view.View;

import com.odoo.odoorx.core.data.dto.PosOrder;

public interface OnMoreButtonClickListener {
    void onItemClick(View view , PosOrder posOrder, MenuItem menuItem);
}