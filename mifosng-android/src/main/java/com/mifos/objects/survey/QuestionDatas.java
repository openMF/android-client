package com.mifos.objects.survey;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nasim Banu on 15-Jan-2016.
 */
public class QuestionDatas {
    private String componentKey;

    private Integer id;
    private String key;
    private String text;
    private String description;
    private Integer sequenceNo;
    private List<ResponseDatas> responseDatas = new ArrayList<ResponseDatas>();

    public List<ResponseDatas> getResponseDatas() {
        return responseDatas;
    }

    public void setResponseDatas(List<ResponseDatas> responseDatas) {
        this.responseDatas = responseDatas;
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
}
