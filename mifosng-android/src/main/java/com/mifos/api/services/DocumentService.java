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
import okhttp3.ResponseBody;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface DocumentService {

    @GET("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS)
    Observable<List<Document>> getDocuments(@Path("entityType") String entityType,
                                            @Path("entityId") int entityId);

    /**
     * @param entityType              - Type for which document is being uploaded (Client, Loan
     *                                or Savings etc)
     * @param entityId                - Id of Entity
     * @param nameOfDocument          - Document Name
     * @param description             - Mandatory - Document Description
     * @param typedFile               - Mandatory
     */
    @POST("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS)
    @Multipart
    Observable<GenericResponse> createDocument(@Path("entityType") String entityType,
                                               @Path("entityId") int entityId,
                                               @Part("name") String nameOfDocument,
                                               @Part("description") String description,
                                               @Part() MultipartBody.Part typedFile);


    /**
     * This Service is for downloading the Document with EntityType and EntityId and Document Id
     * Rest End Point :
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}/attachment
     *
     * @param entityType    - Type for which document is being uploaded (Client, Loan
     *                                or Savings etc)
     * @param entityId      - Id of Entity
     * @param documentId    - Document Id
     * @return ResponseBody
     */
    @GET("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}/attachment")
    Observable<ResponseBody> downloadDocument(@Path("entityType") String entityType,
                                              @Path("entityId") int entityId,
                                              @Path("documentId") int documentId);

    /**
     * This Service is for Deleting the Document with EntityType and EntityId and Document Id.
     * Rest End Point :
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}
     *
     * @param entityType    - Type for which document is being uploaded (Client, Loan
     *                                or Savings etc)
     * @param entityId      - Id of Entity
     * @param documentId    - Document Id
     * @return
     */
    @DELETE("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}")
    Observable<GenericResponse> removeDocument(@Path("entityType") String entityType,
                                               @Path("entityId") int entityId,
                                               @Path("documentId") int documentId);

    /**
     * This Service for Updating the Document with EntityType and EntityId and Document Id.
     * Rest End Point :
     * PUT
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}
     *
     * @param entityType              - Type for which document is being uploaded (Client, Loan
     *                                or Savings etc)
     * @param entityId                - Id of Entity
     * @param documentId              - Id of document
     * @param nameOfDocument          - Document Name
     * @param description             - Mandatory - Document Description
     * @param typedFile               - Mandatory
     */
    @PUT("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}")
    @Multipart
    Observable<GenericResponse> updateDocument(@Path("entityType") String entityType,
                                               @Path("entityId") int entityId,
                                               @Path("documentId") int documentId,
                                               @Part("name") String nameOfDocument,
                                               @Part("description") String description,
                                               @Part() MultipartBody.Part typedFile);
}
