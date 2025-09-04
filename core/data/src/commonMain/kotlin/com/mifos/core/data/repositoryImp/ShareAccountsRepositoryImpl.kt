package com.mifos.core.data.repositoryImp

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mifos.core.common.utils.Page
import com.mifos.core.data.pagingSource.ShareAccountsPagingSource
import com.mifos.core.data.repository.ShareAccountsRepository
import com.mifos.core.model.objects.account.share.ShareAccounts
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.datamanager.DataManagerShare
import kotlinx.coroutines.flow.Flow

class ShareAccountsRepositoryImpl(
    private val dataManagerShare: DataManagerShare
) : ShareAccountsRepository {

    override fun getAllShareAccounts(): Flow<PagingData<ShareAccounts>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            pagingSourceFactory = {
                ShareAccountsPagingSource(dataManagerShare)
            }
        ).flow
    }
}