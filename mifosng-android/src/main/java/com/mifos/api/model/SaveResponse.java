/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api.model;


import com.google.gson.Gson;

public class SaveResponse {
    public int groupId;
    public int resourceId;
    public Changes changes;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
