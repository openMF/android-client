/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

@Table(database = MifosDatabase.class)
@ModelContainer
public class PaymentTypeOption extends MifosBaseModel implements Comparable<PaymentTypeOption>,
        Parcelable {


    @SerializedName("id")
    @PrimaryKey
    Integer id;

    @SerializedName("name")
    @Column
    String name;

    @SerializedName("description")
    @Column
    String description;

    @SerializedName("isCashPayment")
    @Column
    Boolean isCashPayment;

    @SerializedName("position")
    @Column
    Integer position;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getCashPayment() {
        return isCashPayment;
    }

    public void setCashPayment(Boolean cashPayment) {
        isCashPayment = cashPayment;
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

    @Override
    public String toString() {
        return "PaymentTypeOption{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isCashPayment=" + isCashPayment +
                ", position=" + position +
                '}';
    }

    public PaymentTypeOption() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeValue(this.isCashPayment);
        dest.writeValue(this.position);
    }

    protected PaymentTypeOption(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.description = in.readString();
        this.isCashPayment = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.position = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<PaymentTypeOption> CREATOR = new Creator<PaymentTypeOption>() {
        @Override
        public PaymentTypeOption createFromParcel(Parcel source) {
            return new PaymentTypeOption(source);
        }

        @Override
        public PaymentTypeOption[] newArray(int size) {
            return new PaymentTypeOption[size];
        }
    };
}
