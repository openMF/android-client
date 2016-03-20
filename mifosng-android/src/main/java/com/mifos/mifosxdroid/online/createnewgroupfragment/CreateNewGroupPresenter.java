package com.mifos.mifosxdroid.online.createnewgroupfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.group.GroupCreationResponse;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.organisation.Staff;
import com.mifos.services.data.GroupPayload;

import java.util.List;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class CreateNewGroupPresenter implements Presenter<CreateNewGroupMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    public CreateNewGroupMvpView mCreateNewGroupMvpView;

    public CreateNewGroupPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(CreateNewGroupMvpView mvpView) {
        mCreateNewGroupMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mCreateNewGroupMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadOfficeList(){
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
                        mCreateNewGroupMvpView.ResponseError("Failed to fetch OfficeList");
                    }

                    @Override
                    public void onNext(List<Office> offices) {
                        mCreateNewGroupMvpView.showOfficeList(offices);
                    }
                });
    }

    public void loadStaffInOfficeList(int officeId){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getStaffForOffice(officeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Staff>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mCreateNewGroupMvpView.ResponseError("Failed to Fetch Staff List");
                    }

                    @Override
                    public void onNext(List<Staff> staffs) {
                        mCreateNewGroupMvpView.showStaffOfficeList(staffs);
                    }
                });

    }

    public void creategroup(GroupPayload groupPayload){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.creategroup(groupPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GroupCreationResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mCreateNewGroupMvpView.GroupCreationError("Try again");
                    }

                    @Override
                    public void onNext(GroupCreationResponse groupCreationResponse) {
                        mCreateNewGroupMvpView.showGroupCreationResponse(groupCreationResponse);
                    }
                });
    }
}
