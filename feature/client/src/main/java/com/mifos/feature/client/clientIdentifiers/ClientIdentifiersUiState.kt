package com.mifos.feature.client.clientIdentifiers

import com.mifos.core.objects.noncore.Identifier

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class ClientIdentifiersUiState {

    data object Loading : ClientIdentifiersUiState()

    data class Error(val message: Int) : ClientIdentifiersUiState()

    data class ClientIdentifiers(val identifiers: List<Identifier>) : ClientIdentifiersUiState()

    data object IdentifierDeletedSuccessfully : ClientIdentifiersUiState()
}