package com.mifos.utils.services.data;

import com.google.gson.Gson;

public class AttendanceTypeOptions {
		  public int id;
	      public String code;
	      public String value;
	      @Override
	      public String toString()
	      {
	          return new Gson().toJson(this);
	      }
}
