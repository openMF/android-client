package com.mifos.feature.center.centerList.ui

import androidx.compose.ui.Modifier

@androidx.compose.runtime.Composable
actual fun CenterListContent(
    state: CenterListUiState,
    isInSelectionMode: Boolean,
    selectedItems: SelectedItemsState,
    onRefresh: () -> Unit,
    onCenterSelect: (Int) -> Unit,
    selectedMode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TODO("Not yet implemented")
}