package com.mifos.mifosxdroid.activity.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mifos.core.common.utils.Constants
import com.mifos.feature.settings.settings.SettingsScreen
import com.mifos.mifosxdroid.activity.login.LoginActivity

/**
 * Created by mayankjindal on 22/07/17.
 */
class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SettingsScreen(
                    onBackPressed = {
                        findNavController().popBackStack()
                    },
                    navigateToLoginScreen = {
                        Intent(requireContext(), LoginActivity::class.java).also {
                            startActivity(it)
                        }
                    },
                    changePasscode = {

                    },
                    languageChanged = {
                        val intent = Intent(activity, activity?.javaClass)
                        intent.putExtra(Constants.HAS_SETTING_CHANGED, true)
                        startActivity(intent)
                        activity?.finish()
                    },
//                    serverConfig = {
//                        findNavController().navigate(R.id.updateServerConfigFragment)
//                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }
}
