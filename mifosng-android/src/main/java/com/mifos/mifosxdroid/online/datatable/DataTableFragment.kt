package com.mifos.mifosxdroid.online.datatable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mifos.core.objects.noncore.DataTable
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Rajan Maurya on 12/02/17.
 */
@AndroidEntryPoint
class DataTableFragment : MifosBaseFragment() {

    private val arg: DataTableFragmentArgs by navArgs()

    private var tableName: String? = null
    private var entityId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tableName = arg.tableName
        entityId = arg.entityId
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                DataTableScreen(
                    tableName = tableName,
                    navigateBack = {
                        findNavController().popBackStack()                    },
                    onClick = {
                        navigateToDatatableData(dataTable = it)
                    }
                )
            }
        }
    }

    private fun navigateToDatatableData(dataTable: DataTable) {
        val action =
            DataTableFragmentDirections.actionDataTableFragmentToDataTableDataFragment(
                dataTable,
                entityId
            )
        findNavController().navigate(action)
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