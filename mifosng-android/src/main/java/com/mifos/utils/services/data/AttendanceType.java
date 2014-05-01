package com.mifos.utils.services.data;

import com.google.gson.Gson;

public class AttendanceType {
	               public int attendanceTypeId;
	     public String code;
	     public String value;
	    
	     @Override
	     public String toString()
	     {
	         return new Gson().toJson(this);
	     }

    public com.mifos.objects.db.AttendanceType getData(){
        com.mifos.objects.db.AttendanceType attendanceType = new com.mifos.objects.db.AttendanceType();
        attendanceType.setCode(this.code);
        attendanceType.setAttendanceTypeId(this.attendanceTypeId);
        attendanceType.setValue(this.value);

        return attendanceType;
    }
}
