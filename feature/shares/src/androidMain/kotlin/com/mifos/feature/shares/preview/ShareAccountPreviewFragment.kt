package com.mifos.androidclient.features.shares.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mifos.androidclient.databinding.FragmentShareAccountPreviewBinding
import com.mifos.androidclient.features.shares.data.models.ShareAccountData

class ShareAccountPreviewFragment : Fragment() {

    private var _binding: FragmentShareAccountPreviewBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShareAccountCreationViewModel by activityViewModels()
    private val chargesAdapter = ChargesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShareAccountPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // For isolated testing, populate the ViewModel with mock data.
        // Remove this line once the previous steps are implemented and passing data.
        viewModel.populateWithMockData()

        binding.chargesRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chargesAdapter
        }

        viewModel.shareAccountData.observe(viewLifecycleOwner) { data ->
            data?.let {
                updateUI(it)
                chargesAdapter.submitList(it.charges)
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.nextBtn.setOnClickListener {
            if (viewModel.shareAccountData.value != null) {
                viewModel.submitNewShareAccount()
            } else {
                Toast.makeText(context, "Data is missing. Please go back to previous steps.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.viewChargesBtn.setOnClickListener {
            val charges = viewModel.shareAccountData.value?.charges ?: emptyList()
            ViewChargesBottomSheetFragment.newInstance(charges).show(
                childFragmentManager, "ViewChargesModal"
            )
        }
    }

    private fun updateUI(data: ShareAccountData) {
        binding.productNameTv.text = "Product Name: ${data.productName}"
        binding.externalIdTv.text = "External ID: ${data.externalId}"
        binding.submittedDateTv.text = "Submitted Date: ${data.submittedDate}"

        binding.currencyTv.text = "Currency: ${data.currency}"
        binding.currentPriceTv.text = "Current Price: ${data.currentPrice}"
        binding.totalSharesTv.text = "Total Shares: ${data.totalNumberOfShares}"
        binding.defaultSavingsAccountTv.text = "Default Savings Account: ${data.defaultSavingsAccount}"
        binding.applicationDateTv.text = "Application Date: ${data.applicationDate}"
        binding.allowDividendsTv.text = "Allow Dividends: ${if (data.allowDividends) "Yes" else "No"}"
        binding.minimumActivePeriodTv.text = "Minimum Active Period: ${data.minimumActivePeriod}"
        binding.lockInPeriodTv.text = "Lock-in Period: ${data.lockInPeriod}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}