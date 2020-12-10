package com.odoo.odoorx.core.data.dto

import com.odoo.odoorx.core.base.orm.OValues

interface DTO {
    fun toOValues(): OValues
}
