package com.mifos.mifosxdroid.online.clientlist

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.client.Client

/**
 * Created by Rajan Maurya on 6/6/16.
 */
interface ClientListMvpView : MvpView {
    fun showUserInterface()
    fun showClientList(clients: List<Client>)
    fun showLoadMoreClients(clients: List<Client>?)
    fun showEmptyClientList(message: Int)
    fun unregisterSwipeAndScrollListener()
    fun showMessage(message: Int)
    fun showError()
}