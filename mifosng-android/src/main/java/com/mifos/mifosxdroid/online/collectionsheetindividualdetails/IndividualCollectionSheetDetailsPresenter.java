package com.mifos.mifosxdroid.online.collectionsheetindividualdetails;

import com.mifos.api.DataManager;
import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerCollectionSheet;
import com.mifos.api.model.IndividualCollectionSheetPayload;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.PaymentTypeOptions;
import com.mifos.objects.collectionsheet.ClientCollectionSheet;
import com.mifos.objects.collectionsheet.LoanAndClientName;
import com.mifos.objects.collectionsheet.LoanCollectionSheet;
import com.mifos.utils.MFErrorParser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by aksh on 20/6/18.
 */

public class IndividualCollectionSheetDetailsPresenter
        extends BasePresenter<IndividualCollectionSheetDetailsMvpView> {

    private DataManager mDataManager;
    private DataManagerCollectionSheet mDataManagerCollection;
    private CompositeSubscription mSubscription;


    @Inject
    IndividualCollectionSheetDetailsPresenter(DataManager manager,
                                          DataManagerCollectionSheet managerCollectionSheet) {
        mDataManager = manager;
        mDataManagerCollection = managerCollectionSheet;
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void attachView(IndividualCollectionSheetDetailsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }
    }

    void submitIndividualCollectionSheet(IndividualCollectionSheetPayload
                                                 individualCollectionSheetPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscription.add(mDataManagerCollection
                .saveIndividualCollectionSheet(individualCollectionSheetPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        try {
                            if (e instanceof HttpException) {
                                String errorMessage = ((HttpException) e).response().errorBody()
                                        .string();
                                getMvpView().showError(MFErrorParser.parseError(errorMessage)
                                        .getErrors().get(0).getDefaultUserMessage());
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                        }
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSuccess();
                    }
                }));
    }

    List<String> filterPaymentTypeOptions(List<PaymentTypeOptions> paymentTypeOptionsList) {
        final List<String> paymentList = new ArrayList<>();
        Observable.from(paymentTypeOptionsList)
                .subscribe(new Action1<PaymentTypeOptions>() {
                    @Override
                    public void call(PaymentTypeOptions paymentTypeOption) {
                        paymentList.add(paymentTypeOption.getName());
                    }
                });
        return paymentList;
    }

    List<LoanAndClientName> filterLoanAndClientNames(List<ClientCollectionSheet>
                                                             clientCollectionSheets) {
        final List<LoanAndClientName> loansAndClientNames = new ArrayList<>();
        Observable.from(clientCollectionSheets)
                .subscribe(new Action1<ClientCollectionSheet>() {
                    @Override
                    public void call(ClientCollectionSheet clientCollectionSheet) {
                        if (clientCollectionSheet.getLoans() != null) {
                            for (LoanCollectionSheet loanCollectionSheet :
                                    clientCollectionSheet.getLoans()) {
                                loansAndClientNames.add(new
                                        LoanAndClientName(loanCollectionSheet,
                                        clientCollectionSheet.getClientName(),
                                        clientCollectionSheet.getClientId()));
                            }
                        }
                    }
                });
        return loansAndClientNames;
    }
}
