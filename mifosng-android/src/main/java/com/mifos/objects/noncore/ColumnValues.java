/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.noncore;

/**
 * Created by ishankhanna on 16/06/14.
 */
public class ColumnValues {

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
}
