package com.mifos.objects.accounts.savings;


/**
 * Created by nkiboi on 12/15/2015.
 */
public class FieldOfficerOptions {
    private Integer id;
    private String firstname;
    private String lastname;
    private String displayName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "interestCompoundingPeriodType{" +
                "id=" + id +
                ", lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}

