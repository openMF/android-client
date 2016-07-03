package com.mifos.mifosxdroid.online.centerlist;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

/**
 * Created by Rajan Maurya on 5/6/16.
 */
public interface CenterListMvpView extends MvpView {

    void showCenters(Page<Center> centerPage);

    void showCentersGroupAndMeeting(CenterWithAssociations centerWithAssociations, int id);

    void showFetchingError(String s);
}
