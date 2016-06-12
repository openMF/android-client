/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects;

import java.util.HashMap;
import java.util.Map;

public class Currency {

    private String code;
    private String name;
    private Integer decimalPlaces;
    private Integer inMultiplesOf;
    private String displaySymbol;
    private String nameCode;
    private String displayLabel;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public Integer getInMultiplesOf() {
        return inMultiplesOf;
    }

    public void setInMultiplesOf(Integer inMultiplesOf) {
        this.inMultiplesOf = inMultiplesOf;
    }

    public String getDisplaySymbol() {
        return displaySymbol;
    }

    public void setDisplaySymbol(String displaySymbol) {
        this.displaySymbol = displaySymbol;
    }

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", decimalPlaces=" + decimalPlaces +
                ", inMultiplesOf=" + inMultiplesOf +
                ", displaySymbol='" + displaySymbol + '\'' +
                ", nameCode='" + nameCode + '\'' +
                ", displayLabel='" + displayLabel + '\'' +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
