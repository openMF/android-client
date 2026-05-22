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
import com.mifos.core.data.pathtracking.store.PathTrackingListKey
import template.core.base.store.screen.DataFreshness
import template.core.base.store.screen.ScreenState
import com.mifos.core.datastore.UserPreferencesRepository
import com.mifos.core.model.objects.users.UserLocation
import com.mifos.core.ui.store.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Path-tracking list ViewModel.
 *
 * Reads flow through Store5 (`repository.pathTrackingStream(...) →
 * ScreenDataStream<List<UserLocation>>`) per RULE-STORE5-FETCH-001 —
 * cache-then-network, auto-refresh on reconnect, `lastContent` preservation,
 * captive-portal detection out-of-box. The Store5 state is mirrored into
 * [PathTrackingState.screenState]; empty content folds to `ScreenState.Empty`
 * so the screen renders the "no tracks" slot. `DataFreshness.UPDATING` from
 * the stream is reflected in [PathTrackingState.isRefreshing] so the
 * `PullToRefreshBox` spinner is driven entirely by the framework — the VM
 * no longer manages refresh state by hand.
 *
 * User-status toggle is a `prefManager.updateUserStatus(...)` write — non-HTTP,
 * so no `SubmitHandler` needed. The VM mirrors `prefManager.userInfo.userStatus`
 * into [PathTrackingState.userStatus] via a collector so the screen has a
 * single state source.
 */
class PathTrackingViewModel(
    private val repository: PathTrackingRepository,
    private val prefManager: UserPreferencesRepository,
) : BaseViewModel<PathTrackingState, PathTrackingEvent, PathTrackingAction>(
    initialState = PathTrackingState(),
) {

    /** Store5 key — populated when prefManager.userData resolves an officeId. */
    private val keyFlow = MutableStateFlow<PathTrackingListKey?>(null)

    /** Cold ScreenDataStream from Store5 — emits once [keyFlow] has a non-null value. */
    private val pathStream = repository.pathTrackingStream(
        keyFlow = keyFlow.filterNotNull(),
        scope = viewModelScope,
    )

    init {
        // Mirror Store5 state into MVI state. Empty Content collapses to
        // ScreenState.Empty; UPDATING freshness drives the pull-to-refresh spinner.
        pathStream.state
            .map { state ->
                if (state is ScreenState.Content<List<UserLocation>> && state.data.isEmpty()) {
                    ScreenState.Empty
                } else {
                    state
                }
            }
            .onEach { screen ->
                val refreshing = screen is ScreenState.Content<*> &&
                    screen.freshness == DataFreshness.UPDATING
                mutableStateFlow.update { it.copy(screenState = screen, isRefreshing = refreshing) }
            }
            .launchIn(viewModelScope)

        // Mirror prefManager.userStatus into the MVI state so the screen has a
        // single source of truth.
        prefManager.userInfo
            .map { it.userStatus ?: false }
            .onEach { status ->
                mutableStateFlow.update { it.copy(userStatus = status) }
            }
            .launchIn(viewModelScope)

        // Resolve userId once and seed the Store5 key flow.
        viewModelScope.launch {
            val officeId = prefManager.userData.first().officeId
            if (officeId != null) {
                keyFlow.value = PathTrackingListKey(userId = officeId.toInt())
            } else {
                // No officeId — nothing to fetch. Surface as Empty without going
                // through the stream (which never emits without a key).
                mutableStateFlow.update {
                    it.copy(screenState = ScreenState.Empty, isRefreshing = false)
                }
            }
        }
    }

    override fun handleAction(action: PathTrackingAction) {
        when (action) {
            PathTrackingAction.NavigateBack -> sendEvent(PathTrackingEvent.NavigateBack)

            PathTrackingAction.OnRetry -> pathStream.retry()
            PathTrackingAction.OnRefresh -> pathStream.refresh()
            PathTrackingAction.LoadPathTracking -> pathStream.retry()

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

    private fun updateUserStatus(status: Boolean) {
        viewModelScope.launch {
            prefManager.updateUserStatus(status)
            // Optimistic local update — the prefManager.userInfo collector above
            // will reconcile when the write propagates.
            mutableStateFlow.update { it.copy(userStatus = status) }
        }
    }
}
