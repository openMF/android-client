package com.mifos.mifosxdroid.online.groupslist;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 7/6/16.
 */
public class GroupsListPresenter extends BasePresenter<GroupsListMvpView> {


    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public GroupsListPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(GroupsListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadAllGroup() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllGroup()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Group>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        if (e instanceof RetrofitError) {
                            Response response = ((RetrofitError) e).getResponse();
                            getMvpView().showFetchingError("Failed to load Groups", response);
                        }

                    }

                    @Override
                    public void onNext(Page<Group> groupPage) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroups(groupPage);
                    }
                });
    }

    public void loadMoreGroups(int offset, int limit) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.listAllGroups(offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Group>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        if (e instanceof RetrofitError) {
                            Response response = ((RetrofitError) e).getResponse();
                            getMvpView().showFetchingError("Failed to load Groups", response);
                        }
                    }

                    @Override
                    public void onNext(Page<Group> groupPage) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showMoreGroups(groupPage);
                    }
                });
    }
}
