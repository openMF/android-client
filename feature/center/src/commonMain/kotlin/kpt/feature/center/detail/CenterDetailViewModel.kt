/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.feature.center.detail

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
import kpt.core.data.center.CenterRepository
import kpt.core.data.group.GroupRepository
import kpt.core.database.center.entity.CenterEntity
import kpt.core.database.group.entity.GroupEntity

/**
 * Center detail — ports BOTH `6b66e8a43:feature/center/centerDetails/CenterDetailsViewModel.kt` and
 * `centerGroupList/GroupListViewModel.kt`.
 *
 * ## Why the two originals become one screen
 *
 * `CenterDetailsScreen` showed a center's fields and a "Groups" button; tapping it pushed
 * `GroupListScreen`, which showed the groups of **that same center** and nothing else. Two
 * destinations, two ViewModels, two loading states, for one center's worth of information — and the
 * second could show a group list for a center whose own detail had failed to load.
 *
 * Here the groups are a section of the detail. Both halves derive from streams this officer already
 * has cached, so the merge costs no extra request and removes a navigation step from the job the
 * screen exists for: look at a center, see its groups.
 *
 * ## No per-id fetch
 *
 * Neither repository exposes a by-id read — `centersStream` / `groupsStream` are list streams over
 * the officer's own caseload, and a center reached from the list is by construction in that list.
 * Selecting from the stream keeps ONE cache warm instead of a second per-id store that would have
 * to be invalidated in step with it.
 */
internal class CenterDetailViewModel(
    centerRepository: CenterRepository,
    groupRepository: GroupRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<Unit, CenterDetailEvent, CenterDetailAction>(Unit) {

    /**
     * The route argument, read by the SAME key the `@Serializable` route property is named with —
     * type-safe navigation puts it in the handle under the property name. Mirrors
     * `ClientProfileViewModel` (`feature/client`).
     */
    private val centerId: Int = savedStateHandle.get<Int>(CENTER_ID_KEY) ?: 0

    private val centerStream = centerRepository.centersStream(viewModelScope)
    private val groupStream = groupRepository.groupsStream(viewModelScope)

    /**
     * The center's own fields. `stateIn(… WhileSubscribed …)`, not an `init{}` collector — see
     * `CenterListViewModel.screenState` for why the hand-lift was wrong rather than merely unidiomatic.
     *
     * filter -> emptyIfContent -> first: a center outside the officer's caseload renders as Empty
     * ("not yours"), never a Content of blank fields. `mapContent` carries `fetchedAt` and
     * `freshnessSignal` through, which a hand-rolled `when` over the states drops.
     */
    /**
     * Whether Activate can be offered, derived from the SAME flow the detail renders.
     *
     * `active != true`, not `active == false`: Fineract leaves `active` NULL on a centre that was
     * never activated, so `== false` withholds the button from exactly the centres that need it.
     * That defect shipped on the GROUP screen and reached a device — see
     * `GroupsDetailViewModel.canActivate`.
     */
    val canActivate: StateFlow<Boolean> = centerStream.state
        .map { state ->
            (state as? ScreenState.Content)?.data
                ?.firstOrNull { it.id == centerId }
                ?.let { it.active != true } == true
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT_MS), false)

    val center: StateFlow<ScreenState<CenterEntity>> = centerStream.state
        .mapContent { centers, _ -> centers.filter { it.id == centerId } }
        .emptyIfContent { it.isEmpty() }
        .mapContent { matches, _ -> matches.first() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT_MS), ScreenState.Loading)

    /**
     * The groups of this center — a SEPARATE StateFlow, which is the whole reason the original's
     * two screens could be folded into one. A groups failure must not blank the center's details,
     * and `CenterDetailViewModelTest` asserts that in both directions.
     */
    val groups: StateFlow<ScreenState<List<GroupEntity>>> = groupStream.state
        .mapContent { groups, _ -> groups.filter { it.centerId == centerId } }
        .emptyIfContent { it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_TIMEOUT_MS), ScreenState.Loading)

    override fun handleAction(action: CenterDetailAction) {
        when (action) {
            is CenterDetailAction.GroupClicked -> sendEvent(CenterDetailEvent.OpenGroup(action.groupId))
            CenterDetailAction.BackClicked -> sendEvent(CenterDetailEvent.NavigateBack)

            // Activation is the shared `feature/activate` screen — it collects the activation date,
            // which Fineract's command requires. Centre activation was logged as unsurfaced in
            // CAPABILITY_GAPS; this closes it.
            CenterDetailAction.ActivateClicked -> sendEvent(CenterDetailEvent.Activate(centerId))
        }
    }
}

internal sealed interface CenterDetailAction {
    data class GroupClicked(val groupId: Int) : CenterDetailAction
    data object BackClicked : CenterDetailAction
    data object ActivateClicked : CenterDetailAction
}

/** Must match `CenterDetailRoute.centerId` — type-safe nav keys the handle by property name. */
private const val CENTER_ID_KEY = "centerId"

/** Template default — long enough to survive a rotation or a brief tab switch. */
private const val STATE_TIMEOUT_MS = 5_000L

internal sealed interface CenterDetailEvent {
    data class OpenGroup(val groupId: Int) : CenterDetailEvent
    data object NavigateBack : CenterDetailEvent

    /** Open the shared activate screen for this centre. */
    data class Activate(val centerId: Int) : CenterDetailEvent
}
