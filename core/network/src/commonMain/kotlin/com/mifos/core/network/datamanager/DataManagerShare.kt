/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.network.datamanager

import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.model.share.ShareTemplate
import kotlinx.coroutines.flow.Flow

class DataManagerShare(
    private val baseApiManager: BaseApiManager,
) {

    fun getShareTemplate(clientId: Int): Flow<ShareTemplate> =
        baseApiManager.shareAccountService.shareProductTemplate(clientId)
}
