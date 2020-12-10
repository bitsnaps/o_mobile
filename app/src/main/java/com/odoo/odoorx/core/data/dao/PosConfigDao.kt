package com.odoo.odoorx.core.data.dao

import android.content.Context


import com.odoo.odoorx.core.base.orm.ODataRow
import com.odoo.odoorx.core.base.orm.OModel
import com.odoo.odoorx.core.base.orm.fields.OColumn
import com.odoo.odoorx.core.base.orm.fields.types.OVarchar
import com.odoo.odoorx.core.base.support.OUser
import com.odoo.odoorx.core.data.db.Columns
import com.odoo.odoorx.core.data.db.ModelNames
import com.odoo.odoorx.core.data.dto.AccountJournal
import com.odoo.odoorx.core.data.dto.PosConfig
import com.odoo.odoorx.rxshop.BuildConfig


class PosConfigDao(context: Context, user: OUser?) : OModel(context, ModelNames.POS_CONFIG, user) {
    internal var name = OColumn("Name", OVarchar::class.java)
    internal var location_id = OColumn("Location", LocationDao::class.java, OColumn.RelationType.ManyToOne)
    internal var pricelist_id = OColumn("Pricelist", PriceListDao::class.java, OColumn.RelationType.ManyToOne)
    internal var company_id = OColumn("Company ", ResCompany::class.java, OColumn.RelationType.ManyToOne)
    internal var journal_id = OColumn("Journal ", AccountJournalDao::class.java, OColumn.RelationType.ManyToOne)
    internal var journal_ids = OColumn("Payment Journals", AccountJournalDao::class.java, OColumn.RelationType.ManyToMany)

    lateinit var accountJournalDao: AccountJournalDao
    lateinit var priceListDao: PriceListDao
    lateinit var companyDao: ResCompany

    override fun initDaos() {
        val daoRepo = DaoRepoBase.getInstance()
        accountJournalDao = daoRepo.getDao(AccountJournalDao::class.java)
        priceListDao = daoRepo.getDao(PriceListDao::class.java)
        companyDao = daoRepo.getDao(ResCompany::class.java)
    }

    override operator fun get(id: Int, qf: QueryFields): PosConfig {
        val oDataRow = browse(id)
        return fromDataRow(oDataRow, qf)
    }

    fun fromDataRow(row: ODataRow, qf: QueryFields): PosConfig {
        val id = if(qf.contains(Columns.id)) row.getInt(Columns.id) else null
        val serverId = if(qf.contains(Columns.server_id)) row.getInt(Columns.server_id) else null
        val name = if(qf.contains(Columns.name)) row.getString(Columns.name) else null
        val locationId = if(qf.contains(Columns.server_id)) row.getInt(this.location_id.name) else null
        val company = if(qf.contains(Columns.server_id)) companyDao.fromRow(row.getM2ORecord(Columns.PosConfig.company_id).browse(), qf.childField(Columns.PosConfig.company_id)) else null
        val journal = if(qf.contains(Columns.PosConfig.journal_id)) accountJournalDao.fromRow(row.getM2ORecord(Columns.PosConfig.journal_id).browse(), qf.childField(Columns.PosConfig.journal_id)) else null
        val paymentJournals = ArrayList<AccountJournal>()
        for(dataRow in row.getM2MRecord(Columns.PosConfig.journal_ids).browseEach()){
            paymentJournals.add(accountJournalDao.fromRow(dataRow, qf.childField(Columns.PosConfig.journal_ids)))
        }
        val priceList = priceListDao.fromRow(row.getM2ORecord(Columns.PosConfig.price_list_id).browse(), qf.childField(Columns.PosConfig.price_list_id))
        return PosConfig(id, serverId, name,  locationId, company, priceList, journal, paymentJournals)
    }

    override fun allowCreateRecordOnServer(): Boolean {
        return false
    }

    override fun allowUpdateRecordOnServer(): Boolean {
        return false
    }

    override fun allowDeleteRecordInLocal(): Boolean {
        return false
    }

    companion object {
        @JvmField
        var AUTHORITY: String = BuildConfig.APPLICATION_ID + ".base.provider.content.sync.pos_config"
        val TAG = PosConfigDao::class.java.simpleName
    }

}
