package com.mifos.mifosxdroid.online.groupslist

import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Group
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 08/08/23.
 */
class GroupsListRepositoryImp @Inject constructor(private val dataManagerGroups: DataManagerGroups) :
    GroupsListRepository {
    override fun getGroups(paged: Boolean, offset: Int, limit: Int): Observable<Page<Group>> {
        return dataManagerGroups.getGroups(paged, offset, limit)
    }

    override fun databaseGroups(): Observable<Page<Group>> {
        return dataManagerGroups.databaseGroups
    }

}