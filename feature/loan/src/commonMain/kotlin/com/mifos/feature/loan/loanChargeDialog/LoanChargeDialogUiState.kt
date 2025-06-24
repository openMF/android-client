/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.loanChargeDialog

import com.mifos.room.entities.client.ChargesEntity
import org.jetbrains.compose.resources.StringResource

sealed class LoanChargeDialogUiState {

    data object Loading : LoanChargeDialogUiState()

    data class Error(val message: StringResource) : LoanChargeDialogUiState()

    data class AllChargesV3(val list: List<ChargesEntity>) : LoanChargeDialogUiState()

    data object LoanChargesCreatedSuccessfully :
        LoanChargeDialogUiState()
}
