/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.collectionsheet;

import com.mifos.objects.Currency;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class LoadProducts {

    private int id;
    private String name;
    private boolean includeInBorrowerCycle;
    private boolean useBorrowerCycle;
    private Currency currency;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isUseBorrowerCycle() {
        return useBorrowerCycle;
    }

    public void setUseBorrowerCycle(boolean useBorrowerCycle) {
        this.useBorrowerCycle = useBorrowerCycle;
    }

    public boolean isIncludeInBorrowerCycle() {
        return includeInBorrowerCycle;
    }

    public void setIncludeInBorrowerCycle(boolean includeInBorrowerCycle) {
        this.includeInBorrowerCycle = includeInBorrowerCycle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "LoadProducts{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", includeInBorrowerCycle=" + includeInBorrowerCycle +
                ", useBorrowerCycle=" + useBorrowerCycle +
                ", currency=" + currency +
                '}';
    }
}
