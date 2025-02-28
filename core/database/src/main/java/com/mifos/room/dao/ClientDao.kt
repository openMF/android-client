/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mifos.room.entities.accounts.loans.LoanAccount
import com.mifos.room.entities.accounts.savings.SavingsAccount
import com.mifos.room.entities.client.Client
import com.mifos.room.entities.client.ClientPayload
import com.mifos.room.entities.noncore.ColumnHeader
import com.mifos.room.entities.noncore.ColumnValue
import com.mifos.room.entities.noncore.DataTable
import com.mifos.room.entities.noncore.DataTablePayload
import com.mifos.room.entities.templates.clients.ClientsTemplate
import com.mifos.room.entities.templates.clients.InterestType
import com.mifos.room.entities.templates.clients.OfficeOptions
import com.mifos.room.entities.templates.clients.Options
import com.mifos.room.entities.templates.clients.SavingProductOptions
import com.mifos.room.entities.templates.clients.StaffOptions
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: Client)

    @Query("SELECT * FROM Client")
    fun getAllClients(): Flow<List<Client>>

    @Query("SELECT * FROM Client WHERE groupId = :groupId")
    fun getClientsByGroupId(groupId: Int): Flow<List<Client>>

    @Query("SELECT * FROM Client WHERE id = :clientId LIMIT 1")
    fun getClientByClientId(clientId: Int): Flow<Client?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoanAccount(loanAccount: LoanAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingsAccount(savingsAccount: SavingsAccount)

    @Query("SELECT * FROM LoanAccount WHERE clientId = :clientId")
    fun getLoanAccountsByClientId(clientId: Long): Flow<List<LoanAccount>>

    @Query("SELECT * FROM SavingsAccount WHERE clientId = :clientId")
    fun getSavingsAccountsByClientId(clientId: Long): Flow<List<SavingsAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClientsTemplate(clientsTemplate: ClientsTemplate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfficeOptions(officeOptions: List<OfficeOptions>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaffOptions(staffOptions: List<StaffOptions>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavingProductOptions(savingProductOptions: List<SavingProductOptions>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOption(options: Options)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterestTypes(interestTypes: List<InterestType>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColumnHeader(columnHeader: ColumnHeader)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertColumnValue(columnValue: ColumnValue)

    @Query("DELETE FROM DataTable")
    suspend fun deleteDataTables()

    @Query("DELETE FROM ColumnHeader")
    suspend fun deleteColumnHeaders()

    @Query("DELETE FROM ColumnValue")
    suspend fun deleteColumnValues()

    @Query("SELECT * FROM ClientsTemplate LIMIT 1")
    suspend fun getClientsTemplate(): ClientsTemplate

    @Query("SELECT * FROM ClientTemplateOfficeOptions")
    fun getOfficeOptions(): Flow<List<OfficeOptions>>

    @Query("SELECT * FROM ClientTemplateStaffOptions")
    fun getStaffOptions(): Flow<List<StaffOptions>>

    @Query("SELECT * FROM ClientTemplateSavingProductsOptions")
    fun getSavingProductOptions(): Flow<List<SavingProductOptions>>

    @Query("SELECT * FROM ClientTemplateOptions WHERE optionType = :optionType")
    fun getOptions(optionType: String): Flow<List<Options>>

    @Query("SELECT * FROM ClientTemplateInterest")
    fun getAllInterestType(): Flow<List<InterestType>>

    @Query("SELECT * FROM DataTable where applicationTableName = :tableName")
    fun getDatatableByTableName(tableName: String): Flow<List<DataTable>>

    @Query("SELECT * FROM ColumnHeader WHERE registeredTableName = :tableName")
    fun getColumnHeadersByTableName(tableName: String): Flow<List<ColumnHeader>>

    @Query("SELECT * FROM ColumnValue WHERE registeredTableName = :tableName")
    fun getColumnValuesByTableName(tableName: String): Flow<List<ColumnValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClientPayload(clientPayload: ClientPayload)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataTablePayload(dataTablePayloads: DataTablePayload)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataTable(dataTable: DataTable)

    @Query("SELECT * FROM ClientPayload")
    fun getAllClientPayload(): Flow<List<ClientPayload>>

    @Query("SELECT * FROM DataTablePayload WHERE clientCreationTime = :clientCreationTime")
    fun getDataTablePayloadByCreationTime(clientCreationTime: Long): List<Flow<DataTablePayload>>

    @Query("DELETE FROM ClientPayload WHERE id = :id")
    suspend fun deleteClientPayloadById(id: Int)

    @Query("DELETE FROM DataTablePayload WHERE clientCreationTime = :clientCreationTime")
    suspend fun deleteDataTablePayloadByCreationTime(clientCreationTime: Long)

    @Update
    suspend fun updateDatabaseClientPayload(clientPayload: ClientPayload): Flow<ClientPayload>

    companion object {
        const val GENDER_OPTIONS = "genderOptions"
        const val CLIENT_TYPE_OPTIONS = "clientTypeOptions"
        const val CLIENT_CLASSIFICATION_OPTIONS = "clientClassificationOptions"
    }
}
