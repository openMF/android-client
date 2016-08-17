package com.mifos.api.local.databasehelper;

import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations_Table;
import com.mifos.objects.accounts.savings.SavingsTransactionDate;
import com.mifos.objects.accounts.savings.Transaction;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * Created by Rajan Maurya on 17/08/16.
 */
@Singleton
public class DatabaseHelperSavings {


    @Inject
    public DatabaseHelperSavings() {

    }


    public Observable<SavingsAccountWithAssociations> saveSavingsAccount(
            final SavingsAccountWithAssociations savingsAccountWithAssociations) {
        return Observable.defer(new Func0<Observable<SavingsAccountWithAssociations>>() {
            @Override
            public Observable<SavingsAccountWithAssociations> call() {

                List<Transaction> transactions = savingsAccountWithAssociations.getTransactions();

                if (transactions.size() != 0) {
                    Observable.from(transactions)
                            .flatMap(new Func1<Transaction, Observable<Transaction>>() {
                                @Override
                                public Observable<Transaction> call(Transaction transaction) {
                                    return Observable.just(transaction);
                                }
                            })
                            .subscribe(new Action1<Transaction>() {
                                @Override
                                public void call(Transaction transaction) {
                                    SavingsTransactionDate savingsTransactionDate =
                                            new SavingsTransactionDate(transaction.getId(),
                                                    transaction.getDate().get(0),
                                                    transaction.getDate().get(1),
                                                    transaction.getDate().get(2));
                                    savingsTransactionDate.save();
                                    /*transaction.associateSavingsAccount(
                                            savingsAccountWithAssociations);
                                    transaction.save();*/
                                }
                            });
                }

                savingsAccountWithAssociations.save();

                return Observable.just(savingsAccountWithAssociations);
            }
        });
    }


    public Observable<SavingsAccountWithAssociations> readSavingsAccount(
            final int savingsAccountId) {
        return Observable.defer(new Func0<Observable<SavingsAccountWithAssociations>>() {
            @Override
            public Observable<SavingsAccountWithAssociations> call() {

                SavingsAccountWithAssociations savingsAccountWithAssociations = SQLite.select()
                        .from(SavingsAccountWithAssociations.class)
                        .where(SavingsAccountWithAssociations_Table.id.eq(savingsAccountId))
                        .querySingle();



                if (savingsAccountWithAssociations != null) {
                    List<Transaction> transactions = savingsAccountWithAssociations.getTransactions();
                    Observable.from(transactions)
                            .flatMap(new Func1<Transaction, Observable<?>>() {
                                @Override
                                public Observable<?> call(Transaction transaction) {

                                    return null;
                                }
                            });
                }


                return null;
            }
        });
    }
}
