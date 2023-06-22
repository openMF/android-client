package com.mifos.mifosxdroid.offline.offlinedashbarod

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.OfflineDashboardAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.FragmentOfflineDashboardBinding
import com.mifos.mifosxdroid.offline.synccenterpayloads.SyncCenterPayloadActivity
import com.mifos.mifosxdroid.offline.syncclientpayloads.SyncClientPayloadActivity
import com.mifos.mifosxdroid.offline.syncgrouppayloads.SyncGroupPayloadsActivity
import com.mifos.mifosxdroid.offline.syncloanrepaymenttransacition.SyncLoanRepaymentTransactionActivity
import com.mifos.mifosxdroid.offline.syncsavingsaccounttransaction.SyncSavingsAccountTransactionActivity
import com.mifos.objects.accounts.loan.LoanRepaymentRequest
import com.mifos.objects.accounts.savings.SavingsAccountTransactionRequest
import com.mifos.objects.client.ClientPayload
import com.mifos.objects.group.GroupPayload
import com.mifos.services.data.CenterPayload
import com.mifos.utils.ItemOffsetDecoration
import javax.inject.Inject

/**
 * This Fragment is the Dashboard of the Offline sync of Clients, Groups, LoanRepayment etc.
 * In which presenter request the DataManager to get the offline created clients or groups or
 * LoanRepayment from DatabaseHelper and get the List of Groups, clients and LoanRepayment etc.
 *
 * if DataManager Response List is not equal to zero then add the Card Name and List Count in
 * OfflineDashboardAdapter.showCard(String cardName, String cardCount) and add the Class in
 * List<Class> mPayloadClasses, which will open onclick the card.
 *
 * mPayloadIndex, this is the counter value of the number of request we are making to the
 * DataManager to load the Clients, Groups, LoanRepayment etc list from DatabaseHelper
 *
 * SYNC_CARD_UI_NAMES is array of the card UI names, which contains the all possible name of cards.
 * due to this array we have maintain the single method in
 * OfflineDashboardAdapter.showCard(String cardName, String cardCount) and update the list.
 *
 * if DatabaseHelper returns the List size zero for all Clients, Groups, LoanRepayment etc
 * then showNoPayloadToShow() and Nothing to Sync.
 *
 * Created by Rajan Maurya on 20/07/16.
</Class> */
class OfflineDashboardFragment : MifosBaseFragment(), OfflineDashboardMvpView {

    private lateinit var binding: FragmentOfflineDashboardBinding

    val LOG_TAG = javaClass.simpleName

    @JvmField
    @Inject
    var mOfflineDashboardPresenter: OfflineDashboardPresenter? = null
    private lateinit var mOfflineDashboardAdapter: OfflineDashboardAdapter

    // update mPayloadIndex to number of request is going to fetch data in Presenter;
    private var mPayloadIndex = 5
    private var mPayloadClasses: MutableList<Class<*>>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        mPayloadClasses = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOfflineDashboardBinding.inflate(inflater, container, false)
        setToolbarTitle(requireActivity().resources.getString(R.string.offline))
        (activity as MifosBaseActivity?)?.activityComponent?.inject(this)
        mOfflineDashboardPresenter?.attachView(this)
        val mLayoutManager: LinearLayoutManager = GridLayoutManager(activity, GRID_COUNT)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvOfflineDashboard.layoutManager = mLayoutManager
        binding.rvOfflineDashboard.setHasFixedSize(true)
        binding.rvOfflineDashboard.itemAnimator = DefaultItemAnimator()
        binding.rvOfflineDashboard.addItemDecoration(
            ItemOffsetDecoration(
                requireActivity(),
                R.dimen.item_offset
            )
        )
        mOfflineDashboardAdapter = OfflineDashboardAdapter { position: Int? ->
            if (position != null) {
                mPayloadClasses?.get(position)?.let { payloadClass ->
                    startPayloadActivity<Any>(payloadClass)
                }
            }
        }
        binding.rvOfflineDashboard.adapter = mOfflineDashboardAdapter
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mOfflineDashboardAdapter.removeAllCards()
        mPayloadClasses?.clear()
        mPayloadIndex = 5
        mOfflineDashboardPresenter?.loadDatabaseClientPayload()
        mOfflineDashboardPresenter?.loadDatabaseGroupPayload()
        mOfflineDashboardPresenter?.loadDatabaseCenterPayload()
        mOfflineDashboardPresenter?.loadDatabaseLoanRepaymentTransactions()
        mOfflineDashboardPresenter?.loadDatabaseSavingsAccountTransactions()
    }

    /**
     * This method set the response of DataManager from DatabaseHelper that if List<ClientPayload>
     * Size is zero the decrease the value of mPayloadIndex by 1 and if size is not equal to zero
     * the update the adapter and add the Card UI name and size() of the List to sync.
     *
     * @param clientPayloads List<ClientPayload> from DatabaseHelperClient
    </ClientPayload></ClientPayload> */
    override fun showClients(clientPayloads: List<ClientPayload>) {
        if (clientPayloads.isNotEmpty()) {
            mOfflineDashboardAdapter.showCard(
                activity
                    ?.resources?.getString(R.string.payloads_count) +
                        clientPayloads.size, SYNC_CARD_UI_NAMES[0]
            )
            mPayloadClasses?.add(SyncClientPayloadActivity::class.java)
        } else {
            mPayloadIndex -= 1
            showNoPayloadToShow()
        }
    }

    /**
     * This method set the response of DataManager from DatabaseHelper that if List<GroupsPayload>
     * Size is zero, then decrease the value of mPayloadIndex by 1 and if size is not equal to zero
     * the update the adapter and add the Card UI name and size() of the List to sync.
     *
     * @param groupPayloads List<GroupPayload> from DatabaseHelperGroup
    </GroupPayload></GroupsPayload> */
    override fun showGroups(groupPayloads: List<GroupPayload>) {
        if (groupPayloads.isNotEmpty()) {
            mOfflineDashboardAdapter.showCard(
                activity
                    ?.resources?.getString(R.string.payloads_count) +
                        groupPayloads.size, SYNC_CARD_UI_NAMES[1]
            )
            mPayloadClasses?.add(SyncGroupPayloadsActivity::class.java)
        } else {
            mPayloadIndex -= 1
            showNoPayloadToShow()
        }
    }

    /**
     * This method set the response of DataManager from DatabaseHelper that if List<CenterPayload>
     * Size is zero, then decrease the value of mPayloadIndex by 1 and if size is not equal to zero
     * the update the adapter and add the Card UI name and size() of the List to sync.
     *
     * @param centerPayloads List<CenterPayload> from DatabaseHelperGroup
    </CenterPayload></CenterPayload> */
    override fun showCenters(centerPayloads: List<CenterPayload>) {
        if (centerPayloads.isNotEmpty()) {
            mOfflineDashboardAdapter.showCard(
                activity
                    ?.resources?.getString(R.string.payloads_count) +
                        centerPayloads.size, SYNC_CARD_UI_NAMES[2]
            )
            mPayloadClasses?.add(SyncCenterPayloadActivity::class.java)
        } else {
            mPayloadIndex -= 1
            showNoPayloadToShow()
        }
    }

    /**
     * This method set the response of DataManager from DatabaseHelper that if
     * List<LoanRepaymentRequest> Size is zero, then decrease the value of mPayloadIndex by 1 and
     * if size is not equal to zero the update the adapter and add the Card UI name and size() of
     * the List to sync.
     *
     * @param loanRepaymentRequests List<LoanRepaymentRequest> from DatabaseHelperLoan
    </LoanRepaymentRequest></LoanRepaymentRequest> */
    override fun showLoanRepaymentTransactions(loanRepaymentRequests: List<LoanRepaymentRequest>) {
        if (loanRepaymentRequests.isNotEmpty()) {
            mOfflineDashboardAdapter.showCard(
                requireActivity().resources
                    .getString(R.string.transactions_count) +
                        loanRepaymentRequests.size, SYNC_CARD_UI_NAMES[3]
            )
            mPayloadClasses?.add(SyncLoanRepaymentTransactionActivity::class.java)
        } else {
            mPayloadIndex -= 1
            showNoPayloadToShow()
        }
    }

    /**
     * This method set the response of DataManager from DatabaseHelper, if
     * List<SavingsAccountTransactionRequest> Size is zero, then decrease the value of
     * mPayloadIndex by 1 and if size is not equal to zero then update the adapter and add the
     * Card UI name and size() of the List to sync.
     *
     * @param transactions List<SavingsAccountTransaction>
    </SavingsAccountTransaction></SavingsAccountTransactionRequest> */
    override fun showSavingsAccountTransaction(transactions: List<SavingsAccountTransactionRequest>) {
        if (transactions.isNotEmpty()) {
            mOfflineDashboardAdapter.showCard(
                requireActivity().resources
                    .getString(R.string.transactions_count) +
                        transactions.size, SYNC_CARD_UI_NAMES[4]
            )
            mPayloadClasses?.add(SyncSavingsAccountTransactionActivity::class.java)
        } else {
            mPayloadIndex -= 1
            showNoPayloadToShow()
        }
    }

    /**
     * This Method setting the main UI RecyclerView.setVisibility(View.GONE) and Set text TO
     * TextView that Nothing to Sync when mPayloadIndex = 0. It means there is nothing in the
     * Database to sync to the Server.
     */
    override fun showNoPayloadToShow() {
        if (mPayloadIndex == 0) {
            binding.rvOfflineDashboard.visibility = View.GONE
            binding.llError.visibility = View.VISIBLE
            binding.noPayloadText.text = activity
                ?.resources?.getString(R.string.nothing_to_sync)
            binding.noPayloadIcon.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        }
    }

    override fun showError(stringId: Int) {
        show(binding.root, resources.getString(stringId))
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            binding.pbOfflineDashboard.visibility = View.VISIBLE
        } else {
            binding.pbOfflineDashboard.visibility = View.GONE
        }
    }

    /**
     * This is the Generic Type method for starting the activity.
     * @param t Activity Class that is user wants to start
     * @param <T>
    </T> */
    private fun <T> startPayloadActivity(t: Class<*>) {
        val intent = Intent(activity, t)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mOfflineDashboardPresenter?.detachView()
    }

    companion object {
        private const val GRID_COUNT = 2
        val SYNC_CARD_UI_NAMES = intArrayOf(
            R.string.sync_clients,
            R.string.sync_groups,
            R.string.sync_centers,
            R.string.sync_loanrepayments,
            R.string.sync_savingsaccounttransactions
        )

        fun newInstance(): OfflineDashboardFragment {
            val fragment = OfflineDashboardFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}