package com.mifos.mifosxdroid.dialogfragments.documentdialog;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerDocument;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.utils.MFErrorParser;

import java.io.File;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody.Part;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.plugins.RxJavaPlugins;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public class DocumentDialogPresenter extends BasePresenter<DocumentDialogMvpView> {

    private final DataManagerDocument mDataManagerDocument;
    private CompositeSubscription mSubscriptions;

    @Inject
    public DocumentDialogPresenter(DataManagerDocument dataManagerDocument) {
        mDataManagerDocument = dataManagerDocument;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(DocumentDialogMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        mSubscriptions.clear();
    }

    public void createDocument(String type, int id, String name, String desc, File file) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerDocument
                .createDocument(type, id, name, desc, getRequestFileBody(file))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        String errorMessage;
                        try {
                            if (e instanceof HttpException) {
                                errorMessage = ((HttpException) e).response().errorBody().string();
                                getMvpView().showUploadError(MFErrorParser.parseError(errorMessage)
                                        .getDeveloperMessage());
                            } else {
                                getMvpView().showError(R.string.failed_to_upload_document);
                            }
                        } catch (Throwable throwable) {
                                RxJavaPlugins.getInstance().getErrorHandler()
                                        .handleError(throwable);
                        }
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showDocumentedCreatedSuccessfully(genericResponse);
                    }
                }));
    }

    public void updateDocument(String entityType, int entityId, int documentId,
                               String name, String desc, File file) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        mSubscriptions.add(mDataManagerDocument.updateDocument(entityType, entityId, documentId,
                name, desc, getRequestFileBody(file))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showError(R.string.failed_to_update_document);
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showDocumentUpdatedSuccessfully();
                    }
                })
        );
    }


    private Part getRequestFileBody(File file) {
        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        return Part.createFormData("file", file.getName(), requestFile);
    }

}
