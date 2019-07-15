package com.odoo.base.addons.abirex.dao;

import com.odoo.core.orm.ODataRow;

public interface Dao {

    public <T> T fromRow(ODataRow oDataRow);

    public Dao get(int id);
}
