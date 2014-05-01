package com.mifos.objects.db;

import com.orm.SugarRecord;

public class Currency extends SugarRecord<Currency>
{
    private String code;
    private String name;
    private int decimalPlaces;
    private int inMultiplesOf;
    private String displaySymbol;
    private String nameCode;
    private String displayLabel;
    private int loanId;

    public int getLoanId()
    {
        return loanId;
    }

    public void setLoanId(int loanId)
    {
        this.loanId = loanId;
    }
    public String getDisplayLabel()
    {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel)
    {
        this.displayLabel = displayLabel;
    }

    public String getNameCode()
    {
        return nameCode;
    }

    public void setNameCode(String nameCode)
    {
        this.nameCode = nameCode;
    }

    public String getDisplaySymbol()
    {
        return displaySymbol;
    }

    public void setDisplaySymbol(String displaySymbol)
    {
        this.displaySymbol = displaySymbol;
    }

    public int getInMultiplesOf()
    {
        return inMultiplesOf;
    }

    public void setInMultiplesOf(int inMultiplesOf)
    {
        this.inMultiplesOf = inMultiplesOf;
    }

    public int getDecimalPlaces()
    {
        return decimalPlaces;
    }

    public void setDecimalPlaces(int decimalPlaces)
    {
        this.decimalPlaces = decimalPlaces;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    @Override
    public String toString()
    {
        return "Currency{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", decimalPlaces=" + decimalPlaces +
                ", inMultiplesOf=" + inMultiplesOf +
                ", displaySymbol='" + displaySymbol + '\'' +
                ", nameCode='" + nameCode + '\'' +
                ", displayLabel='" + displayLabel + '\'' +
                ", loanId=" + loanId +
                '}';
    }
}
