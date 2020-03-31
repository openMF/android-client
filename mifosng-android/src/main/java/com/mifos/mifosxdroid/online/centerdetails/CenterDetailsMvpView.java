package com.mifos.mifosxdroid.online.centerdetails;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.group.CenterInfo;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.List;

/**
 * Created by Rajan Maurya on 05/02/17.
 */

public interface CenterDetailsMvpView extends MvpView {

    void showProgressbar(boolean show);

    void showCenterDetails(CenterWithAssociations centerWithAssociations);

    void showMeetingDetails(CenterWithAssociations centerWithAssociations);

    void showSummaryInfo(List<CenterInfo> centerInfos);

    void showErrorMessage(int message);
}
