package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.objects.SearchedEntity;

import org.apache.fineract.client.services.SearchApiApi;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

/**
 * Created by Rajan Maurya on 12/12/16.
 */
@Singleton
public class DataManagerSearch {

    public final BaseApiManager baseApiManager;
    public final org.mifos.core.apimanager.BaseApiManager sdkBaseApiManager;

    @Inject
    public DataManagerSearch(BaseApiManager baseApiManager,
                             org.mifos.core.apimanager.BaseApiManager sdkBaseApiManager) {
        this.baseApiManager = baseApiManager;
        this.sdkBaseApiManager = sdkBaseApiManager;
    }

    private SearchApiApi getSearchApi() {
        return sdkBaseApiManager.getSearchApi();
    }

    public Observable<List<SearchedEntity>> searchResources(String query, String resources,
                                                            Boolean exactMatch) {
        // todo: invalid return type of method 'searchData'. It should be 'List<GetSearchResponse>'
        return baseApiManager.getSearchApi().searchResources(query, resources, exactMatch);
    }
}
