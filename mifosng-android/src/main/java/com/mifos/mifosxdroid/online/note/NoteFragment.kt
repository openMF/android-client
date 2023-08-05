package com.mifos.mifosxdroid.online.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.NoteAdapter
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.FragmentNotesBinding
import com.mifos.objects.noncore.Note
import com.mifos.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by rahul on 4/3/17.
 */
@AndroidEntryPoint
class NoteFragment : MifosBaseFragment(), NoteMvpView, OnRefreshListener {

    private lateinit var binding: FragmentNotesBinding
    private val arg: NoteFragmentArgs by navArgs()

    @Inject
    lateinit var notePresenter: NotePresenter

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
        notePresenter.loadNote(entityType, entityId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        notePresenter.attachView(this)
        showUserInterface()
        notePresenter.loadNote(entityType, entityId)
        return binding.root
    }

    override fun onRefresh() {
        notePresenter.loadNote(entityType, entityId)
    }

    override fun showUserInterface() {
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

    override fun showNote(notes: List<Note>) {
        this.notes = notes
        noteAdapter?.setNotes(this.notes)
    }

    override fun showEmptyNotes() {
        binding.llError.visibility = View.VISIBLE
        binding.rvNote.visibility = View.GONE
        binding.tvError.setText(R.string.empty_notes)
    }

    override fun showResetVisibility() {
        binding.llError.visibility = View.GONE
        binding.rvNote.visibility = View.VISIBLE
    }

    override fun showError(message: Int) {
        Toaster.show(binding.root, message)
        binding.llError.visibility = View.VISIBLE
        binding.rvNote.visibility = View.GONE
        binding.tvError.text = getString(R.string.failed_to_fetch_notes)
    }

    override fun showProgressbar(show: Boolean) {
        if (show && noteAdapter?.itemCount == 0) {
            showMifosProgressBar()
            binding.swipeContainer.isRefreshing = false
        } else {
            hideMifosProgressBar()
            binding.swipeContainer.isRefreshing = show
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideMifosProgressBar()
        notePresenter.detachView()
    }

    companion object {
        @JvmStatic
        fun newInstance(entityType: String?, entityId: Int): NoteFragment {
            val noteFragment = NoteFragment()
            val args = Bundle()
            args.putString(Constants.ENTITY_TYPE, entityType)
            args.putInt(Constants.ENTITY_ID, entityId)
            noteFragment.arguments = args
            return noteFragment
        }
    }
}