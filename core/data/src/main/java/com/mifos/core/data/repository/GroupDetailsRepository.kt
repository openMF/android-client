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

import com.mifos.room.entities.accounts.GroupAccounts
import com.mifos.room.entities.group.Group
import com.mifos.room.entities.group.GroupWithAssociations
import kotlinx.coroutines.flow.Flow
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface GroupDetailsRepository {

    fun getGroup(groupId: Int): Flow<Group>

    fun getGroupAccounts(groupId: Int): Flow<GroupAccounts>

    fun getGroupWithAssociations(groupId: Int): Observable<GroupWithAssociations>
}
