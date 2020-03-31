package com.mifos.objects.templates.clients;


/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by rajan on 13/3/16.
 */
@Table(database = MifosDatabase.class, name = "ClientTemplateOfficeOptions")
@ModelContainer
public class OfficeOptions extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    int id;

    @Column
    String name;

    @Column
    String nameDecorated;

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

    public String getNameDecorated() {
        return nameDecorated;
    }

    public void setNameDecorated(String nameDecorated) {
        this.nameDecorated = nameDecorated;
    }

    @Override
    public String toString() {
        return "OfficeOptions{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", nameDecorated='" + nameDecorated + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.nameDecorated);
    }

    public OfficeOptions() {
    }

    protected OfficeOptions(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.nameDecorated = in.readString();
    }

    public static final Parcelable.Creator<OfficeOptions> CREATOR = new Parcelable
            .Creator<OfficeOptions>() {
        @Override
        public OfficeOptions createFromParcel(Parcel source) {
            return new OfficeOptions(source);
        }

        @Override
        public OfficeOptions[] newArray(int size) {
            return new OfficeOptions[size];
        }
    };
}