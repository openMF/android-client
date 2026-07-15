/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.account.loan.assignLoanOfficer

import com.mifos.core.model.utils.DateConstants

data class AssignLoanOfficerInput(
    val assignmentDate: String,
    val toLoanOfficerId: Int,
    val fromLoanOfficerId: String? = "",
    val locale: String = DateConstants.LOCALE,
    val dateFormat: String = "dd-MM-yyyy",
)
