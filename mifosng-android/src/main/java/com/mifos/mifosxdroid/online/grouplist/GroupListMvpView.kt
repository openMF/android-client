package com.mifos.mifosxdroid.online.grouplist

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.GroupWithAssociations

/**
 * Created by Rajan Maurya on 06/06/16.
 */
interface GroupListMvpView : MvpView {
    fun showGroupList(centerWithAssociations: CenterWithAssociations?)
    fun showFetchingError(s: String?)
    fun showEmptyGroups(messageId: Int)
    fun showGroups(groupWithAssociations: GroupWithAssociations?)
}