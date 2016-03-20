package com.mifos.mifosxdroid.online.grouplistfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.group.GroupWithAssociations;

/**
 * Created by Rajan Maurya on 18/3/16.
 */
public interface GroupListMvpView extends MvpView {

    void showGroupList(CenterWithAssociations centerWithAssociations);

    void ResponseError(String s);

    void showgroup(GroupWithAssociations groupWithAssociations);
}
