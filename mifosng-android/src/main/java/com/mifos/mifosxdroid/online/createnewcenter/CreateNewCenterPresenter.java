package com.mifos.mifosxdroid.online.createnewcenter;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.group.Center;
import com.mifos.objects.organisation.Office;
import com.mifos.services.data.CenterPayload;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public class CreateNewCenterPresenter extends BasePresenter<CreateNewCenterMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public CreateNewCenterPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(CreateNewCenterMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }


    public void loadOffices() {
        checkViewAttached();
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getOffices()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Office>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError("Failed to load office list");
                    }

                    @Override
                    public void onNext(List<Office> offices) {
                        getMvpView().showOffices(offices);
                    }
                });
    }

    public void createCenter(CenterPayload centerPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.createCenter(centerPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Center>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Try again");
                    }

                    @Override
                    public void onNext(Center center) {
                        getMvpView().showProgressbar(false);
                        getMvpView().centerCreatedSuccessfully(center);
                    }
                });
    }


}
