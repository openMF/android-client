package com.mifos.objects.db;


import com.google.gson.Gson;
import com.orm.SugarRecord;

public class EntityType extends SugarRecord<EntityType> {
    private String code;
    private String value;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
