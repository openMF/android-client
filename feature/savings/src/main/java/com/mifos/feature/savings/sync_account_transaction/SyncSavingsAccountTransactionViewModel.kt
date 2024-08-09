package com.mifos.feature.savings.sync_account_transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.common.utils.Resource
import com.mifos.core.data.repository.SyncSavingsAccountTransactionRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.domain.use_cases.AllSavingsAccountTransactionsUseCase
import com.mifos.core.domain.use_cases.DeleteAndUpdateTransactionsUseCase
import com.mifos.core.domain.use_cases.PaymentTypeOptionUseCase
import com.mifos.core.domain.use_cases.ProcessTransactionUseCase
import com.mifos.core.domain.use_cases.UpdateLoanRepaymentTransactionUseCase
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.feature.savings.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class SyncSavingsAccountTransactionViewModel @Inject constructor(
    private val paymentTypeOptionUseCase: PaymentTypeOptionUseCase,
    private val processTransactionUseCase: ProcessTransactionUseCase,
    private val allSavingsAccountTransactionsUseCase: AllSavingsAccountTransactionsUseCase,
    private val updateLoanRepaymentTransactionUseCase: UpdateLoanRepaymentTransactionUseCase,
    private val deleteAndUpdateTransactionsUseCase: DeleteAndUpdateTransactionsUseCase,
    private val prefManager: PrefManager
) : ViewModel() {

    private val _syncSavingsAccountTransactionUiState =
        MutableStateFlow<SyncSavingsAccountTransactionUiState>(SyncSavingsAccountTransactionUiState.Loading)
    val syncSavingsAccountTransactionUiState: StateFlow<SyncSavingsAccountTransactionUiState>
        get() = _syncSavingsAccountTransactionUiState

    private var mPaymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption> = emptyList()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun refreshTransactions() {
        _isRefreshing.value = true
        loadDatabaseSavingsAccountTransactions()
        loadPaymentTypeOption()
        _isRefreshing.value = false
    }

    private var mSavingsAccountTransactionRequests: MutableList<SavingsAccountTransactionRequest> =
        ArrayList()

    private var mTransactionIndex = 0
    private var mTransactionsFailed = 0

    fun getUserStatus() : Boolean {
        return prefManager.userStatus
    }

    fun syncSavingsAccountTransactions() {
        if (mSavingsAccountTransactionRequests.size != 0) {
            mTransactionIndex = 0
            checkErrorAndSync()
        } else {
            _syncSavingsAccountTransactionUiState.value =
                SyncSavingsAccountTransactionUiState.ShowError(R.string.feature_savings_nothing_to_sync)
        }
    }

    /**
     * This Method Check, SavingsAccountTransactionRequest Error Message is null or not, If
     * error message will not null. It means that SavingsAccountTransactionRequest already tried to
     * synced but there is some error to sync that Transaction.
     * and If error message  is null. It means SavingsAccountTransactionRequest never synced before,
     * start syncing that SavingsAccountTransactionRequest.
     */
    private fun checkErrorAndSync() {
        for (i in mSavingsAccountTransactionRequests.indices) {
            if (mSavingsAccountTransactionRequests[i].errorMessage == null) {
                mTransactionIndex = i
                val savingAccountType = mSavingsAccountTransactionRequests[i].savingsAccountType
                val savingAccountId = mSavingsAccountTransactionRequests[i].savingAccountId
                val transactionType = mSavingsAccountTransactionRequests[i].transactionType
                if (savingAccountId != null) {
                    processTransaction(
                        savingAccountType, savingAccountId, transactionType,
                        mSavingsAccountTransactionRequests[i]
                    )
                }
                break
            } else if (checkTransactionsSyncBeforeOrNot()) {
                _syncSavingsAccountTransactionUiState.value =
                    SyncSavingsAccountTransactionUiState.ShowError(R.string.feature_savings_error_fix_before_sync)
            }
        }
    }

    /**
     * This Method delete the SavingsAccountTransactionRequest from Database and load again
     * List<SavingsAccountTransactionRequest> and Update the UI.
    </SavingsAccountTransactionRequest> */
    fun showTransactionSyncSuccessfully() {
        mSavingsAccountTransactionRequests[mTransactionIndex].savingAccountId?.let {
            deleteAndUpdateSavingsAccountTransaction(
                it
            )
        }
    }

    /**
     * This Method will be called whenever Transaction sync failed. This Method set the Error
     * message to the SavingsAccountTransactionRequest and update
     * SavingsAccountTransactionRequest into the Database
     *
     * @param errorMessage Server Error Message
     */
    fun showTransactionSyncFailed(errorMessage: String?) {
        val transaction = mSavingsAccountTransactionRequests[mTransactionIndex]
        transaction.errorMessage = errorMessage
        updateSavingsAccountTransaction(transaction)
    }

    /**
     * This Method will be called when Transaction will be sync successfully and deleted from
     * Database then This Method Start Syncing next Transaction.
     *
     * @param transaction SavingsAccountTransactionRequest
     */
    fun showTransactionUpdatedSuccessfully(transaction: SavingsAccountTransactionRequest) {
        mSavingsAccountTransactionRequests[mTransactionIndex] = transaction
        updateUiState()
        mTransactionIndex += 1
        if (mSavingsAccountTransactionRequests.size != mTransactionIndex) {
            syncSavingsAccountTransactions()
        }
    }

    /**
     * This Method Update the UI. This Method will be called when sync transaction will be
     * deleted from Database and  load again Transaction from Database
     * List<SavingsAccountTransactionRequest>.
     *
     * @param transactions List<SavingsAccountTransactionRequest>
    </SavingsAccountTransactionRequest></SavingsAccountTransactionRequest> */
    fun showTransactionDeletedAndUpdated(transactions: MutableList<SavingsAccountTransactionRequest>) {
        mTransactionIndex = 0
        mSavingsAccountTransactionRequests = transactions
        updateUiState()
        if (mSavingsAccountTransactionRequests.size != 0) {
            syncSavingsAccountTransactions()
        } else {
            _syncSavingsAccountTransactionUiState.value =
                SyncSavingsAccountTransactionUiState.ShowEmptySavingsAccountTransactions(R.string.feature_savings_nothing_to_sync)
        }
    }

    private fun checkTransactionsSyncBeforeOrNot(): Boolean {
        Observable.from(mSavingsAccountTransactionRequests)
            .filter { savingsAccountTransactionRequest -> savingsAccountTransactionRequest.errorMessage != null }
            .subscribe { mTransactionsFailed += 1 }
        return mTransactionsFailed == mSavingsAccountTransactionRequests.size
    }

    /**
     * This Method Load the List<SavingsAccountTransactionRequest> from
     * SavingsAccountTransactionRequest_Table and Update the UI
    </SavingsAccountTransactionRequest> */
    fun loadDatabaseSavingsAccountTransactions() = viewModelScope.launch(Dispatchers.IO) {
        allSavingsAccountTransactionsUseCase().collect { result ->
            when (result) {
                is Resource.Error -> _syncSavingsAccountTransactionUiState.value =
                    SyncSavingsAccountTransactionUiState.ShowError(R.string.feature_savings_failed_to_load_savingaccounttransaction)

                is Resource.Loading -> _syncSavingsAccountTransactionUiState.value =
                    SyncSavingsAccountTransactionUiState.Loading

                is Resource.Success -> {
                    val r = result.data ?: emptyList()
                    if (r.isNotEmpty()) {
                        mSavingsAccountTransactionRequests = r.toMutableList()
                        updateUiState()
                    } else {
                        _syncSavingsAccountTransactionUiState.value =
                            SyncSavingsAccountTransactionUiState.ShowEmptySavingsAccountTransactions(
                                R.string.feature_savings_no_transaction_to_sync
                            )
                    }
                }
            }
        }
    }

    /**
     * THis Method Load the Payment Type Options from Database PaymentTypeOption_Table
     * and update the UI.
     */
    fun loadPaymentTypeOption() = viewModelScope.launch(Dispatchers.IO) {
        paymentTypeOptionUseCase().collect { result ->
            when (result) {
                is Resource.Error -> _syncSavingsAccountTransactionUiState.value =
                    SyncSavingsAccountTransactionUiState.ShowError(R.string.feature_savings_failed_to_load_paymentoptions)

                is Resource.Loading -> _syncSavingsAccountTransactionUiState.value =
                    SyncSavingsAccountTransactionUiState.Loading

                is Resource.Success -> {
                    mPaymentTypeOptions = result.data ?: emptyList()
                    updateUiState()
                }
            }
        }
    }

    private fun updateUiState() {
        if (mSavingsAccountTransactionRequests.isNotEmpty()) {
            _syncSavingsAccountTransactionUiState.value =
                SyncSavingsAccountTransactionUiState.ShowSavingsAccountTransactions(
                    mSavingsAccountTransactionRequests,
                    mPaymentTypeOptions
                )
        } else {
            _syncSavingsAccountTransactionUiState.value =
                SyncSavingsAccountTransactionUiState.ShowEmptySavingsAccountTransactions(R.string.feature_savings_no_transaction_to_sync)
        }
    }

    /**
     * This Method is for Sync Offline Saved SavingsAccountTransaction to the Server.
     * This method will be called when user will be in online mode and user's Internet connection
     * will be working well. If the Transaction will failed to sync then
     * updateSavingsAccountTransaction(SavingsAccountTransactionRequest request) save the developer
     * error message to Database with the failed Transaction. otherwise
     * deleteAndUpdateSavingsAccountTransaction(int savingsAccountId) delete the sync
     * Transaction from Database and load again Transaction List from
     * SavingsAccountTransactionRequest_Table and then sync next.
     *
     * @param type            SavingsAccount type
     * @param accountId       SavingsAccount Id
     * @param transactionType Transaction type
     * @param request         SavingsAccountTransactionRequest
     */
    private fun processTransaction(
        type: String?, accountId: Int, transactionType: String?,
        request: SavingsAccountTransactionRequest?
    ) = viewModelScope.launch(Dispatchers.IO) {
        processTransactionUseCase(type, accountId, transactionType, request!!).collect { result ->
            when (result) {
                is Resource.Error -> showTransactionSyncFailed(result.message)

                is Resource.Loading -> _syncSavingsAccountTransactionUiState.value =
                    SyncSavingsAccountTransactionUiState.Loading

                is Resource.Success -> showTransactionSyncSuccessfully()
            }
        }
    }

    /**
     * This Method delete the SavingsAccountTransactionRequest from the Database and load again
     * List<SavingsAccountTransactionRequest> from the SavingsAccountTransactionRequest_Table.
     * and returns the List<SavingsAccountTransactionRequest>.
     *
     * @param savingsAccountId SavingsAccountTransactionRequest's SavingsAccount Id
    </SavingsAccountTransactionRequest></SavingsAccountTransactionRequest> */
    private fun deleteAndUpdateSavingsAccountTransaction(savingsAccountId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            deleteAndUpdateTransactionsUseCase(savingsAccountId).collect { result ->
                when (result) {
                    is Resource.Error -> _syncSavingsAccountTransactionUiState.value =
                        SyncSavingsAccountTransactionUiState.ShowError(R.string.feature_savings_failed_to_update_list)

                    is Resource.Loading -> _syncSavingsAccountTransactionUiState.value =
                        SyncSavingsAccountTransactionUiState.Loading

                    is Resource.Success -> showTransactionDeletedAndUpdated(
                        result.data as MutableList<SavingsAccountTransactionRequest>
                    )
                }
            }
        }

    /**
     * This Method Update the SavingsAccountTransactionRequest in the Database. This will be called
     * whenever any transaction will be failed to sync then the sync developer error message will
     * be added to SavingsAccountTransactionRequest to update in Database.
     *
     * @param request SavingsAccountTransactionRequest
     */
    private fun updateSavingsAccountTransaction(request: SavingsAccountTransactionRequest?) =
        viewModelScope.launch(Dispatchers.IO) {
            updateLoanRepaymentTransactionUseCase(request).collect { result ->
                when (result) {
                    is Resource.Error -> _syncSavingsAccountTransactionUiState.value =
                        SyncSavingsAccountTransactionUiState.ShowError(R.string.feature_savings_failed_to_update_savingsaccount)

                    is Resource.Loading -> _syncSavingsAccountTransactionUiState.value =
                        SyncSavingsAccountTransactionUiState.Loading

                    is Resource.Success -> result.data?.let {
                        showTransactionUpdatedSuccessfully(it)
                    }

                }
            }
        }
}