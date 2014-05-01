package com.mifos.utils.services.data;

import com.google.gson.Gson;
import com.orm.SugarRecord;

public class Center extends SugarRecord<Center>
{
	     public int[] dueDate;
	     public LoanProduct loanProducts[];
	     public MifosGroup groups[];
	     public AttendanceTypeOptions attendanceTypeOptions[];
	 
	     @Override
	     public String toString()
	     {
	         return new Gson().toJson(this);
	     }
}
