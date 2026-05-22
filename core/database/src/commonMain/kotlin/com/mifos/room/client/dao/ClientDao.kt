/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.client.dao

import com.mifos.room.client.entity.ClientAddressEntity
import com.mifos.room.client.entity.ClientEntity
import com.mifos.room.client.entity.ClientIdentifierEntity
import com.mifos.room.client.entity.ClientPayloadEntity
import com.mifos.room.client.entity.ClientsTemplateEntity
import com.mifos.room.client.entity.InterestTypeEntity
import com.mifos.room.client.entity.OptionsEntity
import com.mifos.room.datatable.entity.ColumnHeader
import com.mifos.room.datatable.entity.ColumnValue
import com.mifos.room.datatable.entity.DataTableEntity
import com.mifos.room.datatable.entity.DataTablePayload
import com.mifos.room.loan.entity.LoanAccountEntity
import com.mifos.room.office.entity.OfficeOptionsEntity
import com.mifos.room.savings.entity.SavingProductOptionsEntity
import com.mifos.room.savings.entity.SavingsAccountEntity
import com.mifos.room.staff.entity.StaffOptionsEntity
import kotlinx.coroutines.flow.Flow
import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update

@Dao
interface ClientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ClientEntity::class)
    suspend fun insertClient(client: ClientEntity)

    @Query("SELECT * FROM Client")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM Client WHERE groupId = :groupId")
    fun getClientsByGroupId(groupId: Int): Flow<List<ClientEntity>>

    @Query("SELECT * FROM Client WHERE id = :clientId LIMIT 1")
    fun getClientByClientId(clientId: Int): Flow<ClientEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = LoanAccountEntity::class)
    suspend fun insertLoanAccount(loanAccount: LoanAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = SavingsAccountEntity::class)
    suspend fun insertSavingsAccount(savingsAccount: SavingsAccountEntity)

    @Query("SELECT * FROM LoanAccountEntity WHERE clientId = :clientId")
    fun getLoanAccountsByClientId(clientId: Long): Flow<List<LoanAccountEntity>>

    @Query("SELECT * FROM SavingsAccount WHERE clientId = :clientId")
    fun getSavingsAccountsByClientId(clientId: Long): Flow<List<SavingsAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ClientsTemplateEntity::class)
    suspend fun insertClientsTemplate(clientsTemplate: ClientsTemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = OfficeOptionsEntity::class)
    suspend fun insertOfficeOptions(officeOptions: List<OfficeOptionsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = StaffOptionsEntity::class)
    suspend fun insertStaffOptions(staffOptions: List<StaffOptionsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = SavingProductOptionsEntity::class)
    suspend fun insertSavingProductOptions(savingProductOptions: List<SavingProductOptionsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = OptionsEntity::class)
    suspend fun insertOption(options: OptionsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = InterestTypeEntity::class)
    suspend fun insertInterestTypes(interestTypes: List<InterestTypeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ColumnHeader::class)
    suspend fun insertColumnHeader(columnHeader: ColumnHeader)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ColumnValue::class)
    suspend fun insertColumnValue(columnValue: ColumnValue)

    @Query("DELETE FROM DataTable")
    suspend fun deleteDataTables()

    @Query("DELETE FROM ColumnHeader")
    suspend fun deleteColumnHeaders()

    @Query("DELETE FROM ColumnValue")
    suspend fun deleteColumnValues()

    @Query("SELECT * FROM ClientsTemplate LIMIT 1")
    suspend fun getClientsTemplate(): ClientsTemplateEntity

    @Query("SELECT * FROM ClientTemplateOfficeOptions")
    fun getOfficeOptions(): Flow<List<OfficeOptionsEntity>>

    @Query("SELECT * FROM ClientTemplateStaffOptions")
    fun getStaffOptions(): Flow<List<StaffOptionsEntity>>

    @Query("SELECT * FROM ClientTemplateSavingProductsOptions")
    fun getSavingProductOptions(): Flow<List<SavingProductOptionsEntity>>

    @Query("SELECT * FROM ClientTemplateOptions WHERE optionType = :optionType")
    fun getOptions(optionType: String): Flow<List<OptionsEntity>>

    @Query("SELECT * FROM ClientTemplateInterest")
    fun getAllInterestType(): Flow<List<InterestTypeEntity>>

    @Query("SELECT * FROM DataTable where applicationTableName = :tableName")
    fun getDatatableByTableName(tableName: String): Flow<List<DataTableEntity>>

    @Query("SELECT * FROM ColumnHeader WHERE registeredTableName = :tableName")
    fun getColumnHeadersByTableName(tableName: String): Flow<List<ColumnHeader>>

    @Query("SELECT * FROM ColumnValue WHERE registeredTableName = :tableName")
    fun getColumnValuesByTableName(tableName: String): Flow<List<ColumnValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ClientPayloadEntity::class)
    suspend fun insertClientPayload(clientPayload: ClientPayloadEntity)

    @Insert(entity = DataTablePayload::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataTablePayload(dataTablePayloads: DataTablePayload)

    @Insert(entity = DataTableEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataTable(dataTable: DataTableEntity)

    @Query("SELECT * FROM ClientPayload")
    fun getAllClientPayload(): Flow<List<ClientPayloadEntity>>

    @Query("SELECT * FROM DataTablePayload WHERE clientCreationTime = :clientCreationTime")
    fun getDataTablePayloadByCreationTime(clientCreationTime: Long): Flow<List<DataTablePayload>>

    @Query("DELETE FROM ClientPayload WHERE id = :id")
    suspend fun deleteClientPayloadById(id: Int)

    @Query("DELETE FROM DataTablePayload WHERE clientCreationTime = :clientCreationTime")
    suspend fun deleteDataTablePayloadByCreationTime(clientCreationTime: Long)

    @Update(entity = ClientPayloadEntity::class, onConflict = OnConflictStrategy.NONE)
    suspend fun updateDatabaseClientPayload(clientPayload: ClientPayloadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ClientAddressEntity::class)
    suspend fun insertAddress(address: ClientAddressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ClientAddressEntity::class)
    suspend fun insertAddresses(addresses: List<ClientAddressEntity>)

    @Query("SELECT * FROM ClientAddress WHERE clientID = :clientId")
    fun getAddressesByClientId(clientId: Int): Flow<List<ClientAddressEntity>>

    @Query("SELECT * FROM ClientAddress WHERE addressId = :addressId LIMIT 1")
    fun getAddressById(addressId: Int): Flow<ClientAddressEntity?>

    @Query("DELETE FROM ClientAddress WHERE clientID = :clientId")
    suspend fun deleteAddressesByClientId(clientId: Int)

    @Query(
        """
    SELECT * FROM ClientAddress 
    WHERE addressLine1 LIKE :query ESCAPE '\'
       OR addressLine2 LIKE :query ESCAPE '\'
       OR addressLine3 LIKE :query ESCAPE '\'
       OR city LIKE :query ESCAPE '\'
       OR stateName LIKE :query ESCAPE '\'
       OR countryName LIKE :query ESCAPE '\'
       OR postalCode LIKE :query ESCAPE '\'
    ORDER BY city
    LIMIT 50
    """,
    )
    fun searchAddressesByQuery(query: String): Flow<List<ClientAddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = ClientIdentifierEntity::class)
    suspend fun insertIdentifiers(identifiers: List<ClientIdentifierEntity>)

    @Query("SELECT * FROM ClientIdentifier WHERE clientId = :clientId")
    fun getIdentifiersByClientId(clientId: Int): Flow<List<ClientIdentifierEntity>>

    @Query("DELETE FROM ClientIdentifier WHERE clientId = :clientId")
    suspend fun deleteIdentifiersByClientId(clientId: Int)

    @Query(
        """
    SELECT * FROM ClientIdentifier 
    WHERE CAST(id AS TEXT) LIKE :query ESCAPE '\'
       OR documentKey LIKE :query ESCAPE '\'
       OR description LIKE :query ESCAPE '\'
       OR documentTypeName LIKE :query ESCAPE '\'
    ORDER BY documentKey
    LIMIT 50
    """,
    )
    fun searchIdentifiersByQuery(query: String): Flow<List<ClientIdentifierEntity>>

    companion object {
        const val GENDER_OPTIONS = "genderOptions"
        const val CLIENT_TYPE_OPTIONS = "clientTypeOptions"
        const val CLIENT_CLASSIFICATION_OPTIONS = "clientClassificationOptions"
    }
}
