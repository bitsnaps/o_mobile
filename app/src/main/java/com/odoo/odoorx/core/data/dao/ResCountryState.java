package com.odoo.odoorx.core.data.dao;

import android.content.Context;


import com.odoo.odoorx.core.base.orm.ODataRow;
import com.odoo.odoorx.core.base.orm.OModel;
import com.odoo.odoorx.core.base.orm.fields.OColumn;
import com.odoo.odoorx.core.base.orm.fields.types.OVarchar;
import com.odoo.odoorx.core.base.support.OUser;
import com.odoo.odoorx.core.data.db.Columns;
import com.odoo.odoorx.core.data.dto.State;

public class ResCountryState extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn code = new OColumn("Code", OVarchar.class);
    OColumn country_id = new OColumn("Country", ResCountry.class, OColumn.RelationType.ManyToOne);

    public ResCountryState(Context context, OUser user) {
        super(context, "res.country.state", user);
    }

    public ResCountry countryDao;

    public void initDaos(){
       DaoRepoBase daoRepo = DaoRepoBase.getInstance();
        countryDao = daoRepo.getDao(ResCountry.class);
    }

    public State fromRow(final ODataRow row) {
        if(row == null) return  null;
        State state = new State(
                row.getInt(Columns.id),
                row.getInt(Columns.server_id),
                row.getString(Columns.Partner.name),
                countryDao.fromRow(row.getM2ORecord(Columns.Partner.country_id).browse()));
        return state;
    }
}
