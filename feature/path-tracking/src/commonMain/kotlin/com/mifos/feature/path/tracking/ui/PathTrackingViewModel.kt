/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.path.tracking.ui

import androidx.lifecycle.viewModelScope
import com.mifos.core.data.pathtracking.PathTrackingRepository
import com.mifos.core.data.store.DataFreshness
import com.mifos.core.data.store.ScreenState
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.ui.store.BaseViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Path-tracking list ViewModel.
 *
 * Read flow (`getUserPathTracking`) uses direct suspend + try/catch →
 * `ScreenState<List<UserLocation>>` (no Store5 — see [PathTrackingRepository]
 * doc / RULE-STORE5-FETCH-001 exception for low-value remote reads with no
 * useful offline cache). Re-throws `CancellationException` per
 * structured-concurrency contract (RULE-NO-RUN-CATCHING-001 doesn't permit
 * `runCatching` because it swallows cancellation).
 *
 * Empty-list response is folded into `ScreenState.Empty` (renderable via
 * `ScreenContent`'s `onEmpty` slot), matching the Wave-7 Note pattern. The
 * legacy code folded "empty" into an Error state — that conflated "no entries
 * yet" (an empty state, not a problem) with "the request failed" and is a
 * regression we fix here.
 *
 * User-status toggle is a `prefManager.updateUserStatus(...)` write —
 * non-HTTP, so no `SubmitHandler` needed. The VM mirrors
 * `prefManager.userInfo.userStatus` into [PathTrackingState.userStatus] via a
 * collector so the screen has a single state source.
 */
class PathTrackingViewModel(
    private val repository: PathTrackingRepository,
    private val prefManager: UserPreferencesRepository,
) : BaseViewModel<PathTrackingState, PathTrackingEvent, PathTrackingAction>(
    initialState = PathTrackingState(),
) {

    init {
        // Mirror prefManager.userStatus into the MVI state so the screen has a
        // single source of truth.
        prefManager.userInfo
            .map { it.userStatus ?: false }
            .onEach { status ->
                mutableStateFlow.update { it.copy(userStatus = status) }
            }
            .launchIn(viewModelScope)

        loadPathTracking(initial = true)
    }

    override fun handleAction(action: PathTrackingAction) {
        when (action) {
            PathTrackingAction.NavigateBack -> sendEvent(PathTrackingEvent.NavigateBack)

            PathTrackingAction.OnRetry -> loadPathTracking(initial = true)

            PathTrackingAction.OnRefresh -> {
                mutableStateFlow.update { it.copy(isRefreshing = true) }
                loadPathTracking(initial = false)
            }

            PathTrackingAction.LoadPathTracking -> loadPathTracking(initial = true)

            PathTrackingAction.ToggleUserStatus -> {
                if (state.userStatus) {
                    updateUserStatus(false)
                } else {
                    mutableStateFlow.update { it.copy(checkPermission = true) }
                }
            }

            PathTrackingAction.PermissionGranted -> {
                mutableStateFlow.update { it.copy(checkPermission = false) }
                updateUserStatus(true)
            }

            PathTrackingAction.PermissionDismissed -> mutableStateFlow.update {
                it.copy(checkPermission = false)
            }

            is PathTrackingAction.OnPathTrackingClick ->
                sendEvent(PathTrackingEvent.OpenPathInMaps(action.latLngs))

            is PathTrackingAction.Internal.UserStatusChanged -> mutableStateFlow.update {
                it.copy(userStatus = action.status)
            }
        }
    }

    private fun loadPathTracking(initial: Boolean) {
        viewModelScope.launch {
            if (initial) {
                mutableStateFlow.update { it.copy(screenState = ScreenState.Loading) }
            }
            val officeId = prefManager.userData.firstOrNull()?.officeId
            if (officeId == null) {
                mutableStateFlow.update {
                    it.copy(
                        screenState = ScreenState.Empty,
                        isRefreshing = false,
                    )
                }
                return@launch
            }
            try {
                val tracking = repository.getUserPathTracking(officeId.toInt())
                val screen = if (tracking.isEmpty()) {
                    ScreenState.Empty
                } else {
                    ScreenState.Content(data = tracking, freshness = DataFreshness.FRESH)
                }
                mutableStateFlow.update {
                    it.copy(screenState = screen, isRefreshing = false)
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (t: Throwable) {
                mutableStateFlow.update {
                    it.copy(screenState = ScreenState.Error(t), isRefreshing = false)
                }
            }
        }
    }

    private fun updateUserStatus(status: Boolean) {
        viewModelScope.launch {
            prefManager.updateUserStatus(status)
            // Optimistic local update — the prefManager.userInfo collector above
            // will reconcile when the write propagates.
            mutableStateFlow.update { it.copy(userStatus = status) }
        }
    }
}
