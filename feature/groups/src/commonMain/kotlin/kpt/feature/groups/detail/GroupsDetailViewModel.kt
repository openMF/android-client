/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.groups.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kpt.core.base.store.screen.ScreenState
import kpt.core.base.store.screen.emptyIfContent
import kpt.core.base.store.screen.mapContent
import kpt.core.base.ui.viewmodel.BaseViewModel
import kpt.core.data.group.GroupCommandRepository
import kpt.core.data.group.GroupRepository
import kpt.core.database.group.entity.GroupEntity

/**
 * Group detail — ported from `6b66e8a43:feature/groups/groupDetails/GroupDetailsViewModel.kt`.
 *
 * ## Why this is NOT the Centers detail with a rename
 *
 * The two screens look alike and are not. A center's detail exists to reach its GROUPS, so the port
 * folded the original's separate `centerGroupList` into it. A group's detail is a leaf — the
 * original's six overflow entries (add loan, add savings, documents, group clients, more info,
 * notes) all led OUT of `feature/groups` into features that are not ported. So this screen shows a
 * group's fields and its one working write, and the six are absent rather than drawn inert
 * (RULE-IMPL-DEAD-CLICKABLE-001). They are tracked in CAPABILITY_GAPS, not hidden here.
 *
 * ## Activate
 *
 * The original's "Activate group" button is ported because `GroupCommandRepository.activateGroup`
 * really exists — it is the one action on this screen with a path all the way to Fineract. It is
 * offered ONLY for an inactive group, which is also the original's condition.
 *
 * It NAVIGATES to `feature/activate` rather than writing inline. An earlier pass of this port did
 * the write here with an all-null `ActivatePayload`; see [handleAction]'s Activate branch.
 */
internal class GroupsDetailViewModel(
    repository: GroupRepository,
    private val commands: GroupCommandRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<Unit, GroupsDetailEvent, GroupsDetailAction>(Unit) {

    /** Must match `GroupsDetailRoute.groupId` — type-safe nav keys the handle by property name. */
    private val groupId: Int = savedStateHandle.get<Int>(GROUP_ID_KEY) ?: 0

    private val stream = repository.groupsStream(viewModelScope)

    /**
     * The one group this screen is about.
     *
     * `stateIn(… WhileSubscribed …)`, not an `init{}` collector — see `CenterListViewModel` for why
     * the hand-lift kept a Store subscription alive for screens nobody was looking at.
     *
     * filter -> emptyIfContent -> first, so a group outside the officer's caseload renders as Empty
     * rather than as a detail page of blank fields. `mapContent` carries `fetchedAt` and
     * `freshnessSignal` through, which a hand-rolled `when` over the states would drop.
     */
    val group: StateFlow<ScreenState<GroupEntity>> = stream.state
        .mapContent { groups, _ -> groups.filter { it.id == groupId } }
        .emptyIfContent { it.isEmpty() }
        .mapContent { matches, _ -> matches.first() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT_MS), ScreenState.Loading)

    /**
     * Whether Activate can be offered, derived from the SAME flow the status text reads.
     *
     * `!= true`, NOT `== false`: Fineract leaves `active` NULL for a pending group — exactly the
     * group an officer opens this screen to activate. Measured on device 2026-10-20: group "Ankit"
     * rendered "Inactive" and offered no button, because two predicates disagreed on null.
     */
    val canActivate: StateFlow<Boolean> = group.map { g ->
        (g as? ScreenState.Content)?.data?.let { it.active != true } == true
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT_MS), false)

    override fun handleAction(action: GroupsDetailAction) {
        when (action) {
            GroupsDetailAction.BackClicked -> sendEvent(GroupsDetailEvent.NavigateBack)
            GroupsDetailAction.ActivateClicked -> sendEvent(GroupsDetailEvent.Activate(groupId))

            GroupsDetailAction.DocumentsClicked ->
                sendEvent(GroupsDetailEvent.OpenDocuments(groupId))

            GroupsDetailAction.DataTablesClicked ->
                sendEvent(GroupsDetailEvent.OpenDataTables(groupId))

            GroupsDetailAction.AddLoanAccountClicked ->
                sendEvent(GroupsDetailEvent.OpenAddLoanAccount(groupId))
        }
    }

    /**
     * Activation is a SCREEN, not an inline write.
     *
     * This used to post `ActivatePayload()` with all three fields null, on the assumption that the
     * server would default the activation date to today. That was a guess: Fineract's activate
     * command takes `activationDate` + `dateFormat` + `locale`, and the original has always
     * collected the date from a date picker on a dedicated screen (`feature/activate`, shared with
     * client and centre). Navigating there removes the guess and the §6a divergence at once.
     */

    private companion object {
        const val TAG = "GroupsDetail"
        const val GROUP_ID_KEY = "groupId"

        /** Template default — long enough to survive a rotation or a brief tab switch. */
        const val STATE_TIMEOUT_MS = 5_000L
    }
}

internal sealed interface GroupsDetailAction {
    data object BackClicked : GroupsDetailAction
    data object ActivateClicked : GroupsDetailAction
    data object DocumentsClicked : GroupsDetailAction
    data object DataTablesClicked : GroupsDetailAction
    data object AddLoanAccountClicked : GroupsDetailAction
}

internal sealed interface GroupsDetailEvent {
    data object NavigateBack : GroupsDetailEvent

    /** Open the shared activate screen for this group. */
    data class Activate(val groupId: Int) : GroupsDetailEvent

    /** Open the generic document list for this group — the original's `loadDocumentList` edge. */
    data class OpenDocuments(val groupId: Int) : GroupsDetailEvent

    /** Open the group's datatables — the original's `loadGroupDataTables` edge. */
    data class OpenDataTables(val groupId: Int) : GroupsDetailEvent

    /** Open the group loan application form — the original's `addGroupLoanAccount` edge. */
    data class OpenAddLoanAccount(val groupId: Int) : GroupsDetailEvent
}
