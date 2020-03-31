package com.mifos.mifosxdroid.online.datatablelistfragment;

import com.mifos.api.DataManager;
import com.mifos.api.datamanager.DataManagerClient;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.client.Client;
import com.mifos.objects.client.ClientPayload;
import com.mifos.services.data.GroupLoanPayload;
import com.mifos.services.data.LoansPayload;
import com.mifos.utils.MFErrorParser;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class DataTableListPresenter extends BasePresenter<DataTableListMvpView> {

    private final DataManagerLoan mDataManagerLoan;
    private final DataManager mDataManager;
    private final DataManagerClient dataManagerClient;
    private CompositeSubscription mSubscription;

    @Inject
    public DataTableListPresenter(DataManagerLoan dataManager, DataManager manager,
                                  DataManagerClient dataManagerClient) {
        mDataManagerLoan = dataManager;
        mDataManager = manager;
        this.dataManagerClient = dataManagerClient;
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void attachView(DataTableListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void createLoansAccount(LoansPayload loansPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscription.add(mDataManagerLoan.createLoansAccount(loansPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Loans>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMessage(R.string.generic_failure_message);
                    }

                    @Override
                    public void onNext(Loans loans) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMessage(R.string.loan_creation_success);

                    }
                }));
    }

    public void createGroupLoanAccount(GroupLoanPayload loansPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscription.add(mDataManager.createGroupLoansAccount(loansPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Loans>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMessage(R.string.generic_failure_message);
                    }

                    @Override
                    public void onNext(Loans loans) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMessage(R.string.loan_creation_success);
                    }
                })
        );
    }

    public void createClient(ClientPayload clientPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscription.add(dataManagerClient.createClient(clientPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Client>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMessage(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(Client client) {
                        getMvpView().showProgressbar(false);
                        if (client.getClientId() != null) {
                            getMvpView().showClientCreatedSuccessfully(
                                    client);
                        } else {
                            getMvpView().showWaitingForCheckerApproval(
                                    R.string.waiting_for_checker_approval);
                        }
                    }
                }));
    }

}
