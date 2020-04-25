package com.ehealthinformatics.data.dao

import com.ehealthinformatics.data.dto.DTO
import com.ehealthinformatics.core.orm.ODataRow

interface Dao {

    fun <T> fromRow(oDataRow: ODataRow): T
    fun <T> get(id: Int) : T
    fun <T> put(dto: DTO) : Boolean

}
