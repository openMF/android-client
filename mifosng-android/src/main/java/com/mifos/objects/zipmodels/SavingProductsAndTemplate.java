package com.mifos.objects.zipmodels;

import com.mifos.objects.organisation.ProductSavings;
import com.mifos.objects.templates.savings.SavingProductsTemplate;

import java.util.List;

/**
 * Created by Rajan Maurya on 02/09/16.
 */
public class SavingProductsAndTemplate {

    List<ProductSavings> mProductSavings;

    SavingProductsTemplate mSavingProductsTemplate;

    public SavingProductsAndTemplate(List<ProductSavings> mProductSavings,
                                     SavingProductsTemplate mSavingProductsTemplate) {
        this.mProductSavings = mProductSavings;
        this.mSavingProductsTemplate = mSavingProductsTemplate;
    }

    public List<ProductSavings> getmProductSavings() {
        return mProductSavings;
    }

    public void setmProductSavings(List<ProductSavings> mProductSavings) {
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
