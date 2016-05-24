package com.mifos.objects.templates.clients;

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

import com.google.gson.annotations.SerializedName;

/**
 * Created by rajan on 13/3/16.
 */
public class Options {

    private int id;
    private String name;
    private int position;
    private String description;

    @SerializedName("isActive")
    private boolean is_Active;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return is_Active;
    }

    public void setIsActive(boolean isActive) {
        this.is_Active = isActive;
    }

    @Override
    public String toString() {
        return "Options{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", description='" + description + '\'' +
                ", isActive=" + is_Active +
                '}';
    }
}
