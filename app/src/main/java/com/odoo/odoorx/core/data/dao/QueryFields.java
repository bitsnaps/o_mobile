package com.odoo.odoorx.core.data.dao;

import com.odoo.odoorx.core.data.db.Columns;

import java.util.HashMap;
import java.util.Map;

public class QueryFields {

    public QueryFields(){
        this(QueryMode.FIELDS);
    }
    private QueryFields(QueryMode queryMode){
        this.queryMode  = queryMode;
        fields = new HashMap<>();
    }

    private QueryMode queryMode;
    private Map<String, Object> fields ;

    public boolean contains(String field) {
        if(queryMode.equals(QueryMode.ALL))return true;
        if(queryMode.equals(QueryMode.ROOT) && field.contains("_id") &&
                !field.equalsIgnoreCase(Columns.server_id) &&
                !field.equalsIgnoreCase(Columns.id))return false;
        return true;//fields.containsKey(field);
    }

    public QueryFields childField(String field) {
        if(queryMode.equals(QueryMode.ALL))return this;
        if(contains(field)){
            return (QueryFields) fields.get(field);
        }
        return null;
    }

    public static QueryFields create(String fieldsString){
        String ex = "name&session.name,";
        return id();
    }

    public static QueryFields id(){
        QueryFields queryFields = new QueryFields();
        queryFields.fields.put(Columns.id, null);
        queryFields.fields.put(Columns.server_id, null);
        return queryFields;
    }

    public static QueryFields idName(){
        return new QueryFields(QueryMode.ALL);
    }

    public static QueryFields all(){
        return new QueryFields(QueryMode.ALL);
    }

    public static QueryFields root(){
        return new QueryFields(QueryMode.ROOT);
    }

    public String[] projections(){
        return new String[]{};
    }


    public enum QueryMode{
        ALL, FIELDS, ID, ROOT;
    }

}
