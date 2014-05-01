package com.mifos.objects.db;

import com.orm.SugarRecord;

public class Product extends SugarRecord<Product>
{
    private String productShortName;
    private int productId;
    private int loanId;

    public int getLoanId()
    {
        return loanId;
    }

    public void setLoanId(int loanId)
    {
        this.loanId = loanId;
    }


    @Override
    public String toString()
    {
        return "Product{" +
                "productShortName='" + productShortName + '\'' +
                ", productId=" + productId +
                ", loanId=" + loanId +
                '}';
    }

    public String getProductShortName()
    {
        return productShortName;
    }

    public void setProductShortName(String productShortName)
    {
        this.productShortName = productShortName;
    }

    public int getProductId()
    {
        return productId;
    }

    public void setProductId(int productId)
    {
        this.productId = productId;
    }
}
