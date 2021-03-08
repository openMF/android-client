package com.mifos.mifosxdroid.online.clientidentifiers

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.noncore.Identifier

/**
 * Created by Rajan Maurya on 06/06/16.
 */
interface ClientIdentifiersMvpView : MvpView {
    fun showUserInterface()
    fun showClientIdentifiers(identifiers: MutableList<Identifier>)
    fun showFetchingError(errorMessage: Int)
    fun identifierDeletedSuccessfully(position: Int)
}