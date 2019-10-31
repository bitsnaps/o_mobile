package com.odoo.base.addons.abirex.dto

import com.odoo.core.orm.OValues

open interface DTO{
    fun toOValues(): OValues
}
