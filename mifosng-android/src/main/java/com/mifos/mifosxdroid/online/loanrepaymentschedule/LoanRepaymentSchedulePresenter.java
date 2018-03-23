package com.mifos.mifosxdroid.online.loanrepaymentschedule;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.LoanWithAssociations;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class LoanRepaymentSchedulePresenter extends BasePresenter<LoanRepaymentScheduleMvpView> {

    private final DataManager mDataManager;
    private CompositeDisposable compositeDisposable;

    @Inject
    public LoanRepaymentSchedulePresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(LoanRepaymentScheduleMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeDisposable != null) compositeDisposable.clear();
    }

    public void loadLoanRepaySchedule(int loanId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (compositeDisposable != null) compositeDisposable.clear();
        compositeDisposable = mDataManager.getLoanRepaySchedule(loanId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<LoanWithAssociations>() {
                    @Override
                    public void onComplete() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to load LoanRepayment");
                    }

                    @Override
                    public void onNext(LoanWithAssociations loanWithAssociations) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanRepaySchedule(loanWithAssociations);
                    }
                });
    }


}
