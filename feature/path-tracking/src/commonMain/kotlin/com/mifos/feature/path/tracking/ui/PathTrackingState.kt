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

import template.core.base.store.screen.ScreenState
import com.mifos.core.model.objects.users.UserLatLng
import com.mifos.core.model.objects.users.UserLocation

/**
 * MVI state for the "Track my path" screen. The path-tracking list read flow
 * lives in [screenState] (`ScreenState<List<UserLocation>>`); the pull-to-refresh
 * spinner state + user-status toggle (location-tracking on/off) live in this
 * top-level state.
 *
 * The user-status toggle is a `prefManager.updateUserStatus(...)` write — not an
 * HTTP mutation — so it does **not** need its own `SubmitState<Unit>`. We just
 * fire the suspend write from the VM and let the `prefManager.userInfo` Flow
 * feed [userStatus] back into the state via a `stateIn` collector.
 */
data class PathTrackingState(
    val screenState: ScreenState<List<UserLocation>> = ScreenState.Loading,
    val isRefreshing: Boolean = false,
    val userStatus: Boolean = false,
    val checkPermission: Boolean = false,
)

sealed interface PathTrackingEvent {
    data object NavigateBack : PathTrackingEvent

    /**
     * Open the platform map app (e.g. Google Maps on Android) routed from the
     * start coordinate to the end coordinate of the tapped track. Platform
     * actuals consume this and dispatch the relevant intent / external link.
     */
    data class OpenPathInMaps(val latLngs: List<UserLatLng>) : PathTrackingEvent
}

sealed interface PathTrackingAction {
    data object NavigateBack : PathTrackingAction
    data object OnRetry : PathTrackingAction
    data object OnRefresh : PathTrackingAction
    data object LoadPathTracking : PathTrackingAction

    /** User tapped the toggle button in the top-app-bar. */
    data object ToggleUserStatus : PathTrackingAction

    /** User granted location permission in the [PermissionBox] dialog. */
    data object PermissionGranted : PathTrackingAction

    /** User dismissed the permission dialog without granting. */
    data object PermissionDismissed : PathTrackingAction

    /** User tapped a track entry — open the platform map app with the route. */
    data class OnPathTrackingClick(val latLngs: List<UserLatLng>) : PathTrackingAction

    sealed interface Internal : PathTrackingAction {
        data class UserStatusChanged(val status: Boolean) : Internal
    }
}
