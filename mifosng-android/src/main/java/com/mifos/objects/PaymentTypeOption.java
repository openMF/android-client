/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects;

import com.google.gson.annotations.Expose;

public class PaymentTypeOption implements Comparable<PaymentTypeOption> {

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

    /**
     * Compares two payment type options on the basis
     * of their position specified.
     *
     * @param another
     * @return
     */

    @Override
    public int compareTo(PaymentTypeOption another) {

        if (this.position < another.position) {
            return -1;
        } else if (this.position > another.position) {
            return 1;
        } else {
            return 0;
        }
    }
}
