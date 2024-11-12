package com.mifos.mifosxdroid.online.centerdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.fragment.findNavController
import com.mifos.feature.center.center_details.CenterDetailsScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Rajan Maurya on 05/02/17.
 */
@AndroidEntryPoint
class CenterDetailsFragment : MifosBaseFragment() {

    private var centerId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            centerId = requireArguments().getInt(Constants.CENTER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            setContent {
                CenterDetailsScreen(
                    onBackPressed = {
                        requireActivity().onBackPressed()
                    },
                    onActivateCenter = {
                        onClickActivateCenter()
                    },
                    addSavingsAccount = { centerId ->
                        addCenterSavingAccount(centerId)
                    },
                    groupList = { centerId ->
                        loadGroupsOfCenter(centerId)
                    })
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

    private fun onClickActivateCenter() {
//        val action = CenterDetailsFragmentDirections.actionCenterDetailsFragmentToActivateFragment(
//            centerId,
//            Constants.ACTIVATE_CENTER
//        )
//        findNavController().navigate(action)
    }

    private fun addCenterSavingAccount(centerId: Int) {
//        val action =
//            CenterDetailsFragmentDirections.actionCenterDetailsFragmentToSavingsAccountFragment(
//                centerId,
//                true
//            )
//        findNavController().navigate(action)
    }

    private fun loadGroupsOfCenter(centerId: Int) {
    }

}