package com.mifos.mifosxdroid.activity.pinpointclient

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.navigation.navArgs
import com.mifos.feature.client.clientPinpoint.PinpointClientScreen
import com.mifos.mifosxdroid.core.MifosBaseActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author fomenkoo
 */
@AndroidEntryPoint
class PinpointClientActivity : MifosBaseActivity() {

    private val arg: PinpointClientActivityArgs by navArgs()
    private var clientId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clientId = arg.clientId
        setContent {
            PinpointClientScreen(
                onBackPressed = {
                    finish()
                }
            )
        }
    }
}