package com.mifos.objects.sync;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rajan Maurya on 09/08/16.
 */
public class SyncClientInformationStatus implements Parcelable {

    Boolean clientAndAccountsStatus;

    Boolean loanAccountSummaryStatus;

    Boolean loanRepaymentTemplateStatus;

    public Boolean getSyncClientInformationStatus() {
        Boolean status = false;
        if (clientAndAccountsStatus && loanAccountSummaryStatus && loanRepaymentTemplateStatus) {
            status = true;
        }
        return status;
    }

    public Boolean getClientAndAccountsStatus() {
        return clientAndAccountsStatus;
    }

    public void setClientAndAccountsStatus(Boolean clientAndAccountsStatus) {
        this.clientAndAccountsStatus = clientAndAccountsStatus;
    }

    public Boolean getLoanAccountSummaryStatus() {
        return loanAccountSummaryStatus;
    }

    public void setLoanAccountSummaryStatus(Boolean loanAccountSummaryStatus) {
        this.loanAccountSummaryStatus = loanAccountSummaryStatus;
    }

    public Boolean getLoanRepaymentTemplateStatus() {
        return loanRepaymentTemplateStatus;
    }

    public void setLoanRepaymentTemplateStatus(Boolean loanRepaymentTemplateStatus) {
        this.loanRepaymentTemplateStatus = loanRepaymentTemplateStatus;
    }

    @Override
    public String toString() {
        return "SyncClientInformationStatus{" +
                "clientAndAccountsStatus=" + clientAndAccountsStatus +
                ", loanAccountSummaryStatus=" + loanAccountSummaryStatus +
                ", loanRepaymentTemplateStatus=" + loanRepaymentTemplateStatus +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.clientAndAccountsStatus);
        dest.writeValue(this.loanAccountSummaryStatus);
        dest.writeValue(this.loanRepaymentTemplateStatus);
    }

    public SyncClientInformationStatus() {
    }

    protected SyncClientInformationStatus(Parcel in) {
        this.clientAndAccountsStatus = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.loanAccountSummaryStatus = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.loanRepaymentTemplateStatus = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Parcelable.Creator<SyncClientInformationStatus> CREATOR =
            new Parcelable.Creator<SyncClientInformationStatus>() {
        @Override
        public SyncClientInformationStatus createFromParcel(Parcel source) {
            return new SyncClientInformationStatus(source);
        }

        @Override
        public SyncClientInformationStatus[] newArray(int size) {
            return new SyncClientInformationStatus[size];
        }
    };
}
