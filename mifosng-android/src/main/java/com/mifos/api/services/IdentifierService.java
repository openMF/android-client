/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.api.services;

import com.mifos.objects.noncore.Identifier;
import com.mifos.api.GenericResponse;
import com.mifos.api.model.APIEndPoint;

import java.util.List;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * @author fomenkoo
 */
public interface IdentifierService {
    @GET(APIEndPoint.CLIENTS + "/{clientId}" + APIEndPoint.IDENTIFIERS)
    Observable<List<Identifier>> getListOfIdentifiers(@Path("clientId") int clientId);

    @DELETE(APIEndPoint.CLIENTS + "/{clientId}" + APIEndPoint.IDENTIFIERS + "/{identifierId}")
    Observable<GenericResponse> deleteIdentifier(@Path("clientId") int clientId,
                          @Path("identifierId") int identifierId);

}
