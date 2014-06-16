package com.mifos.objects.system;

/**
 * Created by ishankhanna on 16/06/14.
 */
public class Code {

    Integer id;
    String name;
    Boolean isSystemDefined;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsSystemDefined() {
        return isSystemDefined;
    }

    public void setIsSystemDefined(Boolean isSystemDefined) {
        this.isSystemDefined = isSystemDefined;
    }
}
