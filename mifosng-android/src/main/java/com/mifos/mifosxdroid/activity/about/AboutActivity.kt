package com.mifos.mifosxdroid.activity.about

import android.os.Bundle
import androidx.activity.compose.setContent
import com.mifos.feature.about.AboutScreen
import com.mifos.mifosxdroid.core.MifosBaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutActivity : MifosBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AboutScreen(onBackPressed = {
                finish()
            })
        }
    }
}