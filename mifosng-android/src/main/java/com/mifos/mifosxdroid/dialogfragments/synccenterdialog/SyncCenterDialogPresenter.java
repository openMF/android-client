package com.mifos.mifosxdroid.dialogfragments.synccenterdialog;

import com.mifos.api.datamanager.DataManagerCenter;
import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.CenterAccounts;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.savings.SavingsAccount;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.client.Client;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.GroupWithAssociations;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate;
import com.mifos.objects.zipmodels.LoanAndLoanRepayment;
import com.mifos.objects.zipmodels.SavingsAccountAndTransactionTemplate;
import com.mifos.utils.Constants;
import com.mifos.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by mayankjindal on 10/07/17.
 */

public class SyncCenterDialogPresenter extends BasePresenter<SyncCenterDialogMvpView> {

    private final DataManagerCenter mDataManagerCenter;
    private final DataManagerLoan mDataManagerLoan;
    private final DataManagerSavings mDataManagerSavings;
    private final DataManagerGroups mDataManagerGroups;
    private final DataManagerClient mDataManagerClient;
    private List<LoanAccount> mLoanAccountList;
    private List<SavingsAccount> mSavingsAccountList;
    private List<LoanAccount> mMemberLoanAccountsList;

    private CompositeSubscription mSubscriptions;
    private List<Center> mCenterList, mFailedSyncCenter;
    private List<Group> mGroups;
    private List<Client> mClients;

    private Boolean mLoanAccountSyncStatus = false;
    private Boolean mSavingAccountSyncStatus = false;

    private int mCenterSyncIndex = 0, mLoanAndRepaymentSyncIndex = 0,
            mSavingsAndTransactionSyncIndex = 0, mMemberLoanSyncIndex = 0,
            mClientSyncIndex = 0, mGroupSyncIndex;

    @Inject
    public SyncCenterDialogPresenter(DataManagerCenter dataManagerCenter,
                                    DataManagerLoan dataManagerLoan,
                                     DataManagerSavings dataManagerSavings,
                                     DataManagerGroups dataManagerGroups,
                                     DataManagerClient dataManagerClient) {
        mDataManagerCenter = dataManagerCenter;
        mDataManagerLoan = dataManagerLoan;
        mDataManagerSavings = dataManagerSavings;
        mDataManagerGroups = dataManagerGroups;
        mDataManagerClient = dataManagerClient;
        mSubscriptions = new CompositeSubscription();
        mCenterList = new ArrayList<>();
        mFailedSyncCenter = new ArrayList<>();
        mLoanAccountList = new ArrayList<>();
        mSavingsAccountList = new ArrayList<>();
        mMemberLoanAccountsList = new ArrayList<>();
        mClients = new ArrayList<>();
        mGroups = new ArrayList<>();
    }

    @Override
    public void attachView(SyncCenterDialogMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    /**
     * This Method Start Syncing Centers. Start Syncing the Centers Accounts.
     *
     * @param centers Selected Centers For Syncing
     */
    public void startSyncingCenters(List<Center> centers) {
        mCenterList = centers;
        checkNetworkConnectionAndSyncCenter();
    }

    /**
     * This Method checking that mCenterSyncIndex and mCenterList Size are equal or not. If they
     * are equal, It means all centers have been synced otherwise continue syncing centers.
     */
    public void syncCenterAndUpdateUI() {
        resetIndexes();
        updateTotalSyncProgressBarAndCount();
        if (mCenterSyncIndex != mCenterList.size()) {
            updateCenterName();
            syncCenterAccounts(mCenterList.get(mCenterSyncIndex).getId());
        } else {
            getMvpView().showCentersSyncSuccessfully();
        }

    }

    /**
     * This Method for resetting the mLoanAndRepaymentSyncIndex and mSavingsAndTransactionSyncIndex
     * and  mMemberLoanSyncIndex and mLoanAccountSyncStatus and mSavingAccountSyncStatus.
     */
    private void resetIndexes() {
        mLoanAccountSyncStatus = false;
        mSavingAccountSyncStatus = false;
        mLoanAndRepaymentSyncIndex = 0;
        mSavingsAndTransactionSyncIndex = 0;
        mMemberLoanSyncIndex = 0;
    }

    /**
     * This Method checking network connection before starting center synchronization
     */
    public void checkNetworkConnectionAndSyncCenter() {
        if (getMvpView().isOnline()) {
            syncCenterAndUpdateUI();
        } else {
            getMvpView().showNetworkIsNotAvailable();
            getMvpView().dismissDialog();
        }
    }

    /**
     * This Method will be called when ever any request will be failed synced.
     *
     * @param e Throwable
     */
    private void onAccountSyncFailed(Throwable e) {
        try {
            if (e instanceof HttpException) {
                int singleSyncCenterMax = getMvpView().getMaxSingleSyncCenterProgressBar();
                getMvpView().updateSingleSyncCenterProgressBar(singleSyncCenterMax);

                mFailedSyncCenter.add(mCenterList.get(mCenterSyncIndex));
                mCenterSyncIndex = mCenterSyncIndex + 1;

                getMvpView().showSyncedFailedCenters(mFailedSyncCenter.size());
                checkNetworkConnectionAndSyncCenter();
            }
        } catch (Throwable throwable) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(throwable);
        }
    }

    /**
     * Sync the Center Account with Center Id. This method fetching the Center Accounts from the
     * REST API using retrofit 2 and saving these accounts to Database with DatabaseHelperCenter
     * and then DataManagerCenter returns the Center Accounts to Presenter.
     * <p>
     * <p>
     * onNext : As Center Accounts Successfully sync then sync there Loan and LoanRepayment
     * onError :
     *
     * @param centerId Center Id
     */
    public void syncCenterAccounts(int centerId) {
        checkViewAttached();
        mSubscriptions.add(mDataManagerCenter.syncCenterAccounts(centerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CenterAccounts>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError(R.string.failed_to_sync_centeraccounts);

                        //Updating UI
                        mFailedSyncCenter.add(mCenterList.get(mCenterSyncIndex));
                        getMvpView().showSyncedFailedCenters(mFailedSyncCenter.size());
                        mCenterSyncIndex = mCenterSyncIndex + 1;
                        checkNetworkConnectionAndSyncCenter();
                    }

                    @Override
                    public void onNext(CenterAccounts centerAccounts) {
                        mLoanAccountList = Utils.getActiveLoanAccounts(centerAccounts
                                .getLoanAccounts());
                        mSavingsAccountList = Utils.getActiveSavingsAccounts(centerAccounts
                                .getSavingsAccounts());
                        mMemberLoanAccountsList = Utils.getActiveLoanAccounts(centerAccounts
                                .getMemberLoanAccounts());
                        //Updating UI
                        getMvpView().setMaxSingleSyncCenterProgressBar(mLoanAccountList.size() +
                                mSavingsAccountList.size() + mMemberLoanAccountsList.size());
                        checkAccountsSyncStatusAndSyncAccounts();
                    }
                })
        );
    }

    /**
     * This Method check the LoanAccount isEmpty or not, If LoanAccount is not Empty the sync the
     * loanAccount locally and If LoanAccount is empty then check to the next SavingAccount
     * isEmpty or not. if SavingAccount is not empty then sync the SavingsAccount locally and if
     * savingsAccount are Empty then check to the next MemberLoanAccount isEmpty or not. if
     * MemberLoanAccount is not empty then sync the MemberLoanAccount locally and if
     * MemberLoanAccount are Empty the sync the Centers locally, Yep Centers has been synced.
     */
    private void checkAccountsSyncStatusAndSyncAccounts() {
        if (!mLoanAccountList.isEmpty() && !mLoanAccountSyncStatus) {
            //Sync the Active Loan and LoanRepayment
            checkNetworkConnectionAndSyncLoanAndLoanRepayment();
        } else if (!mSavingsAccountList.isEmpty() && !mSavingAccountSyncStatus) {
            //Sync the Active Savings Account
            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate();
        } else if (!mMemberLoanAccountsList.isEmpty()) {
            //Sync the Active member Loan Account
            checkNetworkConnectionAndSyncMemberLoanAndMemberLoanRepayment();
        } else {
            getMvpView().setMaxSingleSyncCenterProgressBar(1);
            loadCenterAssociateGroups(mCenterList.get(mCenterSyncIndex).getId());
        }
    }

    /**
     * This Method Checking network connection and  Syncing the LoanAndLoanRepayment.
     * If found no internet connection then stop syncing the Centers and dismiss the dialog.
     */
    private void checkNetworkConnectionAndSyncLoanAndLoanRepayment() {
        if (getMvpView().isOnline()) {
            syncLoanAndLoanRepayment(mLoanAccountList
                    .get(mLoanAndRepaymentSyncIndex).getId());
        } else {
            getMvpView().showNetworkIsNotAvailable();
            getMvpView().dismissDialog();
        }
    }

    /**
     * This Method Checking network connection and  Syncing the MemberLoanAndMemberLoanRepayment.
     * If found no internet connection then stop syncing the Centers and dismiss the dialog.
     */
    private void checkNetworkConnectionAndSyncMemberLoanAndMemberLoanRepayment() {
        if (getMvpView().isOnline()) {
            syncMemberLoanAndMemberLoanRepayment(mMemberLoanAccountsList
                    .get(mMemberLoanSyncIndex).getId());
        } else {
            getMvpView().showNetworkIsNotAvailable();
            getMvpView().dismissDialog();
        }
    }

    /**
     * This method checking the network connection and syncing the SavingsAccountAndTransactions.
     * If found no internet connection then stop syncing the centers and dismiss the dialog.
     */
    private void checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate() {
        if (getMvpView().isOnline()) {
            syncSavingsAccountAndTemplate(mSavingsAccountList
                            .get(mSavingsAndTransactionSyncIndex).getDepositType().getEndpoint(),
                    mSavingsAccountList.get(mSavingsAndTransactionSyncIndex).getId());
        } else {
            getMvpView().showNetworkIsNotAvailable();
            getMvpView().dismissDialog();
        }
    }

    /**
     * This Method for setting mLoanAccountSyncStatus = true, It means LoanAccounts have been
     * synced locally.
     */
    private void setLoanAccountSyncStatusTrue() {
        if (mLoanAndRepaymentSyncIndex == mLoanAccountList.size()) {
            mLoanAccountSyncStatus = true;
        }
    }

    /**
     * This Method for setting mSavingAccountSyncStatus = true, It means LoanAccounts have been
     * synced locally.
     */
    private void setSavingnAccountSyncStatusTrue() {
        if (mSavingsAndTransactionSyncIndex == mSavingsAccountList.size()) {
            mSavingAccountSyncStatus = true;
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
    private void syncLoanAndLoanRepayment(int loanId) {
        checkViewAttached();
        mSubscriptions.add(getLoanAndLoanRepayment(loanId)
                .subscribe(new Subscriber<LoanAndLoanRepayment>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        onAccountSyncFailed(e);
                    }

                    @Override
                    public void onNext(LoanAndLoanRepayment loanAndLoanRepayment) {
                        mLoanAndRepaymentSyncIndex = mLoanAndRepaymentSyncIndex + 1;
                        getMvpView().updateSingleSyncCenterProgressBar(mLoanAndRepaymentSyncIndex);
                        if (mLoanAndRepaymentSyncIndex != mLoanAccountList.size()) {
                            checkNetworkConnectionAndSyncLoanAndLoanRepayment();
                        } else {
                            setLoanAccountSyncStatusTrue();
                            checkAccountsSyncStatusAndSyncAccounts();
                        }
                    }
                })
        );
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
    private void syncMemberLoanAndMemberLoanRepayment(int loanId) {
        checkViewAttached();
        mSubscriptions.add(getLoanAndLoanRepayment(loanId)
                .subscribe(new Subscriber<LoanAndLoanRepayment>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        onAccountSyncFailed(e);
                    }

                    @Override
                    public void onNext(LoanAndLoanRepayment loanAndLoanRepayment) {
                        mMemberLoanSyncIndex = mMemberLoanSyncIndex + 1;
                        getMvpView().updateSingleSyncCenterProgressBar(mLoanAndRepaymentSyncIndex
                                + mSavingsAndTransactionSyncIndex + mMemberLoanSyncIndex);
                        if (mMemberLoanSyncIndex != mMemberLoanAccountsList.size()) {
                            checkNetworkConnectionAndSyncMemberLoanAndMemberLoanRepayment();
                        } else {
                            loadCenterAssociateGroups(mCenterList.get(mCenterSyncIndex).getId());
                        }
                    }
                })
        );
    }


    /**
     * This Method Fetching the  SavingsAccount and SavingsAccountTransactionTemplate and Syncing
     * them in Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private void syncSavingsAccountAndTemplate(String savingsAccountType, int savingsAccountId) {
        checkViewAttached();
        mSubscriptions.add(getSavingsAccountAndTemplate(savingsAccountType, savingsAccountId)
                .subscribe(new Subscriber<SavingsAccountAndTransactionTemplate>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        onAccountSyncFailed(e);
                    }

                    @Override
                    public void onNext(SavingsAccountAndTransactionTemplate
                                               savingsAccountAndTransactionTemplate) {
                        mSavingsAndTransactionSyncIndex = mSavingsAndTransactionSyncIndex + 1;
                        getMvpView().updateSingleSyncCenterProgressBar(
                                mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex);

                        if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size()) {
                            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate();
                        } else {
                            setSavingnAccountSyncStatusTrue();
                            checkAccountsSyncStatusAndSyncAccounts();
                        }
                    }
                })
        );
    }

    /**
     * This Method syncing the Center into the Database.
     *
     * @param center Center
     */
    private void syncCenter(Center center) {
        checkViewAttached();
        center.setSync(true);
        mSubscriptions.add(mDataManagerCenter.syncCenterInDatabase(center)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Center>() {
                    @Override
                    public void call(Center center) {
                        getMvpView().updateGroupSyncProgressBar(0);
                        getMvpView().updateSingleSyncCenterProgressBar(
                                getMvpView().getMaxSingleSyncCenterProgressBar());
                        mCenterSyncIndex = mCenterSyncIndex + 1;
                        checkNetworkConnectionAndSyncCenter();
                    }
                })
        );
    }


    /**
     * This Method Fetching the LoanAndLoanRepayment
     *
     * @param loanId Loan Id
     * @return LoanAndLoanRepayment
     */
    private Observable<LoanAndLoanRepayment> getLoanAndLoanRepayment(int loanId) {
        return Observable.combineLatest(
                mDataManagerLoan.syncLoanById(loanId),
                mDataManagerLoan.syncLoanRepaymentTemplate(loanId),
                new Func2<LoanWithAssociations, LoanRepaymentTemplate, LoanAndLoanRepayment>() {
                    @Override
                    public LoanAndLoanRepayment call(LoanWithAssociations loanWithAssociations,
                                                     LoanRepaymentTemplate loanRepaymentTemplate) {
                        return new LoanAndLoanRepayment(loanWithAssociations,
                                loanRepaymentTemplate);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    /**
     * This method fetching SavingsAccountAndTemplate.
     *
     * @param savingsAccountType
     * @param savingsAccountId
     * @return SavingsAccountAndTransactionTemplate
     */
    private Observable<SavingsAccountAndTransactionTemplate> getSavingsAccountAndTemplate(
            String savingsAccountType, int savingsAccountId) {
        return Observable.combineLatest(
                mDataManagerSavings.syncSavingsAccount(savingsAccountType, savingsAccountId,
                        Constants.TRANSACTIONS),
                mDataManagerSavings.syncSavingsAccountTransactionTemplate(savingsAccountType,
                        savingsAccountId, Constants.SAVINGS_ACCOUNT_TRANSACTION_DEPOSIT),
                new Func2<SavingsAccountWithAssociations, SavingsAccountTransactionTemplate,
                        SavingsAccountAndTransactionTemplate>() {
                    @Override
                    public SavingsAccountAndTransactionTemplate call(
                            SavingsAccountWithAssociations savingsAccountWithAssociations,
                            SavingsAccountTransactionTemplate savingsAccountTransactionTemplate) {
                        return new SavingsAccountAndTransactionTemplate(
                                savingsAccountWithAssociations, savingsAccountTransactionTemplate);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    /**
     * This Method fetching the Center Associate Groups List.
     *
     * @param centerId Center Id
     */
    private void loadCenterAssociateGroups(int centerId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerCenter.getCenterWithAssociations(centerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CenterWithAssociations>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        onAccountSyncFailed(e);
                    }

                    @Override
                    public void onNext(CenterWithAssociations centerWithAssociations) {
                        mGroups = centerWithAssociations.getGroupMembers();
                        mGroupSyncIndex = 0;
                        resetIndexes();
                        if (mGroups.size() != 0) {
                            getMvpView().setGroupSyncProgressBarMax(mGroups.size());
                            syncGroupAccounts(mGroups.get(mGroupSyncIndex).getId());
                        } else {
                            syncCenter(mCenterList.get(mCenterSyncIndex));
                        }
                    }
                })
        );
    }

    /**
     * This Method fetching the Group Associate Clients List.
     *
     * @param groupId Group Id
     */
    private void loadGroupAssociateClients(int groupId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerGroups.getGroupWithAssociations(groupId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GroupWithAssociations>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        onAccountSyncFailed(e);
                    }

                    @Override
                    public void onNext(GroupWithAssociations groupWithAssociations) {
                        mClients = Utils.getActiveClients(groupWithAssociations.getClientMembers());
                        mClientSyncIndex = 0;
                        resetIndexes();
                        if (mClients.size() != 0) {
                            getMvpView().setClientSyncProgressBarMax(mClients.size());
                            syncClientAccounts(mClients.get(mClientSyncIndex).getId());
                        } else {
                            syncGroup(mGroups.get(mGroupSyncIndex));
                        }
                    }
                })
        );
    }

    /**
     * Sync the Group Account with Group Id. This method fetching the Group Accounts from the
     * REST API using retrofit 2 and saving these accounts to Database with DatabaseHelperGroup
     * and then DataManagerGroup gives the returns the Group Accounts to Presenter.
     * <p/>
     * <p/>
     * onNext : As Group Accounts Successfully sync then now sync the there Loan and LoanRepayment
     * onError :
     *
     * @param groupId Group Id
     */
    private void syncGroupAccounts(int groupId) {
        checkViewAttached();
        mSubscriptions.add(mDataManagerGroups.syncGroupAccounts(groupId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GroupAccounts>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        onAccountSyncFailed(e);
                    }

                    @Override
                    public void onNext(GroupAccounts groupAccounts) {
                        mLoanAccountList = Utils.getActiveLoanAccounts(groupAccounts
                                .getLoanAccounts());
                        mSavingsAccountList = Utils.getActiveSavingsAccounts(groupAccounts
                                .getSavingsAccounts());

                        checkAccountsSyncStatusAndSyncGroupAccounts();
                    }
                })
        );
    }

    /**
     * Sync the Client Account with Client Id. This method fetching the Client Accounts from the
     * REST API using retrofit 2 and saving these accounts to Database with DatabaseHelperClient
     * and then DataManagerClient gives the returns the Clients Accounts to Presenter.
     * <p/>
     * <p/>
     * onNext : As Client Accounts Successfully sync then now sync the there Loan and LoanRepayment
     * onError :
     *
     * @param clientId Client Id
     */
    private void syncClientAccounts(int clientId) {
        checkViewAttached();
        mSubscriptions.add(mDataManagerClient.syncClientAccounts(clientId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ClientAccounts>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        onAccountSyncFailed(e);
                    }

                    @Override
                    public void onNext(ClientAccounts clientAccounts) {
                        mLoanAccountList = Utils.getActiveLoanAccounts(clientAccounts
                                .getLoanAccounts());
                        mSavingsAccountList = Utils.getSyncableSavingsAccounts(clientAccounts
                                .getSavingsAccounts());

                        checkAccountsSyncStatusAndSyncClientAccounts();
                    }
                })
        );
    }

    /**
     * This Method check the Group LoanAccount isEmpty or not, If LoanAccount is not Empty the sync
     * the loanAccount locally and If LoanAccount is empty then check to the next SavingAccount
     * isEmpty or not. if SavingAccount is not empty then sync the SavingsAccount locally and if
     * savingsAccount are Empty the sync the groups locally, Yep Groups has been synced.
     */
    private void checkAccountsSyncStatusAndSyncGroupAccounts() {
        if (!mLoanAccountList.isEmpty() && !mLoanAccountSyncStatus) {
            //Sync the Active Loan and LoanRepayment
            syncGroupLoanAndLoanRepayment(
                    mLoanAccountList.get(mLoanAndRepaymentSyncIndex).getId());
        } else if (!mSavingsAccountList.isEmpty()) {
            //Sync the Active Savings Account
            syncGroupSavingsAccountAndTemplate(mSavingsAccountList
                            .get(mSavingsAndTransactionSyncIndex).getDepositType().getEndpoint(),
                    mSavingsAccountList.get(mSavingsAndTransactionSyncIndex).getId());
        } else {
            loadGroupAssociateClients(mGroups.get(mGroupSyncIndex).getId());
        }
    }

    /**
     * This Method check the Client LoanAccount isEmpty or not, If LoanAccount is not Empty the sync
     * the loanAccount locally and If LoanAccount is empty then check to the next SavingAccount
     * isEmpty or not. if SavingAccount is not empty then sync the SavingsAccount locally and if
     * savingsAccount are Empty the sync the groups locally, Yep Groups has been synced.
     */
    private void checkAccountsSyncStatusAndSyncClientAccounts() {
        if (!mLoanAccountList.isEmpty() && !mLoanAccountSyncStatus) {
            //Sync the Active Loan and LoanRepayment
            syncClientLoanAndLoanRepayment(
                    mLoanAccountList.get(mLoanAndRepaymentSyncIndex).getId());
        } else if (!mSavingsAccountList.isEmpty()) {
            //Sync the Active Savings Account
            syncClientSavingsAccountAndTemplate(mSavingsAccountList
                            .get(mSavingsAndTransactionSyncIndex).getDepositType().getEndpoint(),
                    mSavingsAccountList.get(mSavingsAndTransactionSyncIndex).getId());
        } else {
            syncClient(mClients.get(mClientSyncIndex));
        }
    }

    /**
     * This Method Saving the Groups to Database, If their Accounts, Loan and LoanRepayment
     * saved Synced successfully.
     *
     * @param group
     */
    private void syncGroup(Group group) {
        checkViewAttached();
        group.setCenterId(mCenterList.get(mCenterSyncIndex).getId());
        group.setSync(true);
        mSubscriptions.add(mDataManagerGroups.syncGroupInDatabase(group)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Group>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError(R.string.failed_to_sync_client);
                    }

                    @Override
                    public void onNext(Group Group) {
                        resetIndexes();
                        getMvpView().updateClientSyncProgressBar(0);
                        mGroupSyncIndex = mGroupSyncIndex + 1;
                        getMvpView().updateGroupSyncProgressBar(mGroupSyncIndex);
                        if (mGroups.size() == mGroupSyncIndex) {
                            syncCenter(mCenterList.get(mCenterSyncIndex));
                        } else {
                            syncGroupAccounts(mGroups.get(mGroupSyncIndex).getId());
                        }

                    }
                })
        );
    }

    /**
     * This Method Saving the Clients to Database, If their Accounts, Loan and LoanRepayment
     * saved Synced successfully.
     *
     * @param client
     */
    private void syncClient(Client client) {
        checkViewAttached();
        client.setGroupId(mGroups.get(mGroupSyncIndex).getId());
        client.setSync(true);
        mSubscriptions.add(mDataManagerClient.syncClientInDatabase(client)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Client>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showError(R.string.failed_to_sync_client);
                    }

                    @Override
                    public void onNext(Client client) {
                        resetIndexes();
                        mClientSyncIndex = mClientSyncIndex + 1;
                        getMvpView().updateClientSyncProgressBar(mClientSyncIndex);
                        if (mClients.size() == mClientSyncIndex) {
                            syncGroup(mGroups.get(mGroupSyncIndex));
                        } else {
                            syncClientAccounts(mClients.get(mClientSyncIndex).getId());
                        }

                    }
                })
        );
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
    private void syncGroupLoanAndLoanRepayment(int loanId) {
        checkViewAttached();
        mSubscriptions.add(getLoanAndLoanRepayment(loanId)
                .subscribe(new Subscriber<LoanAndLoanRepayment>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        onAccountSyncFailed(e);
                    }

                    @Override
                    public void onNext(LoanAndLoanRepayment loanAndLoanRepayment) {
                        mLoanAndRepaymentSyncIndex = mLoanAndRepaymentSyncIndex + 1;
                        if (mLoanAndRepaymentSyncIndex != mLoanAccountList.size()) {
                            syncGroupLoanAndLoanRepayment(
                                    mLoanAccountList.get(mLoanAndRepaymentSyncIndex).getId());
                        } else {
                            setLoanAccountSyncStatusTrue();
                            checkAccountsSyncStatusAndSyncGroupAccounts();
                        }
                    }
                })
        );
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
    private void syncClientLoanAndLoanRepayment(int loanId) {
        checkViewAttached();
        mSubscriptions.add(getLoanAndLoanRepayment(loanId)
                .subscribe(new Subscriber<LoanAndLoanRepayment>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        onAccountSyncFailed(e);
                    }

                    @Override
                    public void onNext(LoanAndLoanRepayment loanAndLoanRepayment) {
                        mLoanAndRepaymentSyncIndex = mLoanAndRepaymentSyncIndex + 1;
                        if (mLoanAndRepaymentSyncIndex != mLoanAccountList.size()) {
                            syncClientLoanAndLoanRepayment(
                                    mLoanAccountList.get(mLoanAndRepaymentSyncIndex).getId());
                        } else {
                            setLoanAccountSyncStatusTrue();
                            checkAccountsSyncStatusAndSyncClientAccounts();
                        }
                    }
                })
        );
    }

    /**
     * This Method Fetching the Groups SavingsAccount and SavingsAccountTransactionTemplate and Sync
     * them in Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private void syncGroupSavingsAccountAndTemplate(String savingsAccountType,
                                                     int savingsAccountId) {
        checkViewAttached();
        mSubscriptions.add(getSavingsAccountAndTemplate(savingsAccountType, savingsAccountId)
                .subscribe(new Subscriber<SavingsAccountAndTransactionTemplate>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        onAccountSyncFailed(e);
                    }

                    @Override
                    public void onNext(SavingsAccountAndTransactionTemplate
                                               savingsAccountAndTransactionTemplate) {
                        mSavingsAndTransactionSyncIndex = mSavingsAndTransactionSyncIndex + 1;
                        if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size()) {
                            syncGroupSavingsAccountAndTemplate(
                                    mSavingsAccountList
                                            .get(mSavingsAndTransactionSyncIndex)
                                            .getDepositType().getEndpoint(),
                                    mSavingsAccountList
                                            .get(mSavingsAndTransactionSyncIndex).getId());
                        } else {
                            loadGroupAssociateClients(mGroups.get(mGroupSyncIndex).getId());
                        }
                    }
                })
        );
    }

    /**
     * This Method Fetching the Client SavingsAccount and SavingsAccountTransactionTemplate and Sync
     * them in Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    private void syncClientSavingsAccountAndTemplate(String savingsAccountType,
                                                     int savingsAccountId) {
        checkViewAttached();
        mSubscriptions.add(getSavingsAccountAndTemplate(savingsAccountType, savingsAccountId)
                .subscribe(new Subscriber<SavingsAccountAndTransactionTemplate>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        onAccountSyncFailed(e);
                    }

                    @Override
                    public void onNext(SavingsAccountAndTransactionTemplate
                                               savingsAccountAndTransactionTemplate) {
                        mSavingsAndTransactionSyncIndex = mSavingsAndTransactionSyncIndex + 1;
                        if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size()) {
                            syncClientSavingsAccountAndTemplate(
                                    mSavingsAccountList
                                            .get(mSavingsAndTransactionSyncIndex)
                                            .getDepositType().getEndpoint(),
                                    mSavingsAccountList
                                            .get(mSavingsAndTransactionSyncIndex).getId());
                        } else {
                            syncClient(mClients.get(mClientSyncIndex));
                        }
                    }
                })
        );
    }

    public void updateTotalSyncProgressBarAndCount() {
        getMvpView().updateTotalSyncCenterProgressBarAndCount(mCenterSyncIndex);
    }

    public void updateCenterName() {
        String centerName = mCenterList.get(mCenterSyncIndex).getName();
        getMvpView().showSyncingCenter(centerName);
    }
}
