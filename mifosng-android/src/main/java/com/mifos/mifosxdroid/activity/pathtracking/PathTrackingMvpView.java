package com.mifos.mifosxdroid.activity.pathtracking;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.user.UserLocation;

import java.util.List;

/**
 * Created by Rajan Maurya on 24/01/17.
 */

public interface PathTrackingMvpView extends MvpView {

    void showUserInterface();

    void showPathTracking(List<UserLocation> userLocations);

    void showEmptyPathTracking();

    void showError();

}
