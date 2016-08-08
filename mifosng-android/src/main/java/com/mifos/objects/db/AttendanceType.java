/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.db;

import com.google.gson.Gson;

public class AttendanceType {
    private int attendanceTypeId;
    private String code;
    private String value;
    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getAttendanceTypeId() {
        return attendanceTypeId;
    }

    public void setAttendanceTypeId(int attendanceTypeId) {
        this.attendanceTypeId = attendanceTypeId;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
