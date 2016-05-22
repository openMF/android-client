/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.api.GenericResponse;
import com.mifos.api.model.APIEndPoint;
import com.mifos.objects.noncore.Identifier;

import java.util.List;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * @author fomenkoo
 */
public interface IdentifierService {
    @GET(APIEndPoint.CLIENTS + "/{clientId}" + APIEndPoint.IDENTIFIERS)
    void getListOfIdentifiers(@Path("clientId") int clientId,
                              Callback<List<Identifier>> identifierListCallback);

    @DELETE(APIEndPoint.CLIENTS + "/{clientId}" + APIEndPoint.IDENTIFIERS + "/{identifierId}")
    void deleteIdentifier(@Path("clientId") int clientId,
                          @Path("identifierId") int identifierId,
                          Callback<GenericResponse> genericResponseCallback);

}
