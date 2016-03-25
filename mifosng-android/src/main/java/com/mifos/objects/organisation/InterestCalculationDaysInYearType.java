/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.objects.organisation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nkiboi on 12/15/2015.
 */
public class InterestCalculationDaysInYearType {
    private Integer id;
    private String code;
    private String value;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
        return "interestCalculationDaysInYearTypeOptions{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
