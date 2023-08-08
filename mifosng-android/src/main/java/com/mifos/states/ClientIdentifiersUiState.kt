package com.mifos.states

import com.mifos.objects.noncore.Identifier

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class ClientIdentifiersUiState {

    object ShowProgressbar : ClientIdentifiersUiState()

    data class ShowFetchingError(val message: Int) : ClientIdentifiersUiState()

    data class ShowClientIdentifiers(val identifiers: List<Identifier>) : ClientIdentifiersUiState()

    data class IdentifierDeletedSuccessfully(val genericResponse: Int) : ClientIdentifiersUiState()
}
