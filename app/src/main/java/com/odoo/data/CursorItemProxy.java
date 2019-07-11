package com.odoo.data;

import android.database.Cursor;
import android.support.annotation.NonNull;

abstract class CursorItemProxyp {

    private Cursor mCursor;
    private int mIndex;

    public CursorItemProxyp(@NonNull Cursor cursor, int index) {
        mCursor = cursor;
        mIndex = index;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public int getIndex() {
        return mIndex;
    }

}