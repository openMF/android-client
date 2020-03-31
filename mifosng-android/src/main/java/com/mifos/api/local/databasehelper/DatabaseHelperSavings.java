package com.mifos.api.local.databasehelper;

import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest_Table;
import com.mifos.objects.accounts.savings.SavingsAccountTransactionResponse;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations_Table;
import com.mifos.objects.accounts.savings.SavingsTransactionDate;
import com.mifos.objects.accounts.savings.Transaction;
import com.mifos.objects.accounts.savings.Transaction_Table;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate_Table;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;

/**
 * Created by Rajan Maurya on 17/08/16.
 */
@Singleton
public class DatabaseHelperSavings {


    @Inject
    public DatabaseHelperSavings() {
    }


    /**
     * This Saving the SavingsAccountSummary template into Database
     * SavingsAccountWithAssociations_Table with the Primary key SavingsAccountId.
     *
     * @param savingsAccountWithAssociations SavingAccountSummary Template.
     * @return SavingsAccountWithAssociations.
     */
    public Observable<SavingsAccountWithAssociations> saveSavingsAccount(
            final SavingsAccountWithAssociations savingsAccountWithAssociations) {
        return Observable.defer(new Func0<Observable<SavingsAccountWithAssociations>>() {
            @Override
            public Observable<SavingsAccountWithAssociations> call() {

                List<Transaction> transactions = savingsAccountWithAssociations.getTransactions();

                if (transactions.size() != 0) {
                    Observable.from(transactions)
                            .subscribe(new Action1<Transaction>() {
                                @Override
                                public void call(Transaction transaction) {
                                    SavingsTransactionDate savingsTransactionDate =
                                            new SavingsTransactionDate(transaction.getId(),
                                                    transaction.getDate().get(0),
                                                    transaction.getDate().get(1),
                                                    transaction.getDate().get(2));
                                    transaction.setSavingsAccountId
                                            (savingsAccountWithAssociations.getId());
                                    transaction.setSavingsTransactionDate(savingsTransactionDate);
                                    transaction.save();
                                }
                            });
                }
                savingsAccountWithAssociations.getSummary().setSavingsId
                        (savingsAccountWithAssociations.getId());
                savingsAccountWithAssociations.save();

                return Observable.just(savingsAccountWithAssociations);
            }
        });
    }

    /**
     * This Method Read the SavingsAccountSummary Template from the
     * SavingsAccountWithAssociations_Table and give the response SavingsAccountWithAssociations.
     * If the Query returns the null its means SavingsAccountSummary template doest exist into the
     * Database with savingAccountId.
     *
     * @param savingsAccountId Savings Account Id
     * @return SavingsAccountWithAssociations SavingsAccountSummary Template.
     */
    public Observable<SavingsAccountWithAssociations> readSavingsAccount(
            final int savingsAccountId) {
        return Observable.defer(new Func0<Observable<SavingsAccountWithAssociations>>() {
            @Override
            public Observable<SavingsAccountWithAssociations> call() {

                SavingsAccountWithAssociations savingsAccountWithAssociations = SQLite.select()
                        .from(SavingsAccountWithAssociations.class)
                        .where(SavingsAccountWithAssociations_Table.id.eq(savingsAccountId))
                        .querySingle();


                List<Transaction> transactions = SQLite.select()
                        .from(Transaction.class)
                        .where(Transaction_Table.savingsAccountId.eq(savingsAccountId))
                        .queryList();

                Observable.from(transactions)
                        .subscribe(new Action1<Transaction>() {
                            @Override
                            public void call(Transaction transaction) {
                                transaction.setDate(Arrays.asList(
                                        transaction.getSavingsTransactionDate().getYear(),
                                        transaction.getSavingsTransactionDate().getMonth(),
                                        transaction.getSavingsTransactionDate().getDay()));
                            }
                        });

                if (savingsAccountWithAssociations != null) {
                    savingsAccountWithAssociations.setTransactions(transactions);
                }

                return Observable.just(savingsAccountWithAssociations);
            }
        });
    }


    /**
     * This Method is Saving the SavingsAccountTransactionTemplate into Database.
     *
     * @param savingsAccountTransactionTemplate SavingsAccountTransactionTemplate
     * @return SavingsAccountTransactionTemplate
     */
    public Observable<SavingsAccountTransactionTemplate> saveSavingsAccountTransactionTemplate(
            final SavingsAccountTransactionTemplate savingsAccountTransactionTemplate) {
        return Observable.defer(new Func0<Observable<SavingsAccountTransactionTemplate>>() {
            @Override
            public Observable<SavingsAccountTransactionTemplate> call() {

                Observable.from(savingsAccountTransactionTemplate.getPaymentTypeOptions())
                        .subscribe(new Action1<PaymentTypeOption>() {
                            @Override
                            public void call(PaymentTypeOption paymentTypeOption) {
                                paymentTypeOption.save();
                            }
                        });

                savingsAccountTransactionTemplate.save();

                return Observable.just(savingsAccountTransactionTemplate);
            }
        });
    }


    /**
     * This method is making Query to the Database SavingsAccountTransactionTemplate_Table and
     * with primary key SavingsAccount Id and returns the SavingsAccountTransactionTemplate
     * with Primary Key. If SavingsAccountTransactionTemplate does not exist it returns null.
     * otherwise returns SavingsAccountTransactionTemplate.
     *
     * @param savingsAccountId SavingAccount id
     * @return SavingsAccountTransactionTemplate
     */
    public Observable<SavingsAccountTransactionTemplate> readSavingsAccountTransactionTemplate(
            final int savingsAccountId) {
        return Observable.defer(new Func0<Observable<SavingsAccountTransactionTemplate>>() {
            @Override
            public Observable<SavingsAccountTransactionTemplate> call() {

                SavingsAccountTransactionTemplate savingsAccountTransactionTemplate =
                        SQLite.select()
                                .from(SavingsAccountTransactionTemplate.class)
                                .where(SavingsAccountTransactionTemplate_Table
                                        .accountId.eq(savingsAccountId))
                                .querySingle();

                List<PaymentTypeOption> paymentTypeOptions = SQLite.select()
                        .from(PaymentTypeOption.class)
                        .queryList();

                if (savingsAccountTransactionTemplate != null) {
                    savingsAccountTransactionTemplate.setPaymentTypeOptions(paymentTypeOptions);
                }

                return Observable.just(savingsAccountTransactionTemplate);
            }
        });
    }


    /**
     * This Method saving the SavingAccountTransaction into Database
     * SavingsAccountTransactionRequest_Table , If user have no internet
     * connection or if user making transaction in offline mode.
     *
     * @param savingsAccountTransactionRequest SavingsAccountTransactionRequest Body
     * @param savingsAccountId                 SavingAccount Id
     * @param savingsAccountType               SavingAccountType
     * @param transactionType                  Transaction Type
     *
     * @return SavingsAccountTransactionResponse
     */
    public Observable<SavingsAccountTransactionResponse> saveSavingsAccountTransaction(
            final String savingsAccountType, final int savingsAccountId,
            final String transactionType,
            final SavingsAccountTransactionRequest savingsAccountTransactionRequest) {
        return Observable.defer(new Func0<Observable<SavingsAccountTransactionResponse>>() {
            @Override
            public Observable<SavingsAccountTransactionResponse> call() {

                savingsAccountTransactionRequest.setSavingAccountId(savingsAccountId);
                savingsAccountTransactionRequest.setSavingsAccountType(savingsAccountType);
                savingsAccountTransactionRequest.setTransactionType(transactionType);
                savingsAccountTransactionRequest.save();

                return Observable.just(new SavingsAccountTransactionResponse());
            }
        });
    }

    /**
     * This Method, retrieving SavingsAccountTransactionRequest with the Saving Id from Database
     * SavingsAccountTransactionRequest_Table. If no entry found with the SavingsAccount Id. It
     * returns null.
     *
     * @param savingsAccountId SavingAccount Id
     * @return SavingsAccountTransactionRequest
     */
    public Observable<SavingsAccountTransactionRequest> getSavingsAccountTransaction(
            final int savingsAccountId) {
        return Observable.defer(new Func0<Observable<SavingsAccountTransactionRequest>>() {
            @Override
            public Observable<SavingsAccountTransactionRequest> call() {

                SavingsAccountTransactionRequest savingsAccountTransactionRequest =
                        SQLite.select()
                                .from(SavingsAccountTransactionRequest.class)
                                .where(SavingsAccountTransactionRequest_Table
                                        .savingAccountId.eq(savingsAccountId))
                                .querySingle();

                return Observable.just(savingsAccountTransactionRequest);
            }
        });
    }

    /**
     * This Method Load all Transactions from the SavingsAccountTransactionRequest_Table
     * and give the List<SavingsAccountTransactionRequest> response.
     *
     * @return List<SavingsAccountTransactionRequest>
     */
    public Observable<List<SavingsAccountTransactionRequest>> getAllSavingsAccountTransaction() {
        return Observable.defer(new Func0<Observable<List<SavingsAccountTransactionRequest>>>() {
            @Override
            public Observable<List<SavingsAccountTransactionRequest>> call() {

                List<SavingsAccountTransactionRequest> savingsAccountTransactionRequests =
                        SQLite.select()
                                .from(SavingsAccountTransactionRequest.class)
                                .queryList();

                return Observable.just(savingsAccountTransactionRequests);
            }
        });
    }

    /**
     * This Method Deleting the SavingsAccountTransaction with the SavingsAccount Id and loading the
     * List<SavingsAccountTransactionRequest> from Database and return
     * List<SavingsAccountTransactionRequest> to DataManagerSavings and DataManagerSaving sync the
     * List<SavingsAccountTransactionRequest> to the SyncSavingsAccountTransaction.
     *
     * @param  savingsAccountId SavingsAccount Id
     * @return List<SavingsAccountTransactionRequest>
     */
    public Observable<List<SavingsAccountTransactionRequest>> deleteAndUpdateTransaction(
            final int savingsAccountId) {
        return Observable.defer(new Func0<Observable<List<SavingsAccountTransactionRequest>>>() {
            @Override
            public Observable<List<SavingsAccountTransactionRequest>> call() {

                //Deleting Entry from SavingsAccountTransactionRequest_Table with SavingsAccountId
                Delete.table(SavingsAccountTransactionRequest.class,
                        SavingsAccountTransactionRequest_Table
                                .savingAccountId.eq(savingsAccountId));

                List<SavingsAccountTransactionRequest> savingsAccountTransactionRequests =
                        SQLite.select()
                                .from(SavingsAccountTransactionRequest.class)
                                .queryList();

                return Observable.just(savingsAccountTransactionRequests);
            }
        });
    }

    /**
     * This Method updating the SavingsAccountTransactionRequest to Database Table.
     * this method will be called whenever error will come during sync the LoanRepayment. This
     * method saving the Error message to the Error Table Column .
     *
     * @param savingsAccountTransactionRequest SavingsAccountTransaction to update
     * @return SavingsAccountTransactionRequest
     */
    public Observable<SavingsAccountTransactionRequest> updateSavingsAccountTransaction(
            final SavingsAccountTransactionRequest savingsAccountTransactionRequest) {
        return Observable.defer(new Func0<Observable<SavingsAccountTransactionRequest>>() {
            @Override
            public Observable<SavingsAccountTransactionRequest> call() {
                savingsAccountTransactionRequest.update();
                return Observable.just(savingsAccountTransactionRequest);
            }
        });
    }
}
