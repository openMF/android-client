package com.mifos.mifosxdroid.online.clientidentifiers

import com.mifos.core.objects.noncore.Identifier

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class ClientIdentifiersUiState {

    data object ShowProgressbar : ClientIdentifiersUiState()

    data class ShowFetchingError(val message: Int) : ClientIdentifiersUiState()

    data class ShowClientIdentifiers(val identifiers: List<Identifier>) : ClientIdentifiersUiState()

    data class IdentifierDeletedSuccessfully(val genericResponse: Int) : ClientIdentifiersUiState()
}
