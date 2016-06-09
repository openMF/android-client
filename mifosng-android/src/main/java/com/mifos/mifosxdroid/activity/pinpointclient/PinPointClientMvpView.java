package com.mifos.mifosxdroid.activity.pinpointclient;

import com.mifos.api.model.GpsCoordinatesResponse;
import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface PinPointClientMvpView extends MvpView {

    void showGpsDataUpdatedSuccessfully(GpsCoordinatesResponse gpsCoordinatesResponse);

    void showFailedToUpdateGpsData(String s);
}
