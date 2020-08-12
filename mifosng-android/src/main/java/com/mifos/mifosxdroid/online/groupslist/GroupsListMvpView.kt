package com.mifos.mifosxdroid.online.groupslist

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.group.Group

/**
 * Created by Rajan Maurya on 7/6/16.
 */
interface GroupsListMvpView : MvpView {
    fun showGroups(groups: List<Group?>?)
    fun showUserInterface()
    fun showLoadMoreGroups(clients: List<Group?>?)
    fun showEmptyGroups(message: Int)
    fun unregisterSwipeAndScrollListener()
    fun showMessage(message: Int)
    fun showFetchingError()
}