package com.mifos.mifosxdroid.online.loanaccountdisbursement;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerLoan;
import com.mifos.api.model.APIEndPoint;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.LoanDisbursement;
import com.mifos.objects.templates.loans.LoanTransactionTemplate;
import com.mifos.utils.MFErrorParser;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public class LoanAccountDisbursementPresenter
        extends BasePresenter<LoanAccountDisbursementMvpView> {

    private final DataManagerLoan dataManagerLoan;
    private CompositeDisposable compositeDisposable;

    @Inject
    public LoanAccountDisbursementPresenter(DataManagerLoan dataManager) {
        dataManagerLoan = dataManager;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(LoanAccountDisbursementMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposable.clear();
    }

    public void loadLoanTemplate(int loanId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(dataManagerLoan.getLoanTransactionTemplate(loanId, APIEndPoint.DISBURSE)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<LoanTransactionTemplate>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError("s");
                    }

                    @Override
                    public void onNext(LoanTransactionTemplate loanTransactionTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanTransactionTemplate(loanTransactionTemplate);
                    }
                })
        );
    }

    public void disburseLoan(int loanId, LoanDisbursement loanDisbursement) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(dataManagerLoan.dispurseLoan(loanId, loanDisbursement)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<GenericResponse>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(MFErrorParser.errorMessage(e));
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showDisburseLoanSuccessfully(genericResponse);
                    }
                }));
    }

}
