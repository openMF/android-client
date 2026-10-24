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

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kpt.core.base.store.mutation.BlockReason
import kpt.core.base.store.mutation.MutationResult
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.ui.viewmodel.BaseViewModel
import kpt.core.data.center.CenterCommandRepository
import kpt.core.data.office.OfficeRepository
import kpt.core.model.center.CenterPayload
import kpt.core.model.objects.error.serverErrorMessage
import kpt.core.model.office.Office
import kpt.core.model.utils.ApiDateFormatter
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Ported from `6b66e8a43:feature/center/createCenter/CreateNewCenterViewModel.kt` + the form state its
 * screen held in `rememberSaveable`s. Same fields (name, office, activate + date) and the same name rules.
 *
 * Fixed rather than carried:
 *  - **The activation date allowed only the future.** `>= now` excluded today — the date an officer
 *    activating a center now wants — and every past date. Now today and the past (see the screen).
 *  - **Validation ran on every keystroke and blocked silently.** The original computed an error per
 *    keystroke, and on Submit with an error showed nothing (its Toasts were commented out) — the button
 *    just did nothing. Errors now appear on Submit, next to the field.
 *  - **The office id defaulted to 0** until one was chosen; it is absent now, and required.
 *  - **Any failure said "Failed to create center".** Fineract's own reason is shown when it sent one, and
 *    offline says so (the create needs a connection).
 */
@OptIn(ExperimentalTime::class)
internal class CreateCenterViewModel(
    offices: OfficeRepository,
    private val commands: CenterCommandRepository,
) : BaseViewModel<CreateCenterState, CreateCenterEvent, CreateCenterAction>(
    CreateCenterState(activationDateMillis = Clock.System.now().toEpochMilliseconds()),
) {

    private val officesStream = offices.officesStream(viewModelScope)

    val offices: StateFlow<ScreenState<List<Office>>> = officesStream.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT_MS), ScreenState.Loading)

    fun onRetry() = officesStream.retry()

    override fun handleAction(action: CreateCenterAction) {
        when (action) {
            is CreateCenterAction.NameChanged -> mutableStateFlow.value = state.copy(name = action.value, nameError = null)
            is CreateCenterAction.OfficeSelected -> mutableStateFlow.value =
                state.copy(officeId = action.id, officeName = action.name, officeMissing = false)
            is CreateCenterAction.ActiveToggled -> mutableStateFlow.value = state.copy(active = action.active)
            CreateCenterAction.OpenDatePicker -> mutableStateFlow.value = state.copy(datePickerOpen = true)
            CreateCenterAction.CloseDatePicker -> mutableStateFlow.value = state.copy(datePickerOpen = false)
            is CreateCenterAction.DatePicked ->
                mutableStateFlow.value = state.copy(activationDateMillis = action.millis, datePickerOpen = false)
            CreateCenterAction.Submit -> submit()
            CreateCenterAction.DismissDialog -> {
                val created = state.dialog == CreateCenterState.Dialog.Created
                mutableStateFlow.value = state.copy(dialog = null)
                if (created) sendEvent(CreateCenterEvent.Created)
            }
            CreateCenterAction.NavigateBack -> sendEvent(CreateCenterEvent.NavigateBack)
        }
    }

    private fun submit() {
        if (state.isSubmitting) return
        val nameError = validateCenterName(state.name)
        val officeId = state.officeId
        if (nameError != null || officeId == null) {
            mutableStateFlow.value = state.copy(nameError = nameError, officeMissing = officeId == null)
            return
        }
        mutableStateFlow.value = state.copy(isSubmitting = true)
        val payload = CenterPayload(
            name = state.name.trim(),
            officeId = officeId,
            active = state.active,
            activationDate = if (state.active) ApiDateFormatter.formatForApi(state.activationDateMillis) else null,
            dateFormat = ApiDateFormatter.DATE_FORMAT,
            locale = ApiDateFormatter.LOCALE,
        )
        viewModelScope.launch {
            val dialog = when (val result = commands.createCenter(payload)) {
                is MutationResult.Applied -> CreateCenterState.Dialog.Created
                is MutationResult.Blocked ->
                    if (result.reason == BlockReason.OFFLINE) CreateCenterState.Dialog.Offline else CreateCenterState.Dialog.Failed(null)
                is MutationResult.Failed -> CreateCenterState.Dialog.Failed(result.cause.serverErrorMessage())
                is MutationResult.Conflicted -> CreateCenterState.Dialog.Failed(null)
            }
            mutableStateFlow.value = state.copy(isSubmitting = false, dialog = dialog)
        }
    }

    private companion object {
        const val STATE_TIMEOUT_MS = 5_000L
    }
}

/** The original's three rules, in its order. */
internal enum class CenterNameError { EMPTY, TOO_SHORT, INVALID_CHARACTERS }

internal fun validateCenterName(name: String): CenterNameError? = when {
    name.isBlank() -> CenterNameError.EMPTY
    name.trim().length < MIN_NAME_LENGTH -> CenterNameError.TOO_SHORT
    name.contains(Regex("[^a-zA-Z ]")) -> CenterNameError.INVALID_CHARACTERS
    else -> null
}

private const val MIN_NAME_LENGTH = 4

internal data class CreateCenterState(
    val name: String = "",
    val nameError: CenterNameError? = null,
    val officeId: Int? = null,
    val officeName: String = "",
    val officeMissing: Boolean = false,
    val active: Boolean = false,
    val activationDateMillis: Long,
    val datePickerOpen: Boolean = false,
    val isSubmitting: Boolean = false,
    val dialog: Dialog? = null,
) {
    sealed interface Dialog {
        data object Created : Dialog
        data object Offline : Dialog

        /** [serverMessage] is Fineract's own sentence when it sent one; else the generic copy shows. */
        data class Failed(val serverMessage: String?) : Dialog
    }
}

internal sealed interface CreateCenterEvent {
    data object NavigateBack : CreateCenterEvent
    data object Created : CreateCenterEvent
}

internal sealed interface CreateCenterAction {
    data class NameChanged(val value: String) : CreateCenterAction
    data class OfficeSelected(val id: Int, val name: String) : CreateCenterAction
    data class ActiveToggled(val active: Boolean) : CreateCenterAction
    data object OpenDatePicker : CreateCenterAction
    data object CloseDatePicker : CreateCenterAction
    data class DatePicked(val millis: Long) : CreateCenterAction
    data object Submit : CreateCenterAction
    data object DismissDialog : CreateCenterAction
    data object NavigateBack : CreateCenterAction
}
