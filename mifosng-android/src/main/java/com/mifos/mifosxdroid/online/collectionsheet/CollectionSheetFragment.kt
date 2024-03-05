/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.online.collectionsheet

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.joanzapata.iconify.IconDrawable
import com.joanzapata.iconify.fonts.MaterialIcons
import com.mifos.core.common.utils.Constants
import com.mifos.core.model.BulkRepaymentTransactions
import com.mifos.core.network.model.CollectionSheetPayload
import com.mifos.core.network.model.Payload
import com.mifos.core.objects.db.CollectionSheet
import com.mifos.core.objects.response.SaveResponse
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.CollectionListAdapter
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.databinding.FragmentCollectionSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.HttpException
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [CollectionSheetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class CollectionSheetFragment : MifosBaseFragment() {
    val LOG_TAG = javaClass.simpleName

    private lateinit var binding: FragmentCollectionSheetBinding

    private lateinit var viewModel: CollectionSheetViewModel

    private var collectionListAdapter: CollectionListAdapter? = null
    private var centerId: Int? = null // Center for which collection sheet is being generated = 0
    private var dateOfCollection: String? =
        null // Date of Meeting on which collection has to be done.
    private var calendarInstanceId = 0

    //Called from within the Adapters to show changes when payment amounts are updated
    private fun refreshFragment() {
        collectionListAdapter!!.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            centerId = requireArguments().getInt(Constants.CENTER_ID)
            dateOfCollection = requireArguments().getString(Constants.DATE_OF_COLLECTION)
            calendarInstanceId = requireArguments().getInt(Constants.CALENDAR_INSTANCE_ID)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCollectionSheetBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[CollectionSheetViewModel::class.java]
        fetchCollectionSheet()

        viewModel.collectionSheetUiState.observe(viewLifecycleOwner) {
            when (it) {
                is CollectionSheetUiState.ShowCollectionSheet -> {
                    showCollectionSheet(it.collectionSheet)
                }

                is CollectionSheetUiState.ShowCollectionSheetSuccessfullySaved -> {
                    showCollectionSheetSuccessfullySaved(it.saveResponse)
                }

                is CollectionSheetUiState.ShowFailedToSaveCollectionSheet -> {
                    showFailedToSaveCollectionSheet(it.e)
                }

                is CollectionSheetUiState.ShowFetchingError -> {
                    showFetchingError(it.message)
                }
            }
        }

        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        val mItemSearch =
            menu.add(Menu.NONE, MENU_ITEM_SEARCH, Menu.NONE, getString(R.string.search))
        //        mItemSearch.setIcon(new IconDrawable(getActivity(), MaterialIcons.md_search)
//                .colorRes(Color.WHITE)
//                .actionBarSize());
        mItemSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        val mItemRefresh =
            menu.add(Menu.NONE, MENU_ITEM_REFRESH, Menu.NONE, getString(R.string.refresh))
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
        when (item.itemId) {
            MENU_ITEM_REFRESH -> refreshFragment()
            MENU_ITEM_SAVE -> saveCollectionSheet()
            MENU_ITEM_SEARCH -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchCollectionSheet() {
        val payload = Payload()
        payload.calendarId = calendarInstanceId.toLong()
        payload.transactionDate = dateOfCollection
        payload.dateFormat = "dd-MM-YYYY"
        viewModel.loadCollectionSheet(centerId!!.toLong(), payload)
    }

    @Synchronized
    fun saveCollectionSheet() {
        val collectionSheetPayload = CollectionSheetPayload()
        val bulkRepaymentTransactions: MutableList<BulkRepaymentTransactions> = ArrayList()
        val iterator: MutableIterator<*> =
            CollectionListAdapter.sRepaymentTransactions.entries.iterator()
        while (iterator.hasNext()) {
            val repaymentTransaction = iterator.next() as Map.Entry<*, *>
            bulkRepaymentTransactions.add(
                BulkRepaymentTransactions(
                    (repaymentTransaction.key as Int?)!!,
                    (repaymentTransaction.value as Double?)!!
                )
            )
            iterator.remove()
        }
        collectionSheetPayload.bulkRepaymentTransactions = arrayOf()
        bulkRepaymentTransactions.toArray(collectionSheetPayload.bulkRepaymentTransactions)
        collectionSheetPayload.calendarId = calendarInstanceId.toLong()
        collectionSheetPayload.transactionDate = dateOfCollection
        collectionSheetPayload.dateFormat = "dd-MM-YYYY"

        //Saving Collection Sheet
        centerId?.let {
            viewModel.saveCollectionSheet(
                it,
                collectionSheetPayload
            )
        }
    }

    private fun showCollectionSheet(collectionSheet: CollectionSheet) {
        Log.i(COLLECTION_SHEET_ONLINE, "Received")
        val mifosGroups = collectionSheet.groups
        collectionListAdapter = CollectionListAdapter(requireActivity(), mifosGroups)
        binding.exlvCollectionSheet.setAdapter(collectionListAdapter)
    }

    private fun showCollectionSheetSuccessfullySaved(saveResponse: SaveResponse?) {
        if (saveResponse != null) {
            Toast.makeText(
                activity, "Collection Sheet Saved Successfully",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showFailedToSaveCollectionSheet(response: HttpException) {
        if (response.code() == 400 || response.code() == 403) {
            //TODO for now, It is commented
            //MFErrorParser.parseError(response.response().body());
        }
        Toast.makeText(
            activity, "Collection Sheet could not be saved.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showFetchingError(s: String?) {
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressbar(b: Boolean) {
        if (b) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    companion object {
        const val COLLECTION_SHEET_ONLINE = "Collection Sheet Online"
        private const val MENU_ITEM_SEARCH = 2000
        private const val MENU_ITEM_REFRESH = 2001
        private const val MENU_ITEM_SAVE = 2002

        fun newInstance(
            centerId: Int,
            dateOfCollection: String?,
            calendarInstanceId: Int
        ): CollectionSheetFragment {
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

private fun <E> MutableList<E>.toArray(bulkRepaymentTransactions: Array<BulkRepaymentTransactions>) {

}
