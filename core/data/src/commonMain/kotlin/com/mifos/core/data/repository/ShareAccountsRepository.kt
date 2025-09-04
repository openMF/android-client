package com.mifos.core.data.repository

import androidx.paging.PagingData
import com.mifos.core.common.utils.Page
import com.mifos.core.model.objects.account.share.ShareAccounts
import com.mifos.core.network.BaseApiManager
import kotlinx.coroutines.flow.Flow

interface ShareAccountsRepository {

    fun getAllShareAccounts(): Flow<PagingData<ShareAccounts>>
}