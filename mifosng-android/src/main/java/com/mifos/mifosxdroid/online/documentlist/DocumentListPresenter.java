package com.mifos.mifosxdroid.online.documentlist;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.noncore.Document;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public class DocumentListPresenter extends BasePresenter<DocumentListMvpView> {


    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public DocumentListPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(DocumentListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadDocumentList(String type, int id) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getDocumentsList(type, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Document>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to fetch documents");
                    }

                    @Override
                    public void onNext(List<Document> documents) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showDocumentList(documents);
                    }
                });
    }
}
