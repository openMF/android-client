/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.GenericResponse;
import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.noncore.Document;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

/**
 * @author fomenkoo
 */
public interface DocumentService {

    @GET("/{entityType}/{entityId}" + APIEndPoint.DOCUMENTS)
    void getListOfDocuments(@Path("entityType") String entityType,
                            @Path("entityId") int entityId,
                            Callback<List<Document>> documentListCallback);

    /**
     * @param entityType              - Type for which document is being uploaded (Client, Loan or Savings etc)
     * @param entityId                - Id of Entity
     * @param nameOfDocument          - Document Name
     * @param description             - Mandatory - Document Description
     * @param typedFile               - Mandatory
     * @param genericResponseCallback - Response Callback
     */
    @POST("/{entityType}/{entityId}" + APIEndPoint.DOCUMENTS)
    @Multipart
    void createDocument(@Path("entityType") String entityType,
                        @Path("entityId") int entityId,
                        @Part("name") String nameOfDocument,
                        @Part("description") String description,
                        @Part("file") TypedFile typedFile,
                        Callback<GenericResponse> genericResponseCallback);
}
