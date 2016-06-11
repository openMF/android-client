/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mifos.api.services.AuthService;
import com.mifos.api.services.CenterService;
import com.mifos.api.services.ChargeService;
import com.mifos.api.services.ClientAccountsService;
import com.mifos.api.services.ClientService;
import com.mifos.api.services.CreateSavingsAccountService;
import com.mifos.api.services.DataTableService;
import com.mifos.api.services.DocumentService;
import com.mifos.api.services.GpsCoordinatesService;
import com.mifos.api.services.GroupAccountService;
import com.mifos.api.services.GroupService;
import com.mifos.api.services.IdentifierService;
import com.mifos.api.services.LoanService;
import com.mifos.api.services.OfficeService;
import com.mifos.api.services.SavingsAccountService;
import com.mifos.api.services.SearchService;
import com.mifos.api.services.StaffService;
import com.mifos.api.services.SurveyService;
import com.mifos.utils.JsonDateSerializer;

import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author fomenkoo
 */
public class BaseApiManager {


    private BaseUrl baseUrl = new BaseUrl();
    private final String BASE_URL = baseUrl.getUrl();

    private AuthService authApi;
    private CenterService centerApi;
    private ClientAccountsService accountsApi;
    private ClientService clientsApi;
    private DataTableService dataTableApi;
    private LoanService loanApi;
    private SavingsAccountService savingsApi;
    private ChargeService chargeApi;
    private CreateSavingsAccountService createSavingsAccountService;
    private SearchService searchApi;
    private GpsCoordinatesService gpsApi;
    private GroupService groupApi;
    private DocumentService documentApi;
    private IdentifierService identifierApi;
    private OfficeService officeApi;
    private StaffService staffApi;
    private SurveyService surveyApi;
    private GroupAccountService groupAccountsServiceApi;

    public BaseApiManager() {
        createAuthApi();

        centerApi = createApi(CenterService.class, BASE_URL);
        accountsApi = createApi(ClientAccountsService.class, BASE_URL);
        clientsApi = createApi(ClientService.class, BASE_URL);
        dataTableApi = createApi(DataTableService.class, BASE_URL);
        loanApi = createApi(LoanService.class, BASE_URL);
        savingsApi = createApi(SavingsAccountService.class, BASE_URL);
        searchApi = createApi(SearchService.class, BASE_URL);
        gpsApi = createApi(GpsCoordinatesService.class, BASE_URL);
        groupApi = createApi(GroupService.class, BASE_URL);
        documentApi = createApi(DocumentService.class, BASE_URL);
        identifierApi = createApi(IdentifierService.class, BASE_URL);
        officeApi = createApi(OfficeService.class, BASE_URL);
        staffApi = createApi(StaffService.class, BASE_URL);
        surveyApi = createApi(SurveyService.class, BASE_URL);
        chargeApi = createApi(ChargeService.class, BASE_URL);
        createSavingsAccountService = createApi(CreateSavingsAccountService.class, BASE_URL);
        groupAccountsServiceApi = createApi(GroupAccountService.class, BASE_URL);

    }

    public void setupEndpoint(String instanceUrl) {
        baseUrl.updateInstanceUrl(instanceUrl);
    }

    public OkHttpClient getOkHttpClient(){
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(Level.BODY);
        return new Builder()
                .addInterceptor(logger)
                .addInterceptor(new ApiRequestInterceptor())
                .build();
    }

    private <T> T createApi(Class<T> clazz, String baseUrl) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDateSerializer()).create();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(getOkHttpClient())
                .build()
                .create(clazz);
    }

    private void createAuthApi() {

        authApi = new Retrofit.Builder()
                .baseUrl(baseUrl.getUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(getOkHttpClient())
                .build()
                .create(AuthService.class);
    }

    protected AuthService getAuthApi() {
        return authApi;
    }

    protected CenterService getCenterApi() {
        return centerApi;
    }

    protected ClientAccountsService getAccountsApi() {
        return accountsApi;
    }

    protected ClientService getClientsApi() {
        return clientsApi;
    }

    protected DataTableService getDataTableApi() {
        return dataTableApi;
    }

    protected LoanService getLoanApi() {
        return loanApi;
    }

    protected SavingsAccountService getSavingsApi() {
        return savingsApi;
    }

    protected SearchService getSearchApi() {
        return searchApi;
    }

    protected GpsCoordinatesService getGpsApi() {
        return gpsApi;
    }

    protected GroupService getGroupApi() {
        return groupApi;
    }

    protected GroupAccountService getGroupAccountsServiceApi() {
        return groupAccountsServiceApi;
    }

    protected DocumentService getDocumentApi() {
        return documentApi;
    }

    protected IdentifierService getIdentifierApi() {
        return identifierApi;
    }

    protected OfficeService getOfficeApi() {
        return officeApi;
    }

    protected StaffService getStaffApi() {
        return staffApi;
    }

    protected SurveyService getSurveyApi() {
        return surveyApi;
    }

    public ChargeService getChargeApi() {
        return chargeApi;
    }

    public CreateSavingsAccountService getCreateSavingsAccountService() {
        return createSavingsAccountService;
    }
}
