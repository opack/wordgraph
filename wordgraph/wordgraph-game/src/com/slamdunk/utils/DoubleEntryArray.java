package com.slamdunk.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Cette classe permet de gérer un tableau ? double entrée dont chaque entrée
 * est de type EntryType, et dont la valeur est de type ValueType.
 */
public class DoubleEntryArray<Entry1Type, Entry2Type, ValueType> {
    private Map<Entry1Type, Map<Entry2Type, ValueType>> data;

    public DoubleEntryArray() {
        data = new HashMap<Entry1Type, Map<Entry2Type, ValueType>>();
    }

    public ValueType get(Entry1Type entry1, Entry2Type entry2) {
        Map<Entry2Type, ValueType> values = data.get(entry1);
        if (values == null) {
            return null;
        }
        return values.get(entry2);
    }

    public void put(Entry1Type entry1, Entry2Type entry2, ValueType value) {
        Map<Entry2Type, ValueType> values = data.get(entry1);
        if (values == null) {
            values = new HashMap<Entry2Type, ValueType>();
            data.put(entry1, values);
        }
        values.put(entry2, value);
    }

	public void clear() {
		for (Map<Entry2Type, ValueType> map : data.values()) {
			map.clear();
		}
		data.clear();
	}

	/**
	 * Retourne l'ensemble des clés de type Entry1Type
	 * @return 
	 */
	public Set<Entry1Type> getKeys1() {
		return data.keySet();
	}
}