/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.loan.groupLoanAccount

import com.mifos.core.model.objects.template.loan.GroupLoanTemplate
import org.jetbrains.compose.resources.StringResource

sealed class GroupLoanAccountUiState {

    data object Loading : GroupLoanAccountUiState()

    data class Error(val message: StringResource) : GroupLoanAccountUiState()

    data object GroupLoanAccountCreatedSuccessfully : GroupLoanAccountUiState()

    data class GroupLoanAccountTemplate(val groupLoanTemplate: GroupLoanTemplate) :
        GroupLoanAccountUiState()
}
