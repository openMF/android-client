package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.loan.LoanAccount;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.client.Client;
import com.mifos.objects.sync.SyncClientInformationStatus;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.objects.zipmodels.LoanAndLoanRepayment;

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

    private SyncClientInformationStatus mSyncClientInformationStatus;

    private int mClientSyncIndex, mLoanAndRepaymentSyncIndex = 0;

    @Inject
    public SyncClientsDialogPresenter(DataManagerClient dataManagerClient,
                                      DataManagerLoan dataManagerLoan) {
        mDataManagerClient = dataManagerClient;
        mDataManagerLoan = dataManagerLoan;
        mSubscriptions = new CompositeSubscription();
        mClientList = new ArrayList<>();
        mFailedSyncClient = new ArrayList<>();
        mLoanAccountList = new ArrayList<>();
        mSyncClientInformationStatus = new SyncClientInformationStatus();

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
        syncClientAndUpdateUI();
    }


    public void syncClientAndUpdateUI() {
        updateClientNameAndTotalSyncProgressBarAndCount();
        //TODO Check the mClientList is not going Array Index bound;
        if (mClientList.get(mClientSyncIndex) != null) {
            syncClientAccounts(mClientList.get(mClientSyncIndex).getId());
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
                        getMvpView().showError(R.string.failed_to_sync_client_and_accounts);

                        //Updating UI
                        mFailedSyncClient.add(mClientList.get(mClientSyncIndex));
                        mClientSyncIndex = mClientSyncIndex + 1;
                        syncClientAndUpdateUI();
                        
                    }

                    @Override
                    public void onNext(ClientAccounts clientAccounts) {
                        mSyncClientInformationStatus.setClientAccountsStatus(true);
                        mLoanAccountList = getActiveLoanAccounts(clientAccounts.getLoanAccounts());
                        mLoanAndRepaymentSyncIndex = mLoanAccountList.size();
                        if (!mLoanAccountList.isEmpty()) {
                            //Sync the Active Loan and LoanRepayment
                            syncLoanAndLoanRepayment(mLoanAccountList
                                    .get(mLoanAndRepaymentSyncIndex).getId());

                            //Updating UI
                            getMvpView().setMaxSingleSyncClientProgressBar
                                    (mLoanAndRepaymentSyncIndex + 1);


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

                    }

                    @Override
                    public void onNext(LoanAndLoanRepayment loanAndLoanRepayment) {

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
                        syncClientAndUpdateUI();
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


    public Boolean isClientInformationSync(SyncClientInformationStatus
                                                   syncClientInformationStatus) {
        return syncClientInformationStatus.getSyncClientInformationStatus();
    }

    public void updateClientNameAndTotalSyncProgressBarAndCount() {
        String clientName = mClientList.get(mClientSyncIndex).getFirstname() +
                mClientList.get(mClientSyncIndex).getLastname();
        getMvpView().showSyncingClient(clientName);
        getMvpView().updateTotalSyncClientProgressBarAndCount(mClientSyncIndex);
    }



}
