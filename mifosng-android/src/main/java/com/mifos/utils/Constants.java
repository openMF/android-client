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

    public static final String LOCALE_EN = "en";

    public static final String DATE_FORMAT_LONG = "dd MMM yyyy";

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

    public static final String INDIVIDUAL_SHEET = "collection_sheet";

    public static final String DISBURSEMENT_DATE = "disbursement_date";

    public static final String TRANSACTION_DATE = "transaction_date";

    public static final String ADAPTER_POSITION = "adapter_position";

    public static final String PAYLOAD = "payload";

    public static final String PAYMENT_LIST = "payment_list";

    public static final String LOAN_AND_CLIENT = "loan_and_client_item";

    public static final String PAYMENT_OPTIONS = "payment_options";

    public static final String MEMBERS = "members";

    public static final String NEW = "NEW";

    public static final String SAVED = "SAVED";

    public static final String FILLNOW = "FillNow";



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
     * Constants related to RunReports
     */
    public static final String REPORT_NAME = "report_name";
    public static final String REPORT_ID = "report_id";
    public static final String CLIENT_REPORT_ITEM = "client_report_item";
    public static final String CLIENT_REPORT = "client_report";
    public static final String REPORT_CATEGORY = "report_category";
    public static final String LOAN = "Loan";
    public static final String ACCOUNTING = "Accounting";
    public static final String FUND = "Fund";
    public static final String SAVINGS = "Savings";
    public static final String LOAN_OFFICER_ID_SELECT = "loanOfficerIdSelectAll";
    public static final String LOAN_PRODUCT_ID_SELECT = "loanProductIdSelectAll";
    public static final String LOAN_PURPOSE_ID_SELECT = "loanPurposeIdSelectAll";
    public static final String FUND_ID_SELECT = "fundIdSelectAll";
    public static final String CURRENCY_ID_SELECT = "currencyIdSelectAll";
    public static final String OFFICE_ID_SELECT = "OfficeIdSelectOne";
    public static final String PAR_TYPE_SELECT = "parTypeSelect";
    public static final String SAVINGS_ACCOUNT_SUB_STATUS = "SavingsAccountSubStatus";
    public static final String SELECT_GL_ACCOUNT_NO = "SelectGLAccountNO";
    public static final String OBLIG_DATE_TYPE_SELECT = "obligDateTypeSelect";
    public static final String R_LOAN_OFFICER_ID = "R_loanOfficerId";
    public static final String R_LOAN_PRODUCT_ID = "R_loanProductId";
    public static final String R_LOAN_PURPOSE_ID = "R_loanPurposeId";
    public static final String R_FUND_ID = "R_fundId";
    public static final String R_CURRENCY_ID = "R_currencyId";
    public static final String R_OFFICE_ID = "R_officeId";
    public static final String R_PAR_TYPE = "R_parType";
    public static final String R_SUB_STATUS = "R_subStatus";
    public static final String R_ACCOUNT = "R_account";
    public static final String R_OBLIG_DATE_TYPE = "R_obligDateType";
    public static final String START_DATE_SELECT = "startDateSelect";
    public static final String END_DATE_SELECT = "endDateSelect";
    public static final String SELECT_ACCOUNT = "selectAccount";
    public static final String FROM_X_SELECT = "fromXSelect";
    public static final String TO_Y_SELECT = "toYSelect";
    public static final String OVERDUE_X_SELECT = "overdueXSelect";
    public static final String OVERDUE_Y_SELECT = "overdueYSelect";
    public static final String R_START_DATE = "R_startDate";
    public static final String R_END_DATE = "R_endDate";
    public static final String R_ACCOUNT_NO = "R_accountNo";
    public static final String R_FROM_X = "R_fromX";
    public static final String R_TO_Y = "R_toY";
    public static final String R_OVERDUE_X = "R_overdueX";
    public static final String R_OVERDUE_Y = "R_overdueY";
    public static final String ACTION_REPORT = "report";
    public static final String CURR_PASSWORD = "currentPassword";
    public static final String IS_TO_UPDATE_PASS_CODE = "updatePassCode";
}
