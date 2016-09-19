package com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog;

import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.savings.SavingsAccount;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.group.Group;
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

    private CompositeSubscription mSubscriptions;

    private List<Group> mGroupList, mFailedSyncGroup;
    private List<LoanAccount> mLoanAccountList;
    private List<SavingsAccount> mSavingsAccountList;

    private Boolean mLoanAccountSyncStatus = false;

    private int mGroupSyncIndex, mLoanAndRepaymentSyncIndex = 0,
            mSavingsAndTransactionSyncIndex = 0;

    @Inject
    public SyncGroupsDialogPresenter(DataManagerGroups dataManagerGroups,
                                     DataManagerLoan dataManagerLoan,
                                     DataManagerSavings dataManagerSavings) {
        mDataManagerGroups = dataManagerGroups;
        mDataManagerLoan = dataManagerLoan;
        mDataManagerSavings = dataManagerSavings;
        mSubscriptions = new CompositeSubscription();
        mGroupList = new ArrayList<>();
        mFailedSyncGroup = new ArrayList<>();
        mLoanAccountList = new ArrayList<>();
        mSavingsAccountList = new ArrayList<>();
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
    public void startSyncingClients(List<Group> groups) {
        mGroupList = groups;
        checkNetworkConnectionAndSyncGroup();
    }

    public void syncClientAndUpdateUI() {
        mLoanAndRepaymentSyncIndex = 0;
        mSavingsAndTransactionSyncIndex = 0;
        updateTotalSyncProgressBarAndCount();
        if (mGroupSyncIndex != mGroupList.size()) {
            updateClientName();
            syncGroupAccounts(mGroupList.get(mGroupSyncIndex).getId());
        } else {
            getMvpView().showGroupsSyncSuccessfully();
        }

    }

    public void checkNetworkConnectionAndSyncGroup() {
        if (getMvpView().isOnline()) {
            syncClientAndUpdateUI();
        } else {
            getMvpView().showNetworkIsNotAvailable();
            getMvpView().dismissDialog();
        }
    }

    public void checkNetworkConnectionAndSyncLoanAndLoanRepayment() {
        if (getMvpView().isOnline()) {
            syncLoanAndLoanRepayment(mLoanAccountList
                    .get(mLoanAndRepaymentSyncIndex).getId());
        } else {
            getMvpView().showNetworkIsNotAvailable();
            getMvpView().dismissDialog();
        }
    }

    public void checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate() {
        if (getMvpView().isOnline()) {
            syncSavingsAccountAndTemplate(mSavingsAccountList
                            .get(mSavingsAndTransactionSyncIndex).getDepositType().getEndpoint(),
                    mSavingsAccountList.get(mSavingsAndTransactionSyncIndex).getId());
        } else {
            getMvpView().showNetworkIsNotAvailable();
            getMvpView().dismissDialog();
        }
    }

    public void checkAccountsSyncStatusAndSyncAccounts() {
        if (!mLoanAccountList.isEmpty() && !mLoanAccountSyncStatus) {
            //Sync the Active Loan and LoanRepayment
            checkNetworkConnectionAndSyncLoanAndLoanRepayment();
        } else if (!mSavingsAccountList.isEmpty()) {
            //Sync the Active Savings Account
            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate();
        } else {
            // If LoanAccounts and SavingsAccount are null then sync Client to Database
            getMvpView().setMaxSingleSyncGroupProgressBar(1);
            syncGroup(mGroupList.get(mGroupSyncIndex));
        }
    }

    public void setLoanAccountSyncStatusTrue() {
        if (mLoanAndRepaymentSyncIndex == mLoanAccountList.size()) {
            mLoanAccountSyncStatus = true;
        }
    }

    public void onAccountSyncFailed(Throwable e) {
        try {
            if (e instanceof HttpException) {
                int singleSyncClientMax = getMvpView().getMaxSingleSyncGroupProgressBar();
                getMvpView().updateSingleSyncGroupProgressBar(singleSyncClientMax);

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
    public void syncGroupAccounts(int groupId) {
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
                        getMvpView().showError(R.string.failed_to_sync_clientaccounts);

                        //Updating UI
                        mFailedSyncGroup.add(mGroupList.get(mGroupSyncIndex));
                        getMvpView().showSyncedFailedGroups(mFailedSyncGroup.size());
                        mGroupSyncIndex = mGroupSyncIndex + 1;
                        checkNetworkConnectionAndSyncGroup();
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
    public void syncLoanAndLoanRepayment(int loanId) {
        checkViewAttached();
        mSubscriptions.add(Observable.combineLatest(
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
                .subscribeOn(Schedulers.io())
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
     * This Method Fetching the  SavingsAccount and SavingsAccountTransactionTemplate and Sync
     * them in Database table.
     *
     * @param savingsAccountType SavingsAccount Type Example : savingsaccounts
     * @param savingsAccountId   SavingsAccount Id
     */
    public void syncSavingsAccountAndTemplate(String savingsAccountType, int savingsAccountId) {
        checkViewAttached();
        mSubscriptions.add(Observable.combineLatest(
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
                .subscribeOn(Schedulers.io())
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
                            syncGroup(mGroupList.get(mGroupSyncIndex));
                        }
                    }
                })
        );
    }

    public void syncGroup(Group group) {
        checkViewAttached();
        group.setSync(true);
        mSubscriptions.add(mDataManagerGroups.syncGroupInDatabase(group)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<Group>() {
                    @Override
                    public void call(Group group) {
                        int singleSyncClientMax = getMvpView().getMaxSingleSyncGroupProgressBar();
                        getMvpView().updateSingleSyncGroupProgressBar(singleSyncClientMax);
                        mGroupSyncIndex = mGroupSyncIndex + 1;
                        checkNetworkConnectionAndSyncGroup();
                    }
                })
        );
    }

    public void updateTotalSyncProgressBarAndCount() {
        getMvpView().updateTotalSyncGroupProgressBarAndCount(mGroupSyncIndex);
    }

    public void updateClientName() {
        String groupName = mGroupList.get(mGroupSyncIndex).getName();
        getMvpView().showSyncingGroup(groupName);
    }
}
