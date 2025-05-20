package com.mifos.feature.about

import androidclient.feature.about.generated.resources.Res
import androidclient.feature.about.generated.resources.feature_about
import androidclient.feature.about.generated.resources.feature_about_app
import androidclient.feature.about.generated.resources.feature_about_ic_launcher
import androidclient.feature.about.generated.resources.feature_about_mifos
import androidclient.feature.about.generated.resources.feature_about_mifos_x_droid
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
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
        Column(
            modifier = Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(Res.drawable.feature_about_ic_launcher),
                contentDescription = "App icon",
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
                        ShareUtils.openUrl("https://github.com/openMF/android-client/graphs/contributors")
                    },
                text = stringResource(Res.string.feature_about_mifos),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
            LazyColumn {
                items(aboutItems) { about ->
                    AboutCardItem(about = about) {
                        when (it) {
                            AboutItems.CONTRIBUTIONS -> ShareUtils.openUrl("https://github.com/openMF/android-client/graphs/contributors")
                            AboutItems.APP_VERSION -> Unit
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
