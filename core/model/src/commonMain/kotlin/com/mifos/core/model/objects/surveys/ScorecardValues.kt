/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.surveys

import kotlinx.serialization.Serializable

/**
 * Created by Nasim Banu on 28,January,2016.
 */
@Serializable
data class ScorecardValues(
    var questionId: Int? = null,

    var responseId: Int? = null,

    var value: Int? = null,
)
