package com.mifos.mifosxdroid.online.groupdetails

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.joanzapata.iconify.fonts.MaterialIcons
import com.joanzapata.iconify.widget.IconTextView
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.adapters.LoanAccountsListAdapter
import com.mifos.mifosxdroid.adapters.SavingsAccountsListAdapter
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.online.activate.ActivateFragment
import com.mifos.mifosxdroid.online.datatable.DataTableFragment
import com.mifos.mifosxdroid.online.documentlist.DocumentListFragment
import com.mifos.mifosxdroid.online.grouploanaccount.GroupLoanAccountFragment
import com.mifos.mifosxdroid.online.note.NoteFragment
import com.mifos.mifosxdroid.online.savingsaccount.SavingsAccountFragment
import com.mifos.objects.accounts.GroupAccounts
import com.mifos.objects.accounts.savings.DepositType
import com.mifos.objects.client.Client
import com.mifos.objects.group.Group
import com.mifos.utils.Constants
import com.mifos.utils.FragmentConstants
import com.mifos.utils.Utils
import javax.inject.Inject
/**
 * Created by nellyk on 2/27/2016.
 */
class GroupDetailsFragment : MifosBaseFragment(), GroupDetailsMvpView {
    @JvmField
    @BindView(R.id.tv_groupsName)
    var tv_fullName: TextView? = null

    @JvmField
    @BindView(R.id.tv_groupexternalId)
    var tv_externalId: TextView? = null

    @JvmField
    @BindView(R.id.tv_groupactivationDate)
    var tv_activationDate: TextView? = null

    @JvmField
    @BindView(R.id.tv_groupoffice)
    var tv_office: TextView? = null

    @JvmField
    @BindView(R.id.row_account)
    var rowAccount: TableRow? = null

    @JvmField
    @BindView(R.id.row_external)
    var rowExternal: TableRow? = null

    @JvmField
    @BindView(R.id.row_activation)
    var rowActivation: TableRow? = null

    @JvmField
    @BindView(R.id.row_office)
    var rowOffice: TableRow? = null

    @JvmField
    @BindView(R.id.row_group)
    var rowGroup: TableRow? = null

    @JvmField
    @BindView(R.id.row_staff)
    var rowStaff: TableRow? = null

    @JvmField
    @BindView(R.id.row_loan)
    var rowLoan: TableRow? = null

    @JvmField
    @BindView(R.id.rl_group)
    var rlGroup: RelativeLayout? = null

    @JvmField
    @BindView(R.id.ll_bottom_panel)
    var llBottomPanel: LinearLayout? = null

    @JvmField
    @Inject
    var mGroupDetailsPresenter: GroupDetailsPresenter? = null
    private lateinit var rootView: View
    private var groupId = 0
    private var accountAccordion: AccountAccordion? = null
    private var mListener: OnFragmentInteractionListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent.inject(this)
        if (arguments != null) {
            groupId = requireArguments().getInt(Constants.GROUP_ID)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_group_details, container, false)
        ButterKnife.bind(this, rootView)
        mGroupDetailsPresenter!!.attachView(this)
        mGroupDetailsPresenter!!.loadGroupDetailsAndAccounts(groupId)
        return rootView
    }

    @OnClick(R.id.btn_activate_group)
    fun onClickActivateGroup() {
        val activateFragment = ActivateFragment.newInstance(groupId, Constants.ACTIVATE_GROUP)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS)
        fragmentTransaction.replace(R.id.container, activateFragment)
        fragmentTransaction.commit()
    }

    fun loadDocuments() {
        val documentListFragment = DocumentListFragment.newInstance(Constants.ENTITY_TYPE_GROUPS, groupId)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_GROUP_DETAILS)
        fragmentTransaction.replace(R.id.container, documentListFragment)
        fragmentTransaction.commit()
    }

    fun loadNotes() {
        val noteFragment = NoteFragment.newInstance(Constants.ENTITY_TYPE_GROUPS, groupId)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS)
        fragmentTransaction.replace(R.id.container, noteFragment)
        fragmentTransaction.commit()
    }

    fun addGroupSavingsAccount() {
        val savingsAccountFragment = SavingsAccountFragment.newInstance(groupId, true)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_GROUP_DETAILS)
        fragmentTransaction.replace(R.id.container, savingsAccountFragment)
        fragmentTransaction.commit()
    }

    fun addGroupLoanAccount() {
        val grouploanAccountFragment = GroupLoanAccountFragment.newInstance(groupId)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_GROUP_DETAILS)
        fragmentTransaction.replace(R.id.container, grouploanAccountFragment)
        fragmentTransaction.commit()
    }

    fun loadGroupDataTables() {
        val dataTableFragment = DataTableFragment.newInstance(Constants.DATA_TABLE_NAME_GROUP, groupId)
        val fragmentTransaction = requireActivity().supportFragmentManager
                .beginTransaction()
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_GROUP_DETAILS)
        fragmentTransaction.replace(R.id.container, dataTableFragment)
        fragmentTransaction.commit()
    }

    override fun showProgressbar(show: Boolean) {
        if (show) {
            rlGroup!!.visibility = View.GONE
            showMifosProgressBar()
        } else {
            rlGroup!!.visibility = View.VISIBLE
            hideMifosProgressBar()
        }
    }

    override fun showGroup(group: Group?) {
        if (group != null) {
            if (!group.active) {
                llBottomPanel!!.visibility = View.VISIBLE
            }
            setToolbarTitle(getString(R.string.group) + " - " + group.name)
            tv_fullName!!.text = group.name
            if (group.externalId != null) {
                tv_externalId!!.text = group.externalId
            } else {
                tv_externalId!!.setText(R.string.not_available)
            }
            try {
                val dateString = Utils.getStringOfDate(group.activationDate)
                tv_activationDate!!.text = dateString
                if (TextUtils.isEmpty(dateString)) rowActivation!!.visibility = View.GONE
            } catch (e: IndexOutOfBoundsException) {
                Toast.makeText(activity, getString(R.string.error_group_inactive),
                        Toast.LENGTH_SHORT).show()
                tv_activationDate!!.text = ""
            }
            tv_office!!.text = group.officeName
            if (TextUtils.isEmpty(group.officeName)) rowOffice!!.visibility = View.GONE
        }
    }

    override fun showGroupClients(clients: List<Client?>?) {
        mListener!!.loadGroupClients(clients as List<Client>?)
    }

    override fun showGroupAccounts(groupAccounts: GroupAccounts?) {
        // Proceed only when the fragment is added to the activity.
        if (!isAdded) {
            return
        }
        accountAccordion = AccountAccordion(activity)
        if (groupAccounts?.loanAccounts!!.size > 0) {
            val section = AccountAccordion.Section.LOANS
            val adapter = LoanAccountsListAdapter(requireActivity().applicationContext,
                    groupAccounts?.loanAccounts)
            section.connect(activity, adapter, AdapterView.OnItemClickListener { adapterView, view, i, l -> mListener!!.loadLoanAccountSummary(adapter.getItem(i).id) })
        }
        if (groupAccounts.nonRecurringSavingsAccounts.size > 0) {
            val section = AccountAccordion.Section.SAVINGS
            val adapter = SavingsAccountsListAdapter(requireActivity().applicationContext,
                    groupAccounts.nonRecurringSavingsAccounts)
            section.connect(activity, adapter, AdapterView.OnItemClickListener { adapterView, view, i, l ->
                mListener!!.loadSavingsAccountSummary(adapter.getItem(i).id,
                        adapter.getItem(i).depositType)
            })
        }
        if (groupAccounts.recurringSavingsAccounts.size > 0) {
            val section = AccountAccordion.Section.RECURRING
            val adapter = SavingsAccountsListAdapter(requireActivity().applicationContext,
                    groupAccounts.recurringSavingsAccounts)
            section.connect(activity, adapter, AdapterView.OnItemClickListener { adapterView, view, i, l ->
                mListener!!.loadSavingsAccountSummary(adapter.getItem(i).id,
                        adapter.getItem(i).depositType)
            })
        }
    }

    override fun showFetchingError(errorMessage: Int) {
        Toast.makeText(activity, getStringMessage(errorMessage), Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mListener = try {
            activity as OnFragmentInteractionListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.javaClass.getSimpleName() + " must " +
                    "implement OnFragmentInteractionListener")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_group, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.more_group_info -> loadGroupDataTables()
            R.id.documents -> loadDocuments()
            R.id.add_group_savings_account -> addGroupSavingsAccount()
            R.id.add_group_loan -> addGroupLoanAccount()
            R.id.group_clients -> mGroupDetailsPresenter!!.loadGroupAssociateClients(groupId)
            R.id.group_notes -> loadNotes()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mGroupDetailsPresenter!!.detachView()
    }

    interface OnFragmentInteractionListener {
        fun loadLoanAccountSummary(loanAccountNumber: Int)
        fun loadSavingsAccountSummary(savingsAccountNumber: Int, accountType: DepositType?)
        fun loadGroupClients(clients: List<Client>?)
    }

    private class AccountAccordion(private val context: Activity?) {
        private var currentSection: Section? = null
        fun setCurrentSection(currentSection: Section?) {
            // close previous section
            if (this.currentSection != null) {
                this.currentSection!!.close(context)
            }
            this.currentSection = currentSection

            // open new section
            if (this.currentSection != null) {
                this.currentSection!!.open(context)
            }
        }

        enum class Section(private val sectionId: Int, private val textViewStringId: Int) {
            LOANS(R.id.account_accordion_section_loans, R.string.loanAccounts), SAVINGS(R.id.account_accordion_section_savings, R.string.savingAccounts), RECURRING(R.id.account_accordion_section_recurring, R.string.recurringAccount);

            private var mListViewCount = 0.0
            fun getTextView(context: Activity?): TextView {
                return getSectionView(context).findViewById<View>(R.id.tv_toggle_accounts) as TextView
            }

            fun getIconView(context: Activity?): IconTextView {
                return getSectionView(context).findViewById<View>(R.id.tv_toggle_accounts_icon) as IconTextView
            }

            fun getListView(context: Activity?): ListView {
                return getSectionView(context).findViewById<View>(R.id.lv_accounts) as ListView
            }

            fun getCountView(context: Activity?): TextView {
                return getSectionView(context).findViewById<View>(R.id.tv_count_accounts) as TextView
            }

            fun getSectionView(context: Activity?): View {
                return context!!.findViewById(sectionId)
            }

            fun connect(context: Activity?, adapter: ListAdapter, onItemClickListener: AdapterView.OnItemClickListener?) {
                getCountView(context).text = adapter.count.toString()
                val listView = getListView(context)
                listView.adapter = adapter
                listView.onItemClickListener = onItemClickListener
            }

            fun open(context: Activity?) {
                val iconView = getIconView(context)
                iconView.text = "{" + LIST_CLOSED_ICON.key() + "}"
                mListViewCount = java.lang.Double.valueOf(getCountView(context)
                        .text
                        .toString())
                val listView = getListView(context)
                resizeListView(context, listView)
                listView.visibility = View.VISIBLE
            }

            fun close(context: Activity?) {
                val iconView = getIconView(context)
                iconView.text = "{" + LIST_OPEN_ICON.key() + "}"
                getListView(context).visibility = View.GONE
            }

            private fun configureSection(context: Activity?, accordion: AccountAccordion) {
                val listView = getListView(context)
                val textView = getTextView(context)
                val iconView = getIconView(context)
                val onClickListener = View.OnClickListener {
                    if (this@Section == accordion.currentSection) {
                        accordion.setCurrentSection(null)
                    } else if (listView != null && listView.count > 0) {
                        accordion.setCurrentSection(this@Section)
                    }
                }
                if (textView != null) {
                    textView.setOnClickListener(onClickListener)
                    textView.text = context!!.getString(textViewStringId)
                }
                iconView?.setOnClickListener(onClickListener)
                listView?.setOnTouchListener { view, motionEvent ->
                    view.parent.requestDisallowInterceptTouchEvent(true)
                    false
                }
                // initialize section in closed state
                close(context)
            }

            private fun resizeListView(context: Activity?, listView: ListView) {
                if (mListViewCount < 4) {
                    //default listview height is 200dp,which displays 4 listview items.
                    // This calculates the required listview height
                    // if listview items are less than 4
                    val heightInDp = mListViewCount / 4 * 200
                    val heightInPx = heightInDp * context!!.resources
                            .displayMetrics.density
                    val params = listView.layoutParams
                    params.height = heightInPx.toInt()
                    listView.layoutParams = params
                    listView.requestLayout()
                }
            }

            companion object {
                private val LIST_OPEN_ICON = MaterialIcons.md_add_circle_outline
                private val LIST_CLOSED_ICON = MaterialIcons.md_remove_circle_outline
                fun configure(context: Activity?, accordion: AccountAccordion) {
                    for (section in values()) {
                        section.configureSection(context, accordion)
                    }
                }
            }

        }

        init {
            Section.configure(context, this)
        }
    }

    companion object {
        val LOG_TAG = GroupDetailsFragment::class.java.simpleName
        @JvmStatic
        fun newInstance(groupId: Int): GroupDetailsFragment {
            val fragment = GroupDetailsFragment()
            val args = Bundle()
            args.putInt(Constants.GROUP_ID, groupId)
            fragment.arguments = args
            return fragment
        }
    }
}