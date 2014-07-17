
/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts.savings;

import java.util.HashMap;
import java.util.Map;

public class Status {

    private Integer id;
    private String code;
    private String value;
    private Boolean submittedAndPendingApproval;
    private Boolean approved;
    private Boolean rejected;
    private Boolean withdrawnByApplicant;
    private Boolean active;
    private Boolean closed;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Status withId(Integer id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Status withCode(String code) {
        this.code = code;
        return this;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Status withValue(String value) {
        this.value = value;
        return this;
    }

    public Boolean getSubmittedAndPendingApproval() {
        return submittedAndPendingApproval;
    }

    public void setSubmittedAndPendingApproval(Boolean submittedAndPendingApproval) {
        this.submittedAndPendingApproval = submittedAndPendingApproval;
    }

    public Status withSubmittedAndPendingApproval(Boolean submittedAndPendingApproval) {
        this.submittedAndPendingApproval = submittedAndPendingApproval;
        return this;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Status withApproved(Boolean approved) {
        this.approved = approved;
        return this;
    }

    public Boolean getRejected() {
        return rejected;
    }

    public void setRejected(Boolean rejected) {
        this.rejected = rejected;
    }

    public Status withRejected(Boolean rejected) {
        this.rejected = rejected;
        return this;
    }

    public Boolean getWithdrawnByApplicant() {
        return withdrawnByApplicant;
    }

    public void setWithdrawnByApplicant(Boolean withdrawnByApplicant) {
        this.withdrawnByApplicant = withdrawnByApplicant;
    }

    public Status withWithdrawnByApplicant(Boolean withdrawnByApplicant) {
        this.withdrawnByApplicant = withdrawnByApplicant;
        return this;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Status withActive(Boolean active) {
        this.active = active;
        return this;
    }

    public Boolean getClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    public Status withClosed(Boolean closed) {
        this.closed = closed;
        return this;
    }

    @Override
    public String toString() {
        return "Status{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", value='" + value + '\'' +
                ", submittedAndPendingApproval=" + submittedAndPendingApproval +
                ", approved=" + approved +
                ", rejected=" + rejected +
                ", withdrawnByApplicant=" + withdrawnByApplicant +
                ", active=" + active +
                ", closed=" + closed +
                ", additionalProperties=" + additionalProperties +
                '}';
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
