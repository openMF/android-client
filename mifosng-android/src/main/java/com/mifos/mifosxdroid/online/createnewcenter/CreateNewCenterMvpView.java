package com.mifos.mifosxdroid.online.createnewcenter;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.response.SaveResponse;

import java.util.List;

/**
 * Created by Rajan Maurya on 06/06/16.
 */
public interface CreateNewCenterMvpView extends MvpView {

    void showOffices(List<Office> offices);

    void centerCreatedSuccessfully(SaveResponse saveResponse);

    void showFetchingError(int errorMessage);

    void showFetchingError(String s);
}
