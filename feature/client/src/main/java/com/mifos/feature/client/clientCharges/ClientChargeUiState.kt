package com.mifos.feature.client.clientCharges

import androidx.paging.PagingData
import com.mifos.core.objects.client.Charges
import kotlinx.coroutines.flow.Flow

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class ClientChargeUiState {

    data object Loading : ClientChargeUiState()

    data class Error(val message: Int) : ClientChargeUiState()

    data class ChargesList(val chargesPage: Flow<PagingData<Charges>>) : ClientChargeUiState()
}
