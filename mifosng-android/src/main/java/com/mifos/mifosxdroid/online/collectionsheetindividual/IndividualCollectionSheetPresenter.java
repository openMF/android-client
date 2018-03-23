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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;


/**
 * Created by Tarun on 05-07-2017.
 */

public class IndividualCollectionSheetPresenter
        extends BasePresenter<IndividualCollectionSheetMvpView> {

    private DataManager mDataManager;
    private DataManagerCollectionSheet mDataManagerCollection;
    private CompositeDisposable mCompositeDisposable;

    @Inject
    IndividualCollectionSheetPresenter(DataManager manager,
                                       DataManagerCollectionSheet managerCollectionSheet) {
        mDataManager = manager;
        mDataManagerCollection = managerCollectionSheet;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(IndividualCollectionSheetMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    void submitIndividualCollectionSheet(IndividualCollectionSheetPayload
                                                 individualCollectionSheetPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mCompositeDisposable.add(mDataManagerCollection
                .saveIndividualCollectionSheet(individualCollectionSheetPayload)
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
                                getMvpView().showError(MFErrorParser.parseError(errorMessage)
                                        .getErrors().get(0).getDefaultUserMessage());
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getErrorHandler();
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
        mCompositeDisposable.add(mDataManagerCollection
                .getIndividualCollectionSheet(requestCollectionSheetPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<IndividualCollectionSheet>() {
                    @Override
                    public void onComplete() {

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
                                RxJavaPlugins.getErrorHandler();
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
        mCompositeDisposable.add(mDataManager.getOffices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<Office>>() {
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
                                getMvpView().showError(MFErrorParser.parseError(errorMessage)
                                        .getErrors().get(0).getDefaultUserMessage());
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getErrorHandler();
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
        mCompositeDisposable.add(mDataManager.getStaffInOffice(officeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<Staff>>() {
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
                                getMvpView().showError(MFErrorParser.parseError(errorMessage)
                                        .getErrors().get(0).getDefaultUserMessage());
                            }
                        } catch (Throwable throwable) {
                            RxJavaPlugins.getErrorHandler();
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
        Observable.fromIterable(offices)
                .subscribe(new Consumer<Office>() {
                    @Override
                    public void accept(Office office) {
                        officesList.add(office.getName());
                    }
                });
        return officesList;
    }

    List<String> filterStaff(List<Staff> staffs) {
        final List<String> staffList = new ArrayList<>();
        Observable.fromIterable(staffs)
                .subscribe(new Consumer<Staff>() {
                    @Override
                    public void accept(Staff staff) {
                        staffList.add(staff.getDisplayName());
                    }
                });
        return staffList;
    }

    List<String> filterPaymentTypeOptions(List<PaymentTypeOptions> paymentTypeOptionsList) {
        final List<String> paymentList = new ArrayList<>();
        Observable.fromIterable(paymentTypeOptionsList)
                .subscribe(new Consumer<PaymentTypeOptions>() {
                    @Override
                    public void accept(PaymentTypeOptions paymentTypeOption) {
                        paymentList.add(paymentTypeOption.getName());
                    }
                });
        return paymentList;
    }

    List<LoanAndClientName> filterLoanAndClientNames(List<ClientCollectionSheet>
                                                             clientCollectionSheets) {
        final List<LoanAndClientName> loansAndClientNames = new ArrayList<>();
        Observable.fromIterable(clientCollectionSheets)
                .subscribe(new Consumer<ClientCollectionSheet>() {
                    @Override
                    public void accept(ClientCollectionSheet clientCollectionSheet) {
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