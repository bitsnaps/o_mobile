package com.ehealthinformatics.data.dto

import com.ehealthinformatics.core.orm.OValues
import com.ehealthinformatics.data.db.Columns

class State(var id: Int,var server_id: Int, var name: String, var country: Country) : DTO {

    override fun toOValues(): OValues {
        var oValues = OValues()
        oValues.put(Columns.id, id)
        oValues.put(Columns.name, name)
        return oValues
    }
}