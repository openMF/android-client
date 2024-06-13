package com.mifos.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mifos.core.designsystem.theme.White

@Composable
fun MifosScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    snackbarHostState: SnackbarHostState?,
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {

    Scaffold(
        modifier = modifier,
        topBar = topBar,
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } },
        containerColor = White,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton
    ) { padding ->
        content(padding)
    }
}