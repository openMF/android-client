package com.mifos.mifosxdroid.dialogfragments.identifierdialog

import com.mifos.core.objects.noncore.IdentifierCreationResponse
import com.mifos.core.objects.noncore.IdentifierPayload
import com.mifos.core.objects.noncore.IdentifierTemplate
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface IdentifierDialogRepository {

    fun getClientIdentifierTemplate(clientId: Int): Observable<IdentifierTemplate>

    fun createClientIdentifier(
        clientId: Int, identifierPayload: IdentifierPayload?
    ): Observable<IdentifierCreationResponse>
}