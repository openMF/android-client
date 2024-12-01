/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.document.navigation

import com.mifos.core.common.utils.Constants

/**
 * Created by Pronay Sarker on 17/08/2024 (4:00 AM)
 */
sealed class DocumentScreens(val route: String) {
    data object DocumentListScreen : DocumentScreens("document_list_screen/{${Constants.ENTITY_ID}}/{${Constants.ENTITY_TYPE}}") {
        fun argument(entityId: Int, entityType: String) = "document_list_screen/$entityId/$entityType"
    }
}
