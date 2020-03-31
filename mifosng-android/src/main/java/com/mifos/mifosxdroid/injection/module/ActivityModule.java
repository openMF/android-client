package com.mifos.mifosxdroid.injection.module;

import android.app.Activity;
import android.content.Context;

import com.mifos.mifosxdroid.injection.ActivityContext;

import dagger.Module;
import dagger.Provides;


/**
 * @author Rajan Maurya
 */
@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }

    @Provides
    @ActivityContext
    Context providesContext() {
        return mActivity;
    }
}
