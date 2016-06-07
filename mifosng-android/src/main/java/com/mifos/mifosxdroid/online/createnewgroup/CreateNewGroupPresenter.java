package com.mifos.mifosxdroid.online.createnewgroup;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.group.Group;
import com.mifos.objects.organisation.Office;
import com.mifos.services.data.GroupPayload;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public class CreateNewGroupPresenter extends BasePresenter<CreateNewGroupMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public CreateNewGroupPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(CreateNewGroupMvpView mvpView) {
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

    public void createGroup(GroupPayload groupPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.createGroup(groupPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Group>() {
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
                    public void onNext(Group group) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupCreatedSuccessfully(group);
                    }
                });
    }

}
