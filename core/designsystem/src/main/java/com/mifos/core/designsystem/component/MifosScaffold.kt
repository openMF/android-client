@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
    icon: ImageVector?,
    title: String?,
    onBackPressed: () -> Unit,
    actions: @Composable () -> Unit,
    snackbarHostState: SnackbarHostState?,
    bottomBar: @Composable () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {

    Scaffold(
        topBar = {
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
                                fontSize = 24.sp,
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
        },
        snackbarHost = { snackbarHostState?.let { SnackbarHost(it) } },
        containerColor = White,
        bottomBar = bottomBar
    ) { padding ->
        content(padding)
    }

}