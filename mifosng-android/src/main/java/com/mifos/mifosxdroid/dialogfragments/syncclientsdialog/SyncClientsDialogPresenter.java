package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import android.content.Context;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.mifosxdroid.injection.ApplicationContext;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.client.Client;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.objects.zipmodels.LoanAndLoanRepayment;
import com.mifos.utils.Network;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 *
 * Created by Rajan Maurya on 08/08/16.
 */
public class SyncClientsDialogPresenter extends BasePresenter<SyncClientsDialogMvpView> {

    private final DataManagerClient mDataManagerClient;
    private final DataManagerLoan mDataManagerLoan;

    private CompositeSubscription mSubscriptions;

    private List<Client> mClientList, mFailedSyncClient;
    private List<LoanAccount> mLoanAccountList;

    private int mClientSyncIndex, mLoanAndRepaymentSyncIndex = 0;

    private Context mContext;
    @Inject
    public SyncClientsDialogPresenter(DataManagerClient dataManagerClient,
                                      DataManagerLoan dataManagerLoan,
                                      @ApplicationContext Context context) {
        mDataManagerClient = dataManagerClient;
        mDataManagerLoan = dataManagerLoan;
        mSubscriptions = new CompositeSubscription();
        mClientList = new ArrayList<>();
        mFailedSyncClient = new ArrayList<>();
        mLoanAccountList = new ArrayList<>();
        mContext = context;
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
        updateTotalSyncProgressBarAndCount();
        if (mClientSyncIndex != mClientList.size()) {
            updateClientName();
            syncClientAccounts(mClientList.get(mClientSyncIndex).getId());
        } else {
            getMvpView().showError(R.string.no_more_clients_to_sync);
            getMvpView().showClientsSyncSuccessfully();
        }

    }

    public void checkNetworkConnectionAndSyncClient() {
        if (Network.isOnline(mContext)) {
            syncClientAndUpdateUI();
        } else {
            getMvpView().showNetworkIsNotAvailable();
            getMvpView().dismissDialog();
        }
    }

    public void checkNetworkConnectionAndSyncLoanAndLoanRepayment() {
        if (Network.isOnline(mContext)) {
            syncLoanAndLoanRepayment(mLoanAccountList
                    .get(mLoanAndRepaymentSyncIndex).getId());
        } else {
            getMvpView().showNetworkIsNotAvailable();
            getMvpView().dismissDialog();
        }
    }

    /**
     * Sync the Client Account with Client Id. This method fetching the Client Accounts from the
     * REST API using retrofit 2 and saving these accounts to Database with DatabaseHelperClient
     * and then DataManagerClient gives the returns the Clients Accounts to Presenter.
     * <p/>
     *
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
                        mLoanAccountList = getActiveLoanAccounts(clientAccounts.getLoanAccounts());
                        if (!mLoanAccountList.isEmpty()) {
                            //Sync the Active Loan and LoanRepayment
                            checkNetworkConnectionAndSyncLoanAndLoanRepayment();

                            //Updating UI
                            getMvpView().setMaxSingleSyncClientProgressBar(mLoanAccountList.size());

                        } else {
                            // If LoanAccounts is null then sync Client to Database
                            getMvpView().setMaxSingleSyncClientProgressBar(1);
                            syncClient(mClientList.get(mClientSyncIndex));
                        }
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
        mSubscriptions.add(Observable.zip(
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
                        int singleSyncClientMax = getMvpView().getMaxSingleSyncClientProgressBar();
                        getMvpView().updateSingleSyncClientProgressBar(singleSyncClientMax);
                        mClientSyncIndex = mClientSyncIndex + 1;
                        mFailedSyncClient.add(mClientList.get(mClientSyncIndex));
                        getMvpView().showSyncedFailedClients(mFailedSyncClient.size());
                        checkNetworkConnectionAndSyncClient();
                    }

                    @Override
                    public void onNext(LoanAndLoanRepayment loanAndLoanRepayment) {
                        mLoanAndRepaymentSyncIndex = mLoanAndRepaymentSyncIndex + 1;
                        getMvpView().updateSingleSyncClientProgressBar(mLoanAndRepaymentSyncIndex);
                        if (mLoanAndRepaymentSyncIndex !=  mLoanAccountList.size()) {
                            checkNetworkConnectionAndSyncLoanAndLoanRepayment();
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


    public List<LoanAccount> getActiveLoanAccounts(List<LoanAccount> loanAccountList) {
        final List<LoanAccount> loanAccounts = new ArrayList<>();
        Observable.from(loanAccountList)
                .filter(new Func1<LoanAccount, Boolean>() {
                    @Override
                    public Boolean call(LoanAccount loanAccount) {
                        return loanAccount.getStatus().getActive();
                    }
                })
                .subscribe(new Action1<LoanAccount>() {
                    @Override
                    public void call(LoanAccount loanAccount) {
                        loanAccounts.add(loanAccount);
                    }
                });
        return loanAccounts;
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
