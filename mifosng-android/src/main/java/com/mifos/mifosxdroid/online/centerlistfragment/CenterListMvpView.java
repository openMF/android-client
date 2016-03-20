/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.centerlistfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.List;

/**
 * Created by Rajan Maurya on 17/3/16.
 */
public interface CenterListMvpView extends MvpView{

    void showCenterProgress(boolean status);

    void showCenters(List<Center> centers);

    void showCentersGroupAndMeeting(CenterWithAssociations centerWithAssociations);

    void showResponseError();
}
