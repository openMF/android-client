package com.mifos.mifosxdroid.online.createnewclient

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff
import com.mifos.objects.templates.clients.ClientsTemplate

/**
 * Created by Rajan Maurya on 6/6/16.
 */
interface CreateNewClientMvpView : MvpView {
    fun showUserInterface()
    fun showClientTemplate(clientsTemplate: ClientsTemplate?)
    fun showOffices(offices: List<Office?>?)
    fun showStaffInOffices(staffs: List<Staff?>?)
    fun showClientCreatedSuccessfully(s: Int)
    fun showWaitingForCheckerApproval(s: Int)
    fun showMessage(message: Int)
    fun showMessage(message: String?)
    fun setClientId(id: Int?)
    fun showProgress(message: String?)
    fun hideProgress()
}