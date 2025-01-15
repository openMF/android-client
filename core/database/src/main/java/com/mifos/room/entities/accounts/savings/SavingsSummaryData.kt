/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.entities.accounts.savings

import android.os.Parcelable
import com.mifos.core.entity.accounts.savings.DepositType
import kotlinx.parcelize.Parcelize

/**
 * Created by Pronay Sarker on 15/08/2024 (9:49 PM)
 */
@Parcelize
data class SavingsSummaryData(val id: Int, val type: DepositType) : Parcelable
