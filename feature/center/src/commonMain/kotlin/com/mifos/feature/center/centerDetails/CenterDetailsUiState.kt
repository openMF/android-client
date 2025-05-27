/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.center.centerDetails

import com.mifos.core.model.objects.groups.CenterInfo
import com.mifos.room.entities.group.CenterWithAssociations
import org.jetbrains.compose.resources.StringResource

sealed class CenterDetailsUiState {

    data object Loading : CenterDetailsUiState()

    data class Error(val message: StringResource) : CenterDetailsUiState()

    data class CenterDetails(
        val centerWithAssociations: CenterWithAssociations,
        val centerInfo: CenterInfo,
    ) : CenterDetailsUiState()
}
