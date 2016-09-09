package com.mifos.mifosxdroid.online.groupslist;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.group.Group;

import java.util.List;

/**
 * Created by Rajan Maurya on 7/6/16.
 */
public interface GroupsListMvpView extends MvpView {

    void showGroups(List<Group> groups);

    void showUserInterface();

    void showLoadMoreGroups(List<Group> clients);

    void showEmptyGroups(int message);

    void unregisterSwipeAndScrollListener();

    void showMessage(int message);

    void showFetchingError();
}
