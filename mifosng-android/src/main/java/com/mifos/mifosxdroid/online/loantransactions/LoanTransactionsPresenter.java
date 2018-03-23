package com.mifos.mifosxdroid.online.loantransactions;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.LoanWithAssociations;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Rajan Maurya on 7/6/16.
 */
public class LoanTransactionsPresenter extends BasePresenter<LoanTransactionsMvpView> {


    private final DataManager mDataManager;
    private CompositeDisposable compositeDisposable;

    @Inject
    public LoanTransactionsPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(LoanTransactionsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeDisposable != null) compositeDisposable.clear();
    }

    public void loadLoanTransaction(int loan) {
        checkViewAttached();
        if (compositeDisposable != null) compositeDisposable.clear();
        compositeDisposable = mDataManager.getLoanTransactions(loan)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<LoanWithAssociations>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError("Failed to fetch LoanTransaction");
                    }

                    @Override
                    public void onNext(LoanWithAssociations loanWithAssociations) {
                        getMvpView().showLoanTransaction(loanWithAssociations);
                    }
                });
    }

}
