package com.mifos.mifosxdroid.online.loanaccountapproval;

import com.mifos.api.DataManager;
import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.LoanApproval;
import com.mifos.utils.MFErrorParser;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;


/**
 * Created by Rajan Maurya on 8/6/16.
 */
public class LoanAccountApprovalPresenter extends BasePresenter<LoanAccountApprovalMvpView> {

    private final DataManager mDataManager;
    private CompositeDisposable compositeDisposable;

    @Inject
    public LoanAccountApprovalPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(LoanAccountApprovalMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeDisposable != null) compositeDisposable.clear();
    }

    public void approveLoan(int loanId, LoanApproval loanApproval) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (compositeDisposable != null) compositeDisposable.clear();
        compositeDisposable = mDataManager.approveLoan(loanId, loanApproval)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<GenericResponse>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        try {
                            if (e instanceof HttpException) {
                                String errorMessage = ((HttpException) e).response().errorBody()
                                        .string();
                                getMvpView().showLoanApproveFailed(
                                        MFErrorParser.parseError(errorMessage)
                                                .getErrors().get(0).getDefaultUserMessage());
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getErrorHandler();
                        }
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanApproveSuccessfully(genericResponse);
                    }
                });
    }
}
