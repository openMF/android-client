/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.savingsaccountactivate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.feature.savings.account_activate.SavingsAccountActivateScreen
import com.mifos.feature.savings.account_activate.SavingsAccountActivateViewModel
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Tarun on 01/06/17.
 */
@AndroidEntryPoint
class SavingsAccountActivateFragment : MifosBaseFragment() {

    private val arg: SavingsAccountActivateFragmentArgs by navArgs()
    private val viewmodel: SavingsAccountActivateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel.savingsAccountId = arg.savingsAccountNumber
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SavingsAccountActivateScreen(
                    navigateBack = { findNavController().popBackStack() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        toolbar?.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        toolbar?.visibility = View.VISIBLE
    }
}