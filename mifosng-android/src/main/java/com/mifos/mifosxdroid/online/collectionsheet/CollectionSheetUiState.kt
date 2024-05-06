package com.mifos.mifosxdroid.online.collectionsheet

import com.mifos.core.objects.db.CollectionSheet
import com.mifos.core.objects.response.SaveResponse
import retrofit2.HttpException

/**
 * Created by Aditya Gupta on 10/08/23.
 */
sealed class CollectionSheetUiState {

    data class ShowFetchingError(val message: String) : CollectionSheetUiState()

    data class ShowCollectionSheet(val collectionSheet: CollectionSheet) : CollectionSheetUiState()

    data class ShowFailedToSaveCollectionSheet(val e: HttpException) : CollectionSheetUiState()

    data class ShowCollectionSheetSuccessfullySaved(val saveResponse: SaveResponse?) :
        CollectionSheetUiState()
}
