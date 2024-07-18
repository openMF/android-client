@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.White

@Composable
fun MifosScaffold(
    modifier: Modifier = Modifier,
    isAppBarPresent: Boolean = true,
    icon: ImageVector? = null,
    title: String? = null,
    onBackPressed: () -> Unit = {},
    actions: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState?,
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {

    Scaffold(
        modifier = modifier,
        topBar = {
            if (isAppBarPresent) {
                TopAppBar(
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = White),
                    navigationIcon = {
                        if (icon != null) {
                            IconButton(
                                onClick = { onBackPressed() },
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = Black,
                                )
                            }
                        }
                    },
                    title = {
                        title?.let {
                            Text(
                                text = it,
                                style = TextStyle(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontStyle = FontStyle.Normal
                                ),
                                color = Black,
                                textAlign = TextAlign.Start
                            )
                        }
                    },
                    actions = { actions() }
                )
            }
        },
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } },
        containerColor = White,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
    ) { padding ->
        content(padding)
    }
}

@Composable
fun MifosScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = Color.White,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = bottomBar,
        containerColor = containerColor,
        contentColor = contentColor,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        content(padding)
    }
}