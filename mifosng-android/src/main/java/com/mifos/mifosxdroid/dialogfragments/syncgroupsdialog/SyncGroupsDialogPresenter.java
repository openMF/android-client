package com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.savings.SavingsAccount;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.client.Client;
import com.mifos.objects.group.Group;
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
 * Created by Rajan Maurya on 11/09/16.
 */
public class SyncGroupsDialogPresenter extends BasePresenter<SyncGroupsDialogMvpView> {

    private final DataManagerGroups mDataManagerGroups;
    private final DataManagerLoan mDataManagerLoan;
    private final DataManagerSavings mDataManagerSavings;
    private final DataManagerClient mDataManagerClient;

    private CompositeSubscription mSubscriptions;

    private List<Group> mGroupList, mFailedSyncGroup;
    private List<Client> mClients;
    private List<LoanAccount> mLoanAccountList;
    private List<SavingsAccount> mSavingsAccountList;

    private Boolean mLoanAccountSyncStatus = false;

    private int mGroupSyncIndex = 0, mClientSyncIndex = 0, mLoanAndRepaymentSyncIndex = 0,
            mSavingsAndTransactionSyncIndex = 0;

    @Inject
    public SyncGroupsDialogPresenter(DataManagerGroups dataManagerGroups,
                                     DataManagerLoan dataManagerLoan,
                                     DataManagerSavings dataManagerSavings,
                                     DataManagerClient dataManagerClient) {
        mDataManagerGroups = dataManagerGroups;
        mDataManagerLoan = dataManagerLoan;
        mDataManagerSavings = dataManagerSavings;
        mDataManagerClient = dataManagerClient;
        mSubscriptions = new CompositeSubscription();
        mGroupList = new ArrayList<>();
        mFailedSyncGroup = new ArrayList<>();
        mLoanAccountList = new ArrayList<>();
        mSavingsAccountList = new ArrayList<>();
        mClients = new ArrayList<>();
    }

    @Override
    public void attachView(SyncGroupsDialogMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.clear();
    }

    /**
     * This Method Start Syncing Groups. Start Syncing the Groups Accounts.
     *
     * @param groups Selected Groups For Syncing
     */
    public void startSyncingGroups(List<Group> groups) {
        mGroupList = groups;
        checkNetworkConnectionAndSyncGroup();
    }

    /**
     * This Method checking that mGroupSyncIndex and mGroupList Size are equal or not. If they
     * are equal, It means all groups have been synced otherwise continue syncing groups.
     */
    private void syncGroupAndUpdateUI() {
        resetIndexes();
        updateTotalSyncProgressBarAndCount();
        if (mGroupSyncIndex != mGroupList.size()) {
            updateGroupName();
            syncGroupAccounts(mGroupList.get(mGroupSyncIndex).getId());
        } else {
            getMvpView().showGroupsSyncSuccessfully();
        }

    }

    /**
     * This Method checking network connection before starting group synchronization
     */
    private void checkNetworkConnectionAndSyncGroup() {
        if (getMvpView().isOnline()) {
            syncGroupAndUpdateUI();
        } else {
            getMvpView().showNetworkIsNotAvailable();
            getMvpView().dismissDialog();
        }
    }

    /**
     * This Method Checking network connection and  Syncing the LoanAndLoanRepayment.
     * If found no internet connection then stop syncing the groups and dismiss the dialog.
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
     * This method checking the network connection and syncing the SavingsAccountAndTransactions.
     * If found no internet connection then stop syncing the groups and dismiss the dialog.
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
     * This Method check the LoanAccount isEmpty or not, If LoanAccount is not Empty the sync the
     * loanAccount locally and If LoanAccount is empty then check to the next SavingAccount
     * isEmpty or not. if SavingAccount is not empty then sync the SavingsAccount locally and if
     * savingsAccount are Empty the sync the groups locally, Yep Groups has been synced.
     */
    private void checkAccountsSyncStatusAndSyncAccounts() {
        if (!mLoanAccountList.isEmpty() && !mLoanAccountSyncStatus) {
            //Sync the Active Loan and LoanRepayment
            checkNetworkConnectionAndSyncLoanAndLoanRepayment();
        } else if (!mSavingsAccountList.isEmpty()) {
            //Sync the Active Savings Account
            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate();
        } else {
            // If LoanAccounts and SavingsAccount are null then sync Client to Database
            getMvpView().setMaxSingleSyncGroupProgressBar(1);
            loadGroupAssociateClients(mGroupList.get(mGroupSyncIndex).getId());
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
     * This Method for resetting the mLoanAndRepaymentSyncIndex and mSavingsAndTransactionSyncIndex
     * and mLoanAccountSyncStatus.
     */
    private void resetIndexes() {
        mLoanAccountSyncStatus = false;
        mLoanAndRepaymentSyncIndex = 0;
        mSavingsAndTransactionSyncIndex = 0;
    }

    /**
     * This Method will be called when ever any request will be failed synced.
     *
     * @param e Throwable
     */
    private void onAccountSyncFailed(Throwable e) {
        try {
            if (e instanceof HttpException) {
                int singleSyncGroupMax = getMvpView().getMaxSingleSyncGroupProgressBar();
                getMvpView().updateSingleSyncGroupProgressBar(singleSyncGroupMax);

                mFailedSyncGroup.add(mGroupList.get(mGroupSyncIndex));
                mGroupSyncIndex = mGroupSyncIndex + 1;

                getMvpView().showSyncedFailedGroups(mFailedSyncGroup.size());
                checkNetworkConnectionAndSyncGroup();
            }
        } catch (Throwable throwable) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(throwable);
        }
    }

    /**
     * Sync the Group Account with Group Id. This method fetching the Group Accounts from the
     * REST API using retrofit 2 and saving these accounts to Database with DatabaseHelperClient
     * and then DataManagerClient returns the Group Accounts to Presenter.
     * <p>
     * <p>
     * onNext : As Group Accounts Successfully sync then sync there Loan and LoanRepayment
     * onError :
     *
     * @param groupId Client Id
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
                        mLoanAccountList = Utils.getActiveLoanAccounts(
                                groupAccounts.getLoanAccounts());
                        mSavingsAccountList = Utils.getActiveSavingsAccounts(
                                groupAccounts.getSavingsAccounts());

                        //Updating UI
                        getMvpView().setMaxSingleSyncGroupProgressBar(mLoanAccountList.size() +
                                mSavingsAccountList.size());

                        checkAccountsSyncStatusAndSyncAccounts();
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
                        getMvpView().updateSingleSyncGroupProgressBar(mLoanAndRepaymentSyncIndex);
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
                        getMvpView().updateSingleSyncGroupProgressBar(
                                mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex);

                        if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size()) {
                            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate();
                        } else {
                            loadGroupAssociateClients(mGroupList.get(mGroupSyncIndex).getId());
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
                        mClients = groupWithAssociations.getClientMembers();
                        mClientSyncIndex = 0;
                        resetIndexes();
                        if (mClients.size() != 0) {
                            getMvpView().setClientSyncProgressBarMax(mClients.size());
                            syncClientAccounts(mClients.get(mClientSyncIndex).getId());
                        } else {
                            syncGroup(mGroupList.get(mGroupSyncIndex));
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
        client.setGroupId(mGroupList.get(mGroupSyncIndex).getId());
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
                            syncGroup(mGroupList.get(mGroupSyncIndex));
                        } else {
                            syncClientAccounts(mClients.get(mClientSyncIndex).getId());
                        }

                    }
                })
        );
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


    /**
     * This Method syncing the Group into the Database.
     *
     * @param group Group
     */
    private void syncGroup(Group group) {
        checkViewAttached();
        group.setSync(true);
        mSubscriptions.add(mDataManagerGroups.syncGroupInDatabase(group)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Group>() {
                    @Override
                    public void call(Group group) {
                        getMvpView().updateClientSyncProgressBar(0);
                        int singleSyncClientMax = getMvpView().getMaxSingleSyncGroupProgressBar();
                        getMvpView().updateSingleSyncGroupProgressBar(singleSyncClientMax);
                        mGroupSyncIndex = mGroupSyncIndex + 1;
                        checkNetworkConnectionAndSyncGroup();
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

    private void updateTotalSyncProgressBarAndCount() {
        getMvpView().updateTotalSyncGroupProgressBarAndCount(mGroupSyncIndex);
    }

    private void updateGroupName() {
        String groupName = mGroupList.get(mGroupSyncIndex).getName();
        getMvpView().showSyncingGroup(groupName);
    }
}
