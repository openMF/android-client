package com.mifos.objects.collectionsheet;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Tarun on 06-07-2017.
 */

public class ClientCollectionSheet implements Parcelable {

    private int clientId;
    private String clientName;

    private ArrayList<LoanCollectionSheet> loans;

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
        return loans;
    }

    public void setLoans(ArrayList<LoanCollectionSheet> loans) {
        this.loans = loans;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.clientId);
        dest.writeString(this.clientName);
        dest.writeList(this.loans);
    }

    public ClientCollectionSheet() {
    }

    protected ClientCollectionSheet(Parcel in) {
        this.clientId = in.readInt();
        this.clientName = in.readString();
        this.loans = new ArrayList<LoanCollectionSheet>();
        in.readList(this.loans, LoanCollectionSheet.class.getClassLoader());
    }

    public static final Parcelable.Creator<ClientCollectionSheet> CREATOR = new
            Parcelable.Creator<ClientCollectionSheet>() {
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
