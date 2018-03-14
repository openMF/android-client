package com.mifos.mifosxdroid.online.savingsaccount;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Savings;
import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;

import java.util.List;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public interface SavingsAccountMvpView extends MvpView {

    void showSavingsAccounts(List<ProductSavings> productSavings);

    void showSavingsAccountCreatedSuccessfully(Savings savings);

    void showSavingsAccountTemplateByProduct(SavingProductsTemplate savingProductsTemplate);

    void showFetchingError(int errorMessage);

    void showFetchingError(String errorMessage);
}
