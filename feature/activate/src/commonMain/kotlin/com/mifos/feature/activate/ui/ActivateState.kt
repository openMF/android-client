/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.feature.activate.ui

import androidclient.feature.activate.generated.resources.Res
import androidclient.feature.activate.generated.resources.feature_activate_center
import androidclient.feature.activate.generated.resources.feature_activate_client
import androidclient.feature.activate.generated.resources.feature_activate_failed_to_activate_center
import androidclient.feature.activate.generated.resources.feature_activate_failed_to_activate_client
import androidclient.feature.activate.generated.resources.feature_activate_failed_to_activate_group
import androidclient.feature.activate.generated.resources.feature_activate_group
import org.jetbrains.compose.resources.StringResource

/**
 * MVI state for the Activate screen. Holds the resource-type context (client / center / group)
 * so success / failure UI messages can be looked up via [successMessage] / [failureMessage]
 * properties when [SubmitState] transitions to a terminal value.
 */
data class ActivateState(
    val targetType: TargetType = TargetType.Client,
) {
    val successMessage: StringResource
        get() = when (targetType) {
            TargetType.Client -> Res.string.feature_activate_client
            TargetType.Center -> Res.string.feature_activate_center
            TargetType.Group -> Res.string.feature_activate_group
        }

    val failureMessage: StringResource
        get() = when (targetType) {
            TargetType.Client -> Res.string.feature_activate_failed_to_activate_client
            TargetType.Center -> Res.string.feature_activate_failed_to_activate_center
            TargetType.Group -> Res.string.feature_activate_failed_to_activate_group
        }

    enum class TargetType { Client, Center, Group }
}
