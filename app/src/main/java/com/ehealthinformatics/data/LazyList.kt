package com.ehealthinformatics.data

import com.ehealthinformatics.core.orm.ODataRow
import com.ehealthinformatics.data.db.Columns
import java.util.ArrayList

class LazyList<T>(private val mCreator: ItemFactory<T>, private val oDataRows: List<ODataRow>) : ArrayList<T>() {

    val list: List<*>
        get() = this

    override fun get(index: Int): T {
        val size = super.size
        if (index < size) {
            // find item in the collection
            var item: T = super.get(index)
            if (item == null) {
                item = mCreator.create(oDataRows[index].getInt(Columns.id))
                set(index, item)
            }
            return item
        } else {
            // we have to grow the collection
            for (i in size until index) {
                //add(null!!)
            }
            // create last object, add and return
            val item = mCreator.create(oDataRows[index].getInt(Columns.id))
            add(item)
            return item
        }
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }

    override val size: Int
        get() = oDataRows.size

    interface ItemFactory<T> {
        fun create(id: Int): T
    }

}