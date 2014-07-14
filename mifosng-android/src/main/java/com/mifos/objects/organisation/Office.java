package com.mifos.objects.organisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ishankhanna on 14/07/14.
 */
public class Office {

    private Integer id;
    private String externalId;
    private String name;
    private String nameDecorated;
    private List<Integer> openingDate = new ArrayList<Integer>();
    private HashMap<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameDecorated() {
        return nameDecorated;
    }

    public void setNameDecorated(String nameDecorated) {
        this.nameDecorated = nameDecorated;
    }

    public List<Integer> getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(List<Integer> openingDate) {
        this.openingDate = openingDate;
    }

    public HashMap<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(HashMap<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    public String toString() {
        return "Office{" +
                "id=" + id +
                ", externalId=" + externalId +
                ", name='" + name + '\'' +
                ", nameDecorated='" + nameDecorated + '\'' +
                ", openingDate=" + openingDate +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
