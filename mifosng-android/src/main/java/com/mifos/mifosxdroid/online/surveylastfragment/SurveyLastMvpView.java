package com.mifos.mifosxdroid.online.surveylastfragment;

import com.mifos.mifosxdroid.base.MvpView;
import com.mifos.objects.survey.Scorecard;

/**
 * Created by Rajan Maurya on 19/3/16.
 */
public interface SurveyLastMvpView extends MvpView {

    void submitScoreResultCallBack(Scorecard scorecard);

    void submittingScoreCardError(String s);
}
