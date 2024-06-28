package com.mifos.core.data.repository

import androidx.paging.PagingData
import com.mifos.core.objects.client.Charges
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface ClientChargeRepository {

    fun getClientCharges(clientId: Int): Flow<PagingData<Charges>>

}