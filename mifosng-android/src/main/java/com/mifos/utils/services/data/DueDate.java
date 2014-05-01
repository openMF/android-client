package com.mifos.utils.services.data;

import com.google.gson.Gson;

public class DueDate {
	 public int day;
	  
	  
	      @Override
	      public String toString()
	      {
	          return new Gson().toJson(this);
	      }
}
