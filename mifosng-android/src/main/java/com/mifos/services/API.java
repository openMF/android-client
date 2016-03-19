/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.mifos.api.GenericResponse;
import com.mifos.api.model.APIEndPoint;
import com.mifos.api.model.ClientPayload;
import com.mifos.api.model.CollectionSheetPayload;
import com.mifos.api.model.GpsCoordinatesRequest;
import com.mifos.api.model.GpsCoordinatesResponse;
import com.mifos.api.model.Payload;
import com.mifos.api.model.SaveResponse;
import com.mifos.api.model.ScorecardPayload;
import com.mifos.objects.SearchedEntity;
import com.mifos.objects.User;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.accounts.loan.LoanApproval;
import com.mifos.objects.accounts.loan.LoanApprovalRequest;
import com.mifos.objects.accounts.loan.LoanDisbursement;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.accounts.loan.SavingsApproval;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.objects.client.Savings;
import com.mifos.objects.db.CollectionSheet;
import com.mifos.objects.db.OfflineCenter;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.objects.noncore.DataTable;
import com.mifos.objects.noncore.Document;
import com.mifos.objects.noncore.Identifier;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.ProductLoans;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.organisation.Staff;
import com.mifos.objects.survey.Scorecard;
import com.mifos.objects.survey.Survey;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;
import com.mifos.services.data.CenterPayload;
import com.mifos.services.data.ChargesPayload;
import com.mifos.services.data.GroupLoanPayload;
import com.mifos.services.data.GroupPayload;
import com.mifos.services.data.LoansPayload;
import com.mifos.services.data.SavingsPayload;
import com.mifos.utils.Constants;
import com.mifos.utils.MFErrorResponse;
import com.squareup.okhttp.OkHttpClient;

import org.apache.http.HttpStatus;

import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import retrofit.Callback;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class API {
    public static final String TAG = API.class.getName();
    public static final String ACCEPT_JSON = "Accept: application/json";
    public static final String CONTENT_TYPE_JSON = "Content-Type: application/json";
    public static final String CONTENT_TYPE_MULTIPART_FORM_DATA = "Content-Type: multipart/form-data";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String INSTANCE_URL = "InstanceUrl";
    /*
        As Mifos is a multi-tenant platform, all requests require you to specify a tenant
        as a header in each request.
     */
    public static final String HEADER_MIFOS_TENANT_ID = "Fineract-Platform-TenantId";
    //This instance has more Data for Testing
    public static String mInstanceUrl = "https://demo.openmf.org/fineract-provider/api/v1";
    public static String mTenantIdentifier = "default";
    public CenterService centerService;
    public ClientAccountsService clientAccountsService;
    public ClientService clientService;
    public ChargeService chargeService;
    public GroupAccountsService groupAccountsService;
    public DataTableService dataTableService;
    public CreateGroupLoanAccountService createGroupLoanAccountService;
    public LoanService loanService;
    public CreateSavingsAccountService createSavingsAccountService;
    public CreateLoanAccountService createLoanAccountService;
    public SavingsAccountService savingsAccountService;
    public SearchService searchService;
    public UserAuthService userAuthService;
    public GpsCoordinatesService gpsCoordinatesService;
    public GroupService groupService;
    public DocumentService documentService;
    public IdentifierService identifierService;
    public OfficeService officeService;
    public StaffService staffService;
    public SurveyService surveyService;

    public API(final String url, final String tenantIdentifier, boolean shouldByPassSSLSecurity) {

        RestAdapter.Builder restAdapterBuilder = new RestAdapter.Builder();
        restAdapterBuilder.setEndpoint(url);
        if (shouldByPassSSLSecurity) {
            restAdapterBuilder.setClient(new OkClient(getUnsafeOkHttpClient()));
        }
        restAdapterBuilder.setRequestInterceptor(new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                if (tenantIdentifier != null && !tenantIdentifier.isEmpty()) {
                    request.addHeader(HEADER_MIFOS_TENANT_ID, tenantIdentifier);
                } else {
                    request.addHeader(HEADER_MIFOS_TENANT_ID, "default");
                }
                        /*
                            Look for the Auth token in the shared preferences
                            and add it to the request. Because it is mandatory to
                            supply the Authorization Header in every request
                        */

                /*SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Constants.applicationContext);
                String authToken = pref.getString(User.AUTHENTICATION_KEY, "NA");
                if (authToken != null && !"NA".equals(authToken)) {
                    request.addHeader(HEADER_AUTHORIZATION, authToken);
                }*/
            }
        });
        restAdapterBuilder.setErrorHandler(new MifosRestErrorHandler());
        RestAdapter restAdapter = restAdapterBuilder.build();
        // TODO: This logging is sometimes excessive, e.g. for client image requests.
        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);

        centerService = restAdapter.create(CenterService.class);
        clientService = restAdapter.create(ClientService.class);
        chargeService = restAdapter.create(ChargeService.class);
        groupAccountsService =restAdapter.create(GroupAccountsService.class);
        clientAccountsService = restAdapter.create(ClientAccountsService.class);
        dataTableService = restAdapter.create(DataTableService.class);
        loanService = restAdapter.create(LoanService.class);
        savingsAccountService = restAdapter.create(SavingsAccountService.class);
        searchService = restAdapter.create(SearchService.class);
        userAuthService = restAdapter.create(UserAuthService.class);
        gpsCoordinatesService = restAdapter.create(GpsCoordinatesService.class);
        groupService = restAdapter.create(GroupService.class);
        documentService = restAdapter.create(DocumentService.class);
        identifierService = restAdapter.create(IdentifierService.class);
        officeService = restAdapter.create(OfficeService.class);
        staffService = restAdapter.create(StaffService.class);
        surveyService = restAdapter.create(SurveyService.class);
        createSavingsAccountService= restAdapter.create(CreateSavingsAccountService.class);
        createLoanAccountService=restAdapter.create(CreateLoanAccountService.class);
        createGroupLoanAccountService=restAdapter.create(CreateGroupLoanAccountService.class);
    }


    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setSslSocketFactory(sslSocketFactory);
            okHttpClient.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static <T> Callback<T> getCallback(T t) {
        Callback<T> cb = new Callback<T>() {
            @Override
            public void success(T o, Response response) {
                System.out.println("Object " + o);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                System.out.println("Error: " + retrofitError);
            }
        };

        return cb;
    }

    public static <T> Callback<List<T>> getCallbackList(List<T> t) {
        Callback<List<T>> cb = new Callback<List<T>>() {
            @Override
            public void success(List<T> o, Response response) {
                System.out.println("Object " + o);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                System.out.println("Error: " + retrofitError);
            }
        };

        return cb;
    }

    public interface CenterService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.CENTERS)
        void createCenter(@Body CenterPayload centerPayload, Callback<Center> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CENTERS)
        public void getAllCenters(Callback<List<Center>> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers,collectionMeetingCalendar")
        public void getCenterWithGroupMembersAndCollectionMeetingCalendar(@Path("centerId") int centerId,
                                                                          Callback<CenterWithAssociations> centerWithAssociationsCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CENTERS)
        public void getAllCentersInOffice(@Query("officeId") int officeId, @QueryMap Map<String, Object> additionalParams,
                                          Callback<List<Center>> centersCallback);


        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CENTERS + "/{centerId}?associations=groupMembers")
        public void getAllGroupsForCenter(@Path("centerId") int centerId,
                                          Callback<CenterWithAssociations> centerWithAssociationsCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.CENTERS + "/{centerId}?command=generateCollectionSheet")
        public void getCollectionSheet(@Path("centerId") long centerId, @Body Payload payload, Callback<CollectionSheet> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.CENTERS + "/{centerId}?command=saveCollectionSheet")
        public SaveResponse saveCollectionSheet(@Path("centerId") int centerId, @Body CollectionSheetPayload collectionSheetPayload);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.CENTERS + "/{centerId}?command=saveCollectionSheet")
        public void saveCollectionSheet(@Path("centerId") int centerId, @Body CollectionSheetPayload collectionSheetPayload, Callback<SaveResponse> saveResponseCallback);


        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.CLIENTS + "")
        public void uploadNewClientDetails();


        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CENTERS)
        public void getCenterList(@Query("dateFormat") String dateFormat, @Query("locale") String locale,
                                  @Query("meetingDate") String meetingDate, @Query("officeId") int officeId,
                                  @Query("staffId") int staffId, Callback<List<OfflineCenter>> callback);

    }

    public interface ClientAccountsService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CLIENTS + "/{clientId}/accounts")
        public void getAllAccountsOfClient(@Path("clientId") int clientId, Callback<ClientAccounts> clientAccountsCallback);

    }
    public interface GroupAccountsService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.GROUPS + "/{groupId}/accounts")
        public void getAllGroupsOfClient(@Path("groupId") int groupId, Callback<GroupAccounts> groupAccountsCallback);

    }
    public interface ChargeService {

        /**
         * Fetches List of All the charges
         *
         *
         */


        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CHARGES)
        public void listAllCharges(Callback<Response> chargesCallback);

        @GET(APIEndPoint.CHARGES)
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        public void getAllChargesS(Callback<List<Charges>> listOfChargesCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CLIENTS + "/{clientId}" + APIEndPoint.CHARGES)
        public void getListOfCharges(@Path("clientId") int clientId,Callback<Page<Charges>> chargeListCallback);
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})

        @GET(APIEndPoint.LOANS + "/{loanId}" + APIEndPoint.CHARGES)
        public void getListOfLoanCharges(@Path("loanId") int loanId,Callback<Page<Charges>> loanchargeListCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.CLIENTS +"/{clientId}/charges")
        void createCharges(@Path("clientId") int clientId,@Body ChargesPayload chargesPayload, Callback<Charges> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.LOANS +"/{loanId}/charges")
        void createLoanCharges(@Path("loanId") int loanId,@Body ChargesPayload chargesPayload, Callback<Charges> callback);

    }
    public interface CreateSavingsAccountService {


        @GET(APIEndPoint.CREATESAVINGSPRODUCTS)
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        public void getAllSavingsAccounts(Callback<List<ProductSavings>> listOfSavingsCallback);


        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.CREATESAVINGSACCOUNTS)
        void createSavingsAccount(@Body SavingsPayload savingsPayload, Callback<Savings> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CREATESAVINGSPRODUCTS +"/template")
        public void getSavingsAccountTemplate(Callback<Response> savingsCallback);

    }
    public interface CreateLoanAccountService {
        @GET(APIEndPoint.CREATELOANSPRODUCTS)
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        void getAllLoans(Callback<List<ProductLoans>> listOfLoansCallback);


        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.CREATELOANSACCOUNTS)
        void createLoansAccount(@Body LoansPayload loansPayload, Callback<Loans> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CREATELOANSACCOUNTS +"/template?templateType=individual")
        void getLoansAccountTemplate(@Query("clientId") int clientId,@Query("productId") int productId,Callback<Response> loanCallback);


    }


    public interface CreateGroupLoanAccountService {
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.CREATELOANSACCOUNTS)
        void createGroupLoansAccount(@Body GroupLoanPayload loansPayload, Callback<Loans> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CREATELOANSACCOUNTS + "/template?templateType=group")
        public void getGroupLoansAccountTemplate(@Query("groupId") int groupId, @Query("productId") int productId, Callback<Response> grouploanCallback);

    }
    public interface ClientService {

        @GET(APIEndPoint.CLIENTS)
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        public void getAllclients(Callback<List<Client>> listOfapiClientCallback);


        //This is a default call and Loads client from 0 to 200
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CLIENTS)
        public void listAllClients(Callback<Page<Client>> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CLIENTS)
        public void listAllClients(@Query("offset") int offset, @Query("limit") int limit, Callback<Page<Client>> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CLIENTS + "/{clientId}")
        public void getClient(@Path("clientId") int clientId, Callback<Client> clientCallback);

        @Multipart
        @POST(APIEndPoint.CLIENTS + "/{clientId}/images")
        public void uploadClientImage(@Path("clientId") int clientId,
                                      @Part("file") TypedFile file,
                                      Callback<Response> responseCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @DELETE(APIEndPoint.CLIENTS + "/{clientId}/images")
        void deleteClientImage(@Path("clientId") int clientId, Callback<Response> responseCallback);

        //TODO: Implement when API Fixed
        //@Headers({"Accept: application/octet-stream", CONTENT_TYPE_JSON})
        @GET("/clients/{clientId}/images")
        public void getClientImage(@Path("clientId") int clientId, Callback<TypedString> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.CLIENTS)
        void createClient(@Body ClientPayload clientPayload, Callback<Client> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CLIENTS + "/template")
        public void getClientTemplate(Callback<Response> clientCallback);


    }

    public interface SurveyService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.SURVEYS)
        public void getAllSurveys(Callback<List<Survey>> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.SURVEYS + "/{surveyId}")
        public void getSurvey(@Path("surveyId") int surveyId, Callback<Survey> surveyCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.SURVEYS + "/{surveyId}/scorecards")
        public void submitScore(@Path("surveyId") int surveyId,@Body ScorecardPayload scorecardPayload, Callback<Scorecard> callback);


    }

    public interface SearchService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.SEARCH + "?resource=clients")
        public void searchClientsByName(@Query("query") String clientName,
                                        Callback<List<SearchedEntity>> listCallback);
    }


    public interface LoanService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.LOANS + "/{loanId}?associations=all")
        public void getLoanByIdWithAllAssociations(@Path("loanId") int loanId, Callback<LoanWithAssociations> loanCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.LOANS + "/{loanId}/transactions/template?command=repayment")
        public void getLoanRepaymentTemplate(@Path("loanId") int loanId,
                                             Callback<LoanRepaymentTemplate> loanRepaymentTemplateCallback);


        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.LOANS +"/{loanId}/transactions/template?command=disburse")
        public void getLoanTemplate(Callback<Response> loanTemplateCallback);


        /*
             Mandatory Fields
                1. String approvedOnDate
        */
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.LOANS + "/{loanId}?command=approve")
        public void approveLoanApplication(@Path("loanId") int loanId,
                                           @Body LoanApproval loanApproval,
                                           Callback<GenericResponse> genericResponseCallback);

        /*
            Mandatory Fields
                1. String actualDisbursementDate
          */
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.LOANS + "/{loanId}/?command=disburse")
        public void disburseLoan(@Path("loanId") int loanId,
                                 @Body LoanDisbursement loanDisbursement,
                                 Callback<GenericResponse> genericResponseCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.LOANS + "/{loanId}/transactions?command=repayment")
        public void submitPayment(@Path("loanId") int loanId,
                                  @Body LoanRepaymentRequest loanRepaymentRequest,
                                  Callback<LoanRepaymentResponse> loanRepaymentResponseCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.LOANS + "/{loanId}?associations=repaymentSchedule")
        public void getLoanRepaymentSchedule(@Path("loanId") int loanId,
                                             Callback<LoanWithAssociations> loanWithRepaymentScheduleCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.LOANS + "/{loanId}?associations=transactions")
        public void getLoanWithTransactions(@Path("loanId") int loanId,
                                            Callback<LoanWithAssociations> loanWithAssociationsCallback);
    }

    public interface SavingsAccountService {

        /**
         * @param savingsAccountId                       - savingsAccountId for which information is requested
         * @param association                            - Mention Type of Association Needed, Like :- all, transactions etc.
         * @param savingsAccountWithAssociationsCallback - callback to receive the response
         *                                               <p/>
         *                                               Use this method to retrieve the Savings Account With Associations
         */
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET("/{savingsAccountType}/{savingsAccountId}")
        public void getSavingsAccountWithAssociations(@Path("savingsAccountType") String savingsAccountType,
                                                      @Path("savingsAccountId") int savingsAccountId,
                                                      @Query("associations") String association,
                                                      Callback<SavingsAccountWithAssociations> savingsAccountWithAssociationsCallback);

        /**
         * @param savingsAccountId                          - savingsAccountId for which information is requested
         * @param savingsAccountTransactionTemplateCallback - Savings Account Transaction Template Callback
         *                                                  <p/>
         *                                                  Use this method to retrieve the Savings Account Transaction Template
         */
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET("/{savingsAccountType}/{savingsAccountId}/transactions/template")
        public void getSavingsAccountTransactionTemplate(@Path("savingsAccountType") String savingsAccountType,
                                                         @Path("savingsAccountId") int savingsAccountId,
                                                         @Query("command") String transactionType,
                                                         Callback<SavingsAccountTransactionTemplate> savingsAccountTransactionTemplateCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST("/{savingsAccountType}/{savingsAccountId}/transactions")
        public void processTransaction(@Path("savingsAccountType") String savingsAccountType,
                                       @Path("savingsAccountId") int savingsAccountId,
                                       @Query("command") String transactionType,
                                       @Body SavingsAccountTransactionRequest savingsAccountTransactionRequest,
                                       Callback<SavingsAccountTransactionResponse> savingsAccountTransactionResponseCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.CREATESAVINGSACCOUNTS + "/{savingsAccountId}?command=approve")
        public void approveSavingsApplication(@Path("savingsAccountId") int savingsAccountId,
                                           @Body SavingsApproval savingsApproval,
                                           Callback<GenericResponse> genericResponseCallback);


        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.CREATESAVINGSACCOUNTS + "/{savingsAccountId}/?command=activate")
        public void activateSavings(@Path("savingsAccountId") int savingsAccountId,
                                 @Body HashMap<String, Object> genericRequest,
                                 Callback<GenericResponse> genericResponseCallback);
    }
    public interface DataTableService {

        //TODO refactor the methods to to just one and specify the Query param

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.DATATABLES + "?apptable=m_savings_account")
        public void getDatatablesOfSavingsAccount(Callback<List<DataTable>> listCallback);


        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.DATATABLES + "?apptable=m_client")
        public void getDatatablesOfClient(Callback<List<DataTable>> listCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.DATATABLES + "?apptable=m_loan")
        public void getDatatablesOfLoan(Callback<List<DataTable>> listCallback);


        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/")
        public void getDataOfDataTable(@Path("dataTableName") String dataTableName, @Path("entityId") int entityId, Callback<JsonArray> jsonArrayCallback);

        //TODO Improve Body Implementation with Payload
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/")
        public void createEntryInDataTable(@Path("dataTableName") String dataTableName, @Path("entityId") int entityId, @Body Map<String, Object> requestPayload,
                                           Callback<GenericResponse> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @DELETE(APIEndPoint.DATATABLES + "/{dataTableName}/{entityId}/{dataTableRowId}")
        public void deleteEntryOfDataTableManyToMany(@Path("dataTableName") String dataTableName, @Path("entityId") int entityId,
                                                     @Path("dataTableRowId") int dataTableRowId, Callback<GenericResponse> callback);

    }

    /**
     * Service for management of Documents
     */

    public interface DocumentService {

        @GET("/{entityType}/{entityId}" + APIEndPoint.DOCUMENTS)
        @Headers({CONTENT_TYPE_JSON})
        public void getListOfDocuments(@Path("entityType") String entityType,
                                       @Path("entityId") int entityId,
                                       Callback<List<Document>> documentListCallback);

        /**
         * @param entityType              - Type for which document is being uploaded (Client, Loan or Savings etc)
         * @param entityId                - Id of Entity
         * @param nameOfDocument          - Document Name
         * @param description             - Mandatory - Document Description
         * @param typedFile               - Mandatory
         * @param genericResponseCallback - Response Callback
         */
        @POST("/{entityType}/{entityId}" + APIEndPoint.DOCUMENTS)
        //For some reason you cannot set these headers for this request.
        //@Headers({ACCEPT_JSON, CONTENT_TYPE_MULTIPART_FORM_DATA})
        @Multipart
        public void createDocument(@Path("entityType") String entityType,
                                   @Path("entityId") int entityId,
                                   @Part("name") String nameOfDocument,
                                   @Part("description") String description,
                                   @Part("file") TypedFile typedFile,
                                   Callback<GenericResponse> genericResponseCallback);

    }

    /**
     * Service for management of Identifiers
     */

    public interface IdentifierService {

        @GET(APIEndPoint.CLIENTS + "/{clientId}" + APIEndPoint.IDENTIFIERS)
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        public void getListOfIdentifiers(@Path("clientId") int clientId,
                                         Callback<List<Identifier>> identifierListCallback);

        @DELETE(APIEndPoint.CLIENTS + "/{clientId}" + APIEndPoint.IDENTIFIERS + "/{identifierId}")
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        public void deleteIdentifier(@Path("clientId") int clientId,
                                     @Path("identifierId") int identifierId,
                                     Callback<GenericResponse> genericResponseCallback);

    }


    /**
     * Service for authenticating users.
     * No other service can be used without authentication.
     */

    public interface UserAuthService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.AUTHENTICATION)
        public void authenticate(@Query("username") String username, @Query("password") String password, Callback<User> userCallback);

    }

    /**
     * Service for getting and retrieving GPS coordinates for a client's location, stored
     * in a custom data table.
     */
    public interface GpsCoordinatesService {

        @POST(APIEndPoint.DATATABLES + "/gps_coordinates/{clientId}")
        public void setGpsCoordinates(@Path("clientId") int clientId,
                                      @Body GpsCoordinatesRequest gpsCoordinatesRequest,
                                      Callback<GpsCoordinatesResponse> gpsCoordinatesResponseCallback);

        @PUT(APIEndPoint.DATATABLES + "/gps_coordinates/{clientId}")
        public void updateGpsCoordinates(@Path("clientId") int clientId,
                                         @Body GpsCoordinatesRequest gpsCoordinatesRequest,
                                         Callback<GpsCoordinatesResponse> gpsCoordinatesResponseCallback);


    }

    public interface GroupService {


        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.GROUPS + "/{groupId}")
        public void getGroup(@Path("groupId") int groupId, Callback<Group> groupCallback);


        @GET(APIEndPoint.GROUPS)
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        public void getAllgroups(Callback<List<Group>> listOfGroupCallback);

        //This is a default call and Loads group from 0 to 200
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.GROUPS+"?paged=true")
        public void listAllGroups(Callback<Page<Group>> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.GROUPS+"?paged=true")
        public void listAllGroups(@Query("offset") int offset, @Query("limit") int limit, Callback<Page<Group>> callback);


        @GET(APIEndPoint.GROUPS + "/{groupId}?associations=all")
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        public void getGroupWithAssociations(@Path("groupId") int groupId,
                                             Callback<GroupWithAssociations> groupWithAssociationsCallback);

        @GET(APIEndPoint.GROUPS)
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        public void getAllGroupsInOffice(@Query("officeId") int officeId, @QueryMap Map<String, Object> params, Callback<List<Group>> listOfGroupsCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.GROUPS)
        void createGroup(@Body GroupPayload groupPayload, Callback<Group> callback);

    }

    /**
     * Serivce for interacting with Offices
     */
    public interface OfficeService {

        /**
         * Fetches List of All the Offices
         *
         * @param listOfOfficesCallback
         */
        @GET(APIEndPoint.OFFICES)
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        public void getAllOffices(Callback<List<Office>> listOfOfficesCallback);
    }


    public interface StaffService {

        @GET(APIEndPoint.STAFF + "?status=all")
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        public void getStaffForOffice(@Query("officeId") int officeId, Callback<List<Staff>> staffListCallback);

        @GET(APIEndPoint.STAFF)
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        public void getAllStaff(Callback<List<Staff>> listOfStaffsCallback);


    }

    static class MifosRestErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError retrofitError) {

            Response response = retrofitError.getResponse();
            if (response != null) {

                if (response.getStatus() == HttpStatus.SC_UNAUTHORIZED) {
                    Log.e("Status", "Authentication Error.");


                } else if (response.getStatus() == HttpStatus.SC_BAD_REQUEST) {

                    MFErrorResponse mfErrorResponse = new Gson().fromJson(response.getBody().toString(), MFErrorResponse.class);
                    Log.d("Status", "Bad Request - Invalid Parameter or Data Integrity Issue.");
                    Log.d("URL", response.getUrl());
                    List<retrofit.client.Header> headersList = response.getHeaders();
                    Iterator<retrofit.client.Header> iterator = headersList.iterator();
                    while (iterator.hasNext()) {
                        retrofit.client.Header header = iterator.next();
                        Log.d("Header ", header.toString());
                    }
                } else if (response.getStatus() == HttpStatus.SC_FORBIDDEN) {

                    MFErrorResponse mfErrorResponse = new Gson().fromJson(response.getBody().toString(), MFErrorResponse.class);

                 //   Toast.makeText(Constants.applicationContext, mfErrorResponse.getDefaultUserMessage(), Toast.LENGTH_LONG).show();

                    Log.d("Status", "Bad Request - Invalid Parameter or Data Integrity Issue.");
                    Log.d("URL", response.getUrl());
                    List<retrofit.client.Header> headersList = response.getHeaders();
                    Iterator<retrofit.client.Header> iterator = headersList.iterator();
                    while (iterator.hasNext()) {
                        retrofit.client.Header header = iterator.next();
                        Log.d("Header ", header.toString());
                    }

                }

            }


            return retrofitError;
        }

    }
}
