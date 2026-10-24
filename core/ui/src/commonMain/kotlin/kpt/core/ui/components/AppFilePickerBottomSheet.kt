/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kpt.core.base.designsystem.KptTheme
import kpt.core.base.designsystem.theme.KptTheme
import kpt.core.designsystem.component.AppBottomSheet
import kpt.core.designsystem.component.AppBottomSheetOptionItem
import kpt.core.designsystem.icon.AppIcons
import kpt.core.designsystem.theme.DesignToken
import kpt.core.ui.generated.resources.Res
import kpt.core.ui.generated.resources.file_picker_bottom_sheet_files
import kpt.core.ui.generated.resources.file_picker_bottom_sheet_gallery
import kpt.core.ui.generated.resources.file_picker_bottom_sheet_more
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AppFilePickerBottomSheet(
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onFilesClick: () -> Unit,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit,
) {
    AppBottomSheet(
        onDismiss = onDismiss,
        modifier = modifier,
        containerColor = KptTheme.colorScheme.onPrimary,
    ) {
        Row(
            modifier = Modifier
                .padding(
                    start = DesignToken.padding.large,
                    end = DesignToken.padding.large,
                    bottom = DesignToken.padding.extraLarge,
                ),
        ) {
            AppBottomSheetOptionItem(
                label = stringResource(Res.string.file_picker_bottom_sheet_gallery),
                icon = AppIcons.Gallery,
                onClick = onGalleryClick,
            )
            AppBottomSheetOptionItem(
                label = stringResource(Res.string.file_picker_bottom_sheet_files),
                icon = AppIcons.PickDocument,
                onClick = onFilesClick,
            )
            AppBottomSheetOptionItem(
                label = stringResource(Res.string.file_picker_bottom_sheet_more),
                icon = AppIcons.MoreHoriz,
                onClick = onMoreClick,
            )
        }
    }
}

@Preview
@Composable
fun PreviewAppFilePickerBottomSheet() {
    KptTheme {
        AppFilePickerBottomSheet(
            {},
            {},
            {},
        ) {}
    }
}
