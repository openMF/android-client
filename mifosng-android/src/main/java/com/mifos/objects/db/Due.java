package com.mifos.objects.db;

import com.orm.SugarRecord;

public class Due extends SugarRecord<Due>
{
    private int chargesDue;
    private int totalDue;
    private int loanId;

    public int getLoanId()
    {
        return loanId;
    }

    public void setLoanId(int loanId)
    {
        this.loanId = loanId;
    }
    public int getChargesDue()
    {
        return chargesDue;
    }

    public void setChargesDue(int chargesDue)
    {
        this.chargesDue = chargesDue;
    }

    public int getTotalDue()
    {
        return totalDue;
    }

    public void setTotalDue(int totalDue)
    {
        this.totalDue = totalDue;
    }

    @Override
    public String toString()
    {
        return "Due{" +
                "chargesDue=" + chargesDue +
                ", totalDue=" + totalDue +
                ", loanId=" + loanId +
                '}';
    }
}
