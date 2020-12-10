package com.odoo.odoorx.core.base.rpc.helper.utils.gson;

import com.odoo.odoorx.core.base.utils.LinkedTreeMapWraper;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public abstract class OdooRecord<K, V> extends AbstractMap<K, V> {
    private static final Comparator<Comparable> NATURAL_ORDER = new Comparator<Comparable>() {
        public int compare(Comparable a, Comparable b) {
            return a.compareTo(b);
        }
    };

    Comparator<? super K> comparator;
    public OdooRecord() {
        this((Comparator<? super K>) NATURAL_ORDER);
    }

    /**
     * Create a tree map ordered by {@code comparator}. This map's keys may only
     * be null if {@code comparator} permits.
     *
     * @param comparator the comparator to order elements with, or {@code null} to
     *                   use the natural ordering.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    // unsafe! if comparator is null, this assumes K is comparable
    public OdooRecord(Comparator<? super K> comparator) {
        this.comparator = comparator != null
                ? comparator
                : (Comparator) NATURAL_ORDER;
    }


    public OdooRecord(AbstractMap<K, V> linkedTreeMap) {
        this((Comparator<? super K>) NATURAL_ORDER);
        Set keySet = linkedTreeMap.keySet();
        for(Object key: keySet) {
            put((K)key, linkedTreeMap.get(key));
        }
    }


    public List<OdooRecord> records = new ArrayList<>();

    public String getString(String key) {
        if (containsKey(key))
            return get(key).toString();
        return "false";
    }

    public Double getDouble(String key) {
        return (Double) get(key);
    }

    public Integer getInt(String key) {
        return getDouble(key).intValue();
    }

    public Boolean getBoolean(String key) {
        return (Boolean) get(key);
    }

    public OdooRecord getM20(String key) {
        if (!getString(key).equals("false")) {
            OdooRecord rec = new LinkedTreeMapWraper();
            List<Object> value = getArray(key);
            rec.put("id", value.get(0));
            rec.put("name", value.get(1));
            return rec;
        }
        return null;
    }

    public List<Integer> getM2M(String key) {
        return getO2M(key);
    }

    public List<Integer> getO2M(String key) {
        if (!getString(key).equals("false")) {
            return getArray(key);
        }
        return new ArrayList<>();
    }

    public <T> List<T> getArray(String key) {
        return (List<T>) get(key);
    }
}