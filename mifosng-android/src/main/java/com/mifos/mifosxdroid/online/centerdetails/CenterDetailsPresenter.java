package com.mifos.mifosxdroid.online.centerdetails;

import com.mifos.api.datamanager.DataManagerCenter;
import com.mifos.api.datamanager.DataManagerRunReport;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.group.CenterInfo;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Rajan Maurya on 05/02/17.
 */

public class CenterDetailsPresenter extends BasePresenter<CenterDetailsMvpView> {

    private final DataManagerCenter dataManagerCenter;
    private final DataManagerRunReport dataManagerRunReport;
    private CompositeDisposable compositeDisposable;

    @Inject
    public CenterDetailsPresenter(DataManagerCenter dataManagerCenter,
                                  DataManagerRunReport dataManagerRunReport) {
        this.dataManagerCenter = dataManagerCenter;
        this.dataManagerRunReport = dataManagerRunReport;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(CenterDetailsMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposable.clear();
    }

    public void loadCentersGroupAndMeeting(final int centerId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(dataManagerCenter.getCentersGroupAndMeeting(centerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<CenterWithAssociations>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showErrorMessage(R.string.failed_to_fetch_Group_and_meeting);
                    }

                    @Override
                    public void onNext(CenterWithAssociations centerWithAssociations) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMeetingDetails(centerWithAssociations);
                        getMvpView().showCenterDetails(centerWithAssociations);
                    }
                }));
    }

    public void loadSummaryInfo(int centerId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(dataManagerRunReport.getCenterSummarInfo(centerId, false)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<CenterInfo>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showErrorMessage(R.string.failed_to_fetch_center_info);
                    }

                    @Override
                    public void onNext(List<CenterInfo> centerInfos) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSummaryInfo(centerInfos);
                    }
                }));
    }
}
