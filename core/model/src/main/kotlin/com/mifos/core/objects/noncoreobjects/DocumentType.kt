/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.noncoreobjects

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 03/07/14.
 */
@Parcelize
data class DocumentType(
    var id: Int? = null,

    var name: String? = null,

    var active: Boolean? = null,

    var mandatory: Boolean? = null,

    var description: String? = null,

    var position: Int? = null,
) : Parcelable
