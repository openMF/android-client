package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.organisation.LoanProducts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tarun on 25-07-2017.
 */

public class CollectionSheetResponse implements Parcelable {

    private List<AttendanceTypeOption> attendanceTypeOptions = new ArrayList<>();

    private int[] dueDate;

    private List<GroupCollectionSheet> groups = new ArrayList<>();

    private List<LoanProducts> loanProducts = new ArrayList<>();

    private List<PaymentTypeOption> paymentTypeOptions = new ArrayList<>();

    private List<SavingsProduct> savingsProducts = new ArrayList<>();

    public List<AttendanceTypeOption> getAttendanceTypeOptions() {
        return attendanceTypeOptions;
    }

    public void setAttendanceTypeOptions(List<AttendanceTypeOption> attendanceTypeOptions) {
        this.attendanceTypeOptions = attendanceTypeOptions;
    }

    public int[] getDueDate() {
        return dueDate;
    }

    public void setDueDate(int[] dueDate) {
        this.dueDate = dueDate;
    }

    public List<GroupCollectionSheet> getGroups() {
        return groups;
    }

    public void setGroups(List<GroupCollectionSheet> groups) {
        this.groups = groups;
    }

    public List<LoanProducts> getLoanProducts() {
        return loanProducts;
    }

    public void setLoanProducts(List<LoanProducts> loanProducts) {
        this.loanProducts = loanProducts;
    }

    public List<PaymentTypeOption> getPaymentTypeOptions() {
        return paymentTypeOptions;
    }

    public void setPaymentTypeOptions(List<PaymentTypeOption> paymentTypeOptions) {
        this.paymentTypeOptions = paymentTypeOptions;
    }

    public List<SavingsProduct> getSavingsProducts() {
        return savingsProducts;
    }

    public void setSavingsProducts(List<SavingsProduct> savingsProducts) {
        this.savingsProducts = savingsProducts;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.attendanceTypeOptions);
        dest.writeIntArray(this.dueDate);
        dest.writeTypedList(this.groups);
        dest.writeTypedList(this.loanProducts);
        dest.writeTypedList(this.paymentTypeOptions);
        dest.writeTypedList(this.savingsProducts);
    }

    public CollectionSheetResponse() {
    }

    protected CollectionSheetResponse(Parcel in) {
        this.attendanceTypeOptions = in.createTypedArrayList(AttendanceTypeOption.CREATOR);
        this.dueDate = in.createIntArray();
        this.groups = in.createTypedArrayList(GroupCollectionSheet.CREATOR);
        this.loanProducts = in.createTypedArrayList(LoanProducts.CREATOR);
        this.paymentTypeOptions = in.createTypedArrayList(PaymentTypeOption.CREATOR);
        this.savingsProducts = in.createTypedArrayList(SavingsProduct.CREATOR);
    }

    public static final Creator<CollectionSheetResponse> CREATOR = new
            Creator<CollectionSheetResponse>() {
        @Override
        public CollectionSheetResponse createFromParcel(Parcel source) {
            return new CollectionSheetResponse(source);
        }

        @Override
        public CollectionSheetResponse[] newArray(int size) {
            return new CollectionSheetResponse[size];
        }
    };
}
