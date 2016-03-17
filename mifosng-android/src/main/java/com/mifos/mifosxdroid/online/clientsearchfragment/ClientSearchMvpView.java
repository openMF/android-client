/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientsearchfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.SearchedEntity;
import java.util.List;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public interface ClientSearchMvpView extends MvpView {

    void showsearchresult(List<SearchedEntity> searchedEntities);

    void showSearchNotFound();
}
