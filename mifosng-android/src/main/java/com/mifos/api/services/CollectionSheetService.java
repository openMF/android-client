package com.mifos.api.services;

import com.mifos.api.GenericResponse;
import com.mifos.api.model.APIEndPoint;
import com.mifos.api.model.IndividualCollectionSheetPayload;
import com.mifos.api.model.RequestCollectionSheetPayload;
import com.mifos.objects.collectionsheet.IndividualCollectionSheet;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Tarun on 06-07-2017.
 */

public interface CollectionSheetService {


    @POST(APIEndPoint.COLLECTIONSHEET + "?command=generateCollectionSheet")
    Observable<IndividualCollectionSheet> getIndividualCollectionSheet(
            @Body RequestCollectionSheetPayload payload);

    @POST(APIEndPoint.COLLECTIONSHEET + "?command=saveCollectionSheet")
    Observable<GenericResponse> saveindividualCollectionSheet(
            @Body IndividualCollectionSheetPayload payload);
}
