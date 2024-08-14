/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.clientidentifiers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.core.common.utils.Constants
import com.mifos.feature.client.clientIdentifiers.ClientIdentifiersScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientIdentifiersFragment : MifosBaseFragment() {

    private val arg: ClientIdentifiersFragmentArgs by navArgs()
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
        return ComposeView(requireContext()).apply {
            setContent {
                ClientIdentifiersScreen(
                    onBackPressed = {
                        findNavController().popBackStack()
                    },
                    onDocumentClicked = {
                        documentList(it)
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

    private fun documentList(identifierId: Int) {
        val action =
            ClientIdentifiersFragmentDirections.actionClientIdentifiersFragmentToDocumentListFragment(
                identifierId,
                Constants.ENTITY_TYPE_CLIENTS
            )
        findNavController().navigate(action)
    }
}