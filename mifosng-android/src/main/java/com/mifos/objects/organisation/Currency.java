package com.mifos.objects.organisation;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Rajan Maurya on 15/07/16.
 */
public class Currency implements Parcelable {

    @SerializedName("code")
    String code;

    @SerializedName("name")
    String name;

    @SerializedName("decimalPlaces")
    Integer decimalPlaces;

    @SerializedName("inMultiplesOf")
    Integer inMultiplesOf;

    @SerializedName("displaySymbol")
    String displaySymbol;

    @SerializedName("nameCode")
    String nameCode;

    @SerializedName("displayLabel")
    String displayLabel;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(Integer decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public Integer getInMultiplesOf() {
        return inMultiplesOf;
    }

    public void setInMultiplesOf(Integer inMultiplesOf) {
        this.inMultiplesOf = inMultiplesOf;
    }

    public String getDisplaySymbol() {
        return displaySymbol;
    }

    public void setDisplaySymbol(String displaySymbol) {
        this.displaySymbol = displaySymbol;
    }

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", decimalPlaces=" + decimalPlaces +
                ", inMultiplesOf=" + inMultiplesOf +
                ", displaySymbol='" + displaySymbol + '\'' +
                ", nameCode='" + nameCode + '\'' +
                ", displayLabel='" + displayLabel + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Currency currency = (Currency) o;

        if (!code.equals(currency.code)) return false;
        if (!name.equals(currency.name)) return false;
        if (!decimalPlaces.equals(currency.decimalPlaces)) return false;
        if (!inMultiplesOf.equals(currency.inMultiplesOf)) return false;
        if (!displaySymbol.equals(currency.displaySymbol)) return false;
        if (!nameCode.equals(currency.nameCode)) return false;
        return displayLabel.equals(currency.displayLabel);

    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + decimalPlaces.hashCode();
        result = 31 * result + inMultiplesOf.hashCode();
        result = 31 * result + displaySymbol.hashCode();
        result = 31 * result + nameCode.hashCode();
        result = 31 * result + displayLabel.hashCode();
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.code);
        dest.writeString(this.name);
        dest.writeValue(this.decimalPlaces);
        dest.writeValue(this.inMultiplesOf);
        dest.writeString(this.displaySymbol);
        dest.writeString(this.nameCode);
        dest.writeString(this.displayLabel);
    }

    public Currency() {
    }

    protected Currency(Parcel in) {
        this.code = in.readString();
        this.name = in.readString();
        this.decimalPlaces = (Integer) in.readValue(Integer.class.getClassLoader());
        this.inMultiplesOf = (Integer) in.readValue(Integer.class.getClassLoader());
        this.displaySymbol = in.readString();
        this.nameCode = in.readString();
        this.displayLabel = in.readString();
    }

    public static final Parcelable.Creator<Currency> CREATOR = new Parcelable.Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel source) {
            return new Currency(source);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };
}
