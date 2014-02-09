package com.mifos.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ishankhanna on 08/02/14.
 */
public class Client {

    private int id;
    private String accountNo;
    private Status status;
    private boolean active;
    private List<Integer> activationDate = new ArrayList<Integer>();
    private String fullname;
    private String displayName;
    private int officeId;
    private String officeName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Integer> getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(List<Integer> activationDate) {
        this.activationDate = activationDate;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getOfficeId() {
        return officeId;
    }

    public void setOfficeId(int officeId) {
        this.officeId = officeId;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    @Override
    public String toString() {
        return "PageItem{" +
                "id=" + id +
                ", accountNo='" + accountNo + '\'' +
                ", status=" + status.toString() +
                ", active=" + active +
                ", activationDate=" + activationDate +
                ", fullname='" + fullname + '\'' +
                ", displayName='" + displayName + '\'' +
                ", officeId=" + officeId +
                ", officeName='" + officeName + '\'' +
                '}';
    }



}
