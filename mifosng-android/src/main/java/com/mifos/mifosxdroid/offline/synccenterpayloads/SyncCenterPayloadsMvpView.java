package com.mifos.mifosxdroid.offline.synccenterpayloads;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.services.data.CenterPayload;

import java.util.List;

/**
 * Created by mayankjindal on 04/07/17.
 */

public interface SyncCenterPayloadsMvpView extends MvpView {

    void showOfflineModeDialog();

    void showCenters(List<CenterPayload> centerPayloads);

    void showCenterSyncResponse();

    void showCenterSyncFailed(String errorMessage);

    void showPayloadDeletedAndUpdatePayloads(List<CenterPayload> centerPayloads);

    void showCenterPayloadUpdated(CenterPayload centerPayload);

    void showError(int stringId);
}
