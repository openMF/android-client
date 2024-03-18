package com.mifos.mifosxdroid.dialogfragments.identifierdialog

import com.mifos.core.network.datamanager.DataManagerClient
import com.mifos.core.objects.noncore.IdentifierCreationResponse
import com.mifos.core.objects.noncore.IdentifierPayload
import com.mifos.core.objects.noncore.IdentifierTemplate
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