package com.mifos.mifosxdroid.offline.syncclientpayloads;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.client.ClientPayload;

import java.util.List;

/**
 * Created by Rajan Maurya on 08/07/16.
 */
public interface SyncClientPayloadsMvpView extends MvpView {

    void showPayloads(List<ClientPayload> clientPayload);

    void showError(String s);

    void showSyncResponse();

    void showClientSyncFailed();

    void showOfflineModeDialog();

    void showPayloadDeletedAndUpdatePayloads(List<ClientPayload> clientPayloads);
}
