package com.mifos.feature.client.clientAddDocuments

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.mifos.core.common.utils.DataState
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.ui.components.MifosFilePickerBottomSheet
import kotlinx.coroutines.flow.Flow


@Composable
fun DocumentPreviewScreen(
    file: Flow<DataState<ByteArray>>,
    canUpdateDocument: Boolean = false,
    onBack: ()-> Unit,
    onSubmit: () -> Unit,
    onUploadFromGallery: () -> Unit,
    onUploadFromFiles: () -> Unit,
    onClickMoreOptions: () -> Unit,
    onUploadSuccess: () -> Unit,
    onUploadError: (Exception) -> Unit,
){
    var openBottomSheet by remember {
        mutableStateOf(true)
    }

    MifosScaffold(
        onBackPressed = {},
        bottomBar = {
            if(openBottomSheet){
                MifosFilePickerBottomSheet(
                    onDismiss = {
                        openBottomSheet=false
                    },
                    onGalleryClick = onUploadFromGallery,
                    onFilesClick = onUploadFromFiles,
                    onMoreClick = onClickMoreOptions,
                )
            }
        }
    ){

    }

}