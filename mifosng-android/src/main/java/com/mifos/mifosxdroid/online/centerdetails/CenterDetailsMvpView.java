package com.mifos.mifosxdroid.online.centerdetails;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.group.CenterWithAssociations;

import java.util.Map;

/**
 * Created by ilya on 12/13/16.
 */

public interface CenterDetailsMvpView extends MvpView {

    void showCenterDetails(CenterWithAssociations centerWithAssociations);

    void showMeetingDetails(CenterWithAssociations centerWithAssociations);

    void showSummaryInfo(Map<String, Integer> summaryInfo);
}
