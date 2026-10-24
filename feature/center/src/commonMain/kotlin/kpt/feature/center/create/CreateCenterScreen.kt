/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.center.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kpt.core.base.designsystem.theme.KptTheme
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.ui.effects.EventsEffect
import kpt.core.base.ui.screen.ScreenContent
import kpt.core.common.utils.DateHelper
import kpt.core.designsystem.component.AppButton
import kpt.core.designsystem.component.AppDatePickerTextField
import kpt.core.designsystem.component.AppOutlinedTextField
import kpt.core.designsystem.component.AppScaffold
import kpt.core.designsystem.component.AppTextFieldDropdown
import kpt.core.model.office.Office
import kpt.core.ui.components.AppCheckBox
import kpt.feature.center.generated.resources.Res
import kpt.feature.center.generated.resources.feature_center_activate
import kpt.feature.center.generated.resources.feature_center_activation_date
import kpt.feature.center.generated.resources.feature_center_cancel
import kpt.feature.center.generated.resources.feature_center_center_created_successfully
import kpt.feature.center.generated.resources.feature_center_center_name
import kpt.feature.center.generated.resources.feature_center_center_name_empty
import kpt.feature.center.generated.resources.feature_center_center_name_should_be_more_than_4_characters
import kpt.feature.center.generated.resources.feature_center_center_name_should_not_contains_special_characters_or_numbers
import kpt.feature.center.generated.resources.feature_center_create_new_center
import kpt.feature.center.generated.resources.feature_center_create_offline
import kpt.feature.center.generated.resources.feature_center_failed_to_create_center
import kpt.feature.center.generated.resources.feature_center_office_required
import kpt.feature.center.generated.resources.feature_center_ok
import kpt.feature.center.generated.resources.feature_center_select
import kpt.feature.center.generated.resources.feature_center_select_office
import kpt.feature.center.generated.resources.feature_center_submit
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** Ported from `6b66e8a43:feature/center/createCenter/CreateNewCenterScreen.kt` — see [CreateCenterViewModel]. */
@Composable
internal fun CreateCenterScreen(
    onNavigateBack: () -> Unit,
    onCreated: () -> Unit,
    viewModel: CreateCenterViewModel = koinViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()
    val offices by viewModel.offices.collectAsStateWithLifecycle()

    EventsEffect(viewModel.eventFlow) { event ->
        when (event) {
            CreateCenterEvent.NavigateBack -> onNavigateBack()
            CreateCenterEvent.Created -> onCreated()
        }
    }

    CreateCenterContent(
        state = state,
        offices = offices,
        onAction = remember(viewModel) { { viewModel.trySendAction(it) } },
        onRetry = viewModel::onRetry,
    )
}

/** Stateless half — rendered in tests and previews without Koin. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateCenterContent(
    state: CreateCenterState,
    offices: ScreenState<List<Office>>,
    onAction: (CreateCenterAction) -> Unit,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
) {
    AppScaffold(
        title = stringResource(Res.string.feature_center_create_new_center),
        onBackPressed = { onAction(CreateCenterAction.NavigateBack) },
        modifier = modifier,
    ) { padding ->
        ScreenContent(
            state = offices,
            onRetry = onRetry,
            modifier = Modifier.fillMaxSize().padding(padding).testTag(CreateCenterTestTags.SCREEN),
        ) { officeList, _ ->
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(KptTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(KptTheme.spacing.sm),
            ) {
                AppOutlinedTextField(
                    value = state.name,
                    onValueChange = { onAction(CreateCenterAction.NameChanged(it)) },
                    label = stringResource(Res.string.feature_center_center_name),
                    isError = state.nameError != null,
                    errorText = state.nameError?.let { stringResource(it.message()) },
                    modifier = Modifier.fillMaxWidth().testTag(CreateCenterTestTags.NAME),
                )
                AppTextFieldDropdown(
                    value = state.officeName,
                    onValueChanged = {},
                    onOptionSelected = { index, label ->
                        officeList.getOrNull(index)?.let { onAction(CreateCenterAction.OfficeSelected(it.id, label)) }
                    },
                    options = officeList.map { it.name.orEmpty() },
                    label = stringResource(Res.string.feature_center_office_required),
                    errorMessage = if (state.officeMissing) stringResource(Res.string.feature_center_select_office) else null,
                    modifier = Modifier.fillMaxWidth().testTag(CreateCenterTestTags.OFFICE),
                )
                AppCheckBox(
                    text = stringResource(Res.string.feature_center_activate),
                    checked = state.active,
                    onCheckChanged = { onAction(CreateCenterAction.ActiveToggled(it)) },
                    modifier = Modifier.fillMaxWidth().testTag(CreateCenterTestTags.ACTIVE),
                )
                if (state.active) {
                    AppDatePickerTextField(
                        value = DateHelper.getDateAsStringFromLong(state.activationDateMillis),
                        label = stringResource(Res.string.feature_center_activation_date),
                        openDatePicker = { onAction(CreateCenterAction.OpenDatePicker) },
                        modifier = Modifier.fillMaxWidth().testTag(CreateCenterTestTags.DATE),
                    )
                }
                AppButton(
                    onClick = { onAction(CreateCenterAction.Submit) },
                    enabled = !state.isSubmitting,
                    modifier = Modifier.fillMaxWidth().testTag(CreateCenterTestTags.SUBMIT),
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(strokeWidth = KptTheme.spacing.xs / 2)
                    } else {
                        Text(stringResource(Res.string.feature_center_submit))
                    }
                }
            }
        }

        if (state.datePickerOpen) {
            val picker = rememberDatePickerState(initialSelectedDateMillis = state.activationDateMillis, selectableDates = NotInTheFuture)
            DatePickerDialog(
                onDismissRequest = { onAction(CreateCenterAction.CloseDatePicker) },
                confirmButton = {
                    TextButton(onClick = {
                        picker.selectedDateMillis?.let { onAction(CreateCenterAction.DatePicked(it)) }
                            ?: onAction(CreateCenterAction.CloseDatePicker)
                    }) { Text(stringResource(Res.string.feature_center_select)) }
                },
                dismissButton = {
                    TextButton(onClick = { onAction(CreateCenterAction.CloseDatePicker) }) {
                        Text(stringResource(Res.string.feature_center_cancel))
                    }
                },
            ) { DatePicker(state = picker) }
        }
    }

    state.dialog?.let { dialog ->
        val failed = stringResource(Res.string.feature_center_failed_to_create_center)
        val text = when (dialog) {
            CreateCenterState.Dialog.Created -> stringResource(Res.string.feature_center_center_created_successfully)
            CreateCenterState.Dialog.Offline -> stringResource(Res.string.feature_center_create_offline)
            is CreateCenterState.Dialog.Failed -> dialog.serverMessage?.let { "$failed\n$it" } ?: failed
        }
        AlertDialog(
            onDismissRequest = { onAction(CreateCenterAction.DismissDialog) },
            confirmButton = {
                TextButton(onClick = { onAction(CreateCenterAction.DismissDialog) }) { Text(stringResource(Res.string.feature_center_ok)) }
            },
            text = { Text(text) },
            modifier = Modifier.testTag(CreateCenterTestTags.DIALOG),
        )
    }
}

private fun CenterNameError.message() = when (this) {
    CenterNameError.EMPTY -> Res.string.feature_center_center_name_empty
    CenterNameError.TOO_SHORT -> Res.string.feature_center_center_name_should_be_more_than_4_characters
    CenterNameError.INVALID_CHARACTERS -> Res.string.feature_center_center_name_should_not_contains_special_characters_or_numbers
}

/** A center is activated today or earlier — never in the future (the original allowed ONLY the future). */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
private object NotInTheFuture : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long) = utcTimeMillis <= Clock.System.now().toEpochMilliseconds()
}

/** Stable handles for UI tests and device flows. */
internal object CreateCenterTestTags {
    const val SCREEN = "createCenter:screen"
    const val NAME = "createCenter:name"
    const val OFFICE = "createCenter:office"
    const val ACTIVE = "createCenter:active"
    const val DATE = "createCenter:date"
    const val SUBMIT = "createCenter:submit"
    const val DIALOG = "createCenter:dialog"
}
