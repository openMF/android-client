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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nasim Banu on 27,January,2016.
 */
@Table(database = MifosDatabase.class)
public class QuestionDatas extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    Integer id;

    @Column
    transient int surveyId;

    @Column
    String componentKey;

    @Column
    String key;

    @Column
    String text;

    @Column
    String description;

    @Column
    int sequenceNo;

    List<ResponseDatas> responseDatas = new ArrayList<ResponseDatas>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComponentKey() {
        return componentKey;
    }

    public void setComponentKey(String componentKey) {
        this.componentKey = componentKey;
    }

    public List<ResponseDatas> getResponseDatas() {
        return responseDatas;
    }

    public void setResponseDatas(List<ResponseDatas> responseDatas) {
        this.responseDatas = responseDatas;
    }

    public int getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(int surveyId) {
        this.surveyId = surveyId;
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

    public int getQuestionId() {
        return id;
    }

    public void setQuestionId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "QuestionDatas{" +
                "componentKey='" + componentKey + '\'' +
                ", id=" + id +
                ", key='" + key + '\'' +
                ", text='" + text + '\'' +
                ", description='" + description + '\'' +
                ", sequenceNo=" + sequenceNo +
                ", responseDatas=" + responseDatas +
                '}';
    }

    public QuestionDatas() {
    }

    protected QuestionDatas(Parcel in) {
        this.id = in.readInt();
        this.componentKey = in.readString();
        this.key = in.readString();
        this.text = in.readString();
        this.description = in.readString();
        this.sequenceNo = in.readInt();
        this.responseDatas = in.createTypedArrayList(ResponseDatas.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.componentKey);
        dest.writeString(this.key);
        dest.writeString(this.text);
        dest.writeString(this.description);
        dest.writeInt(this.sequenceNo);
        dest.writeTypedList(this.responseDatas);
    }

    public static final Parcelable.Creator<QuestionDatas> CREATOR =
            new Parcelable.Creator<QuestionDatas>() {
        @Override
        public QuestionDatas createFromParcel(Parcel source) {
            return new QuestionDatas(source);
        }

        @Override
        public QuestionDatas[] newArray(int size) {
            return new QuestionDatas[size];
        }
    };
}
