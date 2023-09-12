package com.mifos.mifosxdroid.dialogfragments.identifierdialog

import com.mifos.api.datamanager.DataManagerClient
import com.mifos.objects.noncore.IdentifierCreationResponse
import com.mifos.objects.noncore.IdentifierPayload
import com.mifos.objects.noncore.IdentifierTemplate
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 16/08/23.
 */
class IdentifierDialogRepositoryImp @Inject constructor(private val dataManagerClient: DataManagerClient) :
    IdentifierDialogRepository {

    override fun getClientIdentifierTemplate(clientId: Int): Observable<IdentifierTemplate> {
        return dataManagerClient.getClientIdentifierTemplate(clientId)
    }

    override fun createClientIdentifier(
        clientId: Int,
        identifierPayload: IdentifierPayload?
    ): Observable<IdentifierCreationResponse> {
        return dataManagerClient.createClientIdentifier(clientId, identifierPayload)
    }
}