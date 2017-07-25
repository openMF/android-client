package com.mifos.objects.accounts;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.savings.SavingsAccount;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayankjindal on 11/07/17.
 */

public class CenterAccounts implements Parcelable {

    private List<LoanAccount> loanAccounts = new ArrayList<LoanAccount>();
    private List<SavingsAccount> savingsAccounts = new ArrayList<SavingsAccount>();
    private List<LoanAccount> memberLoanAccounts = new ArrayList<LoanAccount>();

    public List<LoanAccount> getLoanAccounts() {
        return loanAccounts;
    }

    public void setLoanAccounts(List<LoanAccount> loanAccounts) {
        this.loanAccounts = loanAccounts;
    }

    public CenterAccounts withLoanAccounts(List<LoanAccount> loanAccounts) {
        this.loanAccounts = loanAccounts;
        return this;
    }

    public List<SavingsAccount> getSavingsAccounts() {
        return savingsAccounts;
    }

    public void setSavingsAccounts(List<SavingsAccount> savingsAccounts) {
        this.savingsAccounts = savingsAccounts;
    }

    public List<LoanAccount> getMemberLoanAccounts() {
        return memberLoanAccounts;
    }

    public void setMemberLoanAccounts(List<LoanAccount> memberLoanAccounts) {
        this.memberLoanAccounts = memberLoanAccounts;
    }

    public CenterAccounts withSavingsAccounts(List<SavingsAccount> savingsAccounts) {
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
                ", memberLoanAccounts=" + memberLoanAccounts +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.loanAccounts);
        dest.writeTypedList(this.savingsAccounts);
        dest.writeTypedList(this.memberLoanAccounts);
    }

    public CenterAccounts() {
    }

    protected CenterAccounts(Parcel in) {
        this.loanAccounts = in.createTypedArrayList(LoanAccount.CREATOR);
        this.savingsAccounts = in.createTypedArrayList(SavingsAccount.CREATOR);
        this.memberLoanAccounts = in.createTypedArrayList(LoanAccount.CREATOR);
    }

    public static final Parcelable.Creator<CenterAccounts> CREATOR = new Parcelable
            .Creator<CenterAccounts>() {
        @Override
        public CenterAccounts createFromParcel(Parcel source) {
            return new CenterAccounts(source);
        }

        @Override
        public CenterAccounts[] newArray(int size) {
            return new CenterAccounts[size];
        }
    };
}
