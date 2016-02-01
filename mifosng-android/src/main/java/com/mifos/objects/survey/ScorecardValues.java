/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */


package com.mifos.objects.survey;

/**
 * Created by Nasim Banu on 28,January,2016.
 */
public class ScorecardValues {
    private Integer questionId;
    private Integer responseId;
    private Integer value;

    public ScorecardValues() {
        super();
    }

    public ScorecardValues(final Integer questionId, final Integer responseId,
                           final Integer value) {
        super();
        this.questionId = questionId;
        this.responseId = responseId;
        this.value = value;
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
}
