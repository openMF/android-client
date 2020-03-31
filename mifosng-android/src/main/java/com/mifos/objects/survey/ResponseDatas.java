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
 * Created by Nasim Banu on 27,January,2016.
 */
@Table(database = MifosDatabase.class)
public class ResponseDatas extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    int id;

    @Column
    transient int questionId;

    @Column
    String text;

    @Column
    int sequenceNo;

    @Column
    int value;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getResponseId() {
        return id;
    }

    public void setResponseId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ResponseDatas{" +
                "text='" + text + '\'' +
                ", sequenceNo=" + sequenceNo +
                ", value=" + value +
                ", id=" + id +
                '}';
    }

    public ResponseDatas() {
    }

    protected ResponseDatas(Parcel in) {
        this.id = in.readInt();
        this.text = in.readString();
        this.sequenceNo = in.readInt();
        this.value = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.text);
        dest.writeInt(this.sequenceNo);
        dest.writeInt(this.value);
    }

    public static final Parcelable.Creator<ResponseDatas> CREATOR =
            new Parcelable.Creator<ResponseDatas>() {
        @Override
        public ResponseDatas createFromParcel(Parcel source) {
            return new ResponseDatas(source);
        }

        @Override
        public ResponseDatas[] newArray(int size) {
            return new ResponseDatas[size];
        }
    };
}

