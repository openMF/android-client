package com.mifos.mifosxdroid.online.createnewcenterfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.group.Center;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.services.data.CenterPayload;

import java.util.List;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class CreateNewCenterPresenter implements Presenter<CreateNewCenterMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private CreateNewCenterMvpView mCreateNewCenterMvpView;

    public CreateNewCenterPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(CreateNewCenterMvpView mvpView) {
        mCreateNewCenterMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mCreateNewCenterMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadofficelist(){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllOffices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Office>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mCreateNewCenterMvpView.showfailedtofetch("Failed to Fetch Office List");
                    }

                    @Override
                    public void onNext(List<Office> offices) {
                        mCreateNewCenterMvpView.showofficeList(offices);
                    }
                });
    }

    public void loadStafflist(int officeid){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getStaffForOffice(officeid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Staff>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mCreateNewCenterMvpView.showfailedtofetch("Failed to Fetch Staff List");
                    }

                    @Override
                    public void onNext(List<Staff> staffs) {
                        mCreateNewCenterMvpView.showStaffList(staffs);
                    }
                });
    }

    public void createcenter(CenterPayload centerPayload){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.createCenter(centerPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Center>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mCreateNewCenterMvpView.showfailedtofetch("Try Again");
                    }

                    @Override
                    public void onNext(Center center) {
                        mCreateNewCenterMvpView.CreateCenter(center);
                    }
                });

    }

}
