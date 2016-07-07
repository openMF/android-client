package com.mifos.objects.templates.clients;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by Rajan Maurya on 06/07/16.
 */
@Table(database = MifosDatabase.class, name = "ClientTemplateInterest")
@ModelContainer
public class InterestType extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    Integer id;

    @Column
    String code;

    @Column
    String value;

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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.code);
        dest.writeString(this.value);
    }

    public InterestType() {
    }

    protected InterestType(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.code = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<InterestType> CREATOR = new Parcelable
            .Creator<InterestType>() {
        @Override
        public InterestType createFromParcel(Parcel source) {
            return new InterestType(source);
        }

        @Override
        public InterestType[] newArray(int size) {
            return new InterestType[size];
        }
    };
}
