package com.mifos.mifosxdroid.online.datatablelistfragment

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.client.Client

interface DataTableListMvpView : MvpView {
    fun showMessage(messageId: Int)
    fun showMessage(message: String?)
    fun showClientCreatedSuccessfully(client: Client)
    fun showWaitingForCheckerApproval(message: Int)
}