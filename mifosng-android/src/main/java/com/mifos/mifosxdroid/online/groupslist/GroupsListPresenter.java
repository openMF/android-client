package com.mifos.mifosxdroid.online.groupslist;

import com.mifos.api.datamanager.DataManagerGroups;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Group;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 7/6/16.
 */
public class GroupsListPresenter extends BasePresenter<GroupsListMvpView> {


    private final DataManagerGroups mDataManagerGroups;
    private Subscription mSubscription;

    @Inject
    public GroupsListPresenter(DataManagerGroups dataManagerGroups) {
        mDataManagerGroups = dataManagerGroups;
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

    public void loadAllGroup(boolean paged, int offset, int limit) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManagerGroups.getGroups(paged, offset, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Page<Group>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        if (e instanceof HttpException) {
                            HttpException response = ((HttpException) e);
                            getMvpView().showFetchingError("Failed to load Groups",
                                    response.code());
                        }

                    }

                    @Override
                    public void onNext(Page<Group> groupPage) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroups(groupPage);
                    }
                });
    }
}
