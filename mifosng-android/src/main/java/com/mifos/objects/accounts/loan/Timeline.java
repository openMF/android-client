
package com.mifos.objects.accounts.loan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Timeline {

    private List<Integer> submittedOnDate = new ArrayList<Integer>();
    private String submittedByUsername;
    private String submittedByFirstname;
    private String submittedByLastname;
    private List<Integer> approvedOnDate = new ArrayList<Integer>();
    private String approvedByUsername;
    private String approvedByFirstname;
    private String approvedByLastname;
    private List<Integer> expectedDisbursementDate = new ArrayList<Integer>();
    private List<Integer> actualDisbursementDate = new ArrayList<Integer>();
    private String disbursedByUsername;
    private String disbursedByFirstname;
    private String disbursedByLastname;
    private List<Integer> closedOnDate = new ArrayList<Integer>();
    private List<Integer> expectedMaturityDate = new ArrayList<Integer>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public List<Integer> getSubmittedOnDate() {
        return submittedOnDate;
    }

    public void setSubmittedOnDate(List<Integer> submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }

    public String getSubmittedByUsername() {
        return submittedByUsername;
    }

    public void setSubmittedByUsername(String submittedByUsername) {
        this.submittedByUsername = submittedByUsername;
    }

    public String getSubmittedByFirstname() {
        return submittedByFirstname;
    }

    public void setSubmittedByFirstname(String submittedByFirstname) {
        this.submittedByFirstname = submittedByFirstname;
    }

    public String getSubmittedByLastname() {
        return submittedByLastname;
    }

    public void setSubmittedByLastname(String submittedByLastname) {
        this.submittedByLastname = submittedByLastname;
    }

    public List<Integer> getApprovedOnDate() {
        return approvedOnDate;
    }

    public void setApprovedOnDate(List<Integer> approvedOnDate) {
        this.approvedOnDate = approvedOnDate;
    }

    public String getApprovedByUsername() {
        return approvedByUsername;
    }

    public void setApprovedByUsername(String approvedByUsername) {
        this.approvedByUsername = approvedByUsername;
    }

    public String getApprovedByFirstname() {
        return approvedByFirstname;
    }

    public void setApprovedByFirstname(String approvedByFirstname) {
        this.approvedByFirstname = approvedByFirstname;
    }

    public String getApprovedByLastname() {
        return approvedByLastname;
    }

    public void setApprovedByLastname(String approvedByLastname) {
        this.approvedByLastname = approvedByLastname;
    }

    public List<Integer> getExpectedDisbursementDate() {
        return expectedDisbursementDate;
    }

    public void setExpectedDisbursementDate(List<Integer> expectedDisbursementDate) {
        this.expectedDisbursementDate = expectedDisbursementDate;
    }

    public List<Integer> getActualDisbursementDate() {
        return actualDisbursementDate;
    }

    public void setActualDisbursementDate(List<Integer> actualDisbursementDate) {
        this.actualDisbursementDate = actualDisbursementDate;
    }

    public String getDisbursedByUsername() {
        return disbursedByUsername;
    }

    public void setDisbursedByUsername(String disbursedByUsername) {
        this.disbursedByUsername = disbursedByUsername;
    }

    public String getDisbursedByFirstname() {
        return disbursedByFirstname;
    }

    public void setDisbursedByFirstname(String disbursedByFirstname) {
        this.disbursedByFirstname = disbursedByFirstname;
    }

    public String getDisbursedByLastname() {
        return disbursedByLastname;
    }

    public void setDisbursedByLastname(String disbursedByLastname) {
        this.disbursedByLastname = disbursedByLastname;
    }

    public List<Integer> getClosedOnDate() {
        return closedOnDate;
    }

    public void setClosedOnDate(List<Integer> closedOnDate) {
        this.closedOnDate = closedOnDate;
    }

    public List<Integer> getExpectedMaturityDate() {
        return expectedMaturityDate;
    }

    public void setExpectedMaturityDate(List<Integer> expectedMaturityDate) {
        this.expectedMaturityDate = expectedMaturityDate;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
