package com.mifos.mifosxdroid.dialogfragments.documentdialogfragment;

import com.mifos.api.DataManager;
import com.mifos.api.GenericResponse;
import com.mifos.mifosxdroid.base.Presenter;

import retrofit.mime.TypedFile;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public class DocumentDialogPresenter implements Presenter<DocumentDialogMvpView> {

    private final DataManager mDatamanager;
    private Subscription mSubscription;
    private DocumentDialogMvpView mDocumentDialogMvpView;

    public DocumentDialogPresenter(DataManager dataManager){
        mDatamanager = dataManager;
    }

    @Override
    public void attachView(DocumentDialogMvpView mvpView) {
        mDocumentDialogMvpView = mvpView;
    }

    @Override
    public void detachView() {
        mDocumentDialogMvpView = null;
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void createDocument(String type, int id, String name, String desc, TypedFile file){
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDatamanager.createDocument(type,id,name,desc,file)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GenericResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mDocumentDialogMvpView.ResponseError("Upload Failed");
                    }

                    @Override
                    public void onNext(GenericResponse genericResponse) {
                        mDocumentDialogMvpView.showDocumentCreationResult(genericResponse);
                    }
                });
    }
}
