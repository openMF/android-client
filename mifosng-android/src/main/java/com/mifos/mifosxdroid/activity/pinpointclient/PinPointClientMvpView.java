package com.mifos.mifosxdroid.activity.pinpointclient;

import androidx.annotation.Nullable;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.ClientAddressResponse;

import java.util.List;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public interface PinPointClientMvpView extends MvpView {

    void showUserInterface();

    void requestPermission(int requestCode);

    void showClientPinpointLocations(List<ClientAddressResponse> clientAddressResponses);

    void showFailedToFetchAddress();

    void showEmptyAddress();

    void showPlacePiker(int requestCode);

    void showProgressDialog(boolean show, @Nullable Integer message);

    void showMessage(int message);

    void updateClientAddress(int message);
}
