package com.mifos.feature.about

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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.White
import com.mifos.core.designsystem.theme.aboutItemTextStyle
import com.mifos.core.designsystem.theme.aboutItemTextStyleBold


@Composable
fun AboutScreen(onBackPressed: () -> Unit) {

    val viewModel: AboutViewModel = hiltViewModel()
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
                AboutItems.CONTRIBUTIONS -> uriHandler.openUri("https://github.com/openMF/android-client/graphs/contributors")

                AboutItems.APP_VERSION -> Unit

                AboutItems.OFFICIAL_WEBSITE -> uriHandler.openUri("https://openmf.github.io/mobileapps.github.io/")

                AboutItems.TWITTER -> uriHandler.openUri("https://twitter.com/mifos")

                AboutItems.SOURCE_CODE -> uriHandler.openUri("https://github.com/openMF/android-client")

                AboutItems.LICENSE -> uriHandler.openUri("https://github.com/openMF/android-client/blob/master/LICENSE.md")
            }
        }
    )
}

@Composable
fun AboutScreen(
    state: AboutUiState,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    onOptionClick: (AboutItems) -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(R.string.feature_about),
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                is AboutUiState.AboutOptions -> {
                    AboutScreenContent(
                        aboutOptions = state.aboutOptions,
                        onOptionClick = onOptionClick
                    )
                }

                is AboutUiState.Error -> MifosSweetError(message = stringResource(id = state.message)) {
                    onRetry()
                }

                is AboutUiState.Loading -> MifosCircularProgress()
            }
        }
    }
}

@Composable
fun AboutScreenContent(
    aboutOptions: List<AboutItem>,
    onOptionClick: (AboutItems) -> Unit
) {
    Image(
        modifier = Modifier.size(100.dp),
        painter = painterResource(id = R.drawable.feature_about_ic_launcher),
        contentDescription = null
    )
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        text = stringResource(id = R.string.feature_about_mifos_x_droid),
        style = aboutItemTextStyleBold
    )
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        text = stringResource(id = R.string.feature_about_app),
        style = aboutItemTextStyle
    )
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                onOptionClick(AboutItems.CONTRIBUTIONS)
            },
        text = stringResource(id = R.string.feature_about_mifos),
        style = TextStyle(
            fontSize = 16.sp
        ),
        color = BluePrimary,
        textAlign = TextAlign.Center
    )
    LazyColumn {
        items(aboutOptions) { about ->
            AboutCardItem(about = about, onOptionClick = onOptionClick)
        }
    }
}

@Composable
fun AboutCardItem(
    about: AboutItem,
    onOptionClick: (AboutItems) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.padding(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 8.dp
        ),
        elevation = CardDefaults.elevatedCardElevation(0.dp),
        colors = CardDefaults.elevatedCardColors(about.color),
        onClick = {
            onOptionClick(about.id)
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            about.icon?.let {
                Icon(
                    painter = painterResource(it),
                    contentDescription = null
                )
            }
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp),
                    text = stringResource(id = about.title),
                    style = TextStyle(
                        fontSize = 16.sp
                    ),
                    color = Black
                )
                about.subtitle?.let {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        text = stringResource(id = it),
                        style = TextStyle(
                            fontSize = 16.sp
                        ),
                        color = Black
                    )
                }
            }
        }
    }
}

class AboutUiStateProvider : PreviewParameterProvider<AboutUiState> {

    override val values: Sequence<AboutUiState>
        get() = sequenceOf(
            AboutUiState.Loading,
            AboutUiState.Error(R.string.feature_about_failed_to_load),
            AboutUiState.AboutOptions(sampleAboutItem)
        )

}


@Preview(showBackground = true)
@Composable
private fun AboutScreenPreview(
    @PreviewParameter(AboutUiStateProvider::class) state: AboutUiState
) {
    AboutScreen(
        state = state,
        onBackPressed = {},
        onRetry = {},
        onOptionClick = {}
    )
}

val sampleAboutItem = List(4) {
    AboutItem(
        icon = R.drawable.feature_about_icon_twitter,
        title = R.string.feature_about_support_twitter,
        subtitle = R.string.feature_about_license_sub,
        color = White,
        id = AboutItems.TWITTER
    )
}