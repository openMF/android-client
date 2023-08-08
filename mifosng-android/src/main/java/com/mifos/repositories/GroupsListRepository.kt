package com.mifos.repositories

import com.mifos.objects.client.Page
import com.mifos.objects.group.Group
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface GroupsListRepository {

    fun getGroups(paged: Boolean, offset: Int, limit: Int): Observable<Page<Group>>

    fun databaseGroups(): Observable<Page<Group>>

}