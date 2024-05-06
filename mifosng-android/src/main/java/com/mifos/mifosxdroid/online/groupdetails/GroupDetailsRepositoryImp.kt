package com.mifos.mifosxdroid.online.groupdetails

import com.mifos.core.network.datamanager.DataManagerGroups
import com.mifos.core.objects.accounts.GroupAccounts
import com.mifos.core.objects.group.Group
import com.mifos.core.objects.group.GroupWithAssociations
import rx.Observable
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 06/08/23.
 */
class GroupDetailsRepositoryImp @Inject constructor(private val dataManagerGroups: DataManagerGroups) :
    GroupDetailsRepository {

    override fun getGroup(groupId: Int): Observable<Group> {
        return dataManagerGroups.getGroup(groupId)
    }

    override fun getGroupAccounts(groupId: Int): Observable<GroupAccounts> {
        return dataManagerGroups.getGroupAccounts(groupId)
    }

    override fun getGroupWithAssociations(groupId: Int): Observable<GroupWithAssociations> {
        return dataManagerGroups.getGroupWithAssociations(groupId)
    }
}