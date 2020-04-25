package com.ehealthinformatics.data.dto


class Customer( id: Int,  serverId: Int?,  displayName: String?,  email: String,  phone: String,  company: Company? ,  address: String,  state: State?,  locality: String) :
        Partner(id ,  serverId ,  displayName ,  email ,  phone ,  company
                ,  address ,  state, locality ){

    constructor(partner: Partner?): this(partner!!.id, partner.serverId, partner.displayName, partner.email, partner.phone, partner.company
    , partner.address, partner.state, partner.locality)
}

