/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.collectionsheet;

import java.util.List;

/**
 * Created by ishankhanna on 18/07/14.
 */
public class CollectionSheet {

    private List<Integer> dueDate;
    private LoadProducts loanProducts;
    private List<Groups> groups;
    private EntityType attendanceTypeOptions;

    public List<Integer> getDueDate() {
        return dueDate;
    }

    public void setDueDate(List<Integer> dueDate) {
        this.dueDate = dueDate;
    }

    public LoadProducts getLoanProducts() {
        return loanProducts;
    }

    public void setLoanProducts(LoadProducts loanProducts) {
        this.loanProducts = loanProducts;
    }

    public List<Groups> getGroups() {
        return groups;
    }

    public void setGroups(List<Groups> groups) {
        this.groups = groups;
    }

    public EntityType getAttendanceTypeOptions() {
        return attendanceTypeOptions;
    }

    public void setAttendanceTypeOptions(EntityType attendanceTypeOptions) {
        this.attendanceTypeOptions = attendanceTypeOptions;
    }

    @Override
    public String toString() {
        return "CollectionSheet{" +
                "dueDate=" + dueDate +
                ", loanProducts=" + loanProducts +
                ", groups=" + groups +
                ", attendanceTypeOptions=" + attendanceTypeOptions +
                '}';
    }
}
