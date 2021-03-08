package com.mifos.mifosxdroid.online.createnewcenter

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.organisation.Office
import com.mifos.objects.response.SaveResponse

/**
 * Created by Rajan Maurya on 06/06/16.
 */
interface CreateNewCenterMvpView : MvpView {
    fun showOffices(offices: List<Office?>?)
    fun centerCreatedSuccessfully(saveResponse: SaveResponse?)
    fun showFetchingError(errorMessage: Int)
    fun showFetchingError(s: String?)
}