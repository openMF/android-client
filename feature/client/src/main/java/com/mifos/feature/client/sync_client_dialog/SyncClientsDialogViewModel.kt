package com.mifos.feature.client.sync_client_dialog

import androidx.lifecycle.ViewModel
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.NetworkUtilsWrapper
import com.mifos.core.data.repository.SyncClientsDialogRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.zipmodels.LoanAndLoanRepayment
import com.mifos.core.objects.zipmodels.SavingsAccountAndTransactionTemplate
import com.mifos.feature.client.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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
class SyncClientsDialogViewModel @Inject constructor(
    private val repository: SyncClientsDialogRepository,
    private val networkUtilsWrapper: NetworkUtilsWrapper,
    private val prefManager: PrefManager
) : ViewModel() {

    private var mClientList: List<Client> = ArrayList()
    private val mFailedSyncClient: MutableList<Client> = ArrayList()
    private var mLoanAccountList: List<LoanAccount> = ArrayList()
    private var mSavingsAccountList: List<SavingsAccount> = ArrayList()
    private var mLoanAccountSyncStatus = false
    private var mClientSyncIndex = 0
    private var mLoanAndRepaymentSyncIndex = 0
    private var mSavingsAndTransactionSyncIndex = 0
    private var maxSingleSyncClientProgressBar = 0

    private val _syncClientsDialogUiState =
        MutableStateFlow<SyncClientsDialogUiState>(SyncClientsDialogUiState.Loading)
    val syncClientsDialogUiState: StateFlow<SyncClientsDialogUiState> = _syncClientsDialogUiState

    private val _syncClientData: MutableStateFlow<SyncClientsDialogData> = MutableStateFlow(
        SyncClientsDialogData()
    )
    val syncClientData: StateFlow<SyncClientsDialogData> = _syncClientData

    fun setClientList(clientsList: List<Client>) {
        mClientList = clientsList
        _syncClientData.update { it.copy(clientList = clientsList) }
    }

    fun syncClient() {
        if (prefManager.userStatus == Constants.USER_ONLINE) {
            checkNetworkConnection {
                syncClientAndUpdateUI()
            }
        }
    }

    private fun syncClientAndUpdateUI() {
        resetIndexes()
        updateTotalSyncProgressBarAndCount()
        if (mClientSyncIndex != mClientList.size) {
            updateClientName()
            syncClientAccounts(mClientList[mClientSyncIndex].id)
        } else {
            _syncClientData.update { it.copy(isSyncSuccess = true) }
        }
    }

    private fun checkNetworkConnectionAndSyncLoanAndLoanRepayment() {
        checkNetworkConnection {
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let { syncLoanAndLoanRepayment(it) }
        }
    }

    private fun resetIndexes() {
        mLoanAccountSyncStatus = false
        mLoanAndRepaymentSyncIndex = 0
        mSavingsAndTransactionSyncIndex = 0
    }

    private fun checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate() {
        checkNetworkConnection {
            val endPoint =
                mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType?.endpoint
            val id = mSavingsAccountList[mSavingsAndTransactionSyncIndex].id
            if (endPoint != null && id != null) {
                syncSavingsAccountAndTemplate(endPoint, id)
            }
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
                _syncClientData.update { it.copy(singleSyncCount = singleSyncClientMax) }
                mFailedSyncClient.add(mClientList[mClientSyncIndex])
                mClientSyncIndex += 1
                _syncClientData.update { it.copy(failedSyncGroupCount = mFailedSyncClient.size) }
                syncClient()
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
                    onAccountSyncFailed(e)
                }

                override fun onNext(clientAccounts: ClientAccounts) {
                    mLoanAccountList = getActiveLoanAccounts(
                        clientAccounts
                            .loanAccounts
                    )
                    mSavingsAccountList = getSyncableSavingsAccounts(
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
                    _syncClientData.update { it.copy(singleSyncCount = mLoanAndRepaymentSyncIndex) }
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
                    _syncClientData.update { it.copy(singleSyncCount = mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex) }
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
        client.groupId = mClientList[mClientSyncIndex].id
        client.sync = true
        repository.syncClientInDatabase(client)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Client>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncClientsDialogUiState.value =
                        SyncClientsDialogUiState.Error(message = e.message.toString())
                }

                override fun onNext(client: Client) {
                    val singleSyncClientMax = maxSingleSyncClientProgressBar
                    _syncClientData.update { it.copy(singleSyncCount = singleSyncClientMax) }
                    mClientSyncIndex += 1
                    syncClient()
                }
            })

    }

    private fun updateTotalSyncProgressBarAndCount() {
        _syncClientData.update { it.copy(totalSyncCount = mClientSyncIndex) }
    }

    private fun updateClientName() {
        val clientName = mClientList[mClientSyncIndex].firstname +
                mClientList[mClientSyncIndex].lastname
        _syncClientData.update { it.copy(clientName = clientName) }
    }

    private fun checkNetworkConnection(
        taskWhenOnline: () -> Unit
    ) {
        if (networkUtilsWrapper.isNetworkConnected()) {
            taskWhenOnline.invoke()
        } else {
            _syncClientsDialogUiState.value = SyncClientsDialogUiState.Error(
                messageResId = R.string.feature_client_error_network_not_available,
                imageVector = MifosIcons.WifiOff
            )
        }
    }

    fun getActiveLoanAccounts(loanAccountList: List<LoanAccount>?): List<LoanAccount> {
        val loanAccounts: MutableList<LoanAccount> = ArrayList()
        Observable.from(loanAccountList)
            .filter { loanAccount -> loanAccount.status?.active }
            .subscribe { loanAccount -> loanAccounts.add(loanAccount) }
        return loanAccounts
    }

    fun getSyncableSavingsAccounts(savingsAccounts: List<SavingsAccount>?): List<SavingsAccount> {
        val accounts: MutableList<SavingsAccount> = ArrayList()
        Observable.from(savingsAccounts)
            .filter { savingsAccount ->
                savingsAccount.depositType?.value == "Savings" &&
                        savingsAccount.status?.active == true &&
                        !savingsAccount.depositType!!.isRecurring
            }
            .subscribe { savingsAccount -> accounts.add(savingsAccount) }
        return accounts
    }
}