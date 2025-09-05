package com.mifos.feature.client.clientAddDocuments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.mifos.core.data.repository.DocumentDialogRepository
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.ui.util.BaseViewModel
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class ClientAddDocumentViewModel(
    stateHandler: SavedStateHandle,
    private val networkMonitor: NetworkMonitor,
    private val documentDialogRepository: DocumentDialogRepository,
) : BaseViewModel
<
        ClientAddDocumentState,
        ClientAddDocumentEvents,
        ClientAddDocumentAction,
        >(
    initialState = ClientAddDocumentState(),
) {

    private val clientId = stateHandler.toRoute<ClientAddDocumentRoute>().clientId
    private val documentId = stateHandler.toRoute<ClientAddDocumentRoute>().documentId
    private val entityType = stateHandler.toRoute<ClientAddDocumentRoute>().entityType

    override fun handleAction(action: ClientAddDocumentAction) {
        TODO("Not yet implemented")
    }

    private suspend fun uploadDocument(){
        documentDialogRepository.createDocument(
            entityType = entityType,
            entityId = clientId,
            file = Any() as MultiPartFormDataContent
            // TODO("Use correct document Type")
        )
    }
    private suspend fun updateDocument(){
        documentDialogRepository.updateDocument(
            entityType = entityType,
            entityId = clientId,
            documentId = documentId,
            file = Any() as MultiPartFormDataContent
            // TODO("Use correct document Type")
        )
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isConnected ->
                mutableStateFlow.update { it.copy(isNetworkAvailable = isConnected) }
            }
        }
    }

}


data class ClientAddDocumentState(
    val documentId: Int = -1,
    val isNetworkAvailable: Boolean = false,
    val dialogState: DialogState? = null,
    val documentName: String = "",
    val description: String = "",
    val fileName: String = "",
    val document: ByteArray? = null,
) {
    sealed interface DialogState {
        data object Loading : DialogState
        data class Error(val message: String) : DialogState
    }
}

sealed interface ClientAddDocumentAction {
    data object NavigateBack : ClientAddDocumentAction
    data object AddDocument : ClientAddDocumentAction
    data object SubmitDocument : ClientAddDocumentAction
    data object UploadDocument : ClientAddDocumentAction
    data object UploadNewDocument: ClientAddDocumentAction
    data class UpdateName(val text: String) : ClientAddDocumentAction
    data class UpdateDescription(val text: String) : ClientAddDocumentAction
    data object PickFromGallery : ClientAddDocumentAction
    data object PickFromFiles : ClientAddDocumentAction
    data object UseMoreOptions : ClientAddDocumentAction
}

sealed interface ClientAddDocumentEvents {
    data object OnNavigateBack : ClientAddDocumentEvents
    data object SubmitDocument : ClientAddDocumentEvents
    data object ShowDocumentPreviewScreen : ClientAddDocumentEvents
    data object ShowAddDocumentScreen: ClientAddDocumentEvents
}