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

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.LanguageHelper
import com.mifos.core.designsystem.component.MifosRadioButtonDialog
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.model.DarkThemeConfig
import com.mifos.core.model.MifosAppLanguage
import com.mifos.core.model.ThemeBrand
import com.mifos.core.model.UserData
import com.mifos.feature.settings.R
import com.mifos.feature.settings.syncSurvey.SyncSurveysDialog
import java.util.Locale

@Composable
internal fun SettingsScreen(
    onBackPressed: () -> Unit,
    changePasscode: () -> Unit,
    onClickUpdateConfig: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val userData by viewModel.userData.collectAsStateWithLifecycle()

    SettingsScreen(
        userData = userData,
        onBackPressed = onBackPressed,
        updateTheme = viewModel::changeThemeBrand,
        updateThemeConfig = viewModel::changeDarkThemeConfig,
        updateLanguage = { language ->
            viewModel.changeLanguage(language)
            updateLanguageLocale(
                context = context,
                language = language.code,
                isSystemLanguage = language == MifosAppLanguage.SYSTEM_LANGUAGE,
            )
        },
        changePasscode = changePasscode,
        onClickUpdateConfig = onClickUpdateConfig,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsScreen(
    userData: UserData,
    updateTheme: (theme: ThemeBrand) -> Unit,
    updateThemeConfig: (darkThemeConfig: DarkThemeConfig) -> Unit,
    updateLanguage: (language: MifosAppLanguage) -> Unit,
    changePasscode: () -> Unit,
    onBackPressed: () -> Unit,
    onClickUpdateConfig: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var showLanguageUpdateDialog by rememberSaveable { mutableStateOf(false) }
    var showThemeUpdateDialog by rememberSaveable { mutableStateOf(false) }
    var showSyncSurveyDialog by rememberSaveable { mutableStateOf(false) }

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
            titleResId = R.string.feature_settings_choose_language,
            items = stringArrayResource(R.array.feature_settings_languages),
            selectItem = { _, index -> updateLanguage(MifosAppLanguage.entries[index]) },
            onDismissRequest = { showLanguageUpdateDialog = false },
            selectedItem = userData.language.displayName,
        )
    }

    if (showThemeUpdateDialog) {
        ThemeDialog(
            selectedTheme = userData.themeBrand,
            darkThemeConfig = userData.darkThemeConfig,
            onThemeSelected = { theme -> updateTheme(theme) },
            onDarkThemeConfigSelected = { darkThemeConfig ->
                updateThemeConfig(darkThemeConfig)
            },
            onDismissRequest = { showThemeUpdateDialog = false },
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
                    fontWeight = FontWeight.SemiBold,
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

private fun updateLanguageLocale(
    context: Context,
    language: String,
    isSystemLanguage: Boolean,
) {
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

@Composable
private fun ThemeDialog(
    selectedTheme: ThemeBrand,
    darkThemeConfig: DarkThemeConfig,
    onThemeSelected: (ThemeBrand) -> Unit,
    onDarkThemeConfigSelected: (DarkThemeConfig) -> Unit,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(modifier = modifier) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = stringResource(id = R.string.feature_settings_change_app_theme))
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 500.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(
                        items = ThemeBrand.entries,
                    ) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    onDismissRequest.invoke()
                                    onThemeSelected(item)
                                }
                                .fillMaxWidth(),
                        ) {
                            RadioButton(
                                selected = (item == selectedTheme),
                                onClick = {
                                    onDismissRequest.invoke()
                                    onThemeSelected(item)
                                },
                            )
                            Text(
                                text = item.name,
                                modifier = Modifier.padding(start = 4.dp),
                            )
                        }
                    }

                    item {
                        HorizontalDivider()
                    }

                    itemsIndexed(
                        items = DarkThemeConfig.entries,
                    ) { index, item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    onDismissRequest.invoke()
                                    onDarkThemeConfigSelected(item)
                                }
                                .fillMaxWidth(),
                        ) {
                            RadioButton(
                                selected = (item == darkThemeConfig),
                                onClick = {
                                    onDismissRequest.invoke()
                                    onDarkThemeConfigSelected(item)
                                },
                            )
                            Text(
                                text = item.name,
                                modifier = Modifier.padding(start = 4.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
private fun PreviewSettingsScreen() {
    SettingsScreen(
        userData = UserData.DEFAULT,
        updateTheme = {},
        updateThemeConfig = {},
        updateLanguage = {},
        changePasscode = {},
        onBackPressed = {},
        onClickUpdateConfig = {},
    )
}
