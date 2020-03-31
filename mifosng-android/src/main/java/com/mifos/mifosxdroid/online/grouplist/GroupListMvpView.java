package com.mifos.mifosxdroid.online.grouplist;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.GroupWithAssociations;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public interface GroupListMvpView extends MvpView {

    void showGroupList(CenterWithAssociations centerWithAssociations);

    void showFetchingError(String s);

    void showEmptyGroups(int messageId);

    void showGroups(GroupWithAssociations groupWithAssociations);
}
