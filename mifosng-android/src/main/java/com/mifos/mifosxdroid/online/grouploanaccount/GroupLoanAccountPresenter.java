package com.mifos.mifosxdroid.online.grouploanaccount;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.organisation.ProductLoans;
import com.mifos.services.data.GroupLoanPayload;

import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class GroupLoanAccountPresenter extends BasePresenter<GroupLoanAccountMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public GroupLoanAccountPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }


    @Override
    public void attachView(GroupLoanAccountMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadAllLoans() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllLoans()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<ProductLoans>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to fetch Loans");
                    }

                    @Override
                    public void onNext(List<ProductLoans> productLoans) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showAllLoans(productLoans);
                    }
                });
    }

    public void loadGroupLoansAccountTemplate(int groupId, int productId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getGroupLoansAccountTemplate(groupId, productId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to load GroupLoan");
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showLoanPurposeSpinner(response);

                    }
                });
    }


    public void createGroupLoanAccount(GroupLoanPayload loansPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.createGroupLoansAccount(loansPayload)
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
                        getMvpView().showFetchingError("Try Again");
                    }

                    @Override
                    public void onNext(Loans loans) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupLoansAccountCreatedSuccessfully(loans);
                    }
                });
    }


}
