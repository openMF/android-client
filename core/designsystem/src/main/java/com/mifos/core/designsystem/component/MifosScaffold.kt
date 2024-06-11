@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.mifos.core.designsystem.theme.White

@Composable
fun MifosScaffold(
    topBar: @Composable () -> Unit,
    snackbarHostState: SnackbarHostState?,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {

    Scaffold(
        topBar = topBar,
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } },
        containerColor = White,
        bottomBar = bottomBar
    ) { padding ->
        content(padding)
    }
}