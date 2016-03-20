package com.mifos.mifosxdroid.online.loanaccountsummaryfragment;

import com.mifos.api.DataManager;
import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.accounts.loan.LoanApprovalRequest;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.noncore.DataTable;

import java.util.HashMap;
import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class LoanAccountSummaryPresenter implements Presenter<LoanAccountSummaryMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private LoanAccountSummaryMvpView mLoanAccountSummaryMvpView;

    public LoanAccountSummaryPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(LoanAccountSummaryMvpView mvpView) {
        mLoanAccountSummaryMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mLoanAccountSummaryMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadLoanBYId(int loannumber){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getLoadById(loannumber)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoanWithAssociations>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoanAccountSummaryMvpView.ResponseError("Loan Account not found.");
                    }

                    @Override
                    public void onNext(LoanWithAssociations loanWithAssociations) {
                        mLoanAccountSummaryMvpView.showLoanByid(loanWithAssociations);
                    }
                });
    }

    public void loadLoanDataTable(){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getLoanDataTable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<DataTable>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<DataTable> dataTables) {
                        mLoanAccountSummaryMvpView.showloanDataTableList(dataTables);
                    }
                });
    }

    public void approveLoan(int loanid,LoanApprovalRequest loanApprovalRequest){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.approveLoan(loanid, loanApprovalRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoanAccountSummaryMvpView.ResponseError("Failed to approve Loan");
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        mLoanAccountSummaryMvpView.approveloan(genericResponse);
                    }
                });
    }

    public void disputeLoan(int loanid , HashMap<String, Object> request){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.disputeLoan(loanid,request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {

                    }
                });
    }
}
