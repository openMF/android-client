package com.mifos.mifosxdroid.activity.login


import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.mifos.feature.auth.login.LoginScreen
import com.mifos.mifosxdroid.activity.home.HomeActivity
//import com.mifos.mifosxdroid.activity.setting.UpdateServerConfigFragment
import com.mifos.mifosxdroid.core.MifosBaseActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by ishankhanna on 08/02/14.
 */
@AndroidEntryPoint
class LoginActivity : MifosBaseActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginScreen(
                homeIntent = {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }, passcodeIntent = {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                },
                onClickToUpdateServerConfig = {
                    // TODO:: show fragment using navController
//                    UpdateServerConfigFragment().show(
//                        supportFragmentManager,
//                        "UpdateServerConfigFragment"
//                    )
                }
            )
        }
    }
}