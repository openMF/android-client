package com.mifos.repositories

import com.mifos.objects.accounts.GroupAccounts
import com.mifos.objects.group.Group
import com.mifos.objects.group.GroupWithAssociations
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface GroupDetailsRepository {

    fun getGroup(groupId: Int): Observable<Group>

    fun getGroupAccounts(groupId: Int): Observable<GroupAccounts>

    fun getGroupWithAssociations(groupId: Int): Observable<GroupWithAssociations>

}