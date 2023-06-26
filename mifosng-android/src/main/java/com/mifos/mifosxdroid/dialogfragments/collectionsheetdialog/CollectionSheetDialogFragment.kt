package com.mifos.mifosxdroid.dialogfragments.collectionsheetdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.online.collectionsheetindividual.NewIndividualCollectionSheetFragment
import com.mifos.utils.Constants

/**
 * Created by aksh on 2/7/18.
 */
class CollectionSheetDialogFragment : BottomSheetDialogFragment() {
    @JvmField
    @BindView(R.id.tv_due_date)
    var tvDueDate: TextView? = null

    @JvmField
    @BindView(R.id.tv_members)
    var tvMembers: TextView? = null

    @JvmField
    @BindView(R.id.btn_fillnow)
    var btnFillnow: Button? = null

    @JvmField
    @BindView(R.id.btn_cancel)
    var btnCancel: Button? = null
    private lateinit var rootView: View
    private var date: String? = null
    private var members = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MifosBaseActivity).activityComponent?.inject(this)
        date = requireArguments().getString(Constants.REPAYMENT_DATE)
        members = requireArguments().getInt(Constants.MEMBERS)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_collection_sheet_dialog, container, false)
        ButterKnife.bind(this, rootView)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tvDueDate!!.text = date
        tvMembers!!.text = Integer.toString(members)
    }

    @OnClick(R.id.btn_fillnow)
    fun setBtnFillnow() {
        (targetFragment as NewIndividualCollectionSheetFragment).getResponse(Constants.FILLNOW)
    }

    @OnClick(R.id.btn_cancel)
    fun setBtnCancel() {
        dialog!!.dismiss()
    }

    companion object {
        fun newInstance(date: String?, members: Int): CollectionSheetDialogFragment {
            val collectionSheetDialogFragment = CollectionSheetDialogFragment()
            val args = Bundle()
            args.putString(Constants.REPAYMENT_DATE, date)
            args.putInt(Constants.MEMBERS, members)
            collectionSheetDialogFragment.arguments = args
            return collectionSheetDialogFragment
        }
    }
}