/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.templates.clients

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mifos.core.model.MifosBaseModel
import kotlinx.parcelize.Parcelize

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
/**
 * Created by rajan on 13/3/16.
 */
@Parcelize
@Entity("ClientTemplateSavingProductsOptions")
data class SavingProductOptions(
    @PrimaryKey
    var id: Int = 0,
    val name: String = "",
    val withdrawalFeeForTransfers: Boolean = false,
    val allowOverdraft: Boolean = false,
) : MifosBaseModel(), Parcelable
