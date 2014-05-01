package com.mifos.utils.services.data;

import com.google.gson.Gson;

public class Client {
	 public int clientId;
	      public String clientName;
	      public Loan[] loans;
	      public AttendanceType attendanceType;
	      @Override
	      public String toString()
	      {
	          return new Gson().toJson(this);
	      }
}
