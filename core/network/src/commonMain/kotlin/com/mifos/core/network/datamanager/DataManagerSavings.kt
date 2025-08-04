/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.model.objects.account.loan.SavingsApproval
import com.mifos.core.model.objects.account.saving.SavingsAccountTransactionResponse
import com.mifos.core.model.objects.organisations.ProductSavings
import com.mifos.core.model.objects.payloads.SavingsPayload
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociationsEntity
import com.mifos.room.entities.client.Savings
import com.mifos.room.entities.templates.savings.SavingProductsTemplate
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplateEntity
import com.mifos.room.helper.SavingsDaoHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

/**
 * Created by Rajan Maurya on 17/08/16.
 */
class DataManagerSavings(
    val mBaseApiManager: BaseApiManager,
    private val databaseHelperSavings: SavingsDaoHelper,
    private val prefManager: UserPreferencesRepository,
) {
    /**
     * This Method Make the Request to the REST API
     * https://demo.openmf.org/fineract-provider/api/v1/savingsaccounts/{savingsAccountId}
     * ?associations={all or transactions or charges}
     * and fetch savings application/account.
     *
     * @param type             Type of the SavingsAccount
     * @param savingsAccountId Savings Account Id
     * @param association      {all or transactions or charges}
     * 'all': Gets data related to all associations e.g. ?associations=all.
     * 'transactions': Gets data related to transactions on the account e.g.
     * ?associations=transactions
     * 'charges':Savings Account charges data.
     * @return SavingsAccountWithAssociations
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSavingsAccount(
        type: String,
        savingsAccountId: Int,
        association: String?,
    ): Flow<SavingsAccountWithAssociationsEntity?> {
        return prefManager.userInfo.flatMapLatest { userData ->
            when (userData.userStatus) {
                false -> mBaseApiManager.savingsService.getSavingsAccountWithAssociations(
                    type,
                    savingsAccountId,
                    association ?: "",
                )

                true ->
                    /**
                     * Return SavingsAccountWithAssociations from DatabaseHelperSavings.
                     */
                    databaseHelperSavings.readSavingsAccount(savingsAccountId)
            }
        }
    }

    /**
     * This Method Make the Request to the REST API
     * https://demo.openmf.org/fineract-provider/api/v1/savingsaccounts/{savingsAccountId}
     * ?associations={all or transactions or charges}
     * and fetch savings application/account. and After Fetching SavingsAccount DataManager
     * send Fetched SavingsAccount to DatabaseHelperSavings to save the SavingsAccount in Database
     * for Offline use and DatabaseHelperSavings returns saved SavingsAccount.
     *
     * @param type             Type of the SavingsAccount
     * @param savingsAccountId Savings Account Id
     * @param association      {all or transactions or charges}
     * 'all': Gets data related to all associations e.g. ?associations=all.
     * 'transactions': Gets data related to transactions on the account e.g.
     * ?associations=transactions
     * 'charges':Savings Account charges data.
     * @return SavingsAccountWithAssociations
     */
    fun syncSavingsAccount(
        type: String,
        savingsAccountId: Int,
        association: String?,
    ): Flow<SavingsAccountWithAssociationsEntity> {
        return flow {
            mBaseApiManager.savingsService.getSavingsAccountWithAssociations(type, savingsAccountId, association)
                .collect { savingsWithTransaction ->
                    databaseHelperSavings.saveSavingsAccount(savingsWithTransaction)
                    emit(savingsWithTransaction)
                }
        }
    }

    fun activateSavings(
        savingsAccountId: Int,
        request: HashMap<String, String>,
    ): Flow<GenericResponse> {
        return mBaseApiManager.savingsService.activateSavings(savingsAccountId, request)
    }

    /**
     * This Method make the Request to REST API, if the User Status if Online at:
     * https://demo.openmf.org/fineract-provider/api/v1/{savingsAccountType}/{savingsAccountId}
     * /transactions/template.
     * using retrofit 2 with SavingsAccountService and the SavingsAccountTransactionTemplate in
     * response.
     * If User Status is Offline then It make the request to the DatabaseHelperSavings and get
     * the SavingAccount TransactionTemplate According to SavingAccount Id
     *
     * @param type             Savings Account Type Example : savingsaccounts
     * @param savingsAccountId SavingsAccount Id
     * @param transactionType  Transaction Type Example : 'Deposit', 'Withdrawal'
     * @return SavingsAccountTransactionTemplate
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSavingsAccountTransactionTemplate(
        type: String,
        savingsAccountId: Int,
        transactionType: String?,
    ): Flow<SavingsAccountTransactionTemplateEntity?> {
        return prefManager.userInfo.flatMapLatest { userData ->
            when (userData.userStatus) {
                false -> mBaseApiManager.savingsService.getSavingsAccountTransactionTemplate(
                    type,
                    savingsAccountId,
                    transactionType,
                )

                true ->
                    /**
                     * Return SavingsAccountTransactionTemplate from DatabaseHelperSavings.
                     */
                    databaseHelperSavings.readSavingsAccountTransactionTemplate(savingsAccountId)
            }
        }
    }

    /**
     * This Method make the Request to REST API, if the User Status is Online at:
     * https://demo.openmf.org/fineract-provider/api/v1/{savingsAccountType}/{savingsAccountId}
     * /transactions/template.
     * using retrofit 2 with SavingsAccountService and get SavingsAccountTransactionTemplate in
     * response. and then DataManager send fetched SavingsAccountTransactionTemplate to
     * DatabaseHelperSavings to save in the SavingsAccountTransactionTemplate_Table for offline use
     *
     * @param savingsAccountType Savings Account Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     * @param transactionType    Transaction Type Example : 'Deposit', 'Withdrawal'
     * @return SavingsAccountTransactionTemplate
     */
    fun syncSavingsAccountTransactionTemplate(
        savingsAccountType: String,
        savingsAccountId: Int,
        transactionType: String?,
    ): Flow<SavingsAccountTransactionTemplateEntity> {
        return mBaseApiManager.savingsService.getSavingsAccountTransactionTemplate(
            savingsAccountType,
            savingsAccountId,
            transactionType,
        ).map { savingsAccountTransactionTemplate ->
            databaseHelperSavings.saveSavingsAccountTransactionTemplate(
                savingsAccountTransactionTemplate,
            )
            savingsAccountTransactionTemplate
        }
    }

    /**
     * This Method makes the Transaction of SavingAccount. Here is two mode, one is Online.
     * if the user is Online, then request will be made to server and transaction will be sync to
     * server and if user is on offline mode then transaction will be saved in Database.
     * and User is able to sync that transaction when ever he have good internet connection
     *
     * @param savingsAccountType Type of Transaction
     * @param savingsAccountId   Savings Account Id
     * @param transactionType    Transaction Type Example : 'Deposit', 'Withdrawal'
     * @param request            SavingsAccountTransactionRequest
     * @return SavingsAccountTransactionResponse
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun processTransaction(
        savingsAccountType: String,
        savingsAccountId: Int,
        transactionType: String?,
        request: SavingsAccountTransactionRequestEntity,
    ): Flow<SavingsAccountTransactionResponse?> {
        return prefManager.userInfo.flatMapLatest { userData ->
            when (userData.userStatus) {
                false -> flow {
                    emit(
                        mBaseApiManager.savingsService.processTransaction(
                            savingsAccountType,
                            savingsAccountId,
                            transactionType,
                            request,
                        ),
                    )
                }

                true ->
                    /**
                     * Return SavingsAccountTransactionResponse from DatabaseHelperSavings.
                     */
                    databaseHelperSavings
                        .saveSavingsAccountTransaction(
                            savingsAccountType,
                            savingsAccountId,
                            transactionType,
                            request,
                        )
            }
        }
    }

    /**
     * This Method is making Transaction into Database SavingsAccountTransactionRequest_Table
     * and returns the SavingsAccountTransactionRequest with SavingAccount Id {savingAccountId}.
     * If Database does not contain any entry in SavingsAccountTransactionRequest_Table with
     * SavingAccount Id then it return null.
     *
     * @param savingAccountId SavingsAccount Id
     * @return SavingsAccountTransactionRequest
     */
    fun getSavingsAccountTransaction(
        savingAccountId: Int,
    ): Flow<SavingsAccountTransactionRequestEntity?> {
        return databaseHelperSavings.getSavingsAccountTransaction(savingAccountId)
    }

    /**
     * This Method sent the request to DatabaseHelperSavings and DatabaseHelperSavings load the
     * All SavingsAccountTransactions from the SavingsAccountTransactionRequest_Table.
     * and returns the List<SavingsAccountTransactionRequest>
     *
     * @return List<SavingsAccountTransactionRequest></SavingsAccountTransactionRequest>>
     </SavingsAccountTransactionRequest> */
    val allSavingsAccountTransactions: Flow<List<SavingsAccountTransactionRequestEntity>>
        get() = databaseHelperSavings.allSavingsAccountTransaction()

    /**
     * This method sending request DatabaseHelper and Deleting the SavingsAccountTransaction
     * with savingsAccountId from SavingsAccountTransaction_Table and again loading list of
     * SavingsAccountTransaction from Database.
     *
     * @param savingsAccountId Loan Id of the Loan
     * @return List<SavingsAccountTransaction>
     </SavingsAccountTransaction> */
    fun deleteAndUpdateTransactions(
        savingsAccountId: Int,
    ): Flow<List<SavingsAccountTransactionRequestEntity>> {
        return databaseHelperSavings.deleteAndUpdateTransaction(savingsAccountId)
    }

    /**
     * This Method updating SavingsAccountTransactionRequest in to Database and return the same
     * SavingsAccountTransactionRequest to the Presenter
     *
     * @param savingsAccountTransactionRequest Updating SavingsAccountTransactionRequest
     * in to Database.
     * @return LoanRepaymentRequest
     */
    suspend fun updateLoanRepaymentTransaction(
        savingsAccountTransactionRequest: SavingsAccountTransactionRequestEntity,
    ) {
        databaseHelperSavings.updateSavingsAccountTransaction(
            savingsAccountTransactionRequest,
        )
    }

    val getSavingsAccounts: Flow<List<ProductSavings>>
        get() = mBaseApiManager.savingsService.allSavingsAccounts()

    fun createSavingsAccount(savingsPayload: SavingsPayload?): Flow<Savings> {
        return mBaseApiManager.savingsService.createSavingsAccount(savingsPayload)
    }

    val getSavingsAccountTemplate: Flow<SavingProductsTemplate>
        get() = mBaseApiManager.savingsService.savingsAccountTemplate()

    fun getClientSavingsAccountTemplateByProduct(
        clientId: Int,
        productId: Int,
    ): Flow<SavingProductsTemplate> {
        return mBaseApiManager.savingsService.getClientSavingsAccountTemplateByProduct(
            clientId,
            productId,
        )
    }

    fun getGroupSavingsAccountTemplateByProduct(
        groupId: Int,
        productId: Int,
    ): Flow<SavingProductsTemplate> {
        return mBaseApiManager.savingsService.getGroupSavingsAccountTemplateByProduct(
            groupId,
            productId,
        )
    }

    fun approveSavingsApplication(
        savingsAccountId: Int,
        savingsApproval: SavingsApproval?,
    ): Flow<GenericResponse> {
        return mBaseApiManager.savingsService.approveSavingsApplication(
            savingsAccountId,
            savingsApproval,
        )
    }
}
