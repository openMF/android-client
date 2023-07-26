package com.mifos.mifosxdroid.online.groupdetails

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.accounts.GroupAccounts
import com.mifos.objects.client.Client
import com.mifos.objects.group.Group

/**
 * Created by Rajan Maurya on 07/06/16.
 */
interface GroupDetailsMvpView : MvpView {
    fun showGroup(group: Group?)
    fun showGroupClients(clients: List<Client>)
    fun showGroupAccounts(groupAccounts: GroupAccounts?)
    fun showFetchingError(errorMessage: Int)
}