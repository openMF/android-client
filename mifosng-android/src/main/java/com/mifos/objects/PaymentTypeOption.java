
package com.mifos.objects;

import com.google.gson.annotations.Expose;

public class PaymentTypeOption {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private Integer position;

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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

}
