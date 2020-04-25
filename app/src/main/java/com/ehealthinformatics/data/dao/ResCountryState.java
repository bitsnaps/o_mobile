package com.ehealthinformatics.data.dao;

import android.content.Context;

import com.ehealthinformatics.App;
import com.ehealthinformatics.core.orm.ODataRow;
import com.ehealthinformatics.core.orm.OModel;
import com.ehealthinformatics.core.orm.fields.OColumn;
import com.ehealthinformatics.core.orm.fields.types.OVarchar;
import com.ehealthinformatics.core.support.OUser;
import com.ehealthinformatics.data.db.Columns;
import com.ehealthinformatics.data.dto.Partner;
import com.ehealthinformatics.data.dto.State;

public class ResCountryState extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn code = new OColumn("Code", OVarchar.class);
    OColumn country_id = new OColumn("Country", ResCountry.class, OColumn.RelationType.ManyToOne);

    public ResCountryState(Context context, OUser user) {
        super(context, "res.country.state", user);
    }

    public ResCountry countryDao;

    public void initDaos(){
        countryDao = App.getDao(ResCountry.class);
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
