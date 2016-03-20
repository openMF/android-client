package com.mifos.mifosxdroid.online.documentlistfragment;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.Presenter;
import com.mifos.objects.noncore.Document;
import java.util.List;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public class DocumentListPresenter implements Presenter<DocumentListMvpView> {

    private final DataManager mDataManager;
    public Subscription mSubscription;
    private DocumentListMvpView mDocumentListMvpView;

    public DocumentListPresenter(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void attachView(DocumentListMvpView mvpView) {
        mDocumentListMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mDocumentListMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadDocumentList(String entityType, int entityId){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getListOfDocuments(entityType,entityId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Document>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mDocumentListMvpView.ResponseErrorDocumentList("Failed to Fetch DocumentList");
                    }

                    @Override
                    public void onNext(List<Document> documents) {
                        mDocumentListMvpView.showDocumentList(documents);
                    }
                });

    }
}
