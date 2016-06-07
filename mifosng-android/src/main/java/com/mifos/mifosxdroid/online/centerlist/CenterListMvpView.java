package com.mifos.mifosxdroid.online.centerlist;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.List;

/**
 * Created by Rajan Maurya on 5/6/16.
 */
public interface CenterListMvpView extends MvpView {

    void showCenters(List<Center> centers);

    void showCentersGroupAndMeeting(CenterWithAssociations centerWithAssociations, int id);

    void showCenterGroupFetchinError();
}
