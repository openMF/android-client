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
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nasim Banu on 27,January,2016.
 */
@Table(database = MifosDatabase.class)
@ModelContainer
public class Survey extends MifosBaseModel implements Parcelable {

    @PrimaryKey
    int id;

    @Column
    String key;

    @Column
    String name;

    @Column
    String description;

    @Column
    transient boolean sync;

    @Column
    String countryCode;

    List<QuestionDatas> questionDatas = new ArrayList<QuestionDatas>();

    List<ComponentDatas> componentDatas  = new ArrayList<ComponentDatas>();

    public List<ComponentDatas> getComponentDatas() {
        return componentDatas;
    }

    public void setComponentDatas(List<ComponentDatas> componentDatas) {
        this.componentDatas = componentDatas;
    }

    public List<QuestionDatas> getQuestionDatas() {
        return questionDatas;
    }

    public void setQuestionDatas(List<QuestionDatas> questionDatas) {
        this.questionDatas = questionDatas;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", questionDatas=" + questionDatas +
                ", componentDatas=" + componentDatas +
                '}';
    }

    public Survey() {
    }

    protected Survey(Parcel in) {
        this.id = in.readInt();
        this.key = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.countryCode = in.readString();
        this.questionDatas = in.createTypedArrayList(QuestionDatas.CREATOR);
        this.componentDatas = in.createTypedArrayList(ComponentDatas.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.key);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.countryCode);
        dest.writeTypedList(this.questionDatas);
        dest.writeTypedList(this.componentDatas);
    }

    public static final Parcelable.Creator<Survey> CREATOR = new Parcelable.Creator<Survey>() {
        @Override
        public Survey createFromParcel(Parcel source) {
            return new Survey(source);
        }

        @Override
        public Survey[] newArray(int size) {
            return new Survey[size];
        }
    };
}
