/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.api.model;

//TODO Remove calendarId and TransactionDate from this Payload class;
public class Payload {
    private String dateFormat = "dd MMMM YYYY";
    private String locale = "en";
    private long calendarId;
    private String transactionDate;

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(long calendarId) {
        this.calendarId = calendarId;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "{" +
                "dateFormat='" + dateFormat + '\'' +
                ", locale='" + locale + '\'' +
                ", calendarId=" + calendarId +
                ", transactionDate='" + transactionDate + '\'' +
                '}';
    }
}
