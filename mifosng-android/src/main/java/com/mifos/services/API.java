package com.mifos.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.mifos.objects.SearchedEntity;
import com.mifos.objects.User;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Page;
import com.mifos.objects.db.CollectionSheet;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.services.data.Payload;
import com.mifos.services.data.CollectionSheetPayload;
import com.mifos.services.data.SaveResponse;
import com.mifos.utils.Constants;
import retrofit.*;
import retrofit.client.Response;
import retrofit.http.*;

import java.util.Iterator;
import java.util.List;

public class API {

    //This instance has more Data for Testing
    public static String mInstanceUrl = "https://developer.openmf.org/mifosng-provider/api/v1";

    public static final String ACCEPT_JSON = "Accept: application/json";
    public static final String CONTENT_TYPE_JSON = "Content-Type: application/json";

    //public static String mInstanceUrl = "https://demo2.openmf.org/mifosng-provider/api/v1";

    static RestAdapter sRestAdapter;
    public static CenterService centerService;
    public static ClientAccountsService clientAccountsService;
    public static ClientService clientService;
    public static LoanService loanService;
    public static SavingsAccountService savingsAccountService;
    public static SearchService searchService;
    public static UserAuthService userAuthService;

    static {
        init();
    }

    private static synchronized void init() {
        sRestAdapter = createRestAdapter(getInstanceUrl());
        centerService = sRestAdapter.create(CenterService.class);
        clientAccountsService = sRestAdapter.create(ClientAccountsService.class);
        clientService = sRestAdapter.create(ClientService.class);
        loanService = sRestAdapter.create(LoanService.class);
        savingsAccountService = sRestAdapter.create(SavingsAccountService.class);
        searchService = sRestAdapter.create(SearchService.class);
        userAuthService = sRestAdapter.create(UserAuthService.class);
    }

    private static RestAdapter createRestAdapter(final String url) {
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(url)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        if (url.contains("developer")) {
                            request.addHeader("X-Mifos-Platform-TenantId", "developer");
                        } else {
                            request.addHeader("X-Mifos-Platform-TenantId", "default");
                        }

  //                    request.addHeader("Authorization", "Basic VXNlcjE6dGVjaDRtZg==");

                        SharedPreferences pref = PreferenceManager
                                .getDefaultSharedPreferences(Constants.applicationContext);
                        String authToken = pref.getString(User.AUTHENTICATION_KEY, "NA");

                        if (authToken != null && !"NA".equals(authToken)) {
                            request.addHeader("Authorization", authToken);
                        }

                    }
                })
                .setErrorHandler(new MifosRestErrorHandler())
                .build();
        // TODO: This logging is sometimes excessive, e.g. for client image requests.
        restAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        return restAdapter;
    }

     static class MifosRestErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError retrofitError) {

            Response r = retrofitError.getResponse();
            if (r != null && r.getStatus() == 401) {
                Log.e("Status", "Authentication Error.");


            }else if(r.getStatus() == 400){
                Log.d("Status","Bad Request - Invalid Parameter or Data Integrity Issue.");
                Log.d("URL", r.getUrl());
                List<retrofit.client.Header> headersList = r.getHeaders();
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
        @GET("/centers")
        public void getAllCenters(Callback<List<com.mifos.objects.Center>> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST("/centers/2026?command=generateCollectionSheet")
        public void getCenter(@Body Payload payload, Callback<CollectionSheet> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST("/centers/2026?command=saveCollectionSheet")
        public SaveResponse saveCollectionSheet(@Body CollectionSheetPayload collectionSheetPayload);

    }

    public interface ClientAccountsService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET("/clients/{clientId}/accounts")
        public void getAllAccountsOfClient(@Path("clientId") int clientId, Callback<ClientAccounts> callback);

    }

    public interface ClientService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET("/clients")
        public void listAllClients(Callback<Page<Client>> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET("/clients/{clientId}")
        public void getClient(@Path("clientId") int clientId, Callback<Client> callback);

    }

    public interface SearchService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET("/search?resource=clients")
        public void searchClientsByName(@Query("query") String clientName, Callback<List<SearchedEntity>> callback);

    }

    public interface LoanService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET("/loans/{loanId}")
        public void getLoanById(@Path("loanId") int loanId, Callback<com.mifos.objects.accounts.loan.Loan> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET("/loans/{loanId}/transactions/template?command=repayment")
        public void getLoanRepaymentTemplate(@Path("loanId") int loanId, Callback<LoanRepaymentTemplate> callback);

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST("/loans/{loanId}/transactions?command=repayment")
        public void submitPayment(@Path("loanId") int loanId,
                                  @Body LoanRepaymentRequest loanRepaymentRequest,
                                  Callback<LoanRepaymentResponse> loanRepaymentResponseCallback);

    }

    public interface SavingsAccountService {

        /**
         *
         * @param savingsAccountId - savingsAccountId for which information is requested
         * @param association - Mention Type of Association Needed, Like :- all, transactions etc.
         * @param savingsAccountWithAssociationsCallback - callback to receive the response
         */
        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @GET("/savingsaccounts/{savingsAccountId}")
        public void getSavingsAccountWithAssociations(@Path("savingsAccountId") int savingsAccountId,
                                                      @Query("associations") String association,
                                                      Callback<SavingsAccountWithAssociations> savingsAccountWithAssociationsCallback);

    }

    public interface UserAuthService {

        @Headers({ACCEPT_JSON, CONTENT_TYPE_JSON})
        @POST("/authentication")
        public void authenticate(@Query("username") String username, @Query("password") String password, Callback<User> userCallback);

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

