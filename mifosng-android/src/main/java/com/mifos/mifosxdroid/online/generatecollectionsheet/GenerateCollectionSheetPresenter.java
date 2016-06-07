package com.mifos.mifosxdroid.online.generatecollectionsheet;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.Group;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public class GenerateCollectionSheetPresenter
        extends BasePresenter<GenerateCollectionSheetMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public GenerateCollectionSheetPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(GenerateCollectionSheetMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadOffices() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getOffices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Office>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to fetch office list");
                    }

                    @Override
                    public void onNext(List<Office> offices) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showOffices(offices);
                    }
                });
    }

    public void loadStaffInOffice(final int officeId) {
        checkViewAttached();
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getStaffInOffice(officeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Staff>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError("Failed to fetch Staff in Office");
                    }

                    @Override
                    public void onNext(List<Staff> staffs) {
                        getMvpView().showStaffInOffice(staffs, officeId);
                    }
                });
    }

    public void loadCentersInOffice(int id, Map<String, Object> params) {
        checkViewAttached();
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getCentersInOffice(id, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Center>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError("Failed to fetch Centers in Office");
                    }

                    @Override
                    public void onNext(List<Center> centers) {
                        getMvpView().showCentersInOffice(centers);
                    }
                });
    }

    public void loadGroupsInOffice(int office, Map<String, Object> params) {
        checkViewAttached();
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getGroupsByOffice(office, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Group>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError("Failed to load Groups in Office");
                    }

                    @Override
                    public void onNext(List<Group> groups) {
                        getMvpView().showGroupsInOffice(groups);
                    }
                });
    }

    public void loadGroupByCenter(int centerId) {
        checkViewAttached();
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getGroupsByCenter(centerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CenterWithAssociations>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError("Failed to load GroupByCenter");
                    }

                    @Override
                    public void onNext(CenterWithAssociations centerWithAssociations) {
                        getMvpView().showGroupByCenter(centerWithAssociations);
                    }
                });
    }


}
