package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Tarun on 06-07-2017.
 */

public class ClientCollectionSheet implements Parcelable {

    private int clientId;
    private String clientName;

    @Nullable
    @SerializedName("loans")
    private ArrayList<LoanCollectionSheet> loanCollectionSheetList;

    private AttendanceTypeOption attendanceType;

    private ArrayList<SavingsCollectionSheet> savings = new ArrayList<>();

    public ArrayList<SavingsCollectionSheet> getSavings() {
        return savings;
    }

    public void setSavings(ArrayList<SavingsCollectionSheet> savings) {
        this.savings = savings;
    }

    public AttendanceTypeOption getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(AttendanceTypeOption attendanceType) {
        this.attendanceType = attendanceType;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public ArrayList<LoanCollectionSheet> getLoans() {
        return loanCollectionSheetList;
    }

    public void setLoans(ArrayList<LoanCollectionSheet> loans) {
        this.loanCollectionSheetList = loans;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.clientId);
        dest.writeString(this.clientName);
        dest.writeParcelable(this.attendanceType, flags);
        dest.writeTypedList(this.savings);
        dest.writeList(this.loanCollectionSheetList);
    }

    public ClientCollectionSheet() {
    }

    protected ClientCollectionSheet(Parcel in) {
        this.clientId = in.readInt();
        this.clientName = in.readString();
        this.attendanceType = in.readParcelable(AttendanceTypeOption.class.getClassLoader());
        this.savings = in.createTypedArrayList(SavingsCollectionSheet.CREATOR);
        this.loanCollectionSheetList = new ArrayList<LoanCollectionSheet>();
        in.readList(this.loanCollectionSheetList, LoanCollectionSheet.class.getClassLoader());
    }

    public static final Creator<ClientCollectionSheet> CREATOR = new
            Creator<ClientCollectionSheet>() {
                @Override
                public ClientCollectionSheet createFromParcel(Parcel source) {
                    return new ClientCollectionSheet(source);
                }

                @Override
                public ClientCollectionSheet[] newArray(int size) {
                    return new ClientCollectionSheet[size];
                }
            };
}
