/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.db;

import com.google.gson.Gson;

import java.util.List;

public class CollectionSheet {

    public int[] dueDate;

    public List<MifosGroup> groups;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public int[] getDueDate() {
        return dueDate;
    }

    public void setDueDate(int[] dueDate) {
        this.dueDate = dueDate;
    }

    public List<MifosGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<MifosGroup> groups) {
        this.groups = groups;
    }
}
