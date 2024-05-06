/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.services

import com.mifos.core.model.APIEndPoint
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.noncore.Document
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import rx.Observable

/**
 * @author fomenkoo
 */
interface DocumentService {
    @GET("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS)
    fun getDocuments(
        @Path("entityType") entityType: String?,
        @Path("entityId") entityId: Int
    ): Observable<List<Document>>

    /**
     * @param entityType              - Type for which document is being uploaded (Client, Loan
     * or Savings etc)
     * @param entityId                - Id of Entity
     * @param nameOfDocument          - Document Name
     * @param description             - Mandatory - Document Description
     * @param typedFile               - Mandatory
     */
    @POST("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS)
    @Multipart
    fun createDocument(
        @Path("entityType") entityType: String?,
        @Path("entityId") entityId: Int,
        @Part("name") nameOfDocument: String?,
        @Part("description") description: String?,
        @Part typedFile: MultipartBody.Part?
    ): Observable<GenericResponse>

    /**
     * This Service is for downloading the Document with EntityType and EntityId and Document Id
     * Rest End Point :
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}/attachment
     *
     * @param entityType    - Type for which document is being uploaded (Client, Loan
     * or Savings etc)
     * @param entityId      - Id of Entity
     * @param documentId    - Document Id
     * @return ResponseBody
     */
    @GET("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}/attachment")
    fun downloadDocument(
        @Path("entityType") entityType: String?,
        @Path("entityId") entityId: Int,
        @Path("documentId") documentId: Int
    ): Observable<ResponseBody>

    /**
     * This Service is for Deleting the Document with EntityType and EntityId and Document Id.
     * Rest End Point :
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}
     *
     * @param entityType    - Type for which document is being uploaded (Client, Loan
     * or Savings etc)
     * @param entityId      - Id of Entity
     * @param documentId    - Document Id
     * @return
     */
    @DELETE("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}")
    fun removeDocument(
        @Path("entityType") entityType: String?,
        @Path("entityId") entityId: Int,
        @Path("documentId") documentId: Int
    ): Observable<GenericResponse>

    /**
     * This Service for Updating the Document with EntityType and EntityId and Document Id.
     * Rest End Point :
     * PUT
     * https://demo.openmf.org/fineract-provider/api/v1/{entityType}/{entityId}/documents/
     * {documentId}
     *
     * @param entityType              - Type for which document is being uploaded (Client, Loan
     * or Savings etc)
     * @param entityId                - Id of Entity
     * @param documentId              - Id of document
     * @param nameOfDocument          - Document Name
     * @param description             - Mandatory - Document Description
     * @param typedFile               - Mandatory
     */
    @PUT("{entityType}/{entityId}/" + APIEndPoint.DOCUMENTS + "/{documentId}")
    @Multipart
    fun updateDocument(
        @Path("entityType") entityType: String?,
        @Path("entityId") entityId: Int,
        @Path("documentId") documentId: Int,
        @Part("name") nameOfDocument: String?,
        @Part("description") description: String?,
        @Part typedFile: MultipartBody.Part?
    ): Observable<GenericResponse>
}