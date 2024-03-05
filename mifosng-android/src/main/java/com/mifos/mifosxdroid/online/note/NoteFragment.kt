package com.mifos.mifosxdroid.online.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.mifos.core.objects.noncore.Note
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.NoteAdapter
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentNotesBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by rahul on 4/3/17.
 */
@AndroidEntryPoint
class NoteFragment : MifosBaseFragment(), OnRefreshListener {

    private lateinit var binding: FragmentNotesBinding
    private val arg: NoteFragmentArgs by navArgs()

    private lateinit var viewModel: NoteViewModel

    @JvmField
    @Inject
    var noteAdapter: NoteAdapter? = null
    private var entityType: String? = null
    private var entityId = 0
    private var notes: List<Note> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entityId = arg.entiyId
        entityType = arg.entityType
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadNote(entityType, entityId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        showUserInterface()
        viewModel.loadNote(entityType, entityId)
        viewModel.noteUiState.observe(viewLifecycleOwner) {
            when (it) {
                is NoteUiState.ShowEmptyNotes -> {
                    showProgressbar(false)
                    showEmptyNotes()
                }

                is NoteUiState.ShowError -> {
                    showProgressbar(false)
                    showEmptyNotes()
                }

                is NoteUiState.ShowNote -> {
                    showProgressbar(false)
                    showNote(it.note)
                }

                is NoteUiState.ShowProgressbar -> showProgressbar(true)
                is NoteUiState.ShowResetVisibility -> {
                    showProgressbar(false)
                    showResetVisibility()
                }
            }
        }

        return binding.root
    }

    override fun onRefresh() {
        viewModel.loadNote(entityType, entityId)
    }

    private fun showUserInterface() {
        setToolbarTitle(resources.getString(R.string.note))
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvNote.layoutManager = mLayoutManager
        binding.rvNote.setHasFixedSize(true)
        binding.rvNote.adapter = noteAdapter
        binding.swipeContainer.setColorSchemeColors(
            *activity
                ?.resources!!.getIntArray(R.array.swipeRefreshColors)
        )
        binding.swipeContainer.setOnRefreshListener(this)
    }

    private fun showNote(notes: List<Note>) {
        this.notes = notes
        noteAdapter?.setNotes(this.notes)
    }

    private fun showEmptyNotes() {
        binding.llError.visibility = View.VISIBLE
        binding.rvNote.visibility = View.GONE
        binding.tvError.setText(R.string.empty_notes)
    }

    private fun showResetVisibility() {
        binding.llError.visibility = View.GONE
        binding.rvNote.visibility = View.VISIBLE
    }

    private fun showError(message: Int) {
        Toaster.show(binding.root, message)
        binding.llError.visibility = View.VISIBLE
        binding.rvNote.visibility = View.GONE
        binding.tvError.text = getString(R.string.failed_to_fetch_notes)
    }

    private fun showProgressbar(show: Boolean) {
        if (show && noteAdapter?.itemCount == 0) {
            showMifosProgressBar()
            binding.swipeContainer.isRefreshing = false
        } else {
            hideMifosProgressBar()
            binding.swipeContainer.isRefreshing = show
        }
    }
}