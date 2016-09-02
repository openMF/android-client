package com.mifos.objects.zipmodels;

import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;

/**
 * Created by Rajan Maurya on 02/09/16.
 */
public class SavingProductsAndTemplate {

    ProductSavings mProductSavings;

    SavingProductsTemplate mSavingProductsTemplate;

    public ProductSavings getmProductSavings() {
        return mProductSavings;
    }

    public void setmProductSavings(ProductSavings mProductSavings) {
        this.mProductSavings = mProductSavings;
    }

    public SavingProductsTemplate getmSavingProductsTemplate() {
        return mSavingProductsTemplate;
    }

    public void setmSavingProductsTemplate(SavingProductsTemplate mSavingProductsTemplate) {
        this.mSavingProductsTemplate = mSavingProductsTemplate;
    }

    @Override
    public String toString() {
        return "SavingProductsAndTemplate{" +
                "mProductSavings=" + mProductSavings +
                ", mSavingProductsTemplate=" + mSavingProductsTemplate +
                '}';
    }
}
