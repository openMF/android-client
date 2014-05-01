package com.mifos.objects.db;

import com.orm.SugarRecord;

public class Interest extends SugarRecord<Interest>
{
    private int interestDue;
    private int interestPaid;
    private int loanId;

    public int getLoanId()
    {
        return loanId;
    }

    public void setLoanId(int loanId)
    {
        this.loanId = loanId;
    }
    public int getInterestPaid()
    {
        return interestPaid;
    }

    public void setInterestPaid(int interestPaid)
    {
        this.interestPaid = interestPaid;
    }

    public int getInterestDue()
    {
        return interestDue;
    }

    public void setInterestDue(int interestDue)
    {
        this.interestDue = interestDue;
    }

    @Override
    public String toString()
    {
        return "Interest{" +
                "interestDue=" + interestDue +
                ", interestPaid=" + interestPaid +
                ", loanId=" + loanId +
                '}';
    }
}
