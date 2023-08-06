package com.mifos.repositories

import com.mifos.api.DataManager
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.GroupWithAssociations
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