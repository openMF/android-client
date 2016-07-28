/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects;

import android.os.Parcel;
import android.os.Parcelable;

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


    @PrimaryKey
    Integer id;

    @Column
    String name;

    @Column
    Integer position;

    //Payment Type Like Loan, Saving or Reoccurring
    @Column
    String templateType;

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

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

    @Override
    public String toString() {
        return "PaymentTypeOption{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position=" + position +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeValue(this.position);
    }

    public PaymentTypeOption() {
    }

    protected PaymentTypeOption(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.position = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<PaymentTypeOption> CREATOR = new Parcelable
            .Creator<PaymentTypeOption>() {
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
