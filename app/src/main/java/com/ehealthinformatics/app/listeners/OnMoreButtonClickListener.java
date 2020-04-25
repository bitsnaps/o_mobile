package com.ehealthinformatics.app.listeners;

import android.view.MenuItem;
import android.view.View;

import com.ehealthinformatics.data.dto.PosOrder;

public interface OnMoreButtonClickListener {
    void onItemClick(View view , PosOrder posOrder, MenuItem menuItem);
}