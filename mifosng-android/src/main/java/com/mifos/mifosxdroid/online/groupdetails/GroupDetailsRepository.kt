package com.mifos.mifosxdroid.online.groupdetails

import com.mifos.core.objects.accounts.GroupAccounts
import com.mifos.core.objects.group.Group
import com.mifos.core.objects.group.GroupWithAssociations
import rx.Observable

/**
 * Created by Aditya Gupta on 06/08/23.
 */
interface GroupDetailsRepository {

    fun getGroup(groupId: Int): Observable<Group>

    fun getGroupAccounts(groupId: Int): Observable<GroupAccounts>

    fun getGroupWithAssociations(groupId: Int): Observable<GroupWithAssociations>

}