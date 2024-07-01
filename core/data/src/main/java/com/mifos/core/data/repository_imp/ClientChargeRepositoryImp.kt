package com.mifos.core.data.repository_imp

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mifos.core.data.paging_source.ClientChargesPagingSource
import com.mifos.core.data.repository.ClientChargeRepository
import com.mifos.core.network.datamanager.DataManagerCharge
import com.mifos.core.objects.client.Charges
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class ClientChargeRepositoryImp @Inject constructor(private val dataManagerCharge: DataManagerCharge) :
    ClientChargeRepository {

    override fun getClientCharges(
        clientId: Int
    ): Flow<PagingData<Charges>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10
            ), pagingSourceFactory = {
                ClientChargesPagingSource(clientId, dataManagerCharge)
            }
        ).flow
    }
}