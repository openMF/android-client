/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.client.clientChargeDialog

import com.mifos.core.model.objects.template.client.ChargeTemplate
import org.jetbrains.compose.resources.StringResource

/**
 * Created by Aditya Gupta on 13/08/23.
 */
sealed class ChargeDialogUiState {

    data object Loading : ChargeDialogUiState()

    data class Error(val message: StringResource) : ChargeDialogUiState()

    data class AllChargesV2(
        val chargeTemplate: ChargeTemplate,
        val selectedChargeName: String,
        val selectedChargeId: Int,
    ) : ChargeDialogUiState()

    data object ChargesCreatedSuccessfully : ChargeDialogUiState()
}
