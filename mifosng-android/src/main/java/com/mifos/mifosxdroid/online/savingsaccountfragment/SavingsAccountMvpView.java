package com.mifos.mifosxdroid.online.savingsaccountfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Savings;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;
import com.mifos.services.data.SavingsPayload;

import java.util.List;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public interface SavingsAccountMvpView extends MvpView {

    void showAccountCreationStatus(Savings savings);

    void ResponseError(String s);

    void showSavingProductsTemplate(SavingProductsTemplate savingProductsTemplate);

    void showAllSavingAccount(List<ProductSavings> productSavingses );
}
