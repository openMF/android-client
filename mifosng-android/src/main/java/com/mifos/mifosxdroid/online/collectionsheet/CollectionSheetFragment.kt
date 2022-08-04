/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.collectionsheet

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ExpandableListView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.MaterialIcons
import com.mifos.api.model.BulkRepaymentTransactions
import com.mifos.api.model.CollectionSheetPayload
import com.mifos.api.model.Payload
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.CollectionListAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.objects.db.CollectionSheet
import com.mifos.objects.response.SaveResponse
import com.mifos.utils.Constants
import retrofit2.adapter.rxjava.HttpException
import java.util.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 * Use the [CollectionSheetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CollectionSheetFragment : MifosBaseFragment(), CollectionSheetMvpView {
    val LOG_TAG = javaClass.simpleName

    @JvmField
    @BindView(R.id.exlv_collection_sheet)
    var expandableListView: ExpandableListView? = null

    @JvmField
    @Inject
    var mCollectionSheetPresenter: CollectionSheetPresenter? = null
    var collectionListAdapter: CollectionListAdapter? = null
    private var centerId: Int? = null // Center for which collection sheet is being generated = 0
    private var dateOfCollection // Date of Meeting on which collection has to be done.
            : String? = null
    private var calendarInstanceId = 0
    private lateinit var rootView: View

    //Called from within the Adapters to show changes when payment amounts are updated
    fun refreshFragment() {
        collectionListAdapter!!.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) {
            centerId = requireArguments().getInt(Constants.CENTER_ID)
            dateOfCollection = requireArguments().getString(Constants.DATE_OF_COLLECTION)
            calendarInstanceId = requireArguments().getInt(Constants.CALENDAR_INSTANCE_ID)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_collection_sheet, container, false)
        ButterKnife.bind(this, rootView)
        mCollectionSheetPresenter!!.attachView(this)
        fetchCollectionSheet()
        return rootView
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        val mItemSearch = menu.add(Menu.NONE, MENU_ITEM_SEARCH, Menu.NONE, getString(R.string.search))
        //        mItemSearch.setIcon(new IconDrawable(getActivity(), MaterialIcons.md_search)
//                .colorRes(Color.WHITE)
//                .actionBarSize());
        mItemSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        val mItemRefresh = menu.add(Menu.NONE, MENU_ITEM_REFRESH, Menu.NONE, getString(R.string.refresh))
        mItemRefresh.icon = IconDrawable(activity, MaterialIcons.md_refresh)
                .colorRes(R.color.white)
                .actionBarSize()
        mItemRefresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        val mItemSave = menu.add(Menu.NONE, MENU_ITEM_SAVE, Menu.NONE, getString(R.string.save))
        mItemSave.icon = IconDrawable(activity, MaterialIcons.md_save)
                .colorRes(R.color.white)
                .actionBarSize()
        mItemSave.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            MENU_ITEM_REFRESH -> refreshFragment()
            MENU_ITEM_SAVE -> saveCollectionSheet()
            MENU_ITEM_SEARCH -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun fetchCollectionSheet() {
        val payload = Payload()
        payload.calendarId = calendarInstanceId.toLong()
        payload.transactionDate = dateOfCollection
        payload.dateFormat = "dd-MM-YYYY"
        mCollectionSheetPresenter!!.loadCollectionSheet(centerId!!.toLong(), payload)
    }

    @Synchronized
    fun saveCollectionSheet() {
        val collectionSheetPayload = CollectionSheetPayload()
        val bulkRepaymentTransactions: MutableList<BulkRepaymentTransactions> = ArrayList()
        val iterator: MutableIterator<*> = CollectionListAdapter.sRepaymentTransactions.entries.iterator()
        while (iterator.hasNext()) {
            val repaymentTransaction = iterator.next() as Map.Entry<*, *>
            bulkRepaymentTransactions.add(BulkRepaymentTransactions((repaymentTransaction.key as Int?)!!, (repaymentTransaction.value as Double?)!!))
            iterator.remove()
        }
        collectionSheetPayload.bulkRepaymentTransactions = arrayOfNulls(bulkRepaymentTransactions.size)
        bulkRepaymentTransactions.toArray(collectionSheetPayload.bulkRepaymentTransactions)
        collectionSheetPayload.calendarId = calendarInstanceId.toLong()
        collectionSheetPayload.transactionDate = dateOfCollection
        collectionSheetPayload.dateFormat = "dd-MM-YYYY"

        //Saving Collection Sheet
        centerId?.let { mCollectionSheetPresenter!!.saveCollectionSheet(it, collectionSheetPayload) }
    }

    override fun showCollectionSheet(collectionSheet: CollectionSheet) {
        Log.i(COLLECTION_SHEET_ONLINE, "Received")
        val mifosGroups = collectionSheet.groups
        collectionListAdapter = CollectionListAdapter(activity, mifosGroups)
        expandableListView!!.setAdapter(collectionListAdapter)
    }

    override fun showCollectionSheetSuccessfullySaved(saveResponse: SaveResponse?) {
        if (saveResponse != null) {
            Toast.makeText(activity, "Collection Sheet Saved Successfully",
                    Toast.LENGTH_SHORT).show()
        }
    }

    override fun showFailedToSaveCollectionSheet(response: HttpException?) {
        if (response != null) {
            if (response.code() == 400 || response.code() == 403) {
                //TODO for now, It is commented
                //MFErrorParser.parseError(response.response().body());
            }
            Toast.makeText(activity, "Collection Sheet could not be saved.",
                    Toast.LENGTH_SHORT).show()
        }
    }

    override fun showFetchingError(s: String?) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mCollectionSheetPresenter!!.detachView()
    }

    companion object {
        const val COLLECTION_SHEET_ONLINE = "Collection Sheet Online"
        private const val MENU_ITEM_SEARCH = 2000
        private const val MENU_ITEM_REFRESH = 2001
        private const val MENU_ITEM_SAVE = 2002
        @JvmStatic
        fun newInstance(centerId: Int, dateOfCollection: String?, calendarInstanceId: Int): CollectionSheetFragment {
            val fragment = CollectionSheetFragment()
            val args = Bundle()
            args.putInt(Constants.CENTER_ID, centerId)
            args.putString(Constants.DATE_OF_COLLECTION, dateOfCollection)
            args.putInt(Constants.CALENDAR_INSTANCE_ID, calendarInstanceId)
            fragment.arguments = args
            return fragment
        }
    }
}

private fun <E> MutableList<E>.toArray(bulkRepaymentTransactions: Array<E?>) {

}
