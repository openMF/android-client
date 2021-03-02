package com.mifos.mifosxdroid.online.createnewgroup

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.organisation.Office
import com.mifos.objects.response.SaveResponse

/**
 * Created by Rajan Maurya on 06/06/16.
 */
interface CreateNewGroupMvpView : MvpView {
    fun showOffices(offices: List<Office?>?)
    fun showGroupCreatedSuccessfully(group: SaveResponse?)
    fun showFetchingError(s: String?)
}