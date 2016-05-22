/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.survey;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nasim Banu on 27,January,2016.
 */
public class Survey {

    private int id;
    private String key;
    private String name;
    private String description;
    private String countryCode;
    private List<QuestionDatas> questionDatas = new ArrayList<QuestionDatas>();
    private List<ComponentDatas> componentDatas;

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
}
