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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.CountDownTimer
import android.widget.Toast
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.enums.MifosAppLanguage
import com.mifos.core.common.utils.LanguageHelper
import com.mifos.core.designsystem.component.MifosRadioButtonDialog
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.UpdateEndpointDialogScreen
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.feature.settings.R
import com.mifos.feature.settings.syncSurvey.SyncSurveysDialog
import com.mifos.feature.settings.updateServer.UpdateServerConfigScreenRoute
import java.util.Locale

@Composable
internal fun SettingsScreen(
    onBackPressed: () -> Unit,
    navigateToLoginScreen: () -> Unit,
    changePasscode: (String) -> Unit,
    languageChanged: () -> Unit,
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val baseURL by viewModel.baseUrl.collectAsStateWithLifecycle()
    val tenant by viewModel.tenant.collectAsStateWithLifecycle()
    val passcode by viewModel.passcode.collectAsStateWithLifecycle()
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val context = LocalContext.current

    SettingsScreen(
        onBackPressed = onBackPressed,
        selectedLanguage = language ?: "System Language",
        selectedTheme = theme ?: "System Theme",
        baseURL = baseURL ?: "",
        tenant = tenant ?: "",
        changePasscode = { changePasscode(passcode ?: "") },
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
                context = context,
                language = it.code,
                isSystemLanguage = isSystemLanguage,
            )
            languageChanged()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    onBackPressed: () -> Unit,
    selectedLanguage: String,
    selectedTheme: String,
    baseURL: String,
    tenant: String,
    changePasscode: () -> Unit,
    handleEndpointUpdate: (baseURL: String, tenant: String) -> Unit,
    updateTheme: (theme: AppTheme) -> Unit,
    updateLanguage: (language: MifosAppLanguage) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showLanguageUpdateDialog by rememberSaveable { mutableStateOf(false) }
    var showEndpointUpdateDialog by rememberSaveable { mutableStateOf(false) }
    var showThemeUpdateDialog by rememberSaveable { mutableStateOf(false) }
    var showSyncSurveyDialog by rememberSaveable { mutableStateOf(false) }
    var showServerConfig by rememberSaveable { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_settings),
        onBackPressed = onBackPressed,
        snackbarHostState = snackbarHostState,
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

                        SettingsCardItem.SERVER_CONFIG -> showServerConfig = true
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

    if (showServerConfig) {
        ModalBottomSheet(
            onDismissRequest = { showServerConfig = false },
            sheetState = sheetState,
        ) {
            UpdateServerConfigScreenRoute(
                onCloseClick = { showServerConfig = false },
                onSuccessful = {
                    showServerConfig = false
                    showRestartCountdownToast(context, 2)
                },
            )
        }
    }

    if (showLanguageUpdateDialog) {
        MifosRadioButtonDialog(
            titleResId = R.string.feature_settings_choose_language,
            items = stringArrayResource(R.array.feature_settings_languages),
            selectItem = { _, index -> updateLanguage(MifosAppLanguage.entries[index]) },
            onDismissRequest = { showLanguageUpdateDialog = false },
            selectedItem = MifosAppLanguage.fromCode(selectedLanguage).displayName,
        )
    }

    if (showThemeUpdateDialog) {
        MifosRadioButtonDialog(
            titleResId = R.string.feature_settings_change_app_theme,
            items = AppTheme.entries.map { it.themeName }.toTypedArray(),
            selectItem = { _, index -> updateTheme(AppTheme.entries[index]) },
            onDismissRequest = { showThemeUpdateDialog = false },
            selectedItem = selectedTheme,
        )
    }

    if (showEndpointUpdateDialog) {
        UpdateEndpointDialogScreen(
            initialBaseURL = baseURL,
            initialTenant = tenant,
            onDismissRequest = { showEndpointUpdateDialog = false },
            handleEndpointUpdate = handleEndpointUpdate,
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
    title: Int,
    details: Int,
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
                    text = stringResource(id = title),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    modifier = Modifier.padding(end = 16.dp),
                    text = stringResource(id = details),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

private fun updateLanguageLocale(context: Context, language: String, isSystemLanguage: Boolean) {
    if (isSystemLanguage) {
        LanguageHelper.setLocale(context, language)
    } else {
        val systemLanguageCode = Locale.getDefault().language
        if (MifosAppLanguage.entries.find { it.code == systemLanguageCode } == null) {
            LanguageHelper.setLocale(context, MifosAppLanguage.ENGLISH.code)
        } else {
            LanguageHelper.setLocale(context, language)
        }
    }
}

private fun showRestartCountdownToast(context: Context, seconds: Int) {
    val countDownTimer = object : CountDownTimer((seconds * 1000).toLong(), 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val secondsRemaining = millisUntilFinished / 1000
            Toast.makeText(
                context,
                "Restarting app in $secondsRemaining seconds",
                Toast.LENGTH_SHORT,
            ).show()
        }

        override fun onFinish() {
            context.restartApplication()
        }
    }
    countDownTimer.start()
}

private fun Context.restartApplication() {
    val packageManager: PackageManager = this.packageManager
    val intent: Intent = packageManager.getLaunchIntentForPackage(this.packageName)!!
    val componentName: ComponentName = intent.component!!
    val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
    this.startActivity(restartIntent)
    Runtime.getRuntime().exit(0)
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
private fun PreviewSettingsScreen() {
    SettingsScreen(
        onBackPressed = {},
        selectedLanguage = "",
        selectedTheme = "",
        baseURL = "",
        tenant = "",
        handleEndpointUpdate = { _, _ -> },
        updateLanguage = {},
        updateTheme = {},
        changePasscode = {},
    )
}
