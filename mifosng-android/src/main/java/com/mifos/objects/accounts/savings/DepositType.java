package com.mifos.objects.accounts.savings;

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
public class DepositType {

    private static enum ServerTypes {
        // TODO: Are these all the types?
        SAVINGS(100, "depositAccountType.savingsDeposit"),
        FIXED(200, "depositAccountType.fixedDeposit"),
        RECURRING(300, "depositAccountType.recurringDeposit");

        private Integer id;
        private String code;

        ServerTypes(Integer id, String code) {
            this.id = id;
            this.code = code;
        }

        public Integer getId() {
            return id;
        }

        public String getCode() {
            return code;
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
        return this.code == ServerTypes.RECURRING.getCode();
    }

    @Override
    public String toString() {
        return "DepositType{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
