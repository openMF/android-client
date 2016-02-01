/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.objects.survey;

/**
 * Created by Nasim Banu on 27,January,2016.
 */
public class ResponseDatas {
    private String text;
    private Integer sequenceNo;
    private int value;
    private int id;
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
