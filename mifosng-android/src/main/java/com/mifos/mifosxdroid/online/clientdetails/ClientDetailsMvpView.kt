package com.mifos.mifosxdroid.online.clientdetails

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.accounts.ClientAccounts
import com.mifos.objects.client.Client
import okhttp3.ResponseBody

/**
 * Created by Rajan Maurya on 07/06/16.
 */
interface ClientDetailsMvpView : MvpView {
    fun showClientInformation(client: Client?)
    fun showUploadImageSuccessfully(response: ResponseBody?, imagePath: String?)
    fun showUploadImageFailed(s: String?)
    fun showUploadImageProgressbar(b: Boolean)
    fun showClientImageDeletedSuccessfully()
    fun showClientAccount(clientAccounts: ClientAccounts)
    fun showFetchingError(s: String?)
}