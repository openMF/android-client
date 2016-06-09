package com.mifos.mifosxdroid.fragments.centerlist;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.db.OfflineCenter;

import java.util.List;

/**
 * Created by Rajan Maurya on 09/06/16.
 */
public interface CenterListMvpView extends MvpView {

    void showCenterList(List<OfflineCenter> offlineCenters);

    void showError(String s);
}
