package com.mifos.mifosxdroid.dialogfragments.collectionsheetdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mifos.mifosxdroid.databinding.FragmentCollectionSheetDialogBinding
import com.mifos.mifosxdroid.online.collectionsheetindividual.NewIndividualCollectionSheetFragment
import com.mifos.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by aksh on 2/7/18.
 */
@AndroidEntryPoint
class CollectionSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCollectionSheetDialogBinding

    private var date: String? = null
    private var members = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        date =
            requireArguments().getString(Constants.REPAYMENT_DATE)
        members = requireArguments().getInt(Constants.MEMBERS)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCollectionSheetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFillnow.setOnClickListener {
            setBtnFillNow()
        }
        binding.btnCancel.setOnClickListener {
            setBtnCancel()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.tvDueDate.text = date
        binding.tvMembers.text = Integer.toString(members)
    }

    private fun setBtnFillNow() {
        (targetFragment as NewIndividualCollectionSheetFragment).getResponse(Constants.FILLNOW)
    }


    private fun setBtnCancel() {
        dialog?.dismiss()
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