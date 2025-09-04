package com.mifos.core.network.services

import com.mifos.core.common.utils.Page
import com.mifos.core.model.objects.account.share.ShareAccounts
import com.mifos.room.basemodel.APIEndPoint
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.coroutines.flow.Flow

interface ShareService {

    @GET("accounts/{clientId}" )
    suspend fun getAllShareAccounts(
        @Path("clientId") clientId: Int
    ): Page<ShareAccounts>

}