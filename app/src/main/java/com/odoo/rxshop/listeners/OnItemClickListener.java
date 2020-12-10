package com.odoo.rxshop.listeners;

import android.view.View;

public interface OnItemClickListener<T> {
    void onItemClick(View v, T item, int pos);
}
