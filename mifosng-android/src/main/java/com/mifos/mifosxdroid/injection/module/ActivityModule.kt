package com.mifos.mifosxdroid.injection.module

import android.app.Activity
import android.content.Context
import com.mifos.mifosxdroid.injection.ActivityContext
import dagger.Module
import dagger.Provides

/**
 * @author Rajan Maurya
 */
@Module
class ActivityModule(private val mActivity: Activity) {
    @Provides
    fun provideActivity(): Activity {
        return mActivity
    }

    @Provides
    @ActivityContext
    fun providesContext(): Context {
        return mActivity
    }
}