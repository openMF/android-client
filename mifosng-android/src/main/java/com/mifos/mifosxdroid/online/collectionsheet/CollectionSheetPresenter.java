package com.mifos.mifosxdroid.online.collectionsheet;

import com.mifos.api.DataManager;
import com.mifos.api.model.CollectionSheetPayload;
import com.mifos.api.model.Payload;
import com.mifos.objects.response.SaveResponse;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.db.CollectionSheet;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 7/6/16.
 */
public class CollectionSheetPresenter extends BasePresenter<CollectionSheetMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public CollectionSheetPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(CollectionSheetMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadCollectionSheet(long id, Payload payload) {
        checkViewAttached();
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getCollectionSheet(id, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CollectionSheet>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showFetchingError("Failed to fetch CollectionSheet");
                    }

                    @Override
                    public void onNext(CollectionSheet collectionSheet) {
                        getMvpView().showCollectionSheet(collectionSheet);
                    }
                });
    }

    public void saveCollectionSheet(int id, CollectionSheetPayload payload) {
        checkViewAttached();
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.saveCollectionSheetAsync(id, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SaveResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException) {
                            HttpException response = (HttpException) e;
                            getMvpView().showFailedToSaveCollectionSheet(response);
                        }
                    }

                    @Override
                    public void onNext(SaveResponse saveResponse) {
                        getMvpView().showCollectionSheetSuccessfullySaved(saveResponse);
                    }
                });
    }

}
