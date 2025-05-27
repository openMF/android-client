/*
 * Copyright 2025 Mifos Initiative
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
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.aboutItemTextStyle
import com.mifos.core.designsystem.theme.aboutItemTextStyleBold
import com.mifos.core.ui.util.ShareUtils
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AboutScreen(
    onBackPressed: () -> Unit,
) {
    MifosScaffold(
        title = stringResource(Res.string.feature_about),
        onBackPressed = onBackPressed,
        snackbarHostState = remember { SnackbarHostState() },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        modifier = Modifier.size(100.dp).align(Alignment.Center),
                        painter = painterResource(Res.drawable.feature_about_ic_launcher),
                        contentDescription = "App icon",
                    )
                }

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
                            ShareUtils.openUrl("https://github.com/openMF/android-client/graphs/contributors")
                        },
                    text = stringResource(Res.string.feature_about_mifos),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
            items(aboutItems) { about ->
                AboutCardItem(about = about) {
                    when (it) {
                        AboutItems.CONTRIBUTIONS -> ShareUtils.openUrl("https://github.com/openMF/android-client/graphs/contributors")
                        AboutItems.APP_VERSION -> ShareUtils.openAppInfo()
                        AboutItems.OFFICIAL_WEBSITE -> ShareUtils.openUrl("https://openmf.github.io/mobileapps.github.io/")
                        AboutItems.TWITTER -> ShareUtils.openUrl("https://twitter.com/mifos")
                        AboutItems.SOURCE_CODE -> ShareUtils.openUrl("https://github.com/openMF/android-client")
                        AboutItems.LICENSE -> ShareUtils.openUrl("https://github.com/openMF/android-client/blob/master/LICENSE.md")
                    }
                }
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
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(0.dp),
        onClick = { onOptionClick(about.id) },
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            about.icon?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = stringResource(about.title),
                )
            }
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = stringResource(about.title),
                    style = MaterialTheme.typography.titleMedium,
                )
                about.subtitle?.let {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        text = stringResource(it),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
