package com.mifos.utils.services.data;

import com.google.gson.Gson;

public class Currency {
	public String code;
	     public String name;
	     public int decimalPlaces;
	     public int inMultiplesOf;
	     public String displaySymbol;
	     public String nameCode;
	     public String displayLabel;
	 
	     @Override
	     public String toString()
	     {
	         return new Gson().toJson(this);
	     }
}
