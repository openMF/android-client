package com.mifos.mifosxdroid.online.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.navigation.fragment.navArgs
import com.mifos.feature.note.NoteScreen
import com.mifos.mifosxdroid.core.MifosBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by rahul on 4/3/17.
 */
@AndroidEntryPoint
class NoteFragment : MifosBaseFragment() {

//    private val arg: NoteFragmentArgs by navArgs()

//    private var entityType: String? = null
//    private var entityId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        entityId = arg.entiyId
//        entityType = arg.entityType
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        toolbar?.visibility = View.GONE
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                NoteScreen(
//                    entityType = entityType,
//                    entityId = entityId,
                    onBackPressed = { activity?.supportFragmentManager?.popBackStack() }
                )
            }
        }
    }
}