package com.slamdunk.utils;

import java.util.Properties;

/**
 * Classe de propri�t�s �tendues permettant de r�cup�rer directement le bon
 * type de donn�e plut�t qu'un String.
 */
public class PropertiesEx extends Properties {
    private static final long serialVersionUID = 5448778341923435054L;

    public boolean getBooleanProperty(String name, boolean fallback) {
        String v = getProperty(name);
        if (v == null) return fallback;
        return Boolean.parseBoolean(v);
    }

    public float getFloatProperty(String name, float fallback) {
        String v = getProperty(name);
        if (v == null) return fallback;
        return Float.parseFloat(v);
    }

    public int getIntegerProperty(String name, int fallback) {
        String v = getProperty(name);
        if (v == null) return fallback;
        return Integer.parseInt(v);
    }

    public String getStringProperty(String name, String fallback) {
        String v = getProperty(name);
        if (v == null) return fallback;
        return v;
    }
}