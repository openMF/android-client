/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api;

import com.google.gson.GsonBuilder;
import com.mifos.api.services.AuthService;
import com.mifos.api.services.CenterService;
import com.mifos.api.services.ChargeService;
import com.mifos.api.services.ClientAccountSummaryService;
import com.mifos.api.services.ClientAccountsService;
import com.mifos.api.services.ClientService;
import com.mifos.api.services.CreateSavingsAccountService;
import com.mifos.api.services.DataTableService;
import com.mifos.api.services.DocumentService;
import com.mifos.api.services.GpsCoordinatesService;
import com.mifos.api.services.GroupService;
import com.mifos.api.services.IdentifierService;
import com.mifos.api.services.LoanService;
import com.mifos.api.services.OfficeService;
import com.mifos.api.services.SavingsAccountService;
import com.mifos.api.services.SearchService;
import com.mifos.api.services.StaffService;
import com.mifos.api.services.SurveyService;

import retrofit.Endpoint;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * @author fomenkoo
 */
public class BaseApiManager {


    private ApiEndpoint API_ENDPOINT = new ApiEndpoint();

    private AuthService authApi;
    private CenterService centerApi;
    private ClientAccountsService accountsApi;
    private ClientService clientsApi;
    private DataTableService dataTableApi;
    private LoanService loanApi;
    private SavingsAccountService savingsApi;
    private ChargeService chargeService;
    private CreateSavingsAccountService createSavingsAccountService;
    private SearchService searchApi;
    private GpsCoordinatesService gpsApi;
    private GroupService groupApi;
    private DocumentService documentApi;
    private IdentifierService identifierApi;
    private OfficeService officeApi;
    private StaffService staffApi;
    private SurveyService surveyApi;
    private ClientAccountSummaryService accountSummaryApi;

    public BaseApiManager() {
        createAuthApi();

        centerApi = createApi(CenterService.class, API_ENDPOINT);
        accountsApi = createApi(ClientAccountsService.class, API_ENDPOINT);
        clientsApi = createApi(ClientService.class, API_ENDPOINT);
        dataTableApi = createApi(DataTableService.class, API_ENDPOINT);
        loanApi = createApi(LoanService.class, API_ENDPOINT);
        savingsApi = createApi(SavingsAccountService.class, API_ENDPOINT);
        searchApi = createApi(SearchService.class, API_ENDPOINT);
        gpsApi = createApi(GpsCoordinatesService.class, API_ENDPOINT);
        groupApi = createApi(GroupService.class, API_ENDPOINT);
        documentApi = createApi(DocumentService.class, API_ENDPOINT);
        identifierApi = createApi(IdentifierService.class, API_ENDPOINT);
        officeApi = createApi(OfficeService.class, API_ENDPOINT);
        staffApi = createApi(StaffService.class, API_ENDPOINT);
        surveyApi = createApi(SurveyService.class, API_ENDPOINT);
        chargeService = createApi(ChargeService.class, API_ENDPOINT);
        createSavingsAccountService = createApi(CreateSavingsAccountService.class, API_ENDPOINT);
        accountSummaryApi = createApi(ClientAccountSummaryService.class, API_ENDPOINT);
    }

    public void setupEndpoint(String instanceUrl) {
        API_ENDPOINT.updateInstanceUrl(instanceUrl);
    }

    private <T> T createApi(Class<T> clazz, Endpoint endpoint) {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setRequestInterceptor(new ApiRequestInterceptor())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new GsonBuilder().create()))
                .build()
                .create(clazz);
    }

    private void createAuthApi() {
        authApi = new RestAdapter.Builder()
                .setEndpoint(API_ENDPOINT)
                .setConverter(new GsonConverter(new GsonBuilder().create()))
                .setRequestInterceptor(new ApiRequestInterceptor())
                .setLogLevel(RestAdapter.LogLevel.FULL)
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

    protected ClientAccountSummaryService getAccountSummaryApi() {
        return accountSummaryApi;
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

    public ChargeService getChargeService() {
        return chargeService;
    }

    public CreateSavingsAccountService getCreateSavingsAccountService() {
        return createSavingsAccountService;
    }
}
