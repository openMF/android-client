package com.mifos.core.objects.zipmodels

import com.mifos.core.objects.organisation.ProductSavings
import com.mifos.core.objects.templates.savings.SavingProductsTemplate

/**
 * Created by Rajan Maurya on 02/09/16.
 */
class SavingProductsAndTemplate(
    var mProductSavings: List<ProductSavings>,
    var mSavingProductsTemplate: SavingProductsTemplate
) {
    fun getmProductSavings(): List<ProductSavings> {
        return mProductSavings
    }

    fun setmProductSavings(mProductSavings: List<ProductSavings>) {
        this.mProductSavings = mProductSavings
    }

    fun getmSavingProductsTemplate(): SavingProductsTemplate {
        return mSavingProductsTemplate
    }

    fun setmSavingProductsTemplate(mSavingProductsTemplate: SavingProductsTemplate) {
        this.mSavingProductsTemplate = mSavingProductsTemplate
    }

    override fun toString(): String {
        return "SavingProductsAndTemplate{" +
                "mProductSavings=" + mProductSavings +
                ", mSavingProductsTemplate=" + mSavingProductsTemplate +
                '}'
    }
}