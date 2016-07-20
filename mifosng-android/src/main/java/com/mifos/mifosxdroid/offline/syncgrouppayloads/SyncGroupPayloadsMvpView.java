package com.mifos.mifosxdroid.offline.syncgrouppayloads;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.group.GroupPayload;

import java.util.List;

/**
 * Created by Rajan Maurya on 19/07/16.
 */
public interface SyncGroupPayloadsMvpView extends MvpView {

    void showGroupSyncResponse(List<GroupPayload> groupPayloads);

    void showGroupSyncFailed(String s);
}
