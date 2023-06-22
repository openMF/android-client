package com.mifos.mifosxdroid.online.search

import com.mifos.mifosxdroid.base.MvpView
import com.mifos.objects.SearchedEntity

/**
 * Created by Rajan Maurya on 06/06/16.
 */
interface SearchMvpView : MvpView {
    fun showUserInterface()
    fun showSearchedResources(searchedEntities: MutableList<SearchedEntity>)
    fun showNoResultFound()
    fun showMessage(message: Int)
}