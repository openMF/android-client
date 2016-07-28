/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.noncore;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ishankhanna on 16/06/14.
 */
public class ColumnValue implements Parcelable {

    Integer id;
    String value;
    Integer score;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "ColumnValues{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", score=" + score +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.value);
        dest.writeValue(this.score);
    }

    public ColumnValue() {
    }

    protected ColumnValue(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.value = in.readString();
        this.score = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<ColumnValue> CREATOR = new Parcelable
            .Creator<ColumnValue>() {
        @Override
        public ColumnValue createFromParcel(Parcel source) {
            return new ColumnValue(source);
        }

        @Override
        public ColumnValue[] newArray(int size) {
            return new ColumnValue[size];
        }
    };
}
