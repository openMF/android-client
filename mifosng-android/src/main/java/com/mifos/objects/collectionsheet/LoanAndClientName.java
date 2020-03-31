package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tarun on 17-07-2017.
 */

public class LoanAndClientName implements Parcelable {

    private LoanCollectionSheet loan;

    private String clientName;
    private  int id;

    public LoanAndClientName(LoanCollectionSheet loan, String clientName, int id) {
        this.loan = loan;
        this.clientName = clientName;
        this.id = id;
    }

    public LoanCollectionSheet getLoan() {
        return loan;
    }

    public String getClientName() {
        return clientName;
    }

    public int getId() {
        return id;
    }


    protected LoanAndClientName(Parcel in) {
        loan = (LoanCollectionSheet) in.readValue(LoanCollectionSheet.class.getClassLoader());
        clientName = in.readString();
        id = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(loan);
        dest.writeString(clientName);
        dest.writeInt(id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LoanAndClientName> CREATOR = new
            Parcelable.Creator<LoanAndClientName>() {
        @Override
        public LoanAndClientName createFromParcel(Parcel in) {
            return new LoanAndClientName(in);
        }

        @Override
        public LoanAndClientName[] newArray(int size) {
            return new LoanAndClientName[size];
        }
    };
}