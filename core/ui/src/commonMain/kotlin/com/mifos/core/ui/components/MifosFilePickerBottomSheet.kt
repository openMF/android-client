/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.ui.components

import androidclient.core.ui.generated.resources.Res
import androidclient.core.ui.generated.resources.file_picker_bottom_sheet_files
import androidclient.core.ui.generated.resources.file_picker_bottom_sheet_gallery
import androidclient.core.ui.generated.resources.file_picker_bottom_sheet_more
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.component.MifosBottomSheet
import com.mifos.core.designsystem.component.MifosBottomSheetOptionItem
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.DesignToken
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MifosFilePickerBottomSheet(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onFilesClick: () -> Unit,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit,
) {
    MifosBottomSheet(
        onDismiss = onDismiss,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = DesignToken.padding.large,
                    end = DesignToken.padding.large,
                    bottom = DesignToken.padding.extraLarge,
                ),
        ) {
            MifosBottomSheetOptionItem(
                label = stringResource(Res.string.file_picker_bottom_sheet_gallery),
                icon = MifosIcons.Gallery,
                onClick = onGalleryClick,
            )
            MifosBottomSheetOptionItem(
                label = stringResource(Res.string.file_picker_bottom_sheet_files),
                icon = MifosIcons.PickDocument,
                onClick = onFilesClick,
            )
            MifosBottomSheetOptionItem(
                label = stringResource(Res.string.file_picker_bottom_sheet_more),
                icon = MifosIcons.MoreHoriz,
                onClick = onMoreClick,
            )
        }
    }
}

@Preview
@Composable
fun PreviewMifosFilePickerBottomSheet() {
    MaterialTheme {
        MifosFilePickerBottomSheet(
            {},
            {},
            {},
        ) {}
    }
}
