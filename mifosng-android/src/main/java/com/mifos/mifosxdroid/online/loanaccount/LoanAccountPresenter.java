package com.mifos.mifosxdroid.online.loanaccount;

import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.organisation.LoanProducts;
import com.mifos.objects.templates.loans.LoanTemplate;
import com.mifos.services.data.LoansPayload;
import com.mifos.utils.MFErrorParser;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class LoanAccountPresenter extends BasePresenter<LoanAccountMvpView> {

    private final DataManagerLoan mDataManagerLoan;
    private CompositeSubscription mSubscriptions;

    @Inject
    public LoanAccountPresenter(DataManagerLoan dataManagerLoan) {
        mDataManagerLoan = dataManagerLoan;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(LoanAccountMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loadAllLoans() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerLoan.getAllLoans()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<LoanProducts>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMessage(R.string.failed_to_fetch_loan_products);
                    }

                    @Override
                    public void onNext(List<LoanProducts> productLoanses) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showAllLoan(productLoanses);
                    }
                }));
    }

    public void loadLoanAccountTemplate(int clientId, int productId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerLoan.getLoansAccountTemplate(clientId, productId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanTemplate>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMessage(R.string.failed_to_fetch_loan_template);
                    }

                    @Override
                    public void onNext(LoanTemplate loanTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanAccountTemplate(loanTemplate);
                    }
                }));
    }

    public void createLoansAccount(LoansPayload loansPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerLoan.createLoansAccount(loansPayload)
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
                        getMvpView().showFetchingError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(Loans loans) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanAccountCreatedSuccessfully(loans);

                    }
                }));
    }
}
