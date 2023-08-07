package com.mifos.repositories

import com.mifos.api.datamanager.DataManagerGroups
import com.mifos.objects.client.Page
import com.mifos.objects.group.Group
import rx.Observable
import javax.inject.Inject

class GroupsListRepositoryImp @Inject constructor(private val dataManagerGroups: DataManagerGroups) : GroupsListRepository{
    override fun getGroups(paged: Boolean, offset: Int, limit: Int): Observable<Page<Group>> {
        return dataManagerGroups.getGroups(paged,offset,limit)
    }

    override fun databaseGroups(): Observable<Page<Group>> {
        return dataManagerGroups.databaseGroups
    }

}