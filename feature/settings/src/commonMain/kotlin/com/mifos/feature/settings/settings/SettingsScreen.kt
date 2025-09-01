/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.settings.settings

import androidclient.feature.settings.generated.resources.Res
import androidclient.feature.settings.generated.resources.feature_settings
import androidclient.feature.settings.generated.resources.feature_settings_change_app_theme
import androidclient.feature.settings.generated.resources.feature_settings_choose_language
import androidclient.feature.settings.generated.resources.feature_settings_languages
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import co.touchlab.kermit.Logger
import com.mifos.core.common.enums.MifosAppLanguage
import com.mifos.core.datastore.model.AppTheme
import com.mifos.core.designsystem.component.MifosRadioButtonDialog
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.UpdateEndpointDialogScreen
import com.mifos.core.ui.util.DevicePreview
import com.mifos.feature.settings.syncSurvey.SyncSurveysDialog
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SettingsScreen(
    onBackPressed: () -> Unit,
    navigateToLoginScreen: () -> Unit,
    changePasscode: (String) -> Unit,
    onClickUpdateConfig: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        onBackPressed = onBackPressed,
        state = uiState,
        changePasscode = { changePasscode(uiState.passcode) },
        handleEndpointUpdate = { baseURL, tenant ->
            if (viewModel.tryUpdatingEndpoint(selectedBaseUrl = baseURL, selectedTenant = tenant)) {
                navigateToLoginScreen()
            }
        },
        updateTheme = {
            viewModel.updateTheme(it)
        },
        updateLanguage = {
            val isSystemLanguage = viewModel.updateLanguage(it.code)
            updateLanguageLocale(
                language = it.code,
                isSystemLanguage = isSystemLanguage,
            )
        },
        onClickUpdateConfig = onClickUpdateConfig,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    state: SettingsUiState,
    onBackPressed: () -> Unit,
    onClickUpdateConfig: () -> Unit,
    changePasscode: () -> Unit,
    handleEndpointUpdate: (baseURL: String, tenant: String) -> Unit,
    updateTheme: (theme: AppTheme) -> Unit,
    updateLanguage: (language: MifosAppLanguage) -> Unit,
) {
    var showLanguageUpdateDialog by rememberSaveable { mutableStateOf(false) }
    var showEndpointUpdateDialog by rememberSaveable { mutableStateOf(false) }
    var showThemeUpdateDialog by rememberSaveable { mutableStateOf(false) }
    var showSyncSurveyDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    MifosScaffold(
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState,
        title = stringResource(resource = Res.string.feature_settings),
    ) { paddingValues ->
        Column(
            Modifier.padding(paddingValues),
        ) {
            SettingsCards(
                settingsCardClicked = { item ->
                    when (item) {
                        SettingsCardItem.SYNC_SURVEY -> showSyncSurveyDialog = true

                        SettingsCardItem.LANGUAGE -> showLanguageUpdateDialog = true

                        SettingsCardItem.THEME -> showThemeUpdateDialog = true

                        SettingsCardItem.PASSCODE -> changePasscode()

                        SettingsCardItem.ENDPOINT -> showEndpointUpdateDialog = true

                        SettingsCardItem.SERVER_CONFIG -> onClickUpdateConfig()
                    }
                },
            )
        }
    }

    if (showSyncSurveyDialog) {
        SyncSurveysDialog(
            closeDialog = {
                showSyncSurveyDialog = false
            },
        )
    }

    if (showLanguageUpdateDialog) {
        MifosRadioButtonDialog(
            title = stringResource(Res.string.feature_settings_choose_language),
            items = stringArrayResource(Res.array.feature_settings_languages).toTypedArray(),
            selectItem = { _, index -> updateLanguage(MifosAppLanguage.entries[index]) },
            onDismissRequest = { showLanguageUpdateDialog = false },
            selectedItem = state.language.languageName,
        )
    }

    if (showThemeUpdateDialog) {
        MifosRadioButtonDialog(
            title = stringResource(Res.string.feature_settings_change_app_theme),
            items = AppTheme.entries.map { it.themeName }.toTypedArray(),
            selectItem = { _, index -> updateTheme(AppTheme.entries[index]) },
            onDismissRequest = { showThemeUpdateDialog = false },
            selectedItem = state.theme.themeName,
        )
    }

    if (showEndpointUpdateDialog) {
        UpdateEndpointDialogScreen(
            initialBaseURL = state.baseUrl,
            initialTenant = state.tenant,
            onDismissRequest = { showEndpointUpdateDialog = false },
            handleEndpointUpdate = { url, tenant ->
                handleEndpointUpdate(url, tenant)
                showEndpointUpdateDialog = false
            },
        )
    }
}

@Composable
private fun SettingsCards(
    settingsCardClicked: (SettingsCardItem) -> Unit,
) {
    LazyColumn {
        items(SettingsCardItem.entries) { card ->
            SettingsCardItem(
                title = card.title,
                details = card.details,
                icon = card.icon,
                onclick = {
                    settingsCardClicked(card)
                },
            )
        }
    }
}

@Composable
private fun SettingsCardItem(
    title: StringResource,
    details: StringResource,
    icon: ImageVector?,
    onclick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(0.dp),
        onClick = { onclick.invoke() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.weight(0.2f),
                )
            }
            if (icon == null) {
                Spacer(modifier = Modifier.weight(0.2f))
            }
            Column(
                modifier = Modifier.weight(0.8f),
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    modifier = Modifier.padding(end = 16.dp),
                    text = stringResource(details),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

private fun updateLanguageLocale(language: String, isSystemLanguage: Boolean) {
    Logger.d { "updateLanguageLocale: $language" }
//    if (isSystemLanguage) {
//        LanguageHelper.setLocale(context, language)
//    } else {
//        val systemLanguageCode = Locale.getDefault().language
//        if (MifosAppLanguage.entries.find { it.code == systemLanguageCode } == null) {
//            LanguageHelper.setLocale(context, MifosAppLanguage.ENGLISH.code)
//        } else {
//            LanguageHelper.setLocale(context, language)
//        }
//    }
}

@Composable
fun RestartCountdownSnackbar(
    seconds: Int,
    snackbarHostState: SnackbarHostState,
) {
    var secondsRemaining by remember { mutableStateOf(seconds) }

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            snackbarHostState.showSnackbar(
                message = "Restarting in $secondsRemaining seconds...",
                withDismissAction = false,
                duration = SnackbarDuration.Short,
            )
            delay(1000)
            secondsRemaining--
        }
    }
}

@Composable
@DevicePreview
private fun PreviewSettingsScreen() {
    SettingsScreen(
        onBackPressed = {},
        state = SettingsUiState.DEFAULT,
        handleEndpointUpdate = { _, _ -> },
        updateLanguage = {},
        updateTheme = {},
        changePasscode = {},
        onClickUpdateConfig = {},
    )
}
