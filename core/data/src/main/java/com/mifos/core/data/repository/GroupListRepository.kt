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

import com.mifos.core.entity.group.CenterWithAssociations
import com.mifos.core.entity.group.GroupWithAssociations
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface GroupListRepository {

    fun getGroups(groupId: Int): Observable<GroupWithAssociations>

    fun getGroupsByCenter(id: Int): Observable<CenterWithAssociations>
}
