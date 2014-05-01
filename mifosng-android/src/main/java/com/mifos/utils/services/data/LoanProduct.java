package com.mifos.utils.services.data;

import com.google.gson.Gson;

public class LoanProduct {
	public int id;
	     public String name;
	     public boolean includeInBorrowerCycle;
	     public boolean useBorrowerCycle;
	     public Currency currency;
	     public int  principalVariationsForBorrowerCycle[];
	     public int interestRateVariationsForBorrowerCycle[];
	     public int numberOfRepaymentVariationsForBorrowerCycle[];
	 
	 
	     @Override
	     public String toString()
	     {
	         return new Gson().toJson(this);
	     }
}
