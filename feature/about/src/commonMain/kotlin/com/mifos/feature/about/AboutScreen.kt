/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.about

import androidclient.feature.about.generated.resources.Res
import androidclient.feature.about.generated.resources.feature_about
import androidclient.feature.about.generated.resources.feature_about_app
import androidclient.feature.about.generated.resources.feature_about_ic_launcher
import androidclient.feature.about.generated.resources.feature_about_mifos
import androidclient.feature.about.generated.resources.feature_about_mifos_x_droid
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.theme.aboutItemTextStyle
import com.mifos.core.designsystem.theme.aboutItemTextStyleBold
import com.mifos.core.ui.util.ShareUtils
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AboutScreen(
    onBackPressed: () -> Unit,
    viewModel: AboutViewModel = koinViewModel(),
) {
    val state by viewModel.aboutUiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.getAboutOptions()
    }

    AboutScreen(
        state = state,
        onBackPressed = onBackPressed,
        onRetry = { viewModel.getAboutOptions() },
        onOptionClick = {
            when (it) {
                AboutItems.CONTRIBUTIONS -> ShareUtils.openUrl("https://github.com/openMF/android-client/graphs/contributors")

                AboutItems.APP_VERSION -> Unit

                AboutItems.OFFICIAL_WEBSITE -> ShareUtils.openUrl("https://openmf.github.io/mobileapps.github.io/")

                AboutItems.TWITTER -> ShareUtils.openUrl("https://twitter.com/mifos")

                AboutItems.SOURCE_CODE -> ShareUtils.openUrl("https://github.com/openMF/android-client")

                AboutItems.LICENSE -> ShareUtils.openUrl("https://github.com/openMF/android-client/blob/master/LICENSE.md")
            }
        },
    )
}

@Composable
internal fun AboutScreen(
    state: AboutUiState,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    onOptionClick: (AboutItems) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    MifosScaffold(
        title = stringResource(Res.string.feature_about),
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (state) {
                is AboutUiState.AboutOptions -> {
                    AboutScreenContent(
                        aboutOptions = state.aboutOptions,
                        onOptionClick = onOptionClick,
                    )
                }

                is AboutUiState.Error -> MifosSweetError(
                    message = stringResource(state.message),
                ) {
                    onRetry()
                }

                is AboutUiState.Loading -> MifosCircularProgress()
            }
        }
    }
}

@Composable
private fun AboutScreenContent(
    aboutOptions: List<AboutItem>,
    onOptionClick: (AboutItems) -> Unit,
) {
    Column {
        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(Res.drawable.feature_about_ic_launcher),
            contentDescription = null,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(Res.string.feature_about_mifos_x_droid),
            style = aboutItemTextStyleBold,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            text = stringResource(Res.string.feature_about_app),
            style = aboutItemTextStyle,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    onOptionClick(AboutItems.CONTRIBUTIONS)
                },
            text = stringResource(Res.string.feature_about_mifos),
            style = TextStyle(
                fontSize = 16.sp,
            ),
            color = Color.Blue,
            textAlign = TextAlign.Center,
        )
        LazyColumn {
            items(aboutOptions) { about ->
                AboutCardItem(about = about, onOptionClick = onOptionClick)
            }
        }
    }
}

@Composable
private fun AboutCardItem(
    about: AboutItem,
    onOptionClick: (AboutItems) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 8.dp,
        ),
        elevation = CardDefaults.elevatedCardElevation(0.dp),

        colors = CardDefaults.elevatedCardColors(containerColor = about.color ?: MaterialTheme.colorScheme.surface),
        onClick = {
            onOptionClick(about.id)
        },
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            about.icon?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = null,
                )
            }
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    text = stringResource(about.title),
                    style = MaterialTheme.typography.titleMedium,
                    color = Black,
                )
                about.subtitle?.let {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        text = stringResource(it),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Black,
                    )
                }
            }
        }
    }
}
