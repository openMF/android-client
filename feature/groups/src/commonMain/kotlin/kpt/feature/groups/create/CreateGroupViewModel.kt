/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.groups.create

import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kpt.core.base.store.mutation.BlockReason
import kpt.core.base.store.mutation.MutationResult
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.screen.emptyIfContent
import kpt.core.base.ui.viewmodel.BaseViewModel
import kpt.core.common.utils.DateFormatPattern
import kpt.core.data.group.GroupCommandRepository
import kpt.core.data.office.OfficeRepository
import kpt.core.model.group.GroupPayload
import kpt.core.model.office.Office
import kpt.core.model.utils.ApiDateFormatter

/**
 * Create a group — ported from
 * `6b66e8a43:feature/groups/createNewGroup/{CreateNewGroupScreen,CreateNewGroupViewModel,
 * CreateNewGroupUiState}.kt`.
 *
 * Reached from the groups list's FAB, which this port's list did not have at all — the original
 * draws `MifosFAB(icon = MifosIcons.Add, onClick = onAddGroupClick)` and the ported
 * `GroupsListScreen` took no create callback, so the affordance was simply missing rather than
 * null-gated.
 *
 * ## The original is pre-MVI, so the adaptation is larger than usual
 *
 * `CreateNewGroupViewModel` is a plain `ViewModel` with a hand-rolled
 * `MutableStateFlow<CreateNewGroupUiState>` and the `Loading / ShowOffices / ShowFetchingError /
 * ShowGroupCreatedSuccessfully` sealed class that §6a's adaptation 1 replaces. The offices read
 * becomes a [ScreenState] stream and the write becomes an action on the `BaseViewModel` triad;
 * the eleven `rememberSaveable` fields in the screen move here with them.
 *
 * ## FIXED: the success message was decided by a flag, not by what happened
 *
 * ```kotlin
 * fun getResponse(): String = when (userStatus.value) {
 *     false -> "created successfully"
 *     true  -> "Saved into DB Successfully"
 * }
 * ```
 *
 * `userStatus` is a stored PREFERENCE, so the screen announced "created successfully" or "saved
 * locally" according to a setting read at composition — never according to whether the request
 * actually reached Fineract. An officer who went offline after that preference was written was
 * told the group had been created.
 *
 * The substrate already answers this correctly: `MutationResult.Applied` carries `synced`, which
 * is the truth of THIS write. [CreateGroupEvent.Created] carries it so the screen can say which
 * of the two actually happened.
 *
 * ## The date and its declared format come from one value
 *
 * As on the share and fixed-deposit wizards — see [SUBMIT_DATE_FORMAT].
 */
internal class CreateGroupViewModel(
    officeRepository: OfficeRepository,
    private val commands: GroupCommandRepository,
) : BaseViewModel<CreateGroupState, CreateGroupEvent, CreateGroupAction>(CreateGroupState()) {

    /** The offices a group may be opened in. */
    val offices: StateFlow<ScreenState<List<Office>>> =
        officeRepository.officesStream(viewModelScope).state
            // No offices is Empty (the screen's empty slot); the stream reports it as empty Content.
            .emptyIfContent { it.isEmpty() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT_MS), ScreenState.Loading)

    override fun handleAction(action: CreateGroupAction) {
        when (action) {
            CreateGroupAction.NavigateBack -> sendEvent(CreateGroupEvent.NavigateBack)

            is CreateGroupAction.UpdateName ->
                mutableStateFlow.value = state.copy(name = action.value, nameError = false)

            is CreateGroupAction.UpdateOffice ->
                mutableStateFlow.value = state.copy(office = action.option, officeError = false)

            is CreateGroupAction.UpdateExternalId ->
                mutableStateFlow.value = state.copy(externalId = action.value)

            is CreateGroupAction.UpdateSubmissionDate ->
                mutableStateFlow.value = state.copy(submissionDateMillis = action.millis)

            is CreateGroupAction.UpdateActivationDate ->
                mutableStateFlow.value = state.copy(activationDateMillis = action.millis)

            is CreateGroupAction.UpdateActive -> mutableStateFlow.value = state.copy(
                isActive = action.value,
                // An inactive group has no activation date to send; clearing it here means the
                // payload cannot carry a date for a group that is not being activated.
                activationDateMillis = if (action.value) state.activationDateMillis else 0L,
            )

            CreateGroupAction.Submit -> submit()

            CreateGroupAction.DismissError ->
                mutableStateFlow.value = state.copy(error = null)
        }
    }

    private fun submit() {
        val errors = state.validate()
        if (!errors.isValid || state.isSubmitting) {
            mutableStateFlow.value =
                state.copy(nameError = errors.name, officeError = errors.office)
            return
        }

        mutableStateFlow.value = state.copy(isSubmitting = true, error = null)
        viewModelScope.launch {
            when (val result = commands.createGroup(state.toPayload())) {
                is MutationResult.Applied -> {
                    mutableStateFlow.value = state.copy(isSubmitting = false)
                    // `synced` is the truth of THIS write, not a stored preference — see the
                    // class KDoc.
                    sendEvent(CreateGroupEvent.Created(synced = result.synced))
                }

                is MutationResult.Blocked -> mutableStateFlow.value = state.copy(
                    isSubmitting = false,
                    error = if (result.reason == BlockReason.OFFLINE) {
                        CreateGroupError.Offline
                    } else {
                        CreateGroupError.Failed
                    },
                )

                is MutationResult.Failed -> {
                    Logger.e(tag = TAG, throwable = result.cause) { "create group failed" }
                    mutableStateFlow.value =
                        state.copy(isSubmitting = false, error = CreateGroupError.Failed)
                }

                is MutationResult.Conflicted ->
                    mutableStateFlow.value =
                        state.copy(isSubmitting = false, error = CreateGroupError.Failed)
            }
        }
    }

    internal companion object {
        /** One pattern formats the dates AND declares `dateFormat`. */
        val SUBMIT_DATE_FORMAT = DateFormatPattern.NUMERIC_DASH
        const val LOCALE = "en"
        private const val STATE_TIMEOUT_MS = 5_000L
        private const val TAG = "CreateGroup"
    }
}

/** One selectable office. The CHOICE carries its id, so no index can drift against a re-read. */
internal data class OfficeOption(val id: Int, val label: String)

internal data class CreateGroupFieldErrors(
    val name: Boolean = false,
    val office: Boolean = false,
) {
    val isValid: Boolean get() = !name && !office
}

/**
 * The original's two required fields.
 *
 * `name.isBlank()` rather than `isEmpty()`: a whitespace-only name passed the original's check and
 * reached Fineract, the same defect the client forms carried.
 */
internal fun CreateGroupState.validate(): CreateGroupFieldErrors = CreateGroupFieldErrors(
    name = name.isBlank(),
    office = office == null,
)

internal fun CreateGroupState.toPayload(): GroupPayload = GroupPayload(
    name = name.trim(),
    officeId = office?.id ?: 0,
    externalId = externalId.trim().ifEmpty { null },
    active = isActive,
    submittedOnDate = ApiDateFormatter.formatForApi(
        submissionDateMillis,
        CreateGroupViewModel.SUBMIT_DATE_FORMAT,
    ),
    // Only an ACTIVE group carries an activation date; sending one for an inactive group would
    // describe a state the payload is not asking for.
    activationDate = if (isActive) {
        ApiDateFormatter.formatForApi(
            activationDateMillis,
            CreateGroupViewModel.SUBMIT_DATE_FORMAT,
        )
    } else {
        null
    },
    dateFormat = CreateGroupViewModel.SUBMIT_DATE_FORMAT.pattern,
    locale = CreateGroupViewModel.LOCALE,
)

internal enum class CreateGroupError {
    Offline,
    Failed,
}

internal data class CreateGroupState(
    val name: String = "",
    val office: OfficeOption? = null,
    val externalId: String = "",
    val submissionDateMillis: Long = 0L,
    val activationDateMillis: Long = 0L,
    val isActive: Boolean = false,
    val nameError: Boolean = false,
    val officeError: Boolean = false,
    val isSubmitting: Boolean = false,
    val error: CreateGroupError? = null,
) {
    val canSubmit: Boolean get() = !isSubmitting
}

internal sealed interface CreateGroupEvent {
    data object NavigateBack : CreateGroupEvent

    /**
     * The group was created.
     *
     * [synced] says whether it reached Fineract or was queued locally — the distinction the
     * original decided from a stored preference instead of from the write.
     */
    data class Created(val synced: Boolean) : CreateGroupEvent
}

internal sealed interface CreateGroupAction {
    data object NavigateBack : CreateGroupAction
    data object Submit : CreateGroupAction
    data object DismissError : CreateGroupAction
    data class UpdateName(val value: String) : CreateGroupAction
    data class UpdateOffice(val option: OfficeOption) : CreateGroupAction
    data class UpdateExternalId(val value: String) : CreateGroupAction
    data class UpdateSubmissionDate(val millis: Long) : CreateGroupAction
    data class UpdateActivationDate(val millis: Long) : CreateGroupAction
    data class UpdateActive(val value: Boolean) : CreateGroupAction
}
