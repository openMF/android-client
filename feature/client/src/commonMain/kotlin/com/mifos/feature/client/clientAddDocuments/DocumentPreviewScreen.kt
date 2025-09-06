package com.mifos.feature.client.clientAddDocuments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.component.MifosOutlinedButton
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.ui.components.MifosFilePickerBottomSheet
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.nameWithoutExtension


@Composable
fun DocumentPreviewScreen(
    absoluteFilePath: String = "",
    canUpdateDocument: Boolean = false,
    onBack: ()-> Unit,
    onSubmit: (String) -> Unit,
    onUploadFromGallery: () -> Unit,
    onUploadFromFiles: () -> Unit,
    onClickMoreOptions: () -> Unit,
){
    var openBottomSheet by remember {
        mutableStateOf(true)
    }
    val file = PlatformFile(absoluteFilePath)

    MifosScaffold(
        onBackPressed = {},
        bottomBar = {

            MifosFilePickerBottomSheet(
                showBottomSheet = openBottomSheet,
                onDismiss = {
                    openBottomSheet = false
                },
                onGalleryClick = onUploadFromGallery,
                onFilesClick = onUploadFromFiles,
                onMoreClick = onClickMoreOptions,
            )
        },
        modifier = Modifier.fillMaxSize()
    ){paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = DesignToken.padding.large,),
        ) {
            Box(
                modifier = Modifier.weight(1f)
                    .clip(DesignToken.shapes.medium)
                    .border(
                        1.dp,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = DesignToken.shapes.medium
                    ),
            ) {

            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                MifosOutlinedButton(
                    onClick = onBack,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = BorderStroke(
                        1.dp,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = MifosIcons.ArrowBack,
                        "back button",
                        modifier = Modifier.size(DesignToken.sizes.iconMinyMiny)
                    )
                    Spacer(Modifier.height(DesignToken.spacing.medium))
                    Text(
                        "Back",
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

                Spacer(Modifier.height(DesignToken.spacing.small))

                MifosOutlinedButton(
                    onClick = {
                        if (canUpdateDocument) {
                            openBottomSheet = true
                        } else {
                            onSubmit(file.nameWithoutExtension)
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    border = BorderStroke(
                        1.dp,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f)
                ) {
                    Icon(
                        imageVector = MifosIcons.RightTick,
                        "back button",
                        modifier = Modifier.size(DesignToken.sizes.iconMinyMiny),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.height(DesignToken.spacing.medium))
                    Text(
                        "Submit",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

            }
        }

    }

}