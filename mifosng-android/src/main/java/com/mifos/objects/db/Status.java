package com.mifos.objects.db;


import com.google.gson.Gson;
import com.orm.SugarRecord;

public class Status extends SugarRecord<Status> {
    private String code;
    private String value;

    /* public boolean isNew() {
         long count = Select.from(Status.class).where(Condition.prop("id").eq(id)).count();
         return count == 0;
     }*/
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
