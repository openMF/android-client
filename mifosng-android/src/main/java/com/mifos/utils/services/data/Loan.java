package com.mifos.utils.services.data;

import com.google.gson.Gson;

public class Loan {
	 public int disbursementAmount;
	      public int interestDue;
	      public int interestPaid;
	      public int loanId;
	      public int chargesDue;
	      public int totalDue;
	      public int principalDue;
	      public int principalPaid;
	      public String accountId;
	      public int accountStatusId;
	      public String productShortName;
	      public int productId;
	      public Currency currency;
	      @Override
	      public String toString()
	      {
	          return new Gson().toJson(this);
	      }
}
