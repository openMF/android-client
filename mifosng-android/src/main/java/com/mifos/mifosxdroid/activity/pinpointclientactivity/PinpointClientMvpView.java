package com.mifos.mifosxdroid.activity.pinpointclientactivity;

import com.mifos.api.model.GpsCoordinatesResponse;
import com.mifos.mifosxdroid.base.MvpView;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public interface PinpointClientMvpView extends MvpView {

    void updateGpsResquestCallBack(GpsCoordinatesResponse gpsCoordinatesResponse);

    void ResponseError(String s);
}
