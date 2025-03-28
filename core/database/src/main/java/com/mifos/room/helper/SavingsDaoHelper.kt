/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.helper

import com.mifos.core.common.network.Dispatcher
import com.mifos.core.common.network.MifosDispatchers
import com.mifos.core.model.objects.account.saving.SavingsAccountTransactionResponse
import com.mifos.room.dao.SavingsDao
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociationsEntity
import com.mifos.room.entities.accounts.savings.SavingsTransactionDateEntity
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplateEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * Created by Pronay Sarker on 17/02/2025 (4:57 PM)
 */

class SavingsDaoHelper(
    private val savingsDao: SavingsDao,
    @Dispatcher(MifosDispatchers.IO)
    private val ioDispatcher: CoroutineDispatcher,
) {

    /**
     * This Saving the SavingsAccountSummary template into Database
     * SavingsAccountWithAssociations_Table with the Primary key SavingsAccountId.
     *
     * @param savingsAccountWithAssociations SavingAccountSummary Template.
     * @return SavingsAccountWithAssociations.
     */
    fun saveSavingsAccount(
        savingsAccountWithAssociations: SavingsAccountWithAssociationsEntity,
    ): Flow<SavingsAccountWithAssociationsEntity> = flow {
        val updatedTransactions = savingsAccountWithAssociations.transactions.map { transaction ->
            transaction.id?.let { id ->
                transaction.copy(
                    savingsAccountId = savingsAccountWithAssociations.id,
                    savingsTransactionDate = SavingsTransactionDateEntity(
                        id,
                        transaction.date.getOrNull(0),
                        transaction.date.getOrNull(1),
                        transaction.date.getOrNull(2),
                    ),
                )
            } ?: transaction
        }

        savingsDao.insertAllTransactions(updatedTransactions)

        val updatedSavingsAccount = savingsAccountWithAssociations.copy(
            transactions = updatedTransactions,
            summary = savingsAccountWithAssociations.summary?.copy(savingsId = savingsAccountWithAssociations.id),
        )

        savingsDao.insertSavingsAccountWithAssociations(updatedSavingsAccount)
        emit(updatedSavingsAccount)
    }.flowOn(ioDispatcher)

    /**
     * This Method Read the SavingsAccountSummary Template from the
     * SavingsAccountWithAssociations_Table and give the response SavingsAccountWithAssociations.
     * If the Query returns the null its means SavingsAccountSummary template doest exist into the
     * Database with savingAccountId.
     *
     * @param savingsAccountId Savings Account Id
     * @return SavingsAccountWithAssociations SavingsAccountSummary Template.
     */
    fun readSavingsAccount(savingsAccountId: Int): Flow<SavingsAccountWithAssociationsEntity?> =
        savingsDao.getSavingsAccountWithAssociations(savingsAccountId)
            .map { savingsAccountWithAssociations ->
                savingsAccountWithAssociations?.copy(
                    transactions = savingsDao.getAllTransactions(savingsAccountId)
                        .map { transaction ->
                            transaction.copy(
                                date = listOf(
                                    transaction.savingsTransactionDate?.year,
                                    transaction.savingsTransactionDate?.month,
                                    transaction.savingsTransactionDate?.day,
                                ),
                            )
                        },
                )
            }
            .flowOn(ioDispatcher)

    /**
     * This Method is Saving the SavingsAccountTransactionTemplate into Database.
     *
     * @param savingsAccountTransactionTemplate SavingsAccountTransactionTemplate
     * @return SavingsAccountTransactionTemplate
     */
    suspend fun saveSavingsAccountTransactionTemplate(
        savingsAccountTransactionTemplate: SavingsAccountTransactionTemplateEntity,
    ) {
        savingsDao.insertAllPaymentTypeOption(savingsAccountTransactionTemplate.paymentTypeOptions)
        savingsDao.insertSavingsAccountTransactionTemplate(savingsAccountTransactionTemplate)
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
    fun readSavingsAccountTransactionTemplate(savingsAccountId: Int): Flow<SavingsAccountTransactionTemplateEntity?> =
        savingsDao.getSavingsAccountTransactionTemplate(savingsAccountId)
            .map { savingsAccountTransactionTemplate ->
                savingsAccountTransactionTemplate?.copy(
                    paymentTypeOptions = savingsDao.getAllPaymentTypeOption().first(),
                )
            }
            .flowOn(ioDispatcher)

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
    fun saveSavingsAccountTransaction(
        savingsAccountType: String?,
        savingsAccountId: Int,
        transactionType: String?,
        savingsAccountTransactionRequest: SavingsAccountTransactionRequestEntity,
    ): Flow<SavingsAccountTransactionResponse?> = flow {
        savingsDao.insertSavingsAccountTransactionRequest(
            savingsAccountTransactionRequest.copy(
                savingAccountId = savingsAccountId,
                savingsAccountType = savingsAccountType,
                transactionType = transactionType,
            ),
        )
        emit(SavingsAccountTransactionResponse())
    }.flowOn(ioDispatcher)

    /**
     * This Method, retrieving SavingsAccountTransactionRequest with the Saving Id from Database
     * SavingsAccountTransactionRequest_Table. If no entry found with the SavingsAccount Id. It
     * returns null.
     *
     * @param savingsAccountId SavingAccount Id
     * @return SavingsAccountTransactionRequest
     */
    fun getSavingsAccountTransaction(
        savingsAccountId: Int,
    ): Flow<SavingsAccountTransactionRequestEntity?> {
        return savingsDao.getSavingsAccountTransactionRequest(savingsAccountId)
            .flowOn(ioDispatcher)
    }

    /**
     * This Method Load all Transactions from the SavingsAccountTransactionRequest_Table
     * and give the List<SavingsAccountTransactionRequest> response.
     *
     * @return List<SavingsAccountTransactionRequest>
     </SavingsAccountTransactionRequest></SavingsAccountTransactionRequest> */
    fun allSavingsAccountTransaction(): Flow<List<SavingsAccountTransactionRequestEntity>> {
        return savingsDao.getAllSavingsAccountTransactionRequest()
            .flowOn(ioDispatcher)
    }

    /**
     * This Method Deleting the SavingsAccountTransaction with the SavingsAccount Id and loading the
     * List<SavingsAccountTransactionRequest> from Database and return
     * List<SavingsAccountTransactionRequest> to DataManagerSavings and DataManagerSaving sync the
     * List<SavingsAccountTransactionRequest> to the SyncSavingsAccountTransaction.
     *
     * @param savingsAccountId SavingsAccount Id
     * @return List<SavingsAccountTransactionRequest>
     </SavingsAccountTransactionRequest></SavingsAccountTransactionRequest></SavingsAccountTransactionRequest></SavingsAccountTransactionRequest> */
    fun deleteAndUpdateTransaction(savingsAccountId: Int): Flow<List<SavingsAccountTransactionRequestEntity>> =
        flow {
            savingsDao.deleteSavingsAccountTransactionRequest(savingsAccountId)
            emitAll(savingsDao.getAllSavingsAccountTransactionRequest())
        }.flowOn(ioDispatcher)

    /**
     * This Method updating the SavingsAccountTransactionRequest to Database Table.
     * this method will be called whenever error will come during sync the LoanRepayment. This
     * method saving the Error message to the Error Table Column .
     *
     * @param savingsAccountTransactionRequest SavingsAccountTransaction to update
     * @return SavingsAccountTransactionRequest
     */
    suspend fun updateSavingsAccountTransaction(
        savingsAccountTransactionRequest: SavingsAccountTransactionRequestEntity,
    ) {
        savingsDao.updateSavingsAccountTransactionRequest(savingsAccountTransactionRequest)
    }
}
