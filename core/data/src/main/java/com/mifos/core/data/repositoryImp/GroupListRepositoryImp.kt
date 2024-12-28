/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.data.repositoryImp

import com.mifos.core.data.repository.GroupListRepository
import com.mifos.core.dbobjects.group.CenterWithAssociations
import com.mifos.core.dbobjects.group.GroupWithAssociations
import com.mifos.core.network.DataManager
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class GroupListRepositoryImp @Inject constructor(private val dataManager: DataManager) :
    GroupListRepository {

    override fun getGroups(groupId: Int): Observable<GroupWithAssociations> {
        return dataManager.getGroups(groupId)
    }

    override fun getGroupsByCenter(id: Int): Observable<CenterWithAssociations> {
        return dataManager.getGroupsByCenter(id)
    }
}
