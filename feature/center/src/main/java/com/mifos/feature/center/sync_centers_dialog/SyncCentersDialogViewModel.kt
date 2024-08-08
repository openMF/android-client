package com.mifos.feature.center.sync_centers_dialog

import androidx.lifecycle.ViewModel
import com.mifos.core.common.utils.Constants
import com.mifos.core.common.utils.NetworkUtilsWrapper
import com.mifos.core.data.repository.SyncCentersDialogRepository
import com.mifos.core.datastore.PrefManager
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.objects.accounts.CenterAccounts
import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.accounts.GroupAccounts
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.core.objects.group.Group
import com.mifos.core.objects.group.GroupWithAssociations
import com.mifos.core.objects.zipmodels.LoanAndLoanRepayment
import com.mifos.core.objects.zipmodels.SavingsAccountAndTransactionTemplate
import com.mifos.feature.center.R
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
class SyncCentersDialogViewModel @Inject constructor(
    private val repository: SyncCentersDialogRepository,
    private val networkUtilsWrapper: NetworkUtilsWrapper,
    private val prefManager: PrefManager,
) : ViewModel() {

    private val _syncCentersDialogUiState =
        MutableStateFlow<SyncCentersDialogUiState>(SyncCentersDialogUiState.Loading)
    val syncCentersDialogUiState: StateFlow<SyncCentersDialogUiState> = _syncCentersDialogUiState

    private val _syncCenterData: MutableStateFlow<SyncCentersDialogData> = MutableStateFlow(
        SyncCentersDialogData()
    )
    val syncCenterData: StateFlow<SyncCentersDialogData> = _syncCenterData


    private var mLoanAccountList: List<LoanAccount> = ArrayList()
    private var mSavingsAccountList: List<SavingsAccount> = ArrayList()
    private var mMemberLoanAccountsList: List<LoanAccount> = ArrayList()
    private var mCenterList: List<Center> = ArrayList()
    private val mFailedSyncCenter: MutableList<Center> = ArrayList()
    private var mGroups: List<Group> = ArrayList()
    private var mClients: List<Client> = ArrayList()
    private var mLoanAccountSyncStatus = false
    private var mSavingAccountSyncStatus = false
    private var mCenterSyncIndex = 0
    private var mLoanAndRepaymentSyncIndex = 0
    private var mSavingsAndTransactionSyncIndex = 0
    private var mMemberLoanSyncIndex = 0
    private var mClientSyncIndex = 0
    private var mGroupSyncIndex = 0
    private var maxSingleSyncCenterProgressBar = 0

    fun setCentersList(centersList: List<Center>) {
        mCenterList = centersList
        _syncCenterData.update { it.copy(centersList = centersList) }
    }

    fun syncCenter() {
        if (prefManager.userStatus == Constants.USER_ONLINE) {
            checkNetworkConnection {
                syncCenterAndUpdateUI()
            }
        }
    }

    /**
     * This Method checking that mCenterSyncIndex and mCenterList Size are equal or not. If they
     * are equal, It means all centers have been synced otherwise continue syncing centers.
     */
    private fun syncCenterAndUpdateUI() {
        resetIndexes()
        updateTotalSyncProgressBarAndCount()
        if (mCenterSyncIndex != mCenterList.size) {
            updateCenterName()
            mCenterList[mCenterSyncIndex].id?.let { syncCenterAccounts(it) }
        } else {
            _syncCenterData.update { it.copy(isSyncSuccess = true) }
        }
    }

    /**
     * This Method for resetting the mLoanAndRepaymentSyncIndex and mSavingsAndTransactionSyncIndex
     * and  mMemberLoanSyncIndex and mLoanAccountSyncStatus and mSavingAccountSyncStatus.
     */
    private fun resetIndexes() {
        mLoanAccountSyncStatus = false
        mSavingAccountSyncStatus = false
        mLoanAndRepaymentSyncIndex = 0
        mSavingsAndTransactionSyncIndex = 0
        mMemberLoanSyncIndex = 0
    }

    /**
     * This Method will be called when ever any request will be failed synced.
     *
     * @param e Throwable
     */
    private fun onAccountSyncFailed(e: Throwable) {
        try {
            if (e is HttpException) {
                val singleSyncCenterMax = maxSingleSyncCenterProgressBar
                _syncCenterData.update { it.copy(singleSyncCount = singleSyncCenterMax) }
                mFailedSyncCenter.add(mCenterList[mCenterSyncIndex])
                mCenterSyncIndex += 1
                _syncCenterData.update { it.copy(failedSyncGroupCount = mFailedSyncCenter.size) }
                syncCenter()
            }
        } catch (throwable: Throwable) {
            RxJavaPlugins.getInstance().errorHandler.handleError(throwable)
        }
    }

    /**
     * Sync the Center Account with Center Id. This method fetching the Center Accounts from the
     * REST API using retrofit 2 and saving these accounts to Database with DatabaseHelperCenter
     * and then DataManagerCenter returns the Center Accounts to Presenter.
     *
     *
     *
     *
     * onNext : As Center Accounts Successfully sync then sync there Loan and LoanRepayment
     * onError :
     *
     * @param centerId Center Id
     */
    private fun syncCenterAccounts(centerId: Int) {
        repository.syncCenterAccounts(centerId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<CenterAccounts>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(centerAccounts: CenterAccounts) {
                    mLoanAccountList = getActiveLoanAccounts(
                        centerAccounts
                            .loanAccounts
                    )
                    mSavingsAccountList = getActiveSavingsAccounts(
                        centerAccounts
                            .savingsAccounts
                    )
                    mMemberLoanAccountsList = getActiveLoanAccounts(
                        centerAccounts
                            .memberLoanAccounts
                    )
                    //Updating UI
                    maxSingleSyncCenterProgressBar = (mLoanAccountList.size +
                            mSavingsAccountList.size + mMemberLoanAccountsList.size)
                    checkAccountsSyncStatusAndSyncAccounts()
                }
            })

    }

    /**
     * This Method check the LoanAccount isEmpty or not, If LoanAccount is not Empty the sync the
     * loanAccount locally and If LoanAccount is empty then check to the next SavingAccount
     * isEmpty or not. if SavingAccount is not empty then sync the SavingsAccount locally and if
     * savingsAccount are Empty then check to the next MemberLoanAccount isEmpty or not. if
     * MemberLoanAccount is not empty then sync the MemberLoanAccount locally and if
     * MemberLoanAccount are Empty the sync the Centers locally, Yep Centers has been synced.
     */
    private fun checkAccountsSyncStatusAndSyncAccounts() {
        if (mLoanAccountList.isNotEmpty() && !mLoanAccountSyncStatus) {
            //Sync the Active Loan and LoanRepayment
            checkNetworkConnectionAndSyncLoanAndLoanRepayment()
        } else if (mSavingsAccountList.isNotEmpty() && !mSavingAccountSyncStatus) {
            //Sync the Active Savings Account
            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
        } else if (mMemberLoanAccountsList.isNotEmpty()) {
            //Sync the Active member Loan Account
            checkNetworkConnectionAndSyncMemberLoanAndMemberLoanRepayment()
        } else {
            maxSingleSyncCenterProgressBar = 1
            mCenterList[mCenterSyncIndex].id?.let { loadCenterAssociateGroups(it) }
        }
    }

    /**
     * This Method Checking network connection and  Syncing the LoanAndLoanRepayment.
     * If found no internet connection then stop syncing the Centers and dismiss the dialog.
     */
    private fun checkNetworkConnectionAndSyncLoanAndLoanRepayment() {
        checkNetworkConnection {
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let { syncLoanAndLoanRepayment(it) }
        }
    }

    /**
     * This Method Checking network connection and  Syncing the MemberLoanAndMemberLoanRepayment.
     * If found no internet connection then stop syncing the Centers and dismiss the dialog.
     */
    private fun checkNetworkConnectionAndSyncMemberLoanAndMemberLoanRepayment() {
        checkNetworkConnection {
            val endPoint =
                mMemberLoanAccountsList[mMemberLoanSyncIndex].loanType?.value
            val id = mSavingsAccountList[mSavingsAndTransactionSyncIndex].id
            if (endPoint != null && id != null) {
                syncSavingsAccountAndTemplate(endPoint, id)
            }
        }
    }

    /**
     * This method checking the network connection and syncing the SavingsAccountAndTransactions.
     * If found no internet connection then stop syncing the centers and dismiss the dialog.
     */
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

    /**
     * This Method for setting mLoanAccountSyncStatus = true, It means LoanAccounts have been
     * synced locally.
     */
    private fun setLoanAccountSyncStatusTrue() {
        if (mLoanAndRepaymentSyncIndex == mLoanAccountList.size) {
            mLoanAccountSyncStatus = true
        }
    }

    /**
     * This Method for setting mSavingAccountSyncStatus = true, It means LoanAccounts have been
     * synced locally.
     */
    private fun setSavingAccountSyncStatusTrue() {
        if (mSavingsAndTransactionSyncIndex == mSavingsAccountList.size) {
            mSavingAccountSyncStatus = true
        }
    }

    /**
     * This Method Syncing the Center's Loan and their LoanRepayment. This is the
     * Observable.combineLatest In Which two request is going to server Loans and LoanRepayment
     * and This request will not complete till that both request completed successfully with
     * response (200 OK). If one will fail then response will come in onError. and If both
     * request is 200 response then response will come in onNext.
     *
     * @param loanId Loan Id
     */
    private fun syncLoanAndLoanRepayment(loanId: Int) {
        getLoanAndLoanRepayment(loanId)
            .subscribe(object : Subscriber<LoanAndLoanRepayment>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(loanAndLoanRepayment: LoanAndLoanRepayment) {
                    mLoanAndRepaymentSyncIndex += 1
                    _syncCenterData.update { it.copy(singleSyncCount = mLoanAndRepaymentSyncIndex) }
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
     * This Method Syncing the Member's Loan and their LoanRepayment. This is the
     * Observable.combineLatest In Which two request is going to server Loans and LoanRepayment
     * and This request will not complete till that both request completed successfully with
     * response (200 OK). If one will fail then response will come in onError. and If both
     * request is 200 response then response will come in onNext.
     *
     * @param loanId Loan Id
     */
    private fun syncMemberLoanAndMemberLoanRepayment(loanId: Int) {
        getLoanAndLoanRepayment(loanId)
            .subscribe(object : Subscriber<LoanAndLoanRepayment>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(loanAndLoanRepayment: LoanAndLoanRepayment) {
                    mMemberLoanSyncIndex += 1
                    _syncCenterData.update { it.copy(singleSyncCount = mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex + mMemberLoanSyncIndex) }
                    if (mMemberLoanSyncIndex != mMemberLoanAccountsList.size) {
                        checkNetworkConnectionAndSyncMemberLoanAndMemberLoanRepayment()
                    } else {
                        mCenterList[mCenterSyncIndex].id?.let { loadCenterAssociateGroups(it) }
                    }
                }
            })

    }

    /**
     * This Method Fetching the  SavingsAccount and SavingsAccountTransactionTemplate and Syncing
     * them in Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private fun syncSavingsAccountAndTemplate(savingsAccountType: String, savingsAccountId: Int) {
        getSavingsAccountAndTemplate(savingsAccountType, savingsAccountId)
            .subscribe(object : Subscriber<SavingsAccountAndTransactionTemplate>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(savingsAccountAndTransactionTemplate: SavingsAccountAndTransactionTemplate) {
                    mSavingsAndTransactionSyncIndex += 1
                    _syncCenterData.update { it.copy(singleSyncCount = mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex) }
                    if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                        checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate()
                    } else {
                        setSavingAccountSyncStatusTrue()
                        checkAccountsSyncStatusAndSyncAccounts()
                    }
                }
            })

    }

    /**
     * This Method syncing the Center into the Database.
     *
     * @param center Center
     */
    private fun syncCenter(center: Center) {
        center.id = mCenterList[mCenterSyncIndex].id
        center.sync = true
        repository.syncCenterInDatabase(center)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                val singleSyncCenterMax = maxSingleSyncCenterProgressBar
                _syncCenterData.update { it.copy(singleSyncCount = singleSyncCenterMax) }
                mCenterSyncIndex += 1
                syncCenter()
            }
    }

    /**
     * This Method Fetching the LoanAndLoanRepayment
     *
     * @param loanId Loan Id
     * @return LoanAndLoanRepayment
     */
    private fun getLoanAndLoanRepayment(loanId: Int): Observable<LoanAndLoanRepayment> {
        return Observable.combineLatest(
            repository.syncLoanById(loanId),
            repository.syncLoanRepaymentTemplate(loanId)
        ) { loanWithAssociations, loanRepaymentTemplate ->
            LoanAndLoanRepayment(
                loanWithAssociations,
                loanRepaymentTemplate
            )
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    /**
     * This method fetching SavingsAccountAndTemplate.
     *
     * @param savingsAccountType
     * @param savingsAccountId
     * @return SavingsAccountAndTransactionTemplate
     */
    private fun getSavingsAccountAndTemplate(
        savingsAccountType: String, savingsAccountId: Int
    ): Observable<SavingsAccountAndTransactionTemplate> {
        return Observable.combineLatest(
            repository.syncSavingsAccount(
                savingsAccountType, savingsAccountId,
                Constants.TRANSACTIONS
            ),
            repository.syncSavingsAccountTransactionTemplate(
                savingsAccountType,
                savingsAccountId, Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT
            )
        ) { savingsAccountWithAssociations, savingsAccountTransactionTemplate ->
            SavingsAccountAndTransactionTemplate(
                savingsAccountWithAssociations, savingsAccountTransactionTemplate
            )
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    /**
     * This Method fetching the Center Associate Groups List.
     *
     * @param centerId Center Id
     */
    private fun loadCenterAssociateGroups(centerId: Int) {
        _syncCentersDialogUiState.value = SyncCentersDialogUiState.Loading
        repository.getCenterWithAssociations(centerId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<CenterWithAssociations>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(centerWithAssociations: CenterWithAssociations) {
                    mGroups = centerWithAssociations.groupMembers
                    mGroupSyncIndex = 0
                    resetIndexes()
                    if (mGroups.isNotEmpty()) {
                        _syncCenterData.update { it.copy(totalGroupsSyncCount = mGroups.size) }
                        mGroups[mGroupSyncIndex].id?.let { syncGroupAccounts(it) }
                    } else {
                        syncCenter(mCenterList[mCenterSyncIndex])
                    }
                }
            })

    }

    /**
     * This Method fetching the Group Associate Clients List.
     *
     * @param groupId Group Id
     */
    private fun loadGroupAssociateClients(groupId: Int) {
        _syncCentersDialogUiState.value = SyncCentersDialogUiState.Loading
        repository.getGroupWithAssociations(groupId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GroupWithAssociations>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(groupWithAssociations: GroupWithAssociations) {
                    mClients = getActiveClients(groupWithAssociations.clientMembers)
                    mClientSyncIndex = 0
                    resetIndexes()
                    if (mClients.isNotEmpty()) {
                        _syncCenterData.update { it.copy(totalClientSyncCount = mClients.size) }
                        syncClientAccounts(mClients[mClientSyncIndex].id)
                    } else {
                        syncGroup(mGroups[mGroupSyncIndex])
                    }
                }
            })

    }

    /**
     * Sync the Group Account with Group Id. This method fetching the Group Accounts from the
     * REST API using retrofit 2 and saving these accounts to Database with DatabaseHelperGroup
     * and then DataManagerGroup gives the returns the Group Accounts to Presenter.
     *
     *
     *
     *
     * onNext : As Group Accounts Successfully sync then now sync the there Loan and LoanRepayment
     * onError :
     *
     * @param groupId Group Id
     */
    private fun syncGroupAccounts(groupId: Int) {
        repository.syncGroupAccounts(groupId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GroupAccounts>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(groupAccounts: GroupAccounts) {
                    mLoanAccountList = getActiveLoanAccounts(
                        groupAccounts
                            .loanAccounts
                    )
                    mSavingsAccountList = getActiveSavingsAccounts(
                        groupAccounts
                            .savingsAccounts
                    )
                    checkAccountsSyncStatusAndSyncGroupAccounts()
                }
            })

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
                    checkAccountsSyncStatusAndSyncClientAccounts()
                }
            })

    }

    /**
     * This Method check the Group LoanAccount isEmpty or not, If LoanAccount is not Empty the sync
     * the loanAccount locally and If LoanAccount is empty then check to the next SavingAccount
     * isEmpty or not. if SavingAccount is not empty then sync the SavingsAccount locally and if
     * savingsAccount are Empty the sync the groups locally, Yep Groups has been synced.
     */
    private fun checkAccountsSyncStatusAndSyncGroupAccounts() {
        if (mLoanAccountList.isNotEmpty() && !mLoanAccountSyncStatus) {
            //Sync the Active Loan and LoanRepayment
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let {
                syncGroupLoanAndLoanRepayment(
                    it
                )
            }
        } else if (mSavingsAccountList.isNotEmpty()) {
            //Sync the Active Savings Account
            mSavingsAccountList[mSavingsAndTransactionSyncIndex].id?.let {
                mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType?.endpoint?.let { it1 ->
                    syncGroupSavingsAccountAndTemplate(
                        it1,
                        it
                    )
                }
            }
        } else {
            mGroups[mGroupSyncIndex].id?.let { loadGroupAssociateClients(it) }
        }
    }

    /**
     * This Method check the Client LoanAccount isEmpty or not, If LoanAccount is not Empty the sync
     * the loanAccount locally and If LoanAccount is empty then check to the next SavingAccount
     * isEmpty or not. if SavingAccount is not empty then sync the SavingsAccount locally and if
     * savingsAccount are Empty the sync the groups locally, Yep Groups has been synced.
     */
    private fun checkAccountsSyncStatusAndSyncClientAccounts() {
        if (mLoanAccountList.isNotEmpty() && !mLoanAccountSyncStatus) {
            //Sync the Active Loan and LoanRepayment
            mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let {
                syncClientLoanAndLoanRepayment(
                    it
                )
            }
        } else if (mSavingsAccountList.isNotEmpty()) {
            //Sync the Active Savings Account
            mSavingsAccountList[mSavingsAndTransactionSyncIndex].id?.let {
                mSavingsAccountList[mSavingsAndTransactionSyncIndex].depositType?.endpoint?.let { it1 ->
                    syncClientSavingsAccountAndTemplate(
                        it1,
                        it
                    )
                }
            }
        } else {
            syncClient(mClients[mClientSyncIndex])
        }
    }

    /**
     * This Method Saving the Groups to Database, If their Accounts, Loan and LoanRepayment
     * saved Synced successfully.
     *
     * @param group
     */
    private fun syncGroup(group: Group) {
        group.centerId = mCenterList[mCenterSyncIndex].id
        group.sync = true
        repository.syncGroupInDatabase(group)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Group>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncCentersDialogUiState.value =
                        SyncCentersDialogUiState.Error(message = e.message.toString())
                }

                override fun onNext(group: Group) {
                    resetIndexes()
                    mGroupSyncIndex += 1
                    if (mGroups.size == mGroupSyncIndex) {
                        syncCenter(mCenterList[mCenterSyncIndex])
                    } else {
                        mGroups[mGroupSyncIndex].id?.let { syncGroupAccounts(it) }
                    }
                }
            })

    }

    /**
     * This Method Saving the Clients to Database, If their Accounts, Loan and LoanRepayment
     * saved Synced successfully.
     *
     * @param client
     */
    private fun syncClient(client: Client) {
        client.groupId = mGroups[mGroupSyncIndex].id
        client.sync = true
        repository.syncClientInDatabase(client)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<Client>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _syncCentersDialogUiState.value =
                        SyncCentersDialogUiState.Error(message = e.message.toString())
                }

                override fun onNext(client: Client) {
                    resetIndexes()
                    mClientSyncIndex += 1
                    if (mClients.size == mClientSyncIndex) {
                        syncGroup(mGroups[mGroupSyncIndex])
                    } else {
                        syncClientAccounts(mClients[mClientSyncIndex].id)
                    }
                }
            })

    }

    /**
     * This Method Syncing the Group's Loan and their LoanRepayment. This is the
     * Observable.combineLatest In Which two request is going to server Loans and LoanRepayment
     * and This request will not complete till that both request completed successfully with
     * response (200 OK). If one will fail then response will come in onError. and If both
     * request is 200 response then response will come in onNext.
     *
     * @param loanId Loan Id
     */
    private fun syncGroupLoanAndLoanRepayment(loanId: Int) {
        getLoanAndLoanRepayment(loanId)
            .subscribe(object : Subscriber<LoanAndLoanRepayment>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(loanAndLoanRepayment: LoanAndLoanRepayment) {
                    mLoanAndRepaymentSyncIndex += 1
                    if (mLoanAndRepaymentSyncIndex != mLoanAccountList.size) {
                        mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let {
                            syncGroupLoanAndLoanRepayment(
                                it
                            )
                        }
                    } else {
                        setLoanAccountSyncStatusTrue()
                        checkAccountsSyncStatusAndSyncGroupAccounts()
                    }
                }
            })

    }

    /**
     * This Method Syncing the Client's Loan and their LoanRepayment. This is the
     * Observable.combineLatest In Which two request is going to server Loans and LoanRepayment
     * and This request will not complete till that both request completed successfully with
     * response (200 OK). If one will fail then response will come in onError. and If both
     * request is 200 response then response will come in onNext.
     *
     * @param loanId Loan Id
     */
    private fun syncClientLoanAndLoanRepayment(loanId: Int) {
        getLoanAndLoanRepayment(loanId)
            .subscribe(object : Subscriber<LoanAndLoanRepayment>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(loanAndLoanRepayment: LoanAndLoanRepayment) {
                    mLoanAndRepaymentSyncIndex += 1
                    if (mLoanAndRepaymentSyncIndex != mLoanAccountList.size) {
                        mLoanAccountList[mLoanAndRepaymentSyncIndex].id?.let {
                            syncClientLoanAndLoanRepayment(
                                it
                            )
                        }
                    } else {
                        setLoanAccountSyncStatusTrue()
                        checkAccountsSyncStatusAndSyncClientAccounts()
                    }
                }
            })

    }

    /**
     * This Method Fetching the Groups SavingsAccount and SavingsAccountTransactionTemplate and Sync
     * them in Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private fun syncGroupSavingsAccountAndTemplate(
        savingsAccountType: String,
        savingsAccountId: Int
    ) {
        getSavingsAccountAndTemplate(savingsAccountType, savingsAccountId)
            .subscribe(object : Subscriber<SavingsAccountAndTransactionTemplate>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(savingsAccountAndTransactionTemplate: SavingsAccountAndTransactionTemplate) {
                    mSavingsAndTransactionSyncIndex += 1
                    if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                        mSavingsAccountList[mSavingsAndTransactionSyncIndex]
                            .depositType?.endpoint?.let {
                                mSavingsAccountList[mSavingsAndTransactionSyncIndex].id?.let { it1 ->
                                    syncGroupSavingsAccountAndTemplate(
                                        it,
                                        it1
                                    )
                                }
                            }
                    } else {
                        mGroups[mGroupSyncIndex].id?.let { loadGroupAssociateClients(it) }
                    }
                }
            })

    }

    /**
     * This Method Fetching the Client SavingsAccount and SavingsAccountTransactionTemplate and Sync
     * them in Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private fun syncClientSavingsAccountAndTemplate(
        savingsAccountType: String,
        savingsAccountId: Int
    ) {
        getSavingsAccountAndTemplate(savingsAccountType, savingsAccountId)
            .subscribe(object : Subscriber<SavingsAccountAndTransactionTemplate>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    onAccountSyncFailed(e)
                }

                override fun onNext(savingsAccountAndTransactionTemplate: SavingsAccountAndTransactionTemplate) {
                    mSavingsAndTransactionSyncIndex += 1
                    if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size) {
                        mSavingsAccountList[mSavingsAndTransactionSyncIndex].id?.let {
                            mSavingsAccountList[mSavingsAndTransactionSyncIndex]
                                .depositType?.endpoint?.let { it1 ->
                                    syncClientSavingsAccountAndTemplate(
                                        it1,
                                        it
                                    )
                                }
                        }
                    } else {
                        syncClient(mClients[mClientSyncIndex])
                    }
                }
            })

    }

    private fun updateTotalSyncProgressBarAndCount() {
        _syncCenterData.update { it.copy(totalSyncCount = mCenterSyncIndex) }
    }

    private fun updateCenterName() {
        val centerName = mCenterList[mCenterSyncIndex].name
        if (centerName != null) {
            _syncCenterData.update { it.copy(centerName = centerName) }
        }
    }

    private fun checkNetworkConnection(
        taskWhenOnline: () -> Unit
    ) {
        if (networkUtilsWrapper.isNetworkConnected()) {
            taskWhenOnline.invoke()
        } else {
            _syncCentersDialogUiState.value = SyncCentersDialogUiState.Error(
                messageResId = R.string.feature_center_error_not_connected_internet,
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

    fun getActiveSavingsAccounts(savingsAccounts: List<SavingsAccount>?): List<SavingsAccount> {
        val accounts: MutableList<SavingsAccount> = ArrayList()
        Observable.from(savingsAccounts)
            .filter { savingsAccount ->
                savingsAccount.status?.active == true &&
                        !savingsAccount.depositType!!.isRecurring
            }
            .subscribe { savingsAccount -> accounts.add(savingsAccount) }
        return accounts
    }

    fun getActiveClients(clients: List<Client>?): List<Client> {
        val accounts: MutableList<Client> = ArrayList()
        Observable.from(clients)
            .filter { client -> client.active }
            .subscribe { client -> accounts.add(client) }
        return accounts
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