package com.mifos.core.network.datamanager

import com.mifos.core.common.utils.Page
import com.mifos.core.model.objects.account.share.ShareAccounts
import com.mifos.core.network.BaseApiManager

class DataManagerShare(
    private val baseApiManager: BaseApiManager,
) {
    suspend fun getAllShareAccounts(): Page<ShareAccounts> {
        return baseApiManager.shareService.getAllShareAccounts(1)
    }
}