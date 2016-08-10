package com.mifos.objects.sync;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rajan Maurya on 09/08/16.
 */
public class SyncClientInformationStatus implements Parcelable {

    Boolean clientAccountsStatus;

    Boolean loanAccountSummaryStatus;

    Boolean loanRepaymentTemplateStatus;

    Boolean clientStatus;

    public Boolean getSyncClientInformationStatus() {
        Boolean status = false;
        if (clientAccountsStatus && loanAccountSummaryStatus &&
                loanRepaymentTemplateStatus && clientStatus) {
            status = true;
        }
        return status;
    }

    public Boolean getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(Boolean clientStatus) {
        this.clientStatus = clientStatus;
    }

    public Boolean getClientAccountsStatus() {
        return clientAccountsStatus;
    }

    public void setClientAccountsStatus(Boolean clientAccountsStatus) {
        this.clientAccountsStatus = clientAccountsStatus;
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
                "clientAccountsStatus=" + clientAccountsStatus +
                ", loanAccountSummaryStatus=" + loanAccountSummaryStatus +
                ", loanRepaymentTemplateStatus=" + loanRepaymentTemplateStatus +
                '}';
    }


    public SyncClientInformationStatus() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.clientAccountsStatus);
        dest.writeValue(this.loanAccountSummaryStatus);
        dest.writeValue(this.loanRepaymentTemplateStatus);
        dest.writeValue(this.clientStatus);
    }

    protected SyncClientInformationStatus(Parcel in) {
        this.clientAccountsStatus = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.loanAccountSummaryStatus = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.loanRepaymentTemplateStatus = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.clientStatus = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<SyncClientInformationStatus> CREATOR =
            new Creator<SyncClientInformationStatus>() {
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
