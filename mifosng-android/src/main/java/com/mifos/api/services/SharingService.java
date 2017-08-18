package com.mifos.api.services;

import android.support.annotation.NonNull;

import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.response.ShareResponse;
import com.mifos.objects.templates.sharing.SharingProductTemplate;
import com.mifos.objects.templates.sharing.SharingAccountsTemplate;
import com.mifos.services.data.SharingPayload;
import com.mifos.utils.Constants;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by mayankjindal on 18/08/17.
 */

public interface SharingService {

    /**
     * This Service is for fetching the SharingProductTemplate.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/accounts/share/template?clientId={clientId}
     *
     * @param clientId Client Id
     * @return SharingProductTemplate
     */
    @NonNull
    @GET(APIEndPoint.CREATE_SHARING_ACCOUNTS + "/template")
    Observable<SharingProductTemplate> getSharingAccountTemplate(@Query(Constants.CLIENT_ID)
                                                                         int clientId);

    /**
     * This Service is for fetching the SharingAccountsTemplate.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/accounts/share/template?clientId={clientId}&productId={productId}
     *
     * @param clientId Client Id
     * @return SharingProductTemplate
     */
    @NonNull
    @GET(APIEndPoint.CREATE_SHARING_ACCOUNTS + "/template")
    Observable<SharingAccountsTemplate> getClientSharingAccountTemplateByProduct(
            @Query(Constants.CLIENT_ID) int clientId, @Query(Constants.PRODUCT_ID) int productId);

    /**
     * This Service is for creating the SharingAccount.
     * REST END POINT:
     * https://demo.openmf.org/fineract-provider/api/v1/accounts/share
     *
     * @param sharingPayload SharingPayload
     * @return ShareResponse
     */
    @NonNull
    @POST(APIEndPoint.CREATE_SHARING_ACCOUNTS)
    Observable<ShareResponse> createSharingAccount(@Body SharingPayload sharingPayload);
}
