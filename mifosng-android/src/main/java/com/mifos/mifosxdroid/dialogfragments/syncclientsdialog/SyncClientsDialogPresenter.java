package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.api.datamanager.DataManagerSavings;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.savings.SavingsAccount;
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations;
import com.mifos.objects.client.Client;
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
import rx.functions.Func2;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 08/08/16.
 */
public class SyncClientsDialogPresenter extends BasePresenter<SyncClientsDialogMvpView> {

    private final DataManagerClient mDataManagerClient;
    private final DataManagerLoan mDataManagerLoan;
    private final DataManagerSavings mDataManagerSavings;

    private CompositeSubscription mSubscriptions;

    private List<Client> mClientList, mFailedSyncClient;
    private List<LoanAccount> mLoanAccountList;
    private List<SavingsAccount> mSavingsAccountList;

    private Boolean mLoanAccountSyncStatus = false;

    private int mClientSyncIndex, mLoanAndRepaymentSyncIndex = 0,
            mSavingsAndTransactionSyncIndex = 0;

    @Inject
    public SyncClientsDialogPresenter(DataManagerClient dataManagerClient,
                                      DataManagerLoan dataManagerLoan,
                                      DataManagerSavings dataManagerSavings) {
        mDataManagerClient = dataManagerClient;
        mDataManagerLoan = dataManagerLoan;
        mDataManagerSavings = dataManagerSavings;
        mSubscriptions = new CompositeSubscription();
        mClientList = new ArrayList<>();
        mFailedSyncClient = new ArrayList<>();
        mLoanAccountList = new ArrayList<>();
        mSavingsAccountList = new ArrayList<>();
    }

    @Override
    public void attachView(SyncClientsDialogMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    /**
     * This Method Start Syncing Clients. Start Syncing the Client Accounts.
     *
     * @param clients Selected Clients For Syncing
     */
    public void startSyncingClients(List<Client> clients) {
        mClientList = clients;
        checkNetworkConnectionAndSyncClient();
    }


    public void syncClientAndUpdateUI() {
        mLoanAndRepaymentSyncIndex = 0;
        mSavingsAndTransactionSyncIndex = 0;
        mLoanAccountSyncStatus = false;
        updateTotalSyncProgressBarAndCount();
        if (mClientSyncIndex != mClientList.size()) {
            updateClientName();
            syncClientAccounts(mClientList.get(mClientSyncIndex).getId());
        } else {
            getMvpView().showClientsSyncSuccessfully();
        }

    }

    public void checkNetworkConnectionAndSyncClient() {
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
            getMvpView().setMaxSingleSyncClientProgressBar(1);
            syncClient(mClientList.get(mClientSyncIndex));
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
                int singleSyncClientMax = getMvpView().getMaxSingleSyncClientProgressBar();
                getMvpView().updateSingleSyncClientProgressBar(singleSyncClientMax);

                mFailedSyncClient.add(mClientList.get(mClientSyncIndex));
                mClientSyncIndex = mClientSyncIndex + 1;

                getMvpView().showSyncedFailedClients(mFailedSyncClient.size());
                checkNetworkConnectionAndSyncClient();
            }
        } catch (Throwable throwable) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(throwable);
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
    public void syncClientAccounts(int clientId) {
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
                        getMvpView().showError(R.string.failed_to_sync_clientaccounts);

                        //Updating UI
                        mFailedSyncClient.add(mClientList.get(mClientSyncIndex));
                        getMvpView().showSyncedFailedClients(mFailedSyncClient.size());
                        mClientSyncIndex = mClientSyncIndex + 1;
                        checkNetworkConnectionAndSyncClient();

                    }

                    @Override
                    public void onNext(ClientAccounts clientAccounts) {
                        mLoanAccountList = Utils.getActiveLoanAccounts(clientAccounts
                                .getLoanAccounts());
                        mSavingsAccountList = Utils.getSyncableSavingsAccounts(clientAccounts
                                .getSavingsAccounts());

                        //Updating UI
                        getMvpView().setMaxSingleSyncClientProgressBar(mLoanAccountList.size() +
                                mSavingsAccountList.size());

                        checkAccountsSyncStatusAndSyncAccounts();
                    }
                })
        );
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
    public void syncLoanAndLoanRepayment(int loanId) {
        checkViewAttached();
        mSubscriptions.add(Observable.combineLatest(
                mDataManagerLoan.syncLoanById(loanId),
                mDataManagerLoan.syncLoanRepaymentTemplate(loanId),
                new Func2<LoanWithAssociations, LoanRepaymentTemplate, LoanAndLoanRepayment>() {
                    @Override
                    public LoanAndLoanRepayment call(LoanWithAssociations loanWithAssociations,
                                                     LoanRepaymentTemplate loanRepaymentTemplate) {
                        LoanAndLoanRepayment loanAndLoanRepayment = new LoanAndLoanRepayment();
                        loanAndLoanRepayment.setLoanWithAssociations(loanWithAssociations);
                        loanAndLoanRepayment.setLoanRepaymentTemplate(loanRepaymentTemplate);
                        return loanAndLoanRepayment;
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
                        getMvpView().updateSingleSyncClientProgressBar(mLoanAndRepaymentSyncIndex);
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
     * This Method Fetch SavingsAccount and SavingsAccountTransactionTemplate and Sync them in
     * Database table.
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

                        SavingsAccountAndTransactionTemplate accountAndTransactionTemplate =
                                new SavingsAccountAndTransactionTemplate();
                        accountAndTransactionTemplate.setSavingsAccountTransactionTemplate(
                                savingsAccountTransactionTemplate);
                        accountAndTransactionTemplate.setSavingsAccountWithAssociations(
                                savingsAccountWithAssociations);

                        return accountAndTransactionTemplate;
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
                        getMvpView().updateSingleSyncClientProgressBar(
                                mLoanAndRepaymentSyncIndex + mSavingsAndTransactionSyncIndex);

                        if (mSavingsAndTransactionSyncIndex != mSavingsAccountList.size()) {
                            checkNetworkConnectionAndSyncSavingsAccountAndTransactionTemplate();
                        } else {
                            syncClient(mClientList.get(mClientSyncIndex));
                        }
                    }
                })
        );

    }

    /**
     * This Method Saving the Clients to Database, If their Accounts, Loan and LoanRepayment
     * saved successfully to Synced.
     *
     * @param client
     */
    public void syncClient(Client client) {
        checkViewAttached();
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
                        int singleSyncClientMax = getMvpView().getMaxSingleSyncClientProgressBar();
                        getMvpView().updateSingleSyncClientProgressBar(singleSyncClientMax);
                        mClientSyncIndex = mClientSyncIndex + 1;
                        checkNetworkConnectionAndSyncClient();
                    }
                })
        );
    }

    public void updateTotalSyncProgressBarAndCount() {
        getMvpView().updateTotalSyncClientProgressBarAndCount(mClientSyncIndex);
    }

    public void updateClientName() {
        String clientName = mClientList.get(mClientSyncIndex).getFirstname() +
                mClientList.get(mClientSyncIndex).getLastname();
        getMvpView().showSyncingClient(clientName);
    }
}
