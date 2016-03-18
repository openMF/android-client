package com.mifos.mifosxdroid.online.collectionsheetfragment;

import com.mifos.api.DataManager;
import com.mifos.api.model.CollectionSheetPayload;
import com.mifos.api.model.Payload;
import com.mifos.api.model.SaveResponse;
import com.mifos.mifosxdroid.base.Presenter;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class CollectionSheetPresenter implements Presenter<CollectionSheetMvpView> {

    private final DataManager mDataManager;
    private CollectionSheetMvpView mMvpView;
    private Subscription mSubscription;

    public CollectionSheetPresenter(DataManager dataManager){
        mDataManager  = dataManager;
    }

    @Override
    public void attachView(CollectionSheetMvpView mvpView) {
        mMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadcollectionsheet(long centerId ,Payload payload){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getCallectionSheet(centerId , payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<com.mifos.objects.db.CollectionSheet>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mMvpView.showFailedToFetchCollectionsheet();
                    }

                    @Override
                    public void onNext(com.mifos.objects.db.CollectionSheet collectionSheet) {
                        mMvpView.showcollectionsheet(collectionSheet);
                    }
                });
    }

    public void savecallectionsheetAsyn(int centerid , CollectionSheetPayload collectionSheetPayload){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.savecallectionsheetAsync(centerid,collectionSheetPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<SaveResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mMvpView.showsavecaollectionsheelfailed();
                    }

                    @Override
                    public void onNext(SaveResponse saveResponse) {
                        mMvpView.showsaveCollectionsheetResponse(saveResponse);
                    }
                });
    }

}
