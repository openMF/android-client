package com.mifos.api.local.databasehelper;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.loan.LoanAccount_Table;
import com.mifos.objects.accounts.savings.SavingsAccount;
import com.mifos.objects.accounts.savings.SavingsAccount_Table;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.Client_Table;
import com.mifos.objects.client.Page;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Rajan Maurya on 24/06/16.
 */
@Singleton
public class DatabaseHelperClient {

    @Inject
    public DatabaseHelperClient() {
    }


    /**
     * Saving Clients in Database using DBFlow.
     * save() method save the value reference to primary key if its exist the update if not the
     * insert.
     *
     * @param clientPage
     * @return null
     */
    @Nullable
    public Observable<Void> saveAllClients(final Page<Client> clientPage) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                for (Client client : clientPage.getPageItems()) {
                    client.save();
                }
            }
        });
        return null;
    }

    /**
     * Reading All Clients from table of Client and return the ClientList
     *
     * @return List Of Client
     */
    //TODO Implement Observable Transaction to load Client List
    public Observable<Page<Client>> readAllClients() {

        return Observable.create(new Observable.OnSubscribe<Page<Client>>() {
            @Override
            public void call(Subscriber<? super Page<Client>> subscriber) {

                Page<Client> clientPage = new Page<>();
                clientPage.setPageItems(SQLite.select()
                        .from(Client.class)
                        .queryList());
                subscriber.onNext(clientPage);
                subscriber.onCompleted();
            }
        });

    }

    /**
     * This Method select query with clientId, In return the Client Details will be come.
     *
     * @param clientId of the client
     * @return Client
     */
    public Observable<Client> getClient(final int clientId) {
        return Observable.create(new Observable.OnSubscribe<Client>() {
            @Override
            public void call(Subscriber<? super Client> subscriber) {

                Client client = SQLite.select()
                        .from(Client.class)
                        .where(Client_Table.id.eq(clientId))
                        .querySingle();

                subscriber.onNext(client);

            }
        });
    }

    public Observable<Void> saveClientAccounts(final ClientAccounts clientAccounts,
                                               final int clientId) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {

                List<LoanAccount> loanAccounts = clientAccounts.getLoanAccounts();
                List<SavingsAccount> savingsAccounts = clientAccounts.getSavingsAccounts();

                for (LoanAccount loanAccount : loanAccounts) {
                    loanAccount.setClientId(clientId);
                    loanAccount.save();
                }

                for (SavingsAccount savingsAccount : savingsAccounts) {
                    savingsAccount.setClientId(clientId);
                    savingsAccount.save();
                }
            }
        });
        return null;
    }


    public Observable<ClientAccounts> realClientAccounts(final int clientIs) {
        return Observable.create(new Observable.OnSubscribe<ClientAccounts>() {
            @Override
            public void call(Subscriber<? super ClientAccounts> subscriber) {

                List<LoanAccount> loanAccounts = SQLite.select()
                        .from(LoanAccount.class)
                        .where(LoanAccount_Table.clientId.eq(clientIs))
                        .queryList();

                List<SavingsAccount> savingsAccounts = SQLite.select()
                        .from(SavingsAccount.class)
                        .where(SavingsAccount_Table.clientId.eq(clientIs))
                        .queryList();

                ClientAccounts clientAccounts = new ClientAccounts();
                clientAccounts.setLoanAccounts(loanAccounts);
                clientAccounts.setSavingsAccounts(savingsAccounts);

                subscriber.onNext(clientAccounts);

            }
        });
    }
}
