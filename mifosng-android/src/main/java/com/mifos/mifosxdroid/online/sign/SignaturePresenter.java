package com.mifos.mifosxdroid.online.sign;

import com.mifos.api.GenericResponse;
import com.mifos.api.datamanager.DataManagerDocument;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.base.BasePresenter;

import java.io.File;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Tarun on 29-06-2017.
 */

public class SignaturePresenter extends BasePresenter<SignatureMvpView> {
    private final DataManagerDocument mDataManagerDocument;
    private CompositeSubscription mSubscriptions;

    @Inject
    public SignaturePresenter(DataManagerDocument dataManagerDocument) {
        mDataManagerDocument = dataManagerDocument;
        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void attachView(SignatureMvpView mvpView) {
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
                        getMvpView().showError(R.string.failed_to_upload_document);
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showSignatureUploadedSuccessfully(genericResponse);
                    }
                }));
    }

    private MultipartBody.Part getRequestFileBody(File file) {
        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData("file", file.getName(), requestFile);
    }

}
