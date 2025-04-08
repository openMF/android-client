/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.helper

import com.mifos.core.common.utils.Constants.DATA_TABLE_NAME_CLIENT
import com.mifos.core.common.utils.MapDeserializer
import com.mifos.core.common.utils.Page
import com.mifos.room.dao.ClientDao
import com.mifos.room.entities.accounts.ClientAccounts
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import com.mifos.room.entities.client.ClientDateEntity
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.group.GroupWithAssociations
import com.mifos.room.entities.noncore.ColumnHeader
import com.mifos.room.entities.noncore.ColumnValue
import com.mifos.room.entities.noncore.DataTableEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import com.mifos.room.entities.templates.clients.OptionsEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

/**
 * This DatabaseHelper Managing all Database logic and staff (Saving, Update, Delete).
 * Whenever DataManager send response to save or request to read from Database then this class
 * save the response or read the all values from database and return as accordingly.
 */
class ClientDaoHelper(
    private val clientDao: ClientDao,
//    @Dispatcher(MifosDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {

    init {
        Json {
            serializersModule = SerializersModule {
                contextual(MapDeserializer)
            }
        }
    }

    /**
     * This Method save the single Client in Database with ClientId as Primary Id
     *
     * @param client Client
     * @return saved Client
     */
    suspend fun saveClient(client: ClientEntity) {
        val clientDate = client.activationDate.getOrNull(0)?.let { year ->
            client.activationDate.getOrNull(1)?.let { month ->
                client.activationDate.getOrNull(2)?.let { day ->
                    ClientDateEntity(client.id, 0, year, month, day)
                }
            }
        }
        val updatedClient = client.copy(clientDate = clientDate)
        clientDao.insertClient(updatedClient)
    }

    /**
     * Reading All Clients from table of Client and return the ClientList
     *
     * @return List Of Client
     */

    fun readAllClients(): Flow<Page<ClientEntity>> {
        return clientDao.getAllClients().map { clients ->
            Page<ClientEntity>().apply {
                pageItems = clients
            }
        }.flowOn(ioDispatcher)
    }

    /**
     * Retrieves all clients associated with a specific group and returns a `GroupWithAssociations` object.
     *
     * @param groupId The ID of the group for which the associated clients should be retrieved.
     * @return A `Flow` of `GroupWithAssociations`, which contains a list of `clientMembers` associated with the
     *         specified group.
     */

    fun getGroupAssociateClients(groupId: Int): Flow<GroupWithAssociations> {
        return clientDao.getClientsByGroupId(groupId).map { clients ->
            GroupWithAssociations().copy(
                clientMembers = clients,
            )
        }.flowOn(ioDispatcher)
    }

    /**
     * This Method select query with clientId, In return the Client Details will be come.
     *
     * @param clientId of the client
     * @return A 'Flow' of 'Client'
     */
    fun getClient(clientId: Int): Flow<ClientEntity?> {
        return clientDao.getClientByClientId(clientId).map { client ->
            client?.copy(
                activationDate = listOf(
                    client.clientDate?.day,
                    client.clientDate?.month,
                    client.clientDate?.year,
                ),
            )
        }.flowOn(ioDispatcher)
    }

    /**
     * This Method  write the ClientAccount in tho DB. According to Schema Defined in Model
     *
     * @param clientAccounts Model of List of LoanAccount and SavingAccount
     * @param clientId       Client Id
     * @return null
     */
    suspend fun saveClientAccounts(
        clientAccounts: ClientAccounts,
        clientId: Int,
    ) {
        val loanAccounts = clientAccounts.loanAccounts
        val savingsAccounts = clientAccounts.savingsAccounts

        for (loanAccount: LoanAccountEntity in loanAccounts) {
            val updatedLoanAccount = loanAccount.copy(clientId = clientId.toLong())
            clientDao.insertLoanAccount(updatedLoanAccount)
        }

        for (savingsAccount: SavingsAccountEntity in savingsAccounts) {
            val updatedSavingsAccount = savingsAccount.copy(clientId = clientId.toLong())
            clientDao.insertSavingsAccount(updatedSavingsAccount)
        }
    }

    /**
     * This Method Read the Table of LoanAccount and SavingAccount and return the List of
     * LoanAccount and SavingAccount according to clientId
     *
     * @param clientId Client Id
     * @return Return the ClientAccounts according to client Id
     */
    fun readClientAccounts(clientId: Int): Flow<ClientAccounts> {
        return combine(
            clientDao.getLoanAccountsByClientId(clientId.toLong()),
            clientDao.getSavingsAccountsByClientId(clientId.toLong()),
        ) { loanAccounts, savingsAccounts ->
            ClientAccounts(loanAccounts = loanAccounts, savingsAccounts = savingsAccounts)
        }.flowOn(ioDispatcher)
    }

    /**
     * Saving ClientTemplate into Database ClientTemplate_Table
     *
     * @param clientsTemplate fetched from Server
     * @return void
     */
    suspend fun saveClientTemplate(
        clientsTemplate: ClientsTemplateEntity,
    ) {
        clientDao.insertClientsTemplate(clientsTemplate)
        clientsTemplate.officeOptions?.let { clientDao.insertOfficeOptions(it) }
        clientsTemplate.staffOptions?.let { clientDao.insertStaffOptions(it) }
        clientsTemplate.savingProductOptions?.let { clientDao.insertSavingProductOptions(it) }
        clientsTemplate.genderOptions?.let {
            for (option: OptionsEntity in it) {
                val data = option.copy(optionType = GENDER_OPTIONS)
                clientDao.insertOption(data)
            }
        }
        clientsTemplate.clientTypeOptions?.let {
            for (option: OptionsEntity in it) {
                val data = option.copy(optionType = CLIENT_TYPE_OPTIONS)
                clientDao.insertOption(data)
            }
        }
        clientsTemplate.clientClassificationOptions?.let {
            for (option: OptionsEntity in it) {
                val data = option.copy(optionType = CLIENT_CLASSIFICATION_OPTIONS)
                clientDao.insertOption(data)
            }
        }

        clientsTemplate.clientLegalFormOptions?.let { clientDao.insertInterestTypes(it) }

        clientsTemplate.dataTables?.let {
            for (dataTable: DataTableEntity in it) {
                clientDao.deleteDataTables()
                clientDao.deleteColumnHeaders()
                clientDao.deleteColumnValues()

                clientDao.insertDataTable(dataTable)

                for (columnHeader: ColumnHeader in dataTable.columnHeaderData) {
                    val updatedColumnHeader =
                        columnHeader.copy(registeredTableName = dataTable.applicationTableName)
                    clientDao.insertColumnHeader(updatedColumnHeader)

                    for (columnValue: ColumnValue in columnHeader.columnValues) {
                        val updatedColumnValue =
                            columnValue.copy(registeredTableName = dataTable.registeredTableName)
                        clientDao.insertColumnValue(updatedColumnValue)
                    }
                }
            }
        }
    }

    /**
     * Reading ClientTemplate from Database ClientTemplate_Table
     *
     * @return ClientTemplate
     */
    fun readClientTemplate(): Flow<ClientsTemplateEntity> = flow {
        val clientsTemplate = clientDao.getClientsTemplate()
        val officeOptions = clientDao.getOfficeOptions().first()
        val staffOptions = clientDao.getStaffOptions().first()
        val savingsProductOptions = clientDao.getSavingProductOptions().first()
        val genderOptions = clientDao.getOptions(GENDER_OPTIONS).first()
        val clientTypeOptions = clientDao.getOptions(CLIENT_TYPE_OPTIONS).first()
        val clientClassificationOptions =
            clientDao.getOptions(CLIENT_CLASSIFICATION_OPTIONS).first()
        val clientLegalFormOptions = clientDao.getAllInterestType().first()

        val dataTables =
            clientDao.getDatatableByTableName(DATA_TABLE_NAME_CLIENT).first().map { dataTable ->
                if (dataTable.registeredTableName != null) {
                    val columnHeaders =
                        clientDao.getColumnHeadersByTableName(dataTable.registeredTableName)
                            .first().map { columnHeader ->
                                val columnValues =
                                    clientDao.getColumnValuesByTableName(dataTable.registeredTableName)
                                        .firstOrNull() ?: emptyList()
                                columnHeader.copy(columnValues = columnValues)
                            }
                    dataTable.copy(columnHeaderData = columnHeaders)
                } else {
                    dataTable.copy(columnHeaderData = emptyList())
                }
            }

        emit(
            clientsTemplate.copy(
                officeOptions = officeOptions,
                staffOptions = staffOptions,
                savingProductOptions = savingsProductOptions,
                genderOptions = genderOptions,
                clientTypeOptions = clientTypeOptions,
                clientClassificationOptions = clientClassificationOptions,
                clientLegalFormOptions = clientLegalFormOptions,
                dataTables = dataTables,
            ),
        )
    }.flowOn(ioDispatcher)

    /**
     * Saving ClientPayload into Database ClientPayload_Table
     *
     * @param clientPayload created in offline mode
     * @return Client
     */
    @Suppress("UnusedParameter")
    suspend fun saveClientPayloadToDB(clientPayload: ClientPayloadEntity) {
    }
    // todo Use kotlinx.serialization to convert data to JSON string and remove the upper function
//    suspend fun saveClientPayloadToDB(clientPayload: com.mifos.core.entity.client.ClientPayload?) {
//        val currentTime = System.currentTimeMillis()
//        val updatedClientPayload = clientPayload?.copy(
//            clientCreationTime = currentTime,
//        )
//        updatedClientPayload.datatables?.let { datatables ->
//            if (datatables.isNotEmpty()) {
//                datatables.forEach { dataTablePayload ->
//                    dataTablePayload.clientCreationTime = currentTime
//                    // Use kotlinx.serialization to convert data to JSON string
//                    val jsonString = json.encodeToString(dataTablePayload.data)
//                    dataTablePayload.dataTableString = jsonString
//                    clientDao.insertDataTablePayload(dataTablePayload)
//                }
//            }
//        }
//
//        clientDao.insertClientPayload(updatedClientPayload)
//    }

    /**
     * Reading All Entries in the ClientPayload_Table
     *
     * @return List<ClientPayload></ClientPayload>>
     */

    fun readAllClientPayload(): Flow<List<ClientPayloadEntity>> {
        return clientDao.getAllClientPayload().flowOn(ioDispatcher)
    }

    /**
     * This Method for deleting the client payload from the Database according to Id and
     * again fetch the client List from the DataTablePayload
     *
     * @param id is Id of the Client Payload in which reference client was saved into Database
     * @return List<ClientPayload> A flow emitting the updated client payload.
     */

    fun deleteAndUpdatePayloads(
        id: Int,
        clientCreationTIme: Long,
    ): Flow<List<ClientPayloadEntity>> {
        return flow {
            clientDao.deleteClientPayloadById(id)
            clientDao.deleteDataTablePayloadByCreationTime(clientCreationTIme)
            emitAll(readAllClientPayload())
        }.flowOn(ioDispatcher)
    }

    /**
     * This method updates the database with the given ClientPayload and returns the updated payload.
     *
     * @param clientPayload The client payload data to be updated in the database.
     * @return List<ClientPayload> A flow emitting the updated client payload.
     */
    suspend fun updateDatabaseClientPayload(clientPayload: ClientPayloadEntity) {
        clientDao.updateDatabaseClientPayload(clientPayload)
    }

    companion object {
        const val GENDER_OPTIONS = "genderOptions"
        const val CLIENT_TYPE_OPTIONS = "clientTypeOptions"
        const val CLIENT_CLASSIFICATION_OPTIONS = "clientClassificationOptions"
    }
}
