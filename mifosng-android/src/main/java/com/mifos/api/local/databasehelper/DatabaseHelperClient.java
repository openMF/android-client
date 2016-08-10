package com.mifos.api.local.databasehelper;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.loan.LoanAccount_Table;
import com.mifos.objects.accounts.savings.SavingsAccount;
import com.mifos.objects.accounts.savings.SavingsAccount_Table;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.ClientDate;
import com.mifos.objects.client.ClientPayload;
import com.mifos.objects.client.ClientPayload_Table;
import com.mifos.objects.client.Client_Table;
import com.mifos.objects.client.Page;
import com.mifos.objects.templates.clients.ClientsTemplate;
import com.mifos.objects.templates.clients.InterestType;
import com.mifos.objects.templates.clients.OfficeOptions;
import com.mifos.objects.templates.clients.Options;
import com.mifos.objects.templates.clients.Options_Table;
import com.mifos.objects.templates.clients.SavingProductOptions;
import com.mifos.objects.templates.clients.StaffOptions;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

/**
 * This DatabaseHelper Managing all Database logic and staff (Saving, Update, Delete).
 * Whenever DataManager send response to save or request to read from Database then this class
 * save the response or read the all values from database and return as accordingly.
 * <p/>
 * Created by Rajan Maurya on 24/06/16.
 */
@Singleton
public class DatabaseHelperClient {

    public static final String GENDER_OPTIONS = "genderOptions";
    public static final String CLIENT_TYPE_OPTIONS = "clientTypeOptions";
    public static final String CLIENT_CLASSIFICATION_OPTIONS = "clientClassificationOptions";

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

                    ClientDate clientDate = new ClientDate(client.getId(), 0,
                            client.getActivationDate().get(0),
                            client.getActivationDate().get(1),
                            client.getActivationDate().get(2));
                    client.setClientDate(clientDate);
                    client.save();
                }
            }
        });
        return null;
    }

    /**
     * This Method save the single Client in Database with ClientId as Primary Id
     *
     * @param client Client
     * @return saved Client
     */
    public Observable<Client> saveClient(final Client client) {
        return Observable.defer(new Func0<Observable<Client>>() {
            @Override
            public Observable<Client> call() {
                //Saving Client in Database
                ClientDate clientDate = new ClientDate(client.getId(), 0,
                        client.getActivationDate().get(0),
                        client.getActivationDate().get(1),
                        client.getActivationDate().get(2));
                client.setClientDate(clientDate);
                client.save();
                return Observable.just(client);
            }
        });
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

                if (client != null) {
                    client.setActivationDate(Arrays.asList(client.getClientDate().getDay(),
                            client.getClientDate().getMonth(), client.getClientDate().getYear()));
                }

                subscriber.onNext(client);

            }
        });
    }


    /**
     * This Method  write the ClientAccount in tho DB. According to Schema Defined in Model
     *
     * @param clientAccounts Model of List of LoanAccount and SavingAccount
     * @param clientId       Client Id
     * @return null
     */
    public Observable<ClientAccounts> saveClientAccounts(final ClientAccounts clientAccounts,
                                               final int clientId) {

        return Observable.defer(new Func0<Observable<ClientAccounts>>() {
            @Override
            public Observable<ClientAccounts> call() {

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

                return Observable.just(clientAccounts);
            }
        });
    }


    /**
     * This Method Read the Table of LoanAccount and SavingAccount and return the List of
     * LoanAccount and SavingAccount according to clientId
     *
     * @param clientId Client Id
     * @return Return the ClientAccount according to client Id
     */
    public Observable<ClientAccounts> realClientAccounts(final int clientId) {
        return Observable.create(new Observable.OnSubscribe<ClientAccounts>() {
            @Override
            public void call(Subscriber<? super ClientAccounts> subscriber) {

                List<LoanAccount> loanAccounts = SQLite.select()
                        .from(LoanAccount.class)
                        .where(LoanAccount_Table.clientId.eq(clientId))
                        .queryList();

                List<SavingsAccount> savingsAccounts = SQLite.select()
                        .from(SavingsAccount.class)
                        .where(SavingsAccount_Table.clientId.eq(clientId))
                        .queryList();

                ClientAccounts clientAccounts = new ClientAccounts();
                clientAccounts.setLoanAccounts(loanAccounts);
                clientAccounts.setSavingsAccounts(savingsAccounts);

                subscriber.onNext(clientAccounts);

            }
        });
    }


    /**
     * Saving ClientTemplate into Database ClientTemplate_Table
     * @param clientsTemplate fetched from Server
     * @return void
     */
    public Observable<ClientsTemplate> saveClientTemplate(final ClientsTemplate clientsTemplate) {
        return Observable.defer(new Func0<Observable<ClientsTemplate>>() {
            @Override
            public Observable<ClientsTemplate> call() {
                //saving clientTemplate into DB;
                clientsTemplate.save();

                for (OfficeOptions officeOptions : clientsTemplate.getOfficeOptions()) {
                    officeOptions.save();
                }

                for (StaffOptions staffOptions : clientsTemplate.getStaffOptions()) {
                    staffOptions.save();
                }

                for (SavingProductOptions savingProductOptions : clientsTemplate
                        .getSavingProductOptions()) {
                    savingProductOptions.save();
                }

                for (Options options : clientsTemplate.getGenderOptions()) {
                    options.setGenderOptions(GENDER_OPTIONS);
                    options.save();
                }

                for (Options options : clientsTemplate.getClientTypeOptions()) {
                    options.setClientTypeOptions(CLIENT_TYPE_OPTIONS);
                    options.save();
                }

                for (Options options : clientsTemplate.getClientClassificationOptions()) {
                    options.setClientClassificationOptions(CLIENT_CLASSIFICATION_OPTIONS);
                    options.save();
                }

                for (InterestType interestType : clientsTemplate.getClientLegalFormOptions()) {
                    interestType.save();
                }

                return Observable.just(clientsTemplate);
            }
        });
    }


    /**
     * Reading ClientTemplate from Database ClientTemplate_Table
     * @return ClientTemplate
     */
    public Observable<ClientsTemplate> readClientTemplate() {
        return Observable.defer(new Func0<Observable<ClientsTemplate>>() {
            @Override
            public Observable<ClientsTemplate> call() {

                ClientsTemplate clientsTemplate = SQLite.select()
                        .from(ClientsTemplate.class)
                        .querySingle();

                List<OfficeOptions> officeOptionses = SQLite.select()
                        .from(OfficeOptions.class)
                        .queryList();

                List<StaffOptions> staffOptionses = SQLite.select()
                        .from(StaffOptions.class)
                        .queryList();

                List<SavingProductOptions> savingProductOptionses = SQLite.select()
                        .from(SavingProductOptions.class)
                        .queryList();

                List<Options> genderOptions = SQLite.select()
                        .from(Options.class)
                        .where(Options_Table.genderOptions.eq(GENDER_OPTIONS))
                        .queryList();

                List<Options> clientTypeOptions = SQLite.select()
                        .from(Options.class)
                        .where(Options_Table.clientTypeOptions.eq(CLIENT_TYPE_OPTIONS))
                        .queryList();

                List<Options> clientClassificationOptions = SQLite.select()
                        .from(Options.class)
                        .where(Options_Table
                                .clientClassificationOptions.eq(CLIENT_CLASSIFICATION_OPTIONS))
                        .queryList();

                List<InterestType> clientLegalFormOptions = SQLite.select()
                        .from(InterestType.class)
                        .queryList();

                assert clientsTemplate != null;
                clientsTemplate.setOfficeOptions(officeOptionses);
                clientsTemplate.setStaffOptions(staffOptionses);
                clientsTemplate.setSavingProductOptions(savingProductOptionses);
                clientsTemplate.setGenderOptions(genderOptions);
                clientsTemplate.setClientTypeOptions(clientTypeOptions);
                clientsTemplate.setClientClassificationOptions(clientClassificationOptions);
                clientsTemplate.setClientLegalFormOptions(clientLegalFormOptions);

                return Observable.just(clientsTemplate);
            }
        });
    }


    /**
     * Saving ClientPayload into Database ClientPayload_Table
     * @param clientPayload created in offline mode
     * @return Client
     */
    public Observable<Client> saveClientPayloadToDB(final ClientPayload clientPayload) {
        return Observable.create(new Observable.OnSubscribe<Client>() {
            @Override
            public void call(Subscriber<? super Client> subscriber) {
                clientPayload.save();
                subscriber.onNext(new Client());
            }
        });
    }


    /**
     * Reading All Entries in the ClientPayload_Table
     * @return List<ClientPayload></>
     */
    public Observable<List<ClientPayload>> readAllClientPayload() {
        return Observable.create(new Observable.OnSubscribe<List<ClientPayload>>() {
            @Override
            public void call(Subscriber<? super List<ClientPayload>> subscriber) {

                List<ClientPayload> clientPayloads = SQLite.select()
                        .from(ClientPayload.class)
                        .queryList();

                subscriber.onNext(clientPayloads);
            }
        });
    }


    /**
     * This Method for deleting the client payload from the Database according to Id and
     * again fetch the client List from the Database ClientPayload_Table
     * @param id is Id of the Client Payload in which reference client was saved into Database
     * @return List<ClientPayload></>
     */
    public Observable<List<ClientPayload>> deleteAndUpdatePayloads(final int id) {
        return Observable.create(new Observable.OnSubscribe<List<ClientPayload>>() {
            @Override
            public void call(Subscriber<? super List<ClientPayload>> subscriber) {

                Delete.table(ClientPayload.class, ClientPayload_Table.id.eq(id));

                List<ClientPayload> clientPayloads = SQLite.select()
                        .from(ClientPayload.class)
                        .queryList();

                subscriber.onNext(clientPayloads);

            }
        });

    }

    public Observable<ClientPayload> updateDatabaseClientPayload(final ClientPayload
                                                                         clientPayload) {
        return Observable.defer(new Func0<Observable<ClientPayload>>() {
            @Override
            public Observable<ClientPayload> call() {
                clientPayload.update();
                return Observable.just(clientPayload);
            }
        });
    }
}
