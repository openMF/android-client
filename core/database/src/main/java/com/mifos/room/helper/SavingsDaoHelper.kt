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
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociations
import com.mifos.room.entities.accounts.savings.SavingsTransactionDate
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplate
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Created by Pronay Sarker on 17/02/2025 (4:57 PM)
 */

class SavingsDaoHelper @Inject constructor(
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
        savingsAccountWithAssociations: SavingsAccountWithAssociations,
    ): Flow<SavingsAccountWithAssociations> {
        return flow {
            val transactions = savingsAccountWithAssociations.transactions

            if (transactions.isNotEmpty()) {
                transactions.forEach { transaction ->
                    val savingsTransactionDate = transaction.id?.let {
                        SavingsTransactionDate(
                            it,
                            transaction.date.getOrNull(0),
                            transaction.date.getOrNull(1),
                            transaction.date.getOrNull(2),
                        )
                    }
                    transaction.savingsAccountId = savingsAccountWithAssociations.id
                    transaction.savingsTransactionDate = savingsTransactionDate
                    savingsDao.insertTransaction(transaction)
                }
            }

            savingsAccountWithAssociations.summary?.savingsId = savingsAccountWithAssociations.id
            savingsDao.insertSavingsAccountWithAssociations(savingsAccountWithAssociations)
            emit(savingsAccountWithAssociations)
        }.flowOn(ioDispatcher)
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
    fun readSavingsAccount(
        savingsAccountId: Int,
    ): Flow<SavingsAccountWithAssociations?> {
        return flow {
            var savingsAccountWithAssociations =
                savingsDao.getSavingsAccountWithAssociations(savingsAccountId).first()
            val transactions = savingsDao.getAllTransactions(savingsAccountId)

            transactions.forEach { transaction ->
                transaction.date = listOf(
                    transaction.savingsTransactionDate?.year,
                    transaction.savingsTransactionDate?.month,
                    transaction.savingsTransactionDate?.day,
                )
            }

            if (savingsAccountWithAssociations != null) {
                savingsAccountWithAssociations = savingsAccountWithAssociations.copy(
                    transactions = transactions,
                )
            }
            emit(savingsAccountWithAssociations)
        }.flowOn(ioDispatcher)
    }

    /**
     * This Method is Saving the SavingsAccountTransactionTemplate into Database.
     *
     * @param savingsAccountTransactionTemplate SavingsAccountTransactionTemplate
     * @return SavingsAccountTransactionTemplate
     */
    suspend fun saveSavingsAccountTransactionTemplate(
        savingsAccountTransactionTemplate: SavingsAccountTransactionTemplate,
    ) {
        savingsDao.insertPaymentTypeOption(savingsAccountTransactionTemplate.paymentTypeOptions)
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
    fun readSavingsAccountTransactionTemplate(
        savingsAccountId: Int,
    ): Flow<SavingsAccountTransactionTemplate?> {
        return flow {
            var savingsAccountTransactionTemplate =
                savingsDao.getSavingsAccountTransactionTemplate(savingsAccountId).first()
            val paymentTypeOption = savingsDao.getAllPaymentTypeOption().first()

            if (savingsAccountTransactionTemplate != null) {
                savingsAccountTransactionTemplate = savingsAccountTransactionTemplate.copy(
                    paymentTypeOptions = paymentTypeOption,
                )
            }
            emit(savingsAccountTransactionTemplate)
        }.flowOn(ioDispatcher)
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
    fun saveSavingsAccountTransaction(
        savingsAccountType: String?,
        savingsAccountId: Int,
        transactionType: String?,
        savingsAccountTransactionRequest: SavingsAccountTransactionRequest,
    ): Flow<SavingsAccountTransactionResponse?> {
        return flow {
            val updatedRequest = savingsAccountTransactionRequest.copy(
                savingAccountId = savingsAccountId,
                savingsAccountType = savingsAccountType,
                transactionType = transactionType,
            )

            savingsDao.insertSavingsAccountTransactionRequest(updatedRequest)
            emit(SavingsAccountTransactionResponse())
        }.flowOn(ioDispatcher)
    }

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
    ): Flow<SavingsAccountTransactionRequest?> {
        return savingsDao.getSavingsAccountTransactionRequest(savingsAccountId)
            .flowOn(ioDispatcher)
    }

    /**
     * This Method Load all Transactions from the SavingsAccountTransactionRequest_Table
     * and give the List<SavingsAccountTransactionRequest> response.
     *
     * @return List<SavingsAccountTransactionRequest>
     </SavingsAccountTransactionRequest></SavingsAccountTransactionRequest> */
    fun allSavingsAccountTransaction(): Flow<List<SavingsAccountTransactionRequest>> {
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
    fun deleteAndUpdateTransaction(
        savingsAccountId: Int,
    ): Flow<List<SavingsAccountTransactionRequest>> {
        return flow {
            savingsDao.deleteSavingsAccountTransactionRequest(savingsAccountId)
            val savingsAccountTransactionRequests =
                savingsDao.getAllSavingsAccountTransactionRequest()
            emitAll(savingsAccountTransactionRequests)
        }.flowOn(ioDispatcher)
    }

    /**
     * This Method updating the SavingsAccountTransactionRequest to Database Table.
     * this method will be called whenever error will come during sync the LoanRepayment. This
     * method saving the Error message to the Error Table Column .
     *
     * @param savingsAccountTransactionRequest SavingsAccountTransaction to update
     * @return SavingsAccountTransactionRequest
     */
    suspend fun updateSavingsAccountTransaction(
        savingsAccountTransactionRequest: SavingsAccountTransactionRequest,
    ) {
        savingsDao.updateSavingsAccountTransactionRequest(savingsAccountTransactionRequest)
    }
}
