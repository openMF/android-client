/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.noncore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 03/07/14.
 */
@Parcelize
data class Identifier(
    var id: Int? = null,

    var clientId: Int? = null,

    var documentKey: String? = null,

    var documentType: DocumentType? = null,

    var description: String? = null,

    var status: String? = null,
) : Parcelable
