package com.mifos.mifosxdroid.offline.offlinedashbarod

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.OfflineDashboardAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster.show
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
    val LOG_TAG = javaClass.simpleName

    @JvmField
    @BindView(R.id.rv_offline_dashboard)
    var rv_offline_dashboard: RecyclerView? = null

    @JvmField
    @BindView(R.id.pb_offline_dashboard)
    var pb_offline_dashboard: ProgressBar? = null

    @JvmField
    @BindView(R.id.noPayloadText)
    var mNoPayloadText: TextView? = null

    @JvmField
    @BindView(R.id.noPayloadIcon)
    var mNoPayloadIcon: ImageView? = null

    @JvmField
    @BindView(R.id.ll_error)
    var ll_error: LinearLayout? = null
    var rootView: View? = null

    @JvmField
    @Inject
    var mOfflineDashboardPresenter: OfflineDashboardPresenter? = null
    var mOfflineDashboardAdapter: OfflineDashboardAdapter? = null

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
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_offline_dashboard, container, false)
        setToolbarTitle(requireActivity().resources.getString(R.string.offline))
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        mOfflineDashboardPresenter!!.attachView(this)
        ButterKnife.bind(this, rootView!!)
        val mLayoutManager: LinearLayoutManager = GridLayoutManager(activity, GRID_COUNT)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_offline_dashboard!!.layoutManager = mLayoutManager
        rv_offline_dashboard!!.setHasFixedSize(true)
        rv_offline_dashboard!!.itemAnimator = DefaultItemAnimator()
        rv_offline_dashboard!!.addItemDecoration(
            ItemOffsetDecoration(
                requireActivity(),
                R.dimen.item_offset
            )
        )
        mOfflineDashboardAdapter = OfflineDashboardAdapter { position: Int? ->
            startPayloadActivity<Any>(mPayloadClasses!![position!!])
            null
        }
        rv_offline_dashboard!!.adapter = mOfflineDashboardAdapter
        return rootView
    }

    override fun onStart() {
        super.onStart()
        mOfflineDashboardAdapter!!.removeAllCards()
        mPayloadClasses!!.clear()
        mPayloadIndex = 5
        mOfflineDashboardPresenter!!.loadDatabaseClientPayload()
        mOfflineDashboardPresenter!!.loadDatabaseGroupPayload()
        mOfflineDashboardPresenter!!.loadDatabaseCenterPayload()
        mOfflineDashboardPresenter!!.loadDatabaseLoanRepaymentTransactions()
        mOfflineDashboardPresenter!!.loadDatabaseSavingsAccountTransactions()
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
            mOfflineDashboardAdapter!!.showCard(
                activity
                    ?.resources?.getString(R.string.payloads_count) +
                        clientPayloads.size, SYNC_CARD_UI_NAMES[0]
            )
            mPayloadClasses!!.add(SyncClientPayloadActivity::class.java)
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
            mOfflineDashboardAdapter!!.showCard(
                activity
                    ?.resources?.getString(R.string.payloads_count) +
                        groupPayloads.size, SYNC_CARD_UI_NAMES[1]
            )
            mPayloadClasses!!.add(SyncGroupPayloadsActivity::class.java)
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
            mOfflineDashboardAdapter!!.showCard(
                activity
                    ?.resources?.getString(R.string.payloads_count) +
                        centerPayloads.size, SYNC_CARD_UI_NAMES[2]
            )
            mPayloadClasses!!.add(SyncCenterPayloadActivity::class.java)
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
            mOfflineDashboardAdapter!!.showCard(
                requireActivity().resources
                    .getString(R.string.transactions_count) +
                        loanRepaymentRequests.size, SYNC_CARD_UI_NAMES[3]
            )
            mPayloadClasses!!.add(SyncLoanRepaymentTransactionActivity::class.java)
        } else {
            mPayloadIndex = mPayloadIndex - 1
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
            mOfflineDashboardAdapter!!.showCard(
                requireActivity().resources
                    .getString(R.string.transactions_count) +
                        transactions.size, SYNC_CARD_UI_NAMES[4]
            )
            mPayloadClasses!!.add(SyncSavingsAccountTransactionActivity::class.java)
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
            rv_offline_dashboard!!.visibility = View.GONE
            ll_error!!.visibility = View.VISIBLE
            mNoPayloadText!!.text = activity
                ?.resources?.getString(R.string.nothing_to_sync)
            mNoPayloadIcon!!.setImageResource(R.drawable.ic_assignment_turned_in_black_24dp)
        }
    }

    override fun showError(stringId: Int) {
        show(rootView, resources.getString(stringId))
    }

    override fun showProgressbar(b: Boolean) {
        if (b) {
            pb_offline_dashboard!!.visibility = View.VISIBLE
        } else {
            pb_offline_dashboard!!.visibility = View.GONE
        }
    }

    /**
     * This is the Generic Type method for starting the activity.
     * @param t Activity Class that is user wants to start
     * @param <T>
    </T> */
    fun <T> startPayloadActivity(t: Class<*>) {
        val intent = Intent(activity, t)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mOfflineDashboardPresenter!!.detachView()
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