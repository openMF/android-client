package com.mifos.mifosxdroid.online.centerlist;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.List;

/**
 * Created by Rajan Maurya on 5/6/16.
 */
public interface CenterListMvpView extends MvpView {

    void showUserInterface();

    void showCenters(List<Center> centers);

    void showMoreCenters(List<Center> centers);

    void showEmptyCenters(int message);

    void showMessage(int message);

    void unregisterSwipeAndScrollListener();

    void showCentersGroupAndMeeting(CenterWithAssociations centerWithAssociations, int id);

    void showFetchingError();
}
