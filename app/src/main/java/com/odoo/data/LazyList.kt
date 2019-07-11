package com.odoo.data

import android.database.Cursor

import java.util.ArrayList

class LazyList<T>(private val mCursor: Cursor, private val mCreator: ItemFactory<T>) : ArrayList<T>() {

    val list: List<*>
        get() = this

    override fun get(index: Int): T {
        val size = super.size
        if (index < size) {
            // find item in the collection
            var item: T = super.get(index)
            if (item == null) {
                item = mCreator.create(mCursor, index)
                set(index, item)
            }
            return item
        } else {
            // we have to grow the collection
            for (i in size until index) {
                add(null!!)
            }
            // create last object, add and return
            val item = mCreator.create(mCursor, index)
            add(item)
            return item
        }
    }

    override val size: Int
        get() = mCursor.count

    fun closeCursor() {
        //mCursor.close();
    }

    fun cursorIsClosed(): Boolean {
        return mCursor.isClosed
    }

    interface ItemFactory<T> {
        fun create(cursor: Cursor, index: Int): T
    }

}