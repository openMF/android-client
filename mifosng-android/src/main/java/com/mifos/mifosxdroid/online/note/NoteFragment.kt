package com.mifos.mifosxdroid.online.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.NoteAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.objects.noncore.Note
import com.mifos.utils.Constants
import java.util.*
import javax.inject.Inject

/**
 * Created by rahul on 4/3/17.
 */
class NoteFragment : MifosBaseFragment(), NoteMvpView, OnRefreshListener {
    @JvmField
    @BindView(R.id.rv_note)
    var rvNote: RecyclerView? = null

    @JvmField
    @BindView(R.id.swipe_container)
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    @JvmField
    @BindView(R.id.tv_error)
    var tvError: TextView? = null

    @JvmField
    @BindView(R.id.iv_error)
    var ivError: ImageView? = null

    @JvmField
    @BindView(R.id.ll_error)
    var llError: LinearLayout? = null

    @JvmField
    @Inject
    var notePresenter: NotePresenter? = null

    @JvmField
    @Inject
    var noteAdapter: NoteAdapter? = null
    private lateinit var rootView: View
    private var entityType: String? = null
    private var entityId = 0
    private var notes: List<Note>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        notes = ArrayList()
        if (arguments != null) {
            entityType = requireArguments().getString(Constants.ENTITY_TYPE)
            entityId = requireArguments().getInt(Constants.ENTITY_ID)
        }
    }

    override fun onResume() {
        super.onResume()
        notePresenter!!.loadNote(entityType, entityId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_notes, container, false)
        ButterKnife.bind(this, rootView)
        notePresenter!!.attachView(this)
        showUserInterface()
        notePresenter!!.loadNote(entityType, entityId)
        return rootView
    }

    override fun onRefresh() {
        notePresenter!!.loadNote(entityType, entityId)
    }

    override fun showUserInterface() {
        setToolbarTitle(resources.getString(R.string.note))
        val mLayoutManager = LinearLayoutManager(activity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvNote!!.layoutManager = mLayoutManager
        rvNote!!.setHasFixedSize(true)
        rvNote!!.adapter = noteAdapter
        swipeRefreshLayout!!.setColorSchemeColors(*activity
                ?.getResources()!!.getIntArray(R.array.swipeRefreshColors))
        swipeRefreshLayout!!.setOnRefreshListener(this)
    }

    override fun showNote(notes: List<Note>?) {
        this.notes = notes
        noteAdapter!!.setNotes(this.notes)
    }

    override fun showEmptyNotes() {
        llError!!.visibility = View.VISIBLE
        rvNote!!.visibility = View.GONE
        tvError!!.setText(R.string.empty_notes)
    }

    override fun showResetVisibility() {
        llError!!.visibility = View.GONE
        rvNote!!.visibility = View.VISIBLE
    }

    override fun showError(message: Int) {
        Toaster.show(rootView, message)
        llError!!.visibility = View.VISIBLE
        rvNote!!.visibility = View.GONE
        tvError!!.text = getString(R.string.failed_to_fetch_notes)
    }

    override fun showProgressbar(show: Boolean) {
        if (show && noteAdapter!!.itemCount == 0) {
            showMifosProgressBar()
            swipeRefreshLayout!!.isRefreshing = false
        } else {
            hideMifosProgressBar()
            swipeRefreshLayout!!.isRefreshing = show
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideMifosProgressBar()
        notePresenter!!.detachView()
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