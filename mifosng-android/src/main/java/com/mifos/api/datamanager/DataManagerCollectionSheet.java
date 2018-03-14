package com.mifos.api.datamanager;


import com.mifos.api.BaseApiManager;
import com.mifos.api.GenericResponse;
import com.mifos.api.model.IndividualCollectionSheetPayload;
import com.mifos.api.model.RequestCollectionSheetPayload;
import com.mifos.objects.collectionsheet.CenterDetail;
import com.mifos.objects.collectionsheet.CollectionSheetPayload;
import com.mifos.objects.collectionsheet.IndividualCollectionSheet;
import com.mifos.objects.collectionsheet.CollectionSheetResponse;
import com.mifos.objects.collectionsheet.CollectionSheetRequestPayload;
import com.mifos.objects.collectionsheet.ProductiveCollectionSheetPayload;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.List;

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

    /**
     *  Productive CollectionSheet API
     */

    public Observable<List<CenterDetail>> fetchCenterDetails(
            String format, String locale, String meetingDate, int officeId, int staffId) {
        return mBaseApiManager.getCollectionSheetApi().fetchCenterDetails(
                format, locale, meetingDate, officeId, staffId);
    }

    public Observable<CollectionSheetResponse> fetchProductiveCollectionSheet(
            int centerId, CollectionSheetRequestPayload payload) {
        return mBaseApiManager.getCollectionSheetApi().fetchProductiveSheet(centerId, payload);
    }

    public Observable<GenericResponse> submitProductiveSheet(
            int centerId, ProductiveCollectionSheetPayload payload) {
        return mBaseApiManager.getCollectionSheetApi().submitProductiveSheet(centerId, payload);
    }

    /**
     * CollectionSheet API
     */
    public Observable<CollectionSheetResponse> fetchCollectionSheet(
            int groupId, CollectionSheetRequestPayload payload) {
        return mBaseApiManager.getCollectionSheetApi().fetchCollectionSheet(groupId, payload);
    }

    public Observable<GenericResponse> submitCollectionSheet(
            int groupId, CollectionSheetPayload payload) {
        return mBaseApiManager.getCollectionSheetApi().submitCollectionSheet(groupId, payload);
    }

    /**
     * Associated groups API
     */
    public Observable<CenterWithAssociations> fetchGroupsAssociatedWithCenter(int centerId) {
        return mBaseApiManager.getCollectionSheetApi().fetchGroupsAssociatedWithCenter(centerId);
    }
}
