package com.mifos.mifosxdroid.online.clientsearch;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.SearchedEntity;

import java.util.List;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public interface ClientSearchMvpView extends MvpView {

    void showClientsSearched(List<SearchedEntity> searchedEntities);

    void showFetchingError(String s);
}
