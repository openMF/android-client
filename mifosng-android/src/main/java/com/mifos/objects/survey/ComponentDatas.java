/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.survey;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.api.local.MifosBaseModel;
import com.mifos.api.local.MifosDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

/**
 * Created by Rajan Maurya on 28/3/16.
 */
@Table(database = MifosDatabase.class)
public class ComponentDatas extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    Integer id;

    @Column
    String key;

    @Column
    String text;

    @Column
    String description;

    @Column
    int sequenceNo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    @Override
    public String toString() {
        return "ComponentDatas{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", text='" + text + '\'' +
                ", description='" + description + '\'' +
                ", sequenceNo=" + sequenceNo +
                '}';
    }

    public ComponentDatas() {
    }

    protected ComponentDatas(Parcel in) {
        this.id = in.readInt();
        this.key = in.readString();
        this.text = in.readString();
        this.description = in.readString();
        this.sequenceNo = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.key);
        dest.writeString(this.text);
        dest.writeString(this.description);
        dest.writeInt(this.sequenceNo);
    }

    public static final Parcelable.Creator<ComponentDatas> CREATOR =
            new Parcelable.Creator<ComponentDatas>() {
        @Override
        public ComponentDatas createFromParcel(Parcel source) {
            return new ComponentDatas(source);
        }

        @Override
        public ComponentDatas[] newArray(int size) {
            return new ComponentDatas[size];
        }
    };
}
