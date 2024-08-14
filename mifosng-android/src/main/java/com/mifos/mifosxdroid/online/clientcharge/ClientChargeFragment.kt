/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.clientcharge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.feature.client.clientCharges.ClientChargesScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by nellyk on 1/22/2016.
 */
@AndroidEntryPoint
class ClientChargeFragment : MifosBaseFragment() {

    private val arg: ClientChargeFragmentArgs by navArgs()
    private var clientId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clientId = arg.clientId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ClientChargesScreen(
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

    fun addChargesDialog() {
//        val chargeDialogFragment = ChargeDialogFragment.newInstance(clientId)
//        chargeDialogFragment.setOnChargeCreatedListener(this)
//        val fragmentTransaction = requireActivity().supportFragmentManager
//            .beginTransaction()
//        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CHARGE_LIST)
//        chargeDialogFragment.show(fragmentTransaction, "Charge Dialog Fragment")
    }

}
