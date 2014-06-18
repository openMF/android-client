package com.mifos.utils;

import android.content.Context;

/**
 * Created by satya on 13/04/14.
 */
public class Constants {

    public static Context applicationContext;

    public static final String CLIENT_NAME = "clientName";

    public static final String CLIENT_ID = "clientId";

    public static final String LOAN_ACCOUNT_NUMBER = "loanAccountNumber";

    public static final String LOAN_PRODUCT_NAME = "loanProductName";

    public static final String AMOUNT_IN_ARREARS = "amountInArrears";

    public static final String AMOUNT_DUE = "amountDue";

    public static final String FEES_DUE = "feesDue";

    public static final String SAVINGS_ACCOUNT_NUMBER = "savingsAccountNumber";

    public static final String SAVINGS_ACCOUNT_TRANSACTION_TYPE = "transactionType";

    public static final String SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT = "Deposit";

    public static final String SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL = "Withdrawal";

    public static final String DATA_TABLE_REGISTERED_NAME = "dataTableRegisteredName";

    /**
     * Constants to identify which Data Tables have to be shown
     */
    public static final int DATA_TABLE_CLIENTS = 2001;
    public static final int DATA_TABLE_LOANS = 2002;
    public static final int DATA_TABLES_SAVINGS_ACCOUNTS = 2003;

    /**
     * Constants to for Data Table Menu Names
     */
    public static final String DATA_TABLE_CLIENTS_NAME = "More Client Info";
    public static final String DATA_TABLE_LOAN_NAME = "More Loan Info";
    public static final String DATA_TABLE_SAVINGS_ACCOUNTS_NAME = "More Savings Account Info";


}
