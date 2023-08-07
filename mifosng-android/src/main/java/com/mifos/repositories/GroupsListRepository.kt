package com.mifos.repositories

import com.mifos.objects.client.Page
import com.mifos.objects.group.Group
import rx.Observable

interface GroupsListRepository {

    fun getGroups(paged: Boolean, offset: Int, limit: Int): Observable<Page<Group>>

    fun databaseGroups(): Observable<Page<Group>>

}