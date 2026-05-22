/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.recurringdeposit

import com.mifos.core.model.objects.common.CodeValue

/**
 * Recurring-deposit lookup typealiases — every Fineract `{ id, code, value }` enum
 * the recurring-deposit API surface returns. All compile to [CodeValue]; the
 * per-domain names are kept for call-site readability and to preserve the
 * existing import paths of consumers.
 */
typealias AttributeName = CodeValue
typealias ConditionType = CodeValue
typealias DepositPeriodFrequency = CodeValue
typealias DepositType = CodeValue
typealias EntityType = CodeValue
typealias IncentiveType = CodeValue
typealias InMultiplesOfDepositTermType = CodeValue
typealias InterestCalculationDaysInYearType = CodeValue
typealias InterestCalculationType = CodeValue
typealias InterestCompoundingPeriodType = CodeValue
typealias InterestPostingPeriodType = CodeValue
typealias LockinPeriodFrequencyType = CodeValue
typealias MaxDepositTermType = CodeValue
typealias MinDepositTermType = CodeValue
typealias PeriodType = CodeValue
typealias RecurringFrequencyType = CodeValue
