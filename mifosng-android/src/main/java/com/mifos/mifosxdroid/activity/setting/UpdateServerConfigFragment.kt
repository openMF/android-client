package com.mifos.mifosxdroid.activity.setting

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mifos.feature.settings.updateServer.UpdateServerConfigScreenRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateServerConfigFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                UpdateServerConfigScreenRoute(
                    onCloseClick = {
                        // TODO:: make use of navController
//                        findNavController().navigateUp()

                        this@UpdateServerConfigFragment.dismiss()
                    },
                    onSuccessful = {
                        this@UpdateServerConfigFragment.dismiss()

                        showRestartCountdownToast(requireContext(), 2)
                    }
                )
            }
        }
    }
}

private fun showRestartCountdownToast(context: Context, seconds: Int) {
    val countDownTimer = object : CountDownTimer((seconds * 1000).toLong(), 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val secondsRemaining = millisUntilFinished / 1000
            Toast.makeText(
                context,
                "Restarting app in $secondsRemaining seconds",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onFinish() {
            context.restartApplication()
        }
    }
    countDownTimer.start()
}

fun Context.restartApplication() {
    val packageManager: PackageManager = this.packageManager
    val intent: Intent = packageManager.getLaunchIntentForPackage(this.packageName)!!
    val componentName: ComponentName = intent.component!!
    val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
    this.startActivity(restartIntent)
    Runtime.getRuntime().exit(0)
}