package com.mifos.api.datamanager;

import android.support.annotation.NonNull;

import com.mifos.api.BaseApiManager;
import com.mifos.objects.response.ShareResponse;
import com.mifos.objects.templates.sharing.SharingProductTemplate;
import com.mifos.objects.templates.sharing.SharingAccountsTemplate;
import com.mifos.services.data.SharingPayload;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by mayankjindal on 18/08/17.
 */
/**
 * This DataManager is for Managing Client's Share Account, In which Request is going to Server
 * and In Response, We are getting Client's Share Account Observable Response using Retrofit2 .
 */
public class DataManagerSharing {

    public final BaseApiManager baseApiManager;

    @Inject
    public DataManagerSharing(@NonNull BaseApiManager baseApiManager) {
        this.baseApiManager = baseApiManager;
    }

    /**
     * This Method to request the SharingAccountsTemplate according to Client Id and Product Id
     * and get SharingAccountsTemplate in Response. Request goes to the Server End Point directly.
     * Here is End Point :
     * {https://demo.openmf.org/fineract-provider/api/v1/accounts/share/template?clientId=1&productId=1}
     * @param clientId productId
     * @return SharingAccountsTemplate
     */
    @NonNull
    public Observable<SharingAccountsTemplate> getClientSharingAccountTemplateByProduct(
            int clientId, int productId) {
        return baseApiManager.getSharingApi().
                getClientSharingAccountTemplateByProduct(clientId, productId);
    }

    /**
     * This Method to request the SharingProductTemplate according to Client Id
     * and get SharingProductTemplate in Response. Request goes to the Server End Point directly.
     * Here is End Point :
     * {https://demo.openmf.org/fineract-provider/api/v1/accounts/share/template?clientId=1}
     * @param clientId productId
     * @return SharingAccountsTemplate
     */
    @NonNull
    public Observable<SharingProductTemplate> getSharingAccountTemplate(int clientId) {
        return baseApiManager.getSharingApi().getSharingAccountTemplate(clientId);
    }

    /**
     * This Method create the Sharing account by making directly request to server
     * and give response
     * @param sharingPayload Share Account details filled by user
     * @return ShareResponse
     */
    @NonNull
    public Observable<ShareResponse> createSharingAccount(@NonNull SharingPayload sharingPayload) {
        return baseApiManager.getSharingApi().createSharingAccount(sharingPayload);
    }
}
