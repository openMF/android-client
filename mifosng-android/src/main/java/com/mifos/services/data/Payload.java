package com.mifos.services.data;

public class Payload
{
    private String dateFormat = "dd MMMM YYYY";
    private String locale = "en";
    private int calendarId;
    private String transactionDate;

    public String getDateFormat()
    {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat)
    {
        this.dateFormat = dateFormat;
    }

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public int getCalendarId()
    {
        return calendarId;
    }

    public void setCalendarId(int calendarId)
    {
        this.calendarId = calendarId;
    }

    public String getTransactionDate()
    {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate)
    {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString()
    {
        return "{" +
                "dateFormat='" + dateFormat + '\'' +
                ", locale='" + locale + '\'' +
                ", calendarId=" + calendarId +
                ", transactionDate='" + transactionDate + '\'' +
                '}';
    }
}
