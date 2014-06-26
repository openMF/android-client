package com.mifos.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.mifos.objects.SearchedEntity;
import com.mifos.objects.User;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanApprovalRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.objects.db.CollectionSheet;
import com.mifos.objects.noncore.DataTable;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;
import com.mifos.services.data.APIEndPoint;
import com.mifos.services.data.CollectionSheetPayload;
import com.mifos.services.data.GpsCoordinatesRequest;
import com.mifos.services.data.GpsCoordinatesResponse;
import com.mifos.services.data.Payload;
import com.mifos.services.data.SaveResponse;
import com.mifos.utils.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
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
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class API {

    //This instance has more Data for Testing
    public static String mInstanceUrl = "https://demo.openmf.org/mifosng-provider/api/v1";

    public static final String ACCEPT_JSON = "Accept: application/json";
    public static final String CONTENT_TYPE_JSON = "Content-Type: application/json";
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /*
        As Mifos is a multi-tenant platform, all requests require you to specify a tenant
        as a header in each request.
     */
    public static final String HEADER_MIFOS_TENANT_ID = "X-Mifos-Platform-TenantId";

    static RestAdapter sRestAdapter;
    public static CenterService centerService;
    public static ClientAccountsService clientAccountsService;
    public static ClientService clientService;
    public static DataTableService dataTableService;
    public static LoanService loanService;
    public static SavingsAccountService savingsAccountService;
    public static SearchService searchService;
    public static UserAuthService userAuthService;
    // TODO: this service is not done yet!
    public static GpsCoordinatesService gpsCoordinatesService;

    static {
        init();
    }

    private static synchronized void init() {
        sRestAdapter = createRestAdapter(getInstanceUrl());
        centerService = sRestAdapter.create(CenterService.class);
        clientAccountsService = sRestAdapter.create(ClientAccountsService.class);
        clientService = sRestAdapter.create(ClientService.class);
        dataTableService = sRestAdapter.create(DataTableService.class);
        loanService = sRestAdapter.create(LoanService.class);
        savingsAccountService = sRestAdapter.create(SavingsAccountService.class);
        searchService = sRestAdapter.create(SearchService.class);
        userAuthService = sRestAdapter.create(UserAuthService.class);
        gpsCoordinatesService = sRestAdapter.create(GpsCoordinatesService.class);
    }

    private static RestAdapter createRestAdapter(final String url) {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(url)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        if (url.contains("developer")) {
                            request.addHeader(HEADER_MIFOS_TENANT_ID, "developer");
                        } else {
                            request.addHeader(HEADER_MIFOS_TENANT_ID, "default");
                        }

                        /*
                            Look for the Auth token in the shared preferences
                            and add it to the request. Because it is mandatory to
                            supply the Authorization Header in every request
                        */

                        SharedPreferences pref = PreferenceManager
                                .getDefaultSharedPreferences(Constants.applicationContext);
                        String authToken = pref.getString(User.AUTHENTICATION_KEY, "NA");

                        if (authToken != null && !"NA".equals(authToken)) {
                            request.addHeader(HEADER_AUTHORIZATION, authToken);
                        }

                    }
                })
                .setErrorHandler(new MifosRestErrorHandler())
                .build();
        // TODO: This logging is sometimes excessive, e.g. for client image requests.
        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        return restAdapter;
    }

    public static void changeRestAdapterLogLevel(RestAdapter.LogLevel logLevel) {
        sRestAdapter.setLogLevel(logLevel);
    }

    static class MifosRestErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError retrofitError) {

            Response response = retrofitError.getResponse();
            if (response != null && response.getStatus() == 401) {
                Log.e("Status", "Authentication Error.");


            }else if(response.getStatus() == 400){
                Log.d("Status","Bad Request - Invalid Parameter or Data Integrity Issue.");
                Log.d("URL", response.getUrl());
                List<retrofit.client.Header> headersList = response.getHeaders();
                Iterator<retrofit.client.Header> iterator = headersList.iterator();
                while(iterator.hasNext())
                {    retrofit.client.Header header = iterator.next();
                    Log.d("Header ",header.toString());
                }
            }

            return retrofitError;
        }

    }

    public interface CenterService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CENTERS)
        public void getAllCenters(Callback<List<com.mifos.objects.Center>> callback);

        //TODO Remove Static Center Code
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.CENTERS + "/2026?command=generateCollectionSheet")
        public void getCenter(@Body Payload payload, Callback<CollectionSheet> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.CENTERS + "/2026?command=saveCollectionSheet")
        public SaveResponse saveCollectionSheet(@Body CollectionSheetPayload collectionSheetPayload);

    }

    public interface ClientAccountsService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CLIENTS + "/{clientId}/accounts")
        public void getAllAccountsOfClient(@Path("clientId") int clientId, Callback<ClientAccounts> clientAccountsCallback);

    }

    public interface ClientService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.CLIENTS)
        public void listAllClients(Callback<Page<Client>> callback);

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


        /*
             Mandatory Fields
                1. String approvedOnDate
        */
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.LOANS + "/{loanId}?command=approve")
        public void approveLoanApplication(@Path("loanId") int loanId,
                                           @Body LoanApprovalRequest loanApprovalRequest,
                                           Callback<GenericResponse> genericResponseCallback);

        /*
            Mandatory Fields
                1. String actualDisbursementDate
          */
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.LOANS + "/{loanId}/?command=disburse")
        public void disburseLoan(@Path("loanId") int loanId,
                                 @Body HashMap<String,Object> genericRequest,
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
         *
         * @param savingsAccountId - savingsAccountId for which information is requested
         * @param association - Mention Type of Association Needed, Like :- all, transactions etc.
         * @param savingsAccountWithAssociationsCallback - callback to receive the response
         *
         * Use this method to retrieve the Savings Account With Associations
         */
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.SAVINGSACCOUNTS + "/{savingsAccountId}")
        public void getSavingsAccountWithAssociations(@Path("savingsAccountId") int savingsAccountId,
                                                      @Query("associations") String association,
                                                      Callback<SavingsAccountWithAssociations> savingsAccountWithAssociationsCallback);

        /**
         *
         * @param savingsAccountId - savingsAccountId for which information is requested
         * @param savingsAccountTransactionTemplateCallback - Savings Account Transaction Template Callback
         *
         * Use this method to retrieve the Savings Account Transaction Template
         */
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET(APIEndPoint.SAVINGSACCOUNTS + "/{savingsAccountId}/transactions/template")
        public void getSavingsAccountTransactionTemplate(@Path("savingsAccountId") int savingsAccountId, Callback<SavingsAccountTransactionTemplate> savingsAccountTransactionTemplateCallback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST(APIEndPoint.SAVINGSACCOUNTS + "/{savingsAccountId}/transactions")
        public void processTransaction(@Path("savingsAccountId") int savingsAccountId,
                                              @Query("command") String transactionType,
                                              @Body SavingsAccountTransactionRequest savingsAccountTransactionRequest,
                                              Callback<SavingsAccountTransactionResponse> savingsAccountTransactionResponseCallback);


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
        public void getDataOfDataTable(@Path("dataTableName") String dataTableName, @Path("entityId") int clientId, Callback<JsonArray> jsonArrayCallback);


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

    public static synchronized void setInstanceUrl(String url) {
        mInstanceUrl = url;
        init();
    }

    public static synchronized String getInstanceUrl() {
        return mInstanceUrl;
    }
}
