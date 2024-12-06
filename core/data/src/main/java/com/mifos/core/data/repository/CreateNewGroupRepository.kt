/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repository

import com.mifos.core.objects.group.GroupPayload
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse
import rx.Observable

/**
 * Created by Aditya Gupta on 10/08/23.
 */
interface CreateNewGroupRepository {

    fun offices(): Observable<List<Office>>

    fun createGroup(groupPayload: GroupPayload): Observable<SaveResponse>
}
