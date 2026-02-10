/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.centerList.ui

@androidx.compose.runtime.Composable
actual fun CenterListContent(
    state: com.mifos.feature.center.centerList.ui.CenterListUiState,
    isInSelectionMode: Boolean,
    selectedItems: com.mifos.feature.center.centerList.ui.SelectedItemsState,
    onRefresh: () -> Unit,
    onCenterSelect: (Int) -> Unit,
    modifier: androidx.compose.ui.Modifier,
) {
}
