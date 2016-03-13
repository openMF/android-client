package com.mifos.objects.templates.savings;

import com.mifos.objects.InterestType;

/**
 * Created by rajan on 13/3/16.
 */
public class AccountOptions {

	private Integer id;
	private String name;
	private Integer glCode;
	private Boolean disabled;
	private Boolean manualEntriesAllowed;
	private InterestType type;
	private InterestType usage;
	private String nameDecorated;
	private TagId tagId;


	public class TagId{
		private Integer id;
	}
}
