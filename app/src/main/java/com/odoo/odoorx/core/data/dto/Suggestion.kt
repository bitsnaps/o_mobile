package com.odoo.odoorx.core.data.dto

class Suggestion(var id: Int, var name: String, var detail: String, var detail2: String){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Suggestion

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}
