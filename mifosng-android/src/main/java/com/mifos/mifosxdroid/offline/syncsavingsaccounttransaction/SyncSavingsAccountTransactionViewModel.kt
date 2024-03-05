package com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.core.objects.accounts.savings.SavingsAccountTransactionResponse
import com.mifos.mifosxdroid.R
import com.mifos.utils.MFErrorParser
import dagger.hilt.android.lifecycle.HiltViewModel
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class SyncSavingsAccountTransactionViewModel @Inject constructor(private val repository: SyncSavingsAccountTransactionRepository) :
    ViewModel() {

    private val _syncSavingsAccountTransactionUiState =
        MutableLiveData<SyncSavingsAccountTransactionUiState>()

    val syncSavingsAccountTransactionUiState: LiveData<SyncSavingsAccountTransactionUiState>
        get() = _syncSavingsAccountTransactionUiState

    private var mSavingsAccountTransactionRequests: MutableList<SavingsAccountTransactionRequest> =
        ArrayList()
    private var mTransactionIndex = 0
    private var mTransactionsFailed = 0

    fun syncSavingsAccountTransactions() {
        if (mSavingsAccountTransactionRequests.size != 0) {
            mTransactionIndex = 0
            checkErrorAndSync()
        } else {
            _syncSavingsAccountTransactionUiState.value =
                SyncSavingsAccountTransactionUiState.ShowError(R.string.nothing_to_sync)
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
                    SyncSavingsAccountTransactionUiState.ShowError(R.string.error_fix_before_sync)
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
        _syncSavingsAccountTransactionUiState.value =
            SyncSavingsAccountTransactionUiState.ShowSavingsAccountTransactions(
                mSavingsAccountTransactionRequests
            )
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
        _syncSavingsAccountTransactionUiState.value =
            SyncSavingsAccountTransactionUiState.ShowSavingsAccountTransactions(transactions)
        if (mSavingsAccountTransactionRequests.size != 0) {
            syncSavingsAccountTransactions()
        } else {
            _syncSavingsAccountTransactionUiState.value =
                SyncSavingsAccountTransactionUiState.ShowEmptySavingsAccountTransactions(R.string.nothing_to_sync)
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
    fun loadDatabaseSavingsAccountTransactions() {
        _syncSavingsAccountTransactionUiState.value =
            SyncSavingsAccountTransactionUiState.ShowProgressbar
        repository.allSavingsAccountTransactions()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<SavingsAccountTransactionRequest>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncSavingsAccountTransactionUiState.value =
                        SyncSavingsAccountTransactionUiState.ShowError(R.string.failed_to_load_savingaccounttransaction)
                }

                override fun onNext(transactionRequests: List<SavingsAccountTransactionRequest>) {
                    if (transactionRequests.isNotEmpty()) {
                        _syncSavingsAccountTransactionUiState.value =
                            SyncSavingsAccountTransactionUiState.ShowSavingsAccountTransactions(
                                transactionRequests as MutableList<SavingsAccountTransactionRequest>
                            )
                        mSavingsAccountTransactionRequests =
                            transactionRequests
                    } else {
                        _syncSavingsAccountTransactionUiState.value =
                            SyncSavingsAccountTransactionUiState.ShowEmptySavingsAccountTransactions(
                                R.string.no_transaction_to_sync
                            )
                    }
                }
            })

    }

    /**
     * THis Method Load the Payment Type Options from Database PaymentTypeOption_Table
     * and update the UI.
     */
    fun loadPaymentTypeOption() {
        _syncSavingsAccountTransactionUiState.value =
            SyncSavingsAccountTransactionUiState.ShowProgressbar
        repository.paymentTypeOption()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<com.mifos.core.objects.PaymentTypeOption>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncSavingsAccountTransactionUiState.value =
                        SyncSavingsAccountTransactionUiState.ShowError(R.string.failed_to_load_paymentoptions)
                }

                override fun onNext(paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption>) {
                    _syncSavingsAccountTransactionUiState.value =
                        SyncSavingsAccountTransactionUiState.ShowPaymentTypeOptions(
                            paymentTypeOptions
                        )
                }
            })

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
    ) {
        _syncSavingsAccountTransactionUiState.value =
            SyncSavingsAccountTransactionUiState.ShowProgressbar
        repository
            .processTransaction(type, accountId, transactionType, request!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingsAccountTransactionResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    showTransactionSyncFailed(MFErrorParser.errorMessage(e))
                }

                override fun onNext(savingsAccountTransactionResponse: SavingsAccountTransactionResponse) {
                    showTransactionSyncSuccessfully()
                }
            })

    }

    /**
     * This Method delete the SavingsAccountTransactionRequest from the Database and load again
     * List<SavingsAccountTransactionRequest> from the SavingsAccountTransactionRequest_Table.
     * and returns the List<SavingsAccountTransactionRequest>.
     *
     * @param savingsAccountId SavingsAccountTransactionRequest's SavingsAccount Id
    </SavingsAccountTransactionRequest></SavingsAccountTransactionRequest> */
    private fun deleteAndUpdateSavingsAccountTransaction(savingsAccountId: Int) {
        _syncSavingsAccountTransactionUiState.value =
            SyncSavingsAccountTransactionUiState.ShowProgressbar
        repository.deleteAndUpdateTransactions(savingsAccountId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<SavingsAccountTransactionRequest>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncSavingsAccountTransactionUiState.value =
                        SyncSavingsAccountTransactionUiState.ShowError(R.string.failed_to_update_list)
                }

                override fun onNext(savingsAccountTransactionRequests: List<SavingsAccountTransactionRequest>) {
                    showTransactionDeletedAndUpdated(savingsAccountTransactionRequests as MutableList<SavingsAccountTransactionRequest>)
                }
            })

    }

    /**
     * This Method Update the SavingsAccountTransactionRequest in the Database. This will be called
     * whenever any transaction will be failed to sync then the sync developer error message will
     * be added to SavingsAccountTransactionRequest to update in Database.
     *
     * @param request SavingsAccountTransactionRequest
     */
    private fun updateSavingsAccountTransaction(request: SavingsAccountTransactionRequest?) {
        _syncSavingsAccountTransactionUiState.value =
            SyncSavingsAccountTransactionUiState.ShowProgressbar
        repository.updateLoanRepaymentTransaction(request!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingsAccountTransactionRequest>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncSavingsAccountTransactionUiState.value =
                        SyncSavingsAccountTransactionUiState.ShowError(R.string.failed_to_update_savingsaccount)
                }

                override fun onNext(savingsAccountTransactionRequest: SavingsAccountTransactionRequest) {
                    showTransactionUpdatedSuccessfully(savingsAccountTransactionRequest)
                }
            })

    }
}