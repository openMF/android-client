package com.mifos.mifosxdroid.online.grouplistfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.group.CenterWithAssociations;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class GroupListPresenter implements Presenter<GroupListMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private GroupListMvpView mGroupListMvpView;

    public GroupListPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }
    @Override
    public void attachView(GroupListMvpView mvpView) {
        mGroupListMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mGroupListMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadGroupByCenter(int centerid){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllGroupsForCenter(centerid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CenterWithAssociations>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mGroupListMvpView.ResponseError("Failed to Fetch GroupByCenter");
                    }

                    @Override
                    public void onNext(CenterWithAssociations centerWithAssociations) {
                        mGroupListMvpView.showGroupList(centerWithAssociations);
                    }
                });
    }
}
