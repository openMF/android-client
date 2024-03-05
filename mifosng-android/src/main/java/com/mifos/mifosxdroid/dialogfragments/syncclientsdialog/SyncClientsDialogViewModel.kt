package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.zipmodels.LoanAndLoanRepayment
import com.mifos.core.objects.zipmodels.SavingsAccountAndTransactionTemplate
import com.mifos.utils.Constants
import com.mifos.utils.NetworkUtilsWrapper
import com.mifos.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.plugins.RxJavaPlugins
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
@HiltViewModel
class SyncClientsDialogViewModel @Inject constructor(private val repository: SyncClientsDialogRepository) :
    ViewModel() {

    @Inject
    lateinit var networkUtilsWrapper: NetworkUtilsWrapper

    private val _syncClientsDialogUiState = MutableLiveData<SyncClientsDialogUiState>()

    val syncClientsDialogUiState: LiveData<SyncClientsDialogUiState>
        get() = _syncClientsDialogUiState

    private var mClientList: List<Client> = ArrayList()
    private val mFailedSyncClient: MutableList<Client> = ArrayList()
    private var mLoanAccountList: List<LoanAccount> = ArrayList()
    private var mSavingsAccountList: List<SavingsAccount> = ArrayList()
    private var mLoanAccountSyncStatus = false
    private var mClientSyncIndex = 0
    private var mLoanAndRepaymentSyncIndex = 0
    private var mSavingsAndTransactionSyncIndex = 0
    private var maxSingleSyncClientProgressBar = 0

    private fun checkNetworkConnection(): Boolean {
        return networkUtilsWrapper.isNetworkConnected()
    }


    /**
     * This Method Start Syncing Clients. Start Syncing the Client Accounts.
     *
     * @param clients Selected Clients For Syncing
     */
    fun startSyncingClients(clients: List<Client>) {
        mClientList = clients
        checkNetworkConnectionAndSyncClient()
    }

    private fun syncClientAndUpdateUI() {
        mLoanAndRepaymentSyncIndex = 0
        mSavingsAndTransactionSyncIndex = 0
        mLoanAccountSyncStatus = false
        updateTotalSyncProgressBarAndCount()
        if (mClientSyncIndex != mClientList.size) {
            updateClientName()
            syncClientAccounts(mClientList[mClientSyncIndex].id)
        } else {
            _syncClientsDialogUiState.value = SyncClientsDialogUiState.ShowClientsSyncSuccessfully
        }
    }

    fun checkNetworkConnectionAndSyncClient() {
        if (checkNetworkConnection()) {
            syncClientAndUpdateUI()
        } else {
            _syncClientsDialogUiState.value = SyncClientsDialogUiState.ShowClientsSyncSuccessfully
            _syncClientsDialogUiState.value = SyncClientsDialogUiState.DismissDialog
        }
    }

    fun checkNetworkConnectionAndSyncLoanAndLoanRepayment() {
        if (checkNetworkConnection()) {
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let { syncLoanAndLoanRepayment(it) }
        } else {
            _syncClientsDialogUiState.value = SyncClientsDialogUiState.ShowNetworkIsNotAvailable
            _syncClientsDialogUiState.value = SyncClientsDialogUiState.DismissDialog
        }
    }

    fun checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate() {
        if (checkNetworkConnection()) {
            mSavingsAccountList[mSavingsAndTransactionSyncIndex].id?.let {
                syncSavingsAccountAndTemplate(
                    mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType?.endpoint,
                    it
                )
            }
        } else {
            _syncClientsDialogUiState.value = SyncClientsDialogUiState.ShowNetworkIsNotAvailable
            _syncClientsDialogUiState.value = SyncClientsDialogUiState.DismissDialog
        }
    }

    fun checkAccountsSyncStatusAndSyncAccounts() {
        if (mLoanAccountList.isNotEmpty() && !mLoanAccountSyncStatus) {
            //Sync the Active Loan and LoanRepayment
            checkNetworkConnectionAndSyncLoanAndLoanRepayment()
        } else if (mSavingsAccountList.isNotEmpty()) {
            //Sync the Active Savings Account
            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
        } else {
            // If LoanAccounts and SavingsAccount are null then sync Client to Database
            maxSingleSyncClientProgressBar = 1
            syncClient(mClientList[mClientSyncIndex])
        }
    }

    fun setLoanAccountSyncStatusTrue() {
        if (mLoanAndRepaymentSyncIndex == mLoanAccountList.size) {
            mLoanAccountSyncStatus = true
        }
    }

    fun onAccountSyncFailed(e: Throwable?) {
        try {
            if (e is HttpException) {
                val singleSyncClientMax = maxSingleSyncClientProgressBar
                _syncClientsDialogUiState.value =
                    SyncClientsDialogUiState.UpdateSingleSyncClientProgressBar(singleSyncClientMax)
                mFailedSyncClient.add(mClientList[mClientSyncIndex])
                mClientSyncIndex += 1
                _syncClientsDialogUiState.value =
                    SyncClientsDialogUiState.ShowSyncedFailedClients(mFailedSyncClient.size)
                checkNetworkConnectionAndSyncClient()
            }
        } catch (throwable: Throwable) {
            RxJavaPlugins.getInstance().errorHandler.handleError(throwable)
        }
    }

    /**
     * Sync the Client Account with Client Id. This method fetching the Client Accounts from the
     * REST API using retrofit 2 and saving these accounts to Database with DatabaseHelperClient
     * and then DataManagerClient gives the returns the Clients Accounts to Presenter.
     *
     *
     *
     *
     * onNext : As Client Accounts Successfully sync then now sync the there Loan and LoanRepayment
     * onError :
     *
     * @param clientId Client Id
     */
    private fun syncClientAccounts(clientId: Int) {
        repository.syncClientAccounts(clientId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<ClientAccounts>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncClientsDialogUiState.value =
                        SyncClientsDialogUiState.ShowError(e.message.toString())
                    //Updating UI
                    mFailedSyncClient.add(mClientList[mClientSyncIndex])
                    _syncClientsDialogUiState.value =
                        SyncClientsDialogUiState.ShowSyncedFailedClients(mFailedSyncClient.size)
                    mClientSyncIndex += 1
                    checkNetworkConnectionAndSyncClient()
                }

                override fun onNext(clientAccounts: ClientAccounts) {
                    mLoanAccountList = Utils.getActiveLoanAccounts(
                        clientAccounts
                            .loanAccounts
                    )
                    mSavingsAccountList = Utils.getSyncableSavingsAccounts(
                        clientAccounts
                            .savingsAccounts
                    )

                    //Updating UI
                    maxSingleSyncClientProgressBar = mLoanAccountList.size +
                            mSavingsAccountList.size
                    checkAccountsSyncStatusAndSyncAccounts()
                }
            })

    }

    /**
     * This Method Syncing the Client's Loans and their LoanRepayment. This is the Observable.zip
     * In Which two request is going to server Loans and LoanRepayment and This request will not
     * complete till that both request successfully got response (200 OK). In Which one will fail
     * then response will come in onError. and If both request is 200 response then response will
     * come in onNext.
     *
     * @param loanId Loan Id
     */
    private fun syncLoanAndLoanRepayment(loanId: Int) {
        Observable.combineLatest(
            repository.syncLoanById(loanId),
            repository.syncLoanRepaymentTemplate(loanId)
        ) { loanWithAssociations, loanRepaymentTemplate ->
            val loanAndLoanRepayment = LoanAndLoanRepayment()
            loanAndLoanRepayment.loanWithAssociations = loanWithAssociations
            loanAndLoanRepayment.loanRepaymentTemplate = loanRepaymentTemplate
            loanAndLoanRepayment
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<LoanAndLoanRepayment>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(loanAndLoanRepayment: LoanAndLoanRepayment) {
                    mLoanAndRepaymentSyncIndex += 1
                    _syncClientsDialogUiState.value =
                        SyncClientsDialogUiState.UpdateSingleSyncClientProgressBar(
                            mLoanAndRepaymentSyncIndex
                        )
                    if (mLoanAndRepaymentSyncIndex != mLoanAccountList.size) {
                        checkNetworkConnectionAndSyncLoanAndLoanRepayment()
                    } else {
                        setLoanAccountSyncStatusTrue()
                        checkAccountsSyncStatusAndSyncAccounts()
                    }
                }
            })

    }

    /**
     * This Method Fetch SavingsAccount and SavingsAccountTransactionTemplate and Sync them in
     * Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private fun syncSavingsAccountAndTemplate(savingsAccountType: String?, savingsAccountId: Int) {

        Observable.combineLatest(
            repository.syncSavingsAccount(
                savingsAccountType, savingsAccountId,
                Constants.TRANSACTIONS
            ),
            repository.syncSavingsAccountTransactionTemplate(
                savingsAccountType,
                savingsAccountId, Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT
            )
        ) { savingsAccountWithAssociations, savingsAccountTransactionTemplate ->
            val accountAndTransactionTemplate = SavingsAccountAndTransactionTemplate()
            accountAndTransactionTemplate.savingsAccountTransactionTemplate =
                savingsAccountTransactionTemplate
            accountAndTransactionTemplate.savingsAccountWithAssociations =
                savingsAccountWithAssociations
            accountAndTransactionTemplate
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<SavingsAccountAndTransactionTemplate>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(savingsAccountAndTransactionTemplate: SavingsAccountAndTransactionTemplate) {
                    mSavingsAndTransactionSyncIndex += 1
                    _syncClientsDialogUiState.value =
                        SyncClientsDialogUiState.UpdateSingleSyncClientProgressBar(
                            mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex
                        )
                    if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                        checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
                    } else {
                        syncClient(mClientList[mClientSyncIndex])
                    }
                }
            })

    }

    /**
     * This Method Saving the Clients to Database, If their Accounts, Loan and LoanRepayment
     * saved successfully to Synced.
     *
     * @param client
     */
    fun syncClient(client: Client) {
        client.sync = true
        repository.syncClientInDatabase(client)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Client>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncClientsDialogUiState.value =
                        SyncClientsDialogUiState.ShowError(e.message.toString())
                }

                override fun onNext(client: Client) {
                    val singleSyncClientMax = maxSingleSyncClientProgressBar
                    _syncClientsDialogUiState.value =
                        SyncClientsDialogUiState.UpdateSingleSyncClientProgressBar(
                            singleSyncClientMax
                        )
                    mClientSyncIndex += 1
                    checkNetworkConnectionAndSyncClient()
                }
            })

    }

    private fun updateTotalSyncProgressBarAndCount() {
        _syncClientsDialogUiState.value =
            SyncClientsDialogUiState.UpdateTotalSyncClientProgressBarAndCount(mClientSyncIndex)
    }

    private fun updateClientName() {
        val clientName = mClientList[mClientSyncIndex].firstname +
                mClientList[mClientSyncIndex].lastname
        _syncClientsDialogUiState.value = SyncClientsDialogUiState.ShowSyncingClient(clientName)
    }
}