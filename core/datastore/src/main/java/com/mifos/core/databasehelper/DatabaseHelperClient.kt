package com.mifos.core.databasehelper

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.MapDeserializer
import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.loan.LoanAccount_Table
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.core.objects.accounts.savings.SavingsAccount_Table
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.client.ClientDate
import com.mifos.core.objects.client.ClientPayload
import com.mifos.core.objects.client.ClientPayload_Table
import com.mifos.core.objects.client.Client_Table
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.GroupWithAssociations
import com.mifos.core.objects.noncore.ColumnHeader
import com.mifos.core.objects.noncore.ColumnHeader_Table
import com.mifos.core.objects.noncore.ColumnValue
import com.mifos.core.objects.noncore.ColumnValue_Table
import com.mifos.core.objects.noncore.DataTable
import com.mifos.core.objects.noncore.DataTablePayload
import com.mifos.core.objects.noncore.DataTablePayload_Table
import com.mifos.core.objects.noncore.DataTable_Table
import com.mifos.core.objects.templates.clients.ClientsTemplate
import com.mifos.core.objects.templates.clients.InterestType
import com.mifos.core.objects.templates.clients.OfficeOptions
import com.mifos.core.objects.templates.clients.Options
import com.mifos.core.objects.templates.clients.Options_Table
import com.mifos.core.objects.templates.clients.SavingProductOptions
import com.mifos.core.objects.templates.clients.StaffOptions
import com.raizlabs.android.dbflow.sql.language.Delete
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable
import rx.functions.Func0
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This DatabaseHelper Managing all Database logic and staff (Saving, Update, Delete).
 * Whenever DataManager send response to save or request to read from Database then this class
 * save the response or read the all values from database and return as accordingly.
 *
 *
 * Created by Rajan Maurya on 24/06/16.
 */
@Singleton
class DatabaseHelperClient @Inject constructor() {
    private val gson: Gson
    private val type: Type

    init {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(
            object : TypeToken<HashMap<String, Any>>() {}.type,
            MapDeserializer()
        )
        gson = gsonBuilder.create()
        type = object : TypeToken<HashMap<String, Any>>() {}.type
    }

    /**
     * This Method save the single Client in Database with ClientId as Primary Id
     *
     * @param client Client
     * @return saved Client
     */
    fun saveClient(client: Client): Observable<Client> {
        return Observable.defer(Func0 { //Saving Client in Database
            val clientDate = client.activationDate[0]?.let {
                client.activationDate[1]?.let { it1 ->
                    client.activationDate[2]?.let { it2 ->
                        ClientDate(
                            client.id.toLong(), 0,
                            it,
                            it1,
                            it2
                        )
                    }
                }
            }
            client.clientDate = clientDate
            client.save()
            Observable.just(client)
        })
    }

    /**
     * Reading All Clients from table of Client and return the ClientList
     *
     * @return List Of Client
     */
    //TODO Implement Observable Transaction to load Client List
    fun readAllClients(): Observable<Page<Client>> {
        return Observable.create<Page<Client>> { subscriber ->
            val clientPage = Page<Client>()
            clientPage.pageItems = SQLite.select()
                .from(Client::class.java)
                .queryList()
            subscriber.onNext(clientPage)
            subscriber.onCompleted()
        }
    }

    fun getGroupAssociateClients(groupId: Int): Observable<GroupWithAssociations> {
        return Observable.defer {
            val clients = SQLite.select()
                .from(Client::class.java)
                .where(Client_Table.groupId.eq(groupId))
                .queryList()
            val groupWithAssociations = GroupWithAssociations()
            groupWithAssociations.clientMembers = clients
            Observable.just(groupWithAssociations)
        }
    }

    /**
     * This Method select query with clientId, In return the Client Details will be come.
     *
     * @param clientId of the client
     * @return Client
     */
    fun getClient(clientId: Int): Observable<Client> {
        return Observable.create { subscriber ->
            val client = SQLite.select()
                .from(Client::class.java)
                .where(Client_Table.id.eq(clientId))
                .querySingle()
            if (client != null) {
                client.activationDate = listOf(
                    client.clientDate?.day,
                    client.clientDate?.month,
                    client.clientDate?.year
                )
            }
            subscriber.onNext(client)
        }
    }

    /**
     * This Method  write the ClientAccount in tho DB. According to Schema Defined in Model
     *
     * @param clientAccounts Model of List of LoanAccount and SavingAccount
     * @param clientId       Client Id
     * @return null
     */
    fun saveClientAccounts(
        clientAccounts: ClientAccounts,
        clientId: Int
    ): Observable<ClientAccounts> {
        return Observable.defer {
            val loanAccounts = clientAccounts.loanAccounts
            val savingsAccounts = clientAccounts.savingsAccounts
            for (loanAccount: LoanAccount in loanAccounts) {
                loanAccount.clientId = clientId.toLong()
                loanAccount.save()
            }
            for (savingsAccount: SavingsAccount in savingsAccounts) {
                savingsAccount.clientId = clientId.toLong()
                savingsAccount.save()
            }
            Observable.just(clientAccounts)
        }
    }

    /**
     * This Method Read the Table of LoanAccount and SavingAccount and return the List of
     * LoanAccount and SavingAccount according to clientId
     *
     * @param clientId Client Id
     * @return Return the ClientAccount according to client Id
     */
    fun realClientAccounts(clientId: Int): Observable<ClientAccounts> {
        return Observable.create { subscriber ->
            val loanAccounts = SQLite.select()
                .from(LoanAccount::class.java)
                .where(LoanAccount_Table.clientId.eq(clientId.toLong()))
                .queryList()
            val savingsAccounts = SQLite.select()
                .from(SavingsAccount::class.java)
                .where(SavingsAccount_Table.clientId.eq(clientId.toLong()))
                .queryList()
            val clientAccounts = ClientAccounts()
            clientAccounts.loanAccounts = loanAccounts
            clientAccounts.savingsAccounts = savingsAccounts
            subscriber.onNext(clientAccounts)
        }
    }

    /**
     * Saving ClientTemplate into Database ClientTemplate_Table
     *
     * @param clientsTemplate fetched from Server
     * @return void
     */
    fun saveClientTemplate(clientsTemplate: ClientsTemplate): Observable<ClientsTemplate> {
        return Observable.defer { //saving clientTemplate into DB;
            clientsTemplate.save()
            for (officeOptions: OfficeOptions in clientsTemplate.officeOptions) {
                officeOptions.save()
            }
            for (staffOptions: StaffOptions in clientsTemplate.staffOptions) {
                staffOptions.save()
            }
            for (savingProductOptions: SavingProductOptions in clientsTemplate
                .savingProductOptions) {
                savingProductOptions.save()
            }
            for (options: Options in clientsTemplate.genderOptions) {
                options.optionType = GENDER_OPTIONS
                options.save()
            }
            for (options: Options in clientsTemplate.clientTypeOptions) {
                options.optionType = CLIENT_TYPE_OPTIONS
                options.save()
            }
            for (options: Options in clientsTemplate.clientClassificationOptions) {
                options.optionType = CLIENT_CLASSIFICATION_OPTIONS
                options.save()
            }
            for (interestType: InterestType in clientsTemplate.clientLegalFormOptions) {
                interestType.save()
            }
            for (dataTable: DataTable in clientsTemplate.dataTables) {
                Delete.table(DataTable::class.java)
                Delete.table(
                    ColumnHeader::class.java
                )
                Delete.table(ColumnValue::class.java)
                dataTable.save()
                for (columnHeader: ColumnHeader in dataTable.columnHeaderData) {
                    columnHeader.registeredTableName = dataTable.registeredTableName
                    columnHeader.save()
                    for (columnValue: ColumnValue in columnHeader.columnValues) {
                        columnValue.registeredTableName = dataTable.registeredTableName
                        columnValue.save()
                    }
                }
            }
            Observable.just(clientsTemplate)
        }
    }

    /**
     * Reading ClientTemplate from Database ClientTemplate_Table
     *
     * @return ClientTemplate
     */
    fun readClientTemplate(): Observable<ClientsTemplate> {
        return Observable.defer {
            val clientsTemplate = SQLite.select()
                .from(ClientsTemplate::class.java)
                .querySingle()
            val officeOptionses = SQLite.select()
                .from(OfficeOptions::class.java)
                .queryList()
            val staffOptionses = SQLite.select()
                .from(StaffOptions::class.java)
                .queryList()
            val savingProductOptionses = SQLite.select()
                .from(SavingProductOptions::class.java)
                .queryList()
            val genderOptions = SQLite.select()
                .from(Options::class.java)
                .where(Options_Table.optionType.eq(GENDER_OPTIONS))
                .queryList()
            val clientTypeOptions = SQLite.select()
                .from(Options::class.java)
                .where(Options_Table.optionType.eq(CLIENT_TYPE_OPTIONS))
                .queryList()
            val clientClassificationOptions = SQLite.select()
                .from(Options::class.java)
                .where(Options_Table.optionType.eq(CLIENT_CLASSIFICATION_OPTIONS))
                .queryList()
            val clientLegalFormOptions = SQLite.select()
                .from(InterestType::class.java)
                .queryList()
            val dataTables = SQLite.select()
                .from(DataTable::class.java)
                .where(DataTable_Table.applicationTableName.eq(Constants.DATA_TABLE_NAME_CLIENT))
                .queryList()
            if (dataTables.isNotEmpty()) {
                for (dataTable: DataTable in dataTables) {
                    val columnHeaders = SQLite.select()
                        .from(ColumnHeader::class.java)
                        .where(
                            ColumnHeader_Table.registeredTableName
                                .eq(dataTable.registeredTableName)
                        )
                        .queryList()
                    for (columnHeader: ColumnHeader in columnHeaders) {
                        val columnValues = SQLite.select()
                            .from(ColumnValue::class.java)
                            .where(
                                ColumnValue_Table.registeredTableName.eq(
                                    dataTable
                                        .registeredTableName
                                )
                            )
                            .queryList()
                        if (columnValues.isNotEmpty()) {
                            columnHeader.columnValues = columnValues
                        }
                    }
                    if (columnHeaders.isNotEmpty()) {
                        dataTable.columnHeaderData = columnHeaders
                    }
                }
            }
            assert(clientsTemplate != null)
            clientsTemplate!!.officeOptions = officeOptionses
            clientsTemplate.staffOptions = staffOptionses
            clientsTemplate.savingProductOptions = savingProductOptionses
            clientsTemplate.genderOptions = genderOptions
            clientsTemplate.clientTypeOptions = clientTypeOptions
            clientsTemplate.clientClassificationOptions = clientClassificationOptions
            clientsTemplate.clientLegalFormOptions = clientLegalFormOptions
            clientsTemplate.dataTables = dataTables
            Observable.just(clientsTemplate)
        }
    }

    /**
     * Saving ClientPayload into Database ClientPayload_Table
     *
     * @param clientPayload created in offline mode
     * @return Client
     */
    fun saveClientPayloadToDB(clientPayload: ClientPayload): Observable<Client> {
        return Observable.defer {
            val currentTime = System.currentTimeMillis()
            clientPayload.clientCreationTime = currentTime
            if (clientPayload.datatables?.isNotEmpty() == true) {
                Observable.from<DataTablePayload>(clientPayload.datatables)
                    .subscribe { dataTablePayload ->
                        dataTablePayload.clientCreationTime = currentTime
                        val jsonObject = gson.toJsonTree(
                            dataTablePayload
                                .data
                        ).asJsonObject
                        dataTablePayload.dataTableString = jsonObject.toString()
                        dataTablePayload.save()
                    }
            }
            clientPayload.save()
            Observable.just(Client())
        }
    }

    /**
     * Reading All Entries in the ClientPayload_Table
     *
     * @return List<ClientPayload></ClientPayload>>
     */
    fun readAllClientPayload(): Observable<List<ClientPayload>> {
        return Observable.defer {
            val clientPayloads = SQLite.select()
                .from(ClientPayload::class.java)
                .queryList()
            if (clientPayloads.isNotEmpty()) {
                Observable.from(clientPayloads).subscribe { clientPayload ->
                    val dataTablePayloads = SQLite.select()
                        .from(DataTablePayload::class.java)
                        .where(
                            DataTablePayload_Table.clientCreationTime
                                .eq(clientPayload.clientCreationTime)
                        )
                        .queryList()
                    if (dataTablePayloads.isNotEmpty()) {
                        Observable.from(dataTablePayloads)
                            .subscribe { dataTablePayload ->
                                val data = gson.fromJson<HashMap<String, Any>>(
                                    dataTablePayload.dataTableString,
                                    type
                                )
                                dataTablePayload.data = data
                            }
                        clientPayload.datatables = dataTablePayloads
                    }
                }
            }
            Observable.just(clientPayloads)
        }
    }

    /**
     * This Method for deleting the client payload from the Database according to Id and
     * again fetch the client List from the Database ClientPayload_Table
     *
     * @param id is Id of the Client Payload in which reference client was saved into Database
     * @return List<ClientPayload></ClientPayload>>
     */
    fun deleteAndUpdatePayloads(
        id: Int,
        clientCreationTIme: Long
    ): Observable<List<ClientPayload>> {
        return Observable.defer {
            Delete.table(ClientPayload::class.java, ClientPayload_Table.id.eq(id))
            Delete.table(
                DataTablePayload::class.java,
                DataTablePayload_Table.clientCreationTime.eq(clientCreationTIme)
            )
            readAllClientPayload()
        }
    }

    fun updateDatabaseClientPayload(clientPayload: ClientPayload): Observable<ClientPayload> {
        return Observable.defer {
            clientPayload.update()
            Observable.just(clientPayload)
        }
    }

    companion object {
        const val GENDER_OPTIONS = "genderOptions"
        const val CLIENT_TYPE_OPTIONS = "clientTypeOptions"
        const val CLIENT_CLASSIFICATION_OPTIONS = "clientClassificationOptions"
    }
}