package com.mifos.mifosxdroid.online.documentlist;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerDocument;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.noncore.Document;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public class DocumentListPresenter extends BasePresenter<DocumentListMvpView> {


    private final DataManagerDocument mDataManagerDocument;
    private CompositeDisposable compositeDisposable;

    @Inject
    public DocumentListPresenter(DataManagerDocument dataManagerDocument) {
        mDataManagerDocument = dataManagerDocument;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void attachView(DocumentListMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposable.clear();
    }

    public void loadDocumentList(String type, int id) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManagerDocument.getDocumentsList(type, id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<Document>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(R.string.failed_to_fetch_documents);
                    }

                    @Override
                    public void onNext(List<Document> documents) {
                        getMvpView().showProgressbar(false);
                        if (!documents.isEmpty()) {
                            getMvpView().showDocumentList(documents);
                        } else {
                            getMvpView().showEmptyDocuments();
                        }

                    }
                }));
    }

    public void downloadDocument(String entityType, int entityId, int documentId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManagerDocument.downloadDocument(entityType, entityId, documentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(R.string.failed_to_fetch_documents);
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showDocumentFetchSuccessfully(responseBody);
                    }
                })
        );
    }

    public void removeDocument(String entityType, int entityId, int documentId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManagerDocument.removeDocument(entityType, entityId, documentId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<GenericResponse>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError(R.string.failed_to_remove_document);
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showDocumentRemovedSuccessfully();
                    }
                })
        );
    }

}
