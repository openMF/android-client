package com.mifos.mifosxdroid.offline.syncclientpayloads;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.ClientPayload;

import java.util.List;

/**
 * Created by Rajan Maurya on 08/07/16.
 */
public interface SyncClientPayloadsMvpView extends MvpView {

    void showPayloads(List<ClientPayload> clientPayload);

    void showError(int stringId);

    void showSyncResponse();

    void showClientSyncFailed(String errorMessage);

    void showOfflineModeDialog();

    void showClientPayloadUpdated(ClientPayload clientPayload);

    void showPayloadDeletedAndUpdatePayloads(List<ClientPayload> clientPayloads);
}
