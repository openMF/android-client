package com.mifos.objects.db;

import com.orm.SugarRecord;

public class Principal extends SugarRecord<Principal>
{
    private int principalDue;
    private int principalPaid;
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
        return "Principal{" +
                "principalDue=" + principalDue +
                ", principalPaid=" + principalPaid +
                ", loanId=" + loanId +
                '}';
    }

    public int getPrincipalDue()
    {
        return principalDue;
    }

    public void setPrincipalDue(int principalDue)
    {
        this.principalDue = principalDue;
    }

    public int getPrincipalPaid()
    {
        return principalPaid;
    }

    public void setPrincipalPaid(int principalPaid)
    {
        this.principalPaid = principalPaid;
    }
}
