package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.GenericResponse;
import com.mifos.objects.noncore.Document;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MultipartBody;
import rx.Observable;

/**
 * Created by Rajan Maurya on 02/09/16.
 */
@Singleton
public class DataManagerDocument {

    public final BaseApiManager mBaseApiManager;

    @Inject
    public DataManagerDocument(BaseApiManager baseApiManager) {
        mBaseApiManager = baseApiManager;
    }


    public Observable<List<Document>> getDocumentsList(String type, int id) {
        return mBaseApiManager.getDocumentApi().getListOfDocuments(type, id);
    }

    public Observable<GenericResponse> createDocument(
            String type, int id, String name, String desc, MultipartBody.Part file) {
        return mBaseApiManager.getDocumentApi().createDocument(type, id, name, desc, file);
    }
}
