/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.room.collectionsheet.entity

import com.mifos.core.model.objects.collectionsheets.AttendanceTypeOption
import com.mifos.core.model.objects.collectionsheets.SavingsProduct
import com.mifos.room.savings.entity.PaymentTypeOptionEntity

/**
 * Created by Tarun on 25-07-2017.
 */
data class CollectionSheetResponse(
    var attendanceTypeOptions: List<AttendanceTypeOption> = ArrayList(),

    var dueDate: IntArray? = null,

    var groups: List<GroupCollectionSheet> = ArrayList(),

    var loanProducts: List<com.mifos.core.model.objects.organisations.LoanProducts> = ArrayList(),

    var paymentTypeOptions: List<PaymentTypeOptionEntity> = ArrayList(),

    var savingsProducts: List<SavingsProduct> = ArrayList(),
)
