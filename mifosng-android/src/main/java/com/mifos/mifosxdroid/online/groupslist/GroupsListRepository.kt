package com.mifos.mifosxdroid.online.groupslist

import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Group
import rx.Observable

/**
 * Created by Aditya Gupta on 08/08/23.
 */
interface GroupsListRepository {

    fun getGroups(paged: Boolean, offset: Int, limit: Int): Observable<Page<Group>>

    fun databaseGroups(): Observable<Page<Group>>

}