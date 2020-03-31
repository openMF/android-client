/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.accounts;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.savings.SavingsAccount;

import java.util.ArrayList;
import java.util.List;

public class ClientAccounts implements Parcelable {

    private List<LoanAccount> loanAccounts = new ArrayList<LoanAccount>();
    private List<SavingsAccount> savingsAccounts = new ArrayList<SavingsAccount>();

    public ClientAccounts() {
    }

    protected ClientAccounts(Parcel in) {
        this.loanAccounts = in.createTypedArrayList(LoanAccount.CREATOR);
        this.savingsAccounts = new ArrayList<SavingsAccount>();
        in.readList(this.savingsAccounts, SavingsAccount.class.getClassLoader());
    }

    public List<LoanAccount> getLoanAccounts() {
        return loanAccounts;
    }

    public void setLoanAccounts(List<LoanAccount> loanAccounts) {
        this.loanAccounts = loanAccounts;
    }

    public ClientAccounts withLoanAccounts(List<LoanAccount> loanAccounts) {
        this.loanAccounts = loanAccounts;
        return this;
    }

    public List<SavingsAccount> getSavingsAccounts() {
        return savingsAccounts;
    }

    public void setSavingsAccounts(List<SavingsAccount> savingsAccounts) {
        this.savingsAccounts = savingsAccounts;
    }

    public ClientAccounts withSavingsAccounts(List<SavingsAccount> savingsAccounts) {
        this.savingsAccounts = savingsAccounts;
        return this;
    }

    public List<SavingsAccount> getRecurringSavingsAccounts() {
        return getSavingsAccounts(true);
    }

    public List<SavingsAccount> getNonRecurringSavingsAccounts() {
        return getSavingsAccounts(false);
    }

    private List<SavingsAccount> getSavingsAccounts(boolean wantRecurring) {
        List<SavingsAccount> result = new ArrayList<SavingsAccount>();
        if (this.savingsAccounts != null) {
            for (SavingsAccount account : savingsAccounts) {
                if (account.isRecurring() == wantRecurring) {
                    result.add(account);
                }
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "ClientAccounts{" +
                "loanAccounts=" + loanAccounts +
                ", savingsAccounts=" + savingsAccounts +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(loanAccounts);
        dest.writeList(this.savingsAccounts);
    }

    public static final Parcelable.Creator<ClientAccounts> CREATOR = new Parcelable
            .Creator<ClientAccounts>() {
        @Override
        public ClientAccounts createFromParcel(Parcel source) {
            return new ClientAccounts(source);
        }

        @Override
        public ClientAccounts[] newArray(int size) {
            return new ClientAccounts[size];
        }
    };
}
