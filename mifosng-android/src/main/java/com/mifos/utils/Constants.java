/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.utils;

/**
 * Created by satya on 13/04/14.
 */
public class Constants {

    public static final String INSTANCE_URL_KEY = "instanceURL";

    public static final String INSTANCE_DOMAIN_KEY = "instanceDomain";

    public static final String INSTANCE_PORT_KEY = "instancePort";

    public static final String TENANT_IDENTIFIER_KEY = "tenant identifier";

    public static final String PROTOCOL_HTTP = "http://";

    public static final String PROTOCOL_HTTPS = "https://";

    public static final String API_PATH = "/mifosng-provider/api/v1";


    /**
     * Entity Type, Like Clients, Groups, Staff, Loans, Savings and Client Identifiers
     */
    public static final String ENTITY_TYPE_CLIENTS = "clients";

    public static final String ENTITY_TYPE_GROUPS = "groups";

    public static final String ENTITY_TYPE_LOANS = "loans";

    public static final String ENTITY_TYPE_SAVINGS = "savings";

    public static final String ENTITY_TYPE_STAFF = "staff";

    public static final String ENTITY_TYPE_CLIENT_IDENTIFIERS = "client_identifiers";

    //Search Entities
    public static final String SEARCH_ENTITY_CLIENT = "CLIENT";
    public static final String SEARCH_ENTITY_GROUP = "GROUP";
    public static final String SEARCH_ENTITY_LOAN = "LOAN";
    public static final String SEARCH_ENTITY_SAVING = "SAVING";
    public static final String SEARCH_ENTITY_CENTER = "CENTER";

    public static final String CLIENT_NAME = "clientName";

    public static final String CLIENT_ID = "clientId";

    public static final String ID = "id";

    public static final String CLIENT = "Client";

    public static final String CLIENTS = "clients";

    public static final String LOAN_ACCOUNT_NUMBER = "loanAccountNumber";

    public static final String LOAN_PAYMENT_TYPE_OPTIONS = "LoanPaymentTypeOptions";

    public static final String LOAN_SUMMARY = "loanWithAssociation";

    public static final String SAVINGS_ACCOUNT_NUMBER = "savingsAccountNumber";

    public static final String SAVINGS_ACCOUNT_ID = "savingsAccountId";

    public static final String SAVINGS_ACCOUNT_TYPE = "savingsAccountType";

    public static final String SAVINGS_ACCOUNT_TRANSACTION_TYPE = "transactionType";

    public static final String SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT = "Deposit";

    public static final String SAVINGS_ACCOUNT_TRANSACTION_WITHDRAWAL = "Withdrawal";

    public static final String DATA_TABLE_REGISTERED_NAME = "dataTableRegisteredName";

    public static final String CENTER_ID = "centerId";

    public static final String GROUP_ID = "groupId";

    public static final String GROUPS = "groups";

    public static final String GROUP_ACCOUNT = "isGroupAccount";

    public static final String CENTER = "center";

    public static final String ENTITY_TYPE = "entityType";

    public static final String ENTITY_ID = "entityId";

    public static final String DOCUMENT_ACTIONS =  "document_actions";

    public static final String DOCUMENT = "document_title";

    public static final String DOCUMENT_DESCRIPTION = "document_description";

    public static final String CHARGE_ID = "chargeId";

    public static final String DATE_OF_COLLECTION = "dateOfCollection";

    public static final String REPAYMENT_DATE = "repaymentDate";

    public static final String DATE_FORMAT = "dateFormat";

    public static final String CALENDAR_INSTANCE_ID = "calendarInstanceId";

    public static final String LOCALE = "locale";

    public static final String TRANSACTIONS = "transactions";

    public static final String SURVEYS = "surveys";

    public static final String ANSWERS = "answers";

    public static final String QUESTION_DATA = "question data";

    public static final String IS_A_PARENT_FRAGMENT = "isAParentFragment";

    public static final String STOP_TRACKING = "stop_tracking";

    public static final String SERVICE_STATUS = "service_status";

    public static final String DATA_TABLE_NAME = "data_table_name";
    
    public static final int DIALOG_FRAGMENT = 1;

    public static final String ACTIVATE_CLIENT = "activate_client";

    public static final String ACTIVATE_CENTER = "activate_center";

    public static final String ACTIVATE_GROUP = "activate_group";

    public static final String ACTIVATE_TYPE = "activation_type";

    public static final String INTIAL_LOGIN = "initial_login";

    //This needs to be 8 bits because validateRequestPermissionsRequestCode
    // in FragmentActivity requires requestCode to be of 8 bits, meaning the range is from 0 to 255.
    public static final int REQUEST_PERMISSION_SETTING = 254;


    /**
     * PERMISSIONS_........ is an app-defined int constant of RunTime Permission . The callback
     * method gets the result of the request.
     */
    public static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    public static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3;

    /**
     * String Constant of Permissions in SharedPreference
     */
    public static final String READ_EXTERNAL_STORAGE_STATUS = "read_external_storage_status";
    public static final String WRITE_EXTERNAL_STORAGE_STATUS = "write_external_storage_status";
    public static final String ACCESS_FINE_LOCATION_STATUS = "access_fine_location_status";

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


    public static final String DATA_TABLE_NAME_CLIENT = "m_client";
    public static final String DATA_TABLE_NAME_SAVINGS = "m_savings_account";
    public static final String DATA_TABLE_NAME_LOANS = "m_loan";
    public static final String DATA_TABLE_NAME_GROUP = "m_group";

    /**
     * User Logged In Status
     * 0 for Online and 1 for Offline
     */
    public static final int USER_ONLINE = 0;
    public static final int USER_OFFLINE = 1;

    /**
     * Constants to determine in the generic DataTableListFragment, the type of query that
     * has to be forwarded after showing the datatables and adding the values
     * to the corresponding payload.
     */
    public static final int CREATE_CLIENT = 3001;
    public static final int CLIENT_LOAN = 3002;
    public static final int GROUP_LOAN = 3003;

    /**
     * Constant to identify whether Simple Collection Sheet fragment has to be opened
     * or the Individual Collection Sheet.
     */
    public static final String COLLECTION_TYPE = "collection_type";
    public static final String EXTRA_COLLECTION_INDIVIDUAL = "individual";
    public static final String EXTRA_COLLECTION_COLLECTION = "collection";

    /**
     * Constant to identify the various parts of notification
     */
    public static final String NOTIFICATION_SUBJECT = "subject";
    public static final String NOTIFICATION_OBJECT = "object";
    public static final String NOTIFICATION_ACTION = "action";
    public static final String NOTIFICATION_ACTOR = "actor";
}
