package com.mifos.mifosxdroid.online.collectionsheet;

import com.mifos.api.DataManager;
import com.mifos.api.model.CollectionSheetPayload;
import com.mifos.api.model.Payload;
import com.mifos.objects.response.SaveResponse;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.db.CollectionSheet;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * Created by Rajan Maurya on 7/6/16.
 */
public class CollectionSheetPresenter extends BasePresenter<CollectionSheetMvpView> {

    private final DataManager mDataManager;
    private DisposableObserver<CollectionSheet> loaddisposabl1eObserver;
    private DisposableObserver<SaveResponse> savedisposabl1eObserver;

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
        if (loaddisposabl1eObserver != null) loaddisposabl1eObserver.dispose();
    }

    public void loadCollectionSheet(long id, Payload payload) {
        checkViewAttached();
        if (loaddisposabl1eObserver != null) loaddisposabl1eObserver.dispose();
        loaddisposabl1eObserver = mDataManager.getCollectionSheet(id, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<CollectionSheet>() {
                    @Override
                    public void onComplete() {

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
        if (savedisposabl1eObserver != null) savedisposabl1eObserver.dispose();
        savedisposabl1eObserver = mDataManager.saveCollectionSheetAsync(id, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<SaveResponse>() {
                    @Override
                    public void onComplete() {

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
