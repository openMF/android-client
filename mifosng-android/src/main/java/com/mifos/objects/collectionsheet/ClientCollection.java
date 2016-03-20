/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.objects.collectionsheet;

import com.mifos.objects.Currency;

import java.util.List;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class ClientCollection {

    private int clientId;
    private String clientName;
    private List<Loans> loans;
    private EntityType attendanceType;

    public List<Loans> getLoans() {
        return loans;
    }

    public void setLoans(List<Loans> loans) {
        this.loans = loans;
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


    public EntityType getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(EntityType attendanceType) {
        this.attendanceType = attendanceType;
    }

    @Override
    public String toString() {
        return "ClientCollection{" +
                "clientId=" + clientId +
                ", clientName='" + clientName + '\'' +
                ", loans=" + loans +
                ", attendanceType=" + attendanceType +
                '}';
    }

    public class Loans{

        private int loanId;
        private int accountId;
        private int accountStatusId;
        private String productShortName;
        private int productId;
        private Currency currency;
        private int principalDue;
        private int principalPaid;
        private int interestDue;
        private int interestPaid;
        private int totalDue;

        public int getTotalDue() {
            return totalDue;
        }

        public void setTotalDue(int totalDue) {
            this.totalDue = totalDue;
        }

        public int getInterestPaid() {
            return interestPaid;
        }

        public void setInterestPaid(int interestPaid) {
            this.interestPaid = interestPaid;
        }

        public int getInterestDue() {
            return interestDue;
        }

        public void setInterestDue(int interestDue) {
            this.interestDue = interestDue;
        }

        public int getPrincipalPaid() {
            return principalPaid;
        }

        public void setPrincipalPaid(int principalPaid) {
            this.principalPaid = principalPaid;
        }

        public int getPrincipalDue() {
            return principalDue;
        }

        public void setPrincipalDue(int principalDue) {
            this.principalDue = principalDue;
        }

        public Currency getCurrency() {
            return currency;
        }

        public void setCurrency(Currency currency) {
            this.currency = currency;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getAccountStatusId() {
            return accountStatusId;
        }

        public void setAccountStatusId(int accountStatusId) {
            this.accountStatusId = accountStatusId;
        }

        public String getProductShortName() {
            return productShortName;
        }

        public void setProductShortName(String productShortName) {
            this.productShortName = productShortName;
        }

        public int getAccountId() {
            return accountId;
        }

        public void setAccountId(int accountId) {
            this.accountId = accountId;
        }

        public int getLoanId() {
            return loanId;
        }

        public void setLoanId(int loanId) {
            this.loanId = loanId;
        }

        @Override
        public String toString() {
            return "Loans{" +
                    "loanId=" + loanId +
                    ", accountId=" + accountId +
                    ", accountStatusId=" + accountStatusId +
                    ", productShortName='" + productShortName + '\'' +
                    ", productId=" + productId +
                    ", currency=" + currency +
                    ", principalDue=" + principalDue +
                    ", principalPaid=" + principalPaid +
                    ", interestDue=" + interestDue +
                    ", interestPaid=" + interestPaid +
                    ", totalDue=" + totalDue +
                    '}';
        }
    }
}
