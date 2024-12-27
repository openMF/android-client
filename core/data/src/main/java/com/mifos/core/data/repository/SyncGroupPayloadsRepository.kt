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

import com.mifos.core.modelobjects.responses.SaveResponse
import com.mifos.core.objects.group.GroupPayload
import rx.Observable

interface SyncGroupPayloadsRepository {

    fun allDatabaseGroupPayload(): Observable<List<GroupPayload>>

    fun createGroup(groupPayload: GroupPayload): Observable<SaveResponse>

    fun deleteAndUpdateGroupPayloads(id: Int): Observable<List<GroupPayload>>

    fun updateGroupPayload(groupPayload: GroupPayload): Observable<GroupPayload>
}
