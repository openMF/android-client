package com.mifos.mifosxdroid.online.activate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.feature.activate.ActivateScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Rajan Maurya on 09/02/17.
 */
@AndroidEntryPoint
class ActivateFragment : Fragment() {

    private val arg: ActivateFragmentArgs by navArgs()
    private var id = 0
    private lateinit var activateType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = arg.clientId
        activateType = arg.activationType
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                ActivateScreen(
                    id = id,
                    activateType = activateType,
                    onBackPressed = {
                        findNavController().popBackStack()
                    }
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