/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.objects.survey;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class ScorecardValues implements Parcelable {

    public static final Parcelable.Creator<ScorecardValues> CREATOR = new Parcelable
            .Creator<ScorecardValues>() {
        @Override
        public ScorecardValues createFromParcel(Parcel source) {
            return new ScorecardValues(source);
        }

        @Override
        public ScorecardValues[] newArray(int size) {
            return new ScorecardValues[size];
        }
    };
    private Integer questionId;
    private Integer responseId;
    private Integer value;

    public ScorecardValues() {
        super();
    }

    public ScorecardValues(final Integer questionId, final Integer responseId, final Integer
            value) {
        super();
        this.questionId = questionId;
        this.responseId = responseId;
        this.value = value;
    }

    protected ScorecardValues(Parcel in) {
        this.questionId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.responseId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.value = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getResponseId() {
        return responseId;
    }

    public void setResponseId(Integer responseId) {
        this.responseId = responseId;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ScorecardValues{" +
                "questionId=" + questionId +
                ", responseId=" + responseId +
                ", value=" + value +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.questionId);
        dest.writeValue(this.responseId);
        dest.writeValue(this.value);
    }
}
