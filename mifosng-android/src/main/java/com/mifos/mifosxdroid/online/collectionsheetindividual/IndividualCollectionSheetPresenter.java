package com.mifos.mifosxdroid.online.collectionsheetindividual;


import com.mifos.api.DataManager;
import com.mifos.api.datamanager.DataManagerCollectionSheet;
import com.mifos.api.GenericResponse;
import com.mifos.api.model.IndividualCollectionSheetPayload;
import com.mifos.api.model.RequestCollectionSheetPayload;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.PaymentTypeOptions;
import com.mifos.objects.collectionsheet.ClientCollectionSheet;
import com.mifos.objects.collectionsheet.IndividualCollectionSheet;
import com.mifos.objects.collectionsheet.LoanAndClientName;
import com.mifos.objects.collectionsheet.LoanCollectionSheet;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
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
 * Created by Tarun on 05-07-2017.
 */

public class IndividualCollectionSheetPresenter
        extends BasePresenter<IndividualCollectionSheetMvpView> {

    private DataManager mDataManager;
    private DataManagerCollectionSheet mDataManagerCollection;
    private CompositeSubscription mSubscription;

    @Inject
    IndividualCollectionSheetPresenter(DataManager manager,
                                       DataManagerCollectionSheet managerCollectionSheet) {
        mDataManager = manager;
        mDataManagerCollection = managerCollectionSheet;
        mSubscription = new CompositeSubscription();
    }

    @Override
    public void attachView(IndividualCollectionSheetMvpView mvpView) {
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

    void fetchIndividualCollectionSheet(RequestCollectionSheetPayload
                                                requestCollectionSheetPayload) {

        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscription.add(mDataManagerCollection
                .getIndividualCollectionSheet(requestCollectionSheetPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<IndividualCollectionSheet>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        if (e instanceof HttpException) {
                            try {
                                String errorMessage = ((HttpException) e).response().errorBody()
                                        .string();
                                getMvpView().showError(MFErrorParser.parseError(errorMessage)
                                        .getErrors().get(0).getDefaultUserMessage());
                            } catch (Throwable throwable) {
                                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                            }
                        } else {
                            getMvpView().showError(e.getLocalizedMessage());

                        }
                    }

                    @Override
                    public void onNext(IndividualCollectionSheet individualCollectionSheet) {
                        getMvpView().showProgressbar(false);
                        if (individualCollectionSheet.getClients().size() > 0) {
                            getMvpView().showSheet(individualCollectionSheet);
                        } else {
                            getMvpView().showNoSheetFound();
                        }
                    }
                }));
    }

    void fetchOffices() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscription.add(mDataManager.getOffices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Office>>() {
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
                    public void onNext(List<Office> officeList) {
                        getMvpView().setOfficeSpinner(officeList);
                        getMvpView().showProgressbar(false);
                    }
                }));
    }

    void fetchStaff(int officeId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscription.add(mDataManager.getStaffInOffice(officeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Staff>>() {
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
                    public void onNext(List<Staff> staffList) {
                        getMvpView().setStaffSpinner(staffList);
                        getMvpView().showProgressbar(false);
                    }
                }));
    }

    List<String> filterOffices(List<Office> offices) {
        final List<String> officesList = new ArrayList<>();
        Observable.from(offices)
                .subscribe(new Action1<Office>() {
                    @Override
                    public void call(Office office) {
                        officesList.add(office.getName());
                    }
                });
        return officesList;
    }

    List<String> filterStaff(List<Staff> staffs) {
        final List<String> staffList = new ArrayList<>();
        Observable.from(staffs)
                .subscribe(new Action1<Staff>() {
                    @Override
                    public void call(Staff staff) {
                        staffList.add(staff.getDisplayName());
                    }
                });
        return staffList;
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
                                        clientCollectionSheet.getClientName()));
                            }
                        }
                    }
                });
        return loansAndClientNames;
    }

}