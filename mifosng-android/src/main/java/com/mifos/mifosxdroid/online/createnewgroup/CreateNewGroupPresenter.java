package com.mifos.mifosxdroid.online.createnewgroup;

import com.mifos.api.DataManager;
import com.mifos.api.datamanager.DataManagerOffices;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.group.Group;
import com.mifos.objects.organisation.Office;
import com.mifos.services.data.GroupPayload;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public class CreateNewGroupPresenter extends BasePresenter<CreateNewGroupMvpView> {

    private final DataManager mDataManager;
    private final DataManagerOffices mDataManagerOffices;
    private CompositeSubscription mSubscriptions;

    @Inject
    public CreateNewGroupPresenter(DataManager dataManager,
                                   DataManagerOffices dataManagerOffices) {
        mDataManager = dataManager;
        mDataManagerOffices = dataManagerOffices;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(CreateNewGroupMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.unsubscribe();
    }

    public void loadOffices() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerOffices.getOffices()
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
                }));
    }

    public void createGroup(GroupPayload groupPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManager.createGroup(groupPayload)
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
                }));
    }

}
