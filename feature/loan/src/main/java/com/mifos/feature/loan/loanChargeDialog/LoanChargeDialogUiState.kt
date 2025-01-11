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

import com.mifos.core.entity.client.Charges

/**
 * Created by Aditya Gupta on 16/08/23.
 */
sealed class LoanChargeDialogUiState {

    data object Loading : LoanChargeDialogUiState()

    data class Error(val message: Int) : LoanChargeDialogUiState()

    data class AllChargesV3(val list: List<Charges>) : LoanChargeDialogUiState()

    data object LoanChargesCreatedSuccessfully :
        LoanChargeDialogUiState()
}
