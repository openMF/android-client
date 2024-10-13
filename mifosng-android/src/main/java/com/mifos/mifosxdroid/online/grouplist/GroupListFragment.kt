/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.grouplist

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
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.navigation.ClientListArgs
import com.mifos.feature.center.center_group_list.GroupListScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupListFragment : Fragment() {

//    private val arg: GroupListFragmentArgs by navArgs()
    private var centerId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        centerId = arg.centerId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                GroupListScreen(
                    onBackPressed = {
                        findNavController().popBackStack()
                    },
                    loadClientsOfGroup = {
                        loadClientsOfGroup(it)
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

    private fun loadClientsOfGroup(clientList: List<Client>) {
//        val action = GroupListFragmentDirections.actionGroupListFragmentToClientListFragment(
//            ClientListArgs(clientList, true)
//        )
//        findNavController().navigate(action)
    }
}