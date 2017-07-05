package com.mifos.api.datamanager;


import com.mifos.api.BaseApiManager;
import com.mifos.api.GenericResponse;
import com.mifos.api.model.IndividualCollectionSheetPayload;
import com.mifos.api.model.RequestCollectionSheetPayload;
import com.mifos.objects.collectionsheet.IndividualCollectionSheet;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Tarun on 22-07-2017.
 */

@Singleton
public class DataManagerCollectionSheet {

    private final BaseApiManager mBaseApiManager;

    @Inject
    public DataManagerCollectionSheet(BaseApiManager baseApiManager) {
        mBaseApiManager = baseApiManager;
    }

    /**
     * Individual CollectionSheet API
     */

    public Observable<IndividualCollectionSheet> getIndividualCollectionSheet(
            RequestCollectionSheetPayload payload) {
        return mBaseApiManager.getCollectionSheetApi().getIndividualCollectionSheet(payload);
    }

    public Observable<GenericResponse> saveIndividualCollectionSheet(
            IndividualCollectionSheetPayload payload) {
        return mBaseApiManager.getCollectionSheetApi().saveindividualCollectionSheet(payload);
    }

}
