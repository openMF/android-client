/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.survey;

/**
 * Created by Rajan Maurya on 28/3/16.
 */
public class ComponentDatas {

    private int id;
    private String key;
    private String text;
    private String description;
    private int sequenceNo;

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
}
