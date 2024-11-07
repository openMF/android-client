/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.centerlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import com.mifos.core.objects.group.Center
import com.mifos.feature.center.center_list.ui.CenterListScreen
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.activity.home.HomeActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.dialogfragments.synccenterdialog.SyncCentersDialogFragment
import com.mifos.utils.FragmentConstants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by ishankhanna on 11/03/14.
 **/
@AndroidEntryPoint
class CenterListFragment : MifosBaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireActivity()).apply {
            (activity as HomeActivity).supportActionBar?.title = getString(R.string.centers)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
//                CenterListScreen(
//                    paddingValues = PaddingValues(),
//                    createNewCenter = {
//                        onClickCreateNewCenter()
//                    },
//                    onCenterSelect = { center ->
//                        selectedCenters(center)
//                    },
//                    syncClicked = { selectedCenters ->
//                        syncCenters(selectedCenters)
//                    }
//                )
            }
        }
    }


}
