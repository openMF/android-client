/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.savingsaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.feature.savings.account.SavingAccountViewModel
import com.mifos.feature.savings.account.SavingsAccountScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by nellyk on 1/22/2016.
 *
 *
 * Use this Dialog Fragment to Create and/or Update charges
 */
@AndroidEntryPoint
class SavingsAccountFragment : MifosBaseFragment() {

    private val arg: SavingsAccountFragmentArgs by navArgs()
    val viewmodel : SavingAccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel.clientId = arg.id
        viewmodel.groupId = arg.id
        viewmodel.isGroupAccount = arg.isGroupAccount
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SavingsAccountScreen {
                    findNavController().popBackStack()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        toolbar?.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        toolbar?.visibility = View.VISIBLE
    }
}