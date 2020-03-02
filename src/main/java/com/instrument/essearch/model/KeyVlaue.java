package com.instrument.essearch.model;

import java.io.Serializable;

public class KeyVlaue implements Serializable
{
    private String Key ;
    private String Value ;

    public String getKey() {
        return Key;
    }
    public void setKey(String key) {
        Key = key;
    }
    public String getValue() {
        return Value;
    }
    public void setValue(String value) {
        Value = value;
    }
}
