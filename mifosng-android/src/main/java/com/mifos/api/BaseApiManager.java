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
import com.mifos.utils.PrefManager;

import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author fomenkoo
 */
public class BaseApiManager {


    private static Retrofit mRetrofit;
    private static AuthService authApi;
    private static CenterService centerApi;
    private static ClientAccountsService accountsApi;
    private static ClientService clientsApi;
    private static DataTableService dataTableApi;
    private static LoanService loanApi;
    private static SavingsAccountService savingsApi;
    private static ChargeService chargeApi;
    private static CreateSavingsAccountService createSavingsAccountService;
    private static SearchService searchApi;
    private static GpsCoordinatesService gpsApi;
    private static GroupService groupApi;
    private static DocumentService documentApi;
    private static IdentifierService identifierApi;
    private static OfficeService officeApi;
    private static StaffService staffApi;
    private static SurveyService surveyApi;
    private static GroupAccountService groupAccountsServiceApi;

    public BaseApiManager() {
        createService();
    }

    public static void init() {
        authApi = createApi(AuthService.class);
        centerApi = createApi(CenterService.class);
        accountsApi = createApi(ClientAccountsService.class);
        clientsApi = createApi(ClientService.class);
        dataTableApi = createApi(DataTableService.class);
        loanApi = createApi(LoanService.class);
        savingsApi = createApi(SavingsAccountService.class);
        searchApi = createApi(SearchService.class);
        gpsApi = createApi(GpsCoordinatesService.class);
        groupApi = createApi(GroupService.class);
        documentApi = createApi(DocumentService.class);
        identifierApi = createApi(IdentifierService.class);
        officeApi = createApi(OfficeService.class);
        staffApi = createApi(StaffService.class);
        surveyApi = createApi(SurveyService.class);
        chargeApi = createApi(ChargeService.class);
        createSavingsAccountService = createApi(CreateSavingsAccountService.class);
        groupAccountsServiceApi = createApi(GroupAccountService.class);
    }

    private static <T> T createApi(Class<T> clazz) {
        return mRetrofit.create(clazz);
    }

    public static void createService() {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDateSerializer()).create();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(PrefManager.getInstanceUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(new MifosOkHttpClient().getMifosOkHttpClient())
                .build();
        init();
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
