package com.mifos.feature.about

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun MifosWebView(
    htmlContent: String,
    onLoadingChange: (isLoading: Boolean) -> Unit,
    modifier: Modifier,
) {
}