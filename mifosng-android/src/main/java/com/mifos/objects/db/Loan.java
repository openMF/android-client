package com.mifos.objects.db;

import com.orm.SugarRecord;

public class Loan extends SugarRecord<Loan>
{
    private int loanId;
    private Account account;
    private Product product;
    private Currency currency;
    private int disbursementAmount;
    private Principal principal;
    private Interest interest;
    private int clientId;
    private Due due;
    public int getClientId()
    {
        return clientId;
    }

    public void setClientId(int clientId)
    {
        this.clientId = clientId;
    }

    @Override
    public String toString()
    {
        return "Loan{" +
                "loanId=" + loanId +
                ", account=" + account +
                ", product=" + product +
                ", currency=" + currency +
                ", disbursementAmount=" + disbursementAmount +
                ", principal=" + principal +
                ", interest=" + interest +
                ", clientId=" + clientId +
                ", due=" + due +
                '}';
    }

    public Due getDue()
    {
        return due;
    }

    public void setDue(Due due)
    {
        this.due = due;
    }

    public int getLoanId()
    {
        return loanId;
    }

    public void setLoanId(int loanId)
    {
        this.loanId = loanId;
    }

    public Account getAccount()
    {
        return account;
    }

    public void setAccount(Account account)
    {
        this.account = account;
    }

    public Product getProduct()
    {
        return product;
    }

    public void setProduct(Product product)
    {
        this.product = product;
    }

    public Currency getCurrency()
    {
        return currency;
    }

    public void setCurrency(Currency currency)
    {
        this.currency = currency;
    }

    public int getDisbursementAmount()
    {
        return disbursementAmount;
    }

    public void setDisbursementAmount(int disbursementAmount)
    {
        this.disbursementAmount = disbursementAmount;
    }

    public Principal getPrincipal()
    {
        return principal;
    }

    public void setPrincipal(Principal principal)
    {
        this.principal = principal;
    }

    public Interest getInterest()
    {
        return interest;
    }

    public void setInterest(Interest interest)
    {
        this.interest = interest;
    }



}
