package com.mifos.objects.accounts.loan;

/**
 * Created by nellyk on 3/3/2016.
 */
public class PaymentTypeOptions {
    int id;
    String name;

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "id" + id + " name:" + name;
    }
}
