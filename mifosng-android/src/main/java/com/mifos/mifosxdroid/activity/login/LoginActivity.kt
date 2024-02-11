/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.activity.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.mifos.feature.auth.login.presentation.LoginScreen
import com.mifos.mifosxdroid.activity.home.HomeActivity
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.passcode.PassCodeActivity
import com.mifos.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by ishankhanna on 08/02/14.
 */
@AndroidEntryPoint
class LoginActivity : MifosBaseActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen(homeIntent = {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }, passcodeIntent = {
                val intent = Intent(this, PassCodeActivity::class.java)
                intent.putExtra(Constants.INTIAL_LOGIN, true)
                startActivity(intent)
                finish()
            })
        }
    }
}