package com.mifos.mifosxdroid.dialogfragments.documentdialog;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerDocument;
import com.mifos.mifosxdroid.base.BasePresenter;

import java.io.File;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody.Part;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 8/6/16.
 */
public class DocumentDialogPresenter extends BasePresenter<DocumentDialogMvpView> {

    private final DataManagerDocument mDataManagerDocument;
    private Subscription mSubscription;

    @Inject
    public DocumentDialogPresenter(DataManagerDocument dataManagerDocument) {
        mDataManagerDocument = dataManagerDocument;
    }

    @Override
    public void attachView(DocumentDialogMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void createDocument(String type, int id, String name, String desc, File file) {
        checkViewAttached();
        getMvpView().showProgressbar(true);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        Part body = Part.createFormData("file", file.getName(), requestFile);

        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManagerDocument.createDocument(type, id, name, desc, body)
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
                        getMvpView().showError("Upload Failed");
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showDocumentedCreatedSuccessfully(genericResponse);
                    }
                });
    }

}
