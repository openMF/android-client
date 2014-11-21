package com.mifos.objects.accounts.savings;

import android.os.Parcel;
import android.os.Parcelable;

import com.mifos.services.data.APIEndPoint;

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
public class DepositType implements Parcelable {

    public static enum ServerTypes {
        // TODO: Are these all the types?
        SAVINGS(100, "depositAccountType.savingsDeposit", APIEndPoint.SAVINGSACCOUNTS),
        FIXED(200, "depositAccountType.fixedDeposit", APIEndPoint.SAVINGSACCOUNTS),
        RECURRING(300, "depositAccountType.recurringDeposit", APIEndPoint.RECURRING_ACCOUNTS);

        private Integer id;
        private String code;
        private String endpoint;

        ServerTypes(Integer id, String code, String endpoint) {
            this.id = id;
            this.code = code;
            this.endpoint = endpoint;
        }

        public Integer getId() {
            return id;
        }

        public String getCode() {
            return code;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public static ServerTypes fromId(int id) {
            for (ServerTypes type : ServerTypes.values()) {
                if (type.getId().equals(id)) {
                    return type;
                }
            }
            return SAVINGS;
        }
    }

    private Integer id;
    private String code;
    private String value;

    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public boolean isRecurring() {
        return ServerTypes.RECURRING.getId().equals(this.getId());
    }

    public String getEndpoint() {
        return ServerTypes.fromId(getId()).getEndpoint();
    }

    public ServerTypes getServerType() {
        return ServerTypes.fromId(getId());
    }

    @Override
    public String toString() {
        return "DepositType{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.code);
        dest.writeString(this.value);
    }

    public DepositType() {
    }

    private DepositType(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.code = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<DepositType> CREATOR = new Parcelable.Creator<DepositType>() {
        public DepositType createFromParcel(Parcel source) {
            return new DepositType(source);
        }

        public DepositType[] newArray(int size) {
            return new DepositType[size];
        }
    };
}
