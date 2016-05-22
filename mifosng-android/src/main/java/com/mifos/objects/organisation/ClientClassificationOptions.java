/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.objects.organisation;

/**
 * Created by nkiboi on 12/15/2015.
 */
public class ClientClassificationOptions {
    private Integer id;
    private String name;

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


    @Override
    public String toString() {
        return "clientClassificationOptions{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
