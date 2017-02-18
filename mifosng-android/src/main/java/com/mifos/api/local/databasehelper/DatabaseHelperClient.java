package com.mifos.api.local.databasehelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
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
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.objects.noncore.ColumnHeader;
import com.mifos.objects.noncore.ColumnHeader_Table;
import com.mifos.objects.noncore.ColumnValue;
import com.mifos.objects.noncore.ColumnValue_Table;
import com.mifos.objects.noncore.DataTable;
import com.mifos.objects.noncore.DataTablePayload;
import com.mifos.objects.noncore.DataTablePayload_Table;
import com.mifos.objects.noncore.DataTable_Table;
import com.mifos.objects.templates.clients.ClientsTemplate;
import com.mifos.objects.templates.clients.InterestType;
import com.mifos.objects.templates.clients.OfficeOptions;
import com.mifos.objects.templates.clients.Options;
import com.mifos.objects.templates.clients.Options_Table;
import com.mifos.objects.templates.clients.SavingProductOptions;
import com.mifos.objects.templates.clients.StaffOptions;
import com.mifos.utils.Constants;
import com.mifos.utils.MapDeserializer;
import com.raizlabs.android.dbflow.sql.language.Delete;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;

import static com.raizlabs.android.dbflow.sql.language.SQLite.select;

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

    private Gson gson;
    private Type type;

    @Inject
    public DatabaseHelperClient() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(new TypeToken<HashMap<String, Object>>() {
                }.getType(), new MapDeserializer());
        gson = gsonBuilder.create();
        type = new TypeToken<HashMap<String, Object>>() {
        }.getType();
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
                clientPage.setPageItems(select()
                        .from(Client.class)
                        .queryList());
                subscriber.onNext(clientPage);
                subscriber.onCompleted();
            }
        });

    }

    public Observable<GroupWithAssociations> getGroupAssociateClients(final int groupId) {
        return Observable.defer(new Func0<Observable<GroupWithAssociations>>() {
            @Override
            public Observable<GroupWithAssociations> call() {

                List<Client> clients = select()
                        .from(Client.class)
                        .where(Client_Table.groupId.eq(groupId))
                        .queryList();
                GroupWithAssociations groupWithAssociations = new GroupWithAssociations();
                groupWithAssociations.setClientMembers(clients);

                return Observable.just(groupWithAssociations);
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

                Client client = select()
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

                List<LoanAccount> loanAccounts = select()
                        .from(LoanAccount.class)
                        .where(LoanAccount_Table.clientId.eq(clientId))
                        .queryList();

                List<SavingsAccount> savingsAccounts = select()
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
     *
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
                    options.setOptionType(GENDER_OPTIONS);
                    options.save();
                }

                for (Options options : clientsTemplate.getClientTypeOptions()) {
                    options.setOptionType(CLIENT_TYPE_OPTIONS);
                    options.save();
                }

                for (Options options : clientsTemplate.getClientClassificationOptions()) {
                    options.setOptionType(CLIENT_CLASSIFICATION_OPTIONS);
                    options.save();
                }

                for (InterestType interestType : clientsTemplate.getClientLegalFormOptions()) {
                    interestType.save();
                }

                for (DataTable dataTable : clientsTemplate.getDataTables()) {

                    Delete.table(DataTable.class);
                    Delete.table(ColumnHeader.class);
                    Delete.table(ColumnValue.class);

                    dataTable.save();
                    for (ColumnHeader columnHeader : dataTable.getColumnHeaderData()) {
                        columnHeader.setRegisteredTableName(dataTable.getRegisteredTableName());
                        columnHeader.save();
                        for (ColumnValue columnValue : columnHeader.getColumnValues()) {
                            columnValue.setRegisteredTableName(dataTable.getRegisteredTableName());
                            columnValue.save();
                        }
                    }
                }

                return Observable.just(clientsTemplate);
            }
        });
    }


    /**
     * Reading ClientTemplate from Database ClientTemplate_Table
     *
     * @return ClientTemplate
     */
    public Observable<ClientsTemplate> readClientTemplate() {
        return Observable.defer(new Func0<Observable<ClientsTemplate>>() {
            @Override
            public Observable<ClientsTemplate> call() {

                ClientsTemplate clientsTemplate = select()
                        .from(ClientsTemplate.class)
                        .querySingle();

                List<OfficeOptions> officeOptionses = select()
                        .from(OfficeOptions.class)
                        .queryList();

                List<StaffOptions> staffOptionses = select()
                        .from(StaffOptions.class)
                        .queryList();

                List<SavingProductOptions> savingProductOptionses = select()
                        .from(SavingProductOptions.class)
                        .queryList();

                List<Options> genderOptions = select()
                        .from(Options.class)
                        .where(Options_Table.optionType.eq(GENDER_OPTIONS))
                        .queryList();

                List<Options> clientTypeOptions = select()
                        .from(Options.class)
                        .where(Options_Table.optionType.eq(CLIENT_TYPE_OPTIONS))
                        .queryList();

                List<Options> clientClassificationOptions = select()
                        .from(Options.class)
                        .where(Options_Table
                                .optionType.eq(CLIENT_CLASSIFICATION_OPTIONS))
                        .queryList();

                List<InterestType> clientLegalFormOptions = select()
                        .from(InterestType.class)
                        .queryList();

                List<DataTable> dataTables = select()
                        .from(DataTable.class)
                        .where(DataTable_Table.applicationTableName.eq(Constants
                                .DATA_TABLE_NAME_CLIENT))
                        .queryList();

                if (!dataTables.isEmpty()) {
                    for (DataTable dataTable : dataTables) {
                        List<ColumnHeader> columnHeaders = select()
                                .from(ColumnHeader.class)
                                .where(ColumnHeader_Table.registeredTableName
                                        .eq(dataTable.getRegisteredTableName()))
                                .queryList();
                        for (ColumnHeader columnHeader : columnHeaders) {
                            List<ColumnValue> columnValues = select()
                                    .from(ColumnValue.class)
                                    .where(ColumnValue_Table.registeredTableName.eq(dataTable
                                            .getRegisteredTableName()))
                                    .queryList();
                            if (!columnValues.isEmpty()) {
                                columnHeader.setColumnValues(columnValues);
                            }
                        }
                        if (!columnHeaders.isEmpty()) {
                            dataTable.setColumnHeaderData(columnHeaders);
                        }
                    }
                }

                assert clientsTemplate != null;
                clientsTemplate.setOfficeOptions(officeOptionses);
                clientsTemplate.setStaffOptions(staffOptionses);
                clientsTemplate.setSavingProductOptions(savingProductOptionses);
                clientsTemplate.setGenderOptions(genderOptions);
                clientsTemplate.setClientTypeOptions(clientTypeOptions);
                clientsTemplate.setClientClassificationOptions(clientClassificationOptions);
                clientsTemplate.setClientLegalFormOptions(clientLegalFormOptions);
                clientsTemplate.setDataTables(dataTables);

                return Observable.just(clientsTemplate);
            }
        });
    }


    /**
     * Saving ClientPayload into Database ClientPayload_Table
     *
     * @param clientPayload created in offline mode
     * @return Client
     */
    public Observable<Client> saveClientPayloadToDB(final ClientPayload clientPayload) {
        return Observable.defer(new Func0<Observable<Client>>() {
            @Override
            public Observable<Client> call() {
                final long currentTime = System.currentTimeMillis();
                clientPayload.setClientCreationTime(currentTime);
                if (!clientPayload.getDatatables().isEmpty()) {
                    Observable.from(clientPayload.getDatatables())
                            .subscribe(new Action1<DataTablePayload>() {
                                @Override
                                public void call(DataTablePayload dataTablePayload) {
                                    dataTablePayload.setClientCreationTime(currentTime);
                                    JsonObject jsonObject = gson.toJsonTree(dataTablePayload
                                            .getData()).getAsJsonObject();
                                    dataTablePayload.setDataTableString(jsonObject.toString());
                                    dataTablePayload.save();
                                }
                            });
                }
                clientPayload.save();
                return Observable.just(new Client());
            }
        });
    }


    /**
     * Reading All Entries in the ClientPayload_Table
     *
     * @return List<ClientPayload></>
     */
    public Observable<List<ClientPayload>> readAllClientPayload() {
        return Observable.defer(new Func0<Observable<List<ClientPayload>>>() {
            @Override
            public Observable<List<ClientPayload>> call() {
                List<ClientPayload> clientPayloads = select()
                        .from(ClientPayload.class)
                        .queryList();
                if (!clientPayloads.isEmpty()) {
                    Observable.from(clientPayloads).subscribe(new Action1<ClientPayload>() {
                        @Override
                        public void call(ClientPayload clientPayload) {
                            List<DataTablePayload> dataTablePayloads = select()
                                    .from(DataTablePayload.class)
                                    .where(DataTablePayload_Table.clientCreationTime
                                            .eq(clientPayload.getClientCreationTime()))
                                    .queryList();
                            if (!dataTablePayloads.isEmpty()) {
                                Observable.from(dataTablePayloads)
                                        .subscribe(new Action1<DataTablePayload>() {
                                            @Override
                                            public void call(DataTablePayload dataTablePayload) {
                                                HashMap<String, Object> data = gson.fromJson(
                                                        dataTablePayload.getDataTableString(),
                                                        type);
                                                dataTablePayload.setData(data);
                                            }
                                        });
                                clientPayload.setDatatables(dataTablePayloads);
                            }
                        }
                    });
                }
                return Observable.just(clientPayloads);
            }
        });
    }


    /**
     * This Method for deleting the client payload from the Database according to Id and
     * again fetch the client List from the Database ClientPayload_Table
     *
     * @param id is Id of the Client Payload in which reference client was saved into Database
     * @return List<ClientPayload></>
     */
    public Observable<List<ClientPayload>> deleteAndUpdatePayloads(final int id,
                                                                   final long clientCreationTIme) {
        return Observable.defer(new Func0<Observable<List<ClientPayload>>>() {
            @Override
            public Observable<List<ClientPayload>> call() {
                Delete.table(ClientPayload.class, ClientPayload_Table.id.eq(id));
                Delete.table(DataTablePayload.class, DataTablePayload_Table.clientCreationTime.eq
                        (clientCreationTIme));
                return readAllClientPayload();
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
