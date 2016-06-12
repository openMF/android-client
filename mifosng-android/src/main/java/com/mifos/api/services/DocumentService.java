/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.GenericResponse;
import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.noncore.Document;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface DocumentService {

    @GET("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS)
    Observable<List<Document>> getListOfDocuments(@Path("entityType") String entityType,
                                                  @Path("entityId") int entityId);

    /**
     * @param entityType              - Type for which document is being uploaded (Client, Loan
     *                                or Savings etc)
     * @param entityId                - Id of Entity
     * @param nameOfDocument          - Document Name
     * @param description             - Mandatory - Document Description
     * @param typedFile               - Mandatory
     * @param genericResponseCallback - Response Callback
     */
    @POST("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS)
    @Multipart
    Observable<GenericResponse> createDocument(@Path("entityType") String entityType,
                                               @Path("entityId") int entityId,
                                               @Part("name") String nameOfDocument,
                                               @Part("description") String description,
                                               @Part("file") MultipartBody.Part typedFile);
}
