/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.client.Savings;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;
import com.mifos.services.data.SavingsPayload;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

/**
 * @author
 */
public interface CreateSavingsAccountService {
    @GET(APIEndPoint.CREATESAVINGSPRODUCTS)
    Observable<List<ProductSavings>> getAllSavingsAccounts();

    @POST(APIEndPoint.CREATESAVINGSACCOUNTS)
    Observable<Savings> createSavingsAccount(@Body SavingsPayload savingsPayload);

	@GET(APIEndPoint.CREATESAVINGSPRODUCTS + "/template")
	Observable<SavingProductsTemplate> getSavingsProductsTemplate();
}
