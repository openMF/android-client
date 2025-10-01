package com.mifos.androidclient.features.shares.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mifos.androidclient.databinding.BottomSheetViewChargesBinding
import com.mifos.androidclient.features.shares.data.models.Charge

class ViewChargesBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetViewChargesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetViewChargesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val charges = arguments?.getParcelableArrayList<Charge>(CHARGES_KEY) ?: emptyList()
        val adapter = ChargesAdapter()
        binding.chargesListRv.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        adapter.submitList(charges)

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val CHARGES_KEY = "charges_key"

        fun newInstance(charges: List<Charge>): ViewChargesBottomSheetFragment {
            return ViewChargesBottomSheetFragment().apply {
                arguments = bundleOf(CHARGES_KEY to ArrayList(charges))
            }
        }
    }
}