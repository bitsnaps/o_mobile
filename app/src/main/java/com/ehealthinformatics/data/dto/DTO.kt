package com.ehealthinformatics.data.dto

import com.ehealthinformatics.core.orm.OValues

interface DTO {
    fun toOValues(): OValues
}
