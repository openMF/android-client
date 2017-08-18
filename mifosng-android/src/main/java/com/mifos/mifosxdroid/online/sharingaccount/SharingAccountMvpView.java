package com.mifos.mifosxdroid.online.sharingaccount;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.organisation.ProductSharing;
import com.mifos.objects.response.ShareResponse;
import com.mifos.objects.templates.clients.ChargeOptions;
import com.mifos.objects.templates.sharing.SharingAccountsTemplate;

import java.util.List;

/**
 * Created by mayankjindal on 18/08/17.
 */

public interface SharingAccountMvpView extends MvpView {

    void showSharingAccountTemplate(List<ProductSharing> productSavings,
                                    List<ChargeOptions> chargeOptions);

    void showSavingsAccountTemplateByProduct(SharingAccountsTemplate sharingAccountsTemplate);

    void showSharingAccountCreatedSuccessfully(ShareResponse shareResponse);

    void showFetchingError(int errorMessage);

    void showFetchingError(String errorMessage);
}
