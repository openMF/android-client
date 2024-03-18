package com.mifos.mifosxdroid.dialogfragments.identifierdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.mifos.core.objects.noncore.DocumentType
import com.mifos.core.objects.noncore.Identifier
import com.mifos.core.objects.noncore.IdentifierCreationResponse
import com.mifos.core.objects.noncore.IdentifierPayload
import com.mifos.core.objects.noncore.IdentifierTemplate
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.ProgressableDialogFragment
import com.mifos.mifosxdroid.databinding.DialogFragmentIdentifierBinding
import com.mifos.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Rajan Maurya on 01/10/16.
 */
@AndroidEntryPoint
class IdentifierDialogFragment : ProgressableDialogFragment(),
    OnItemSelectedListener {

    private lateinit var binding: DialogFragmentIdentifierBinding

    lateinit var identifierStatus: Array<String>

    private lateinit var viewModel: IdentifierDialogViewModel

    private var clientIdentifierCreationListener: ClientIdentifierCreationListener? = null
    private var clientId = 0
    private var identifierTemplate: IdentifierTemplate? = null
    private var identifierDocumentTypeId: Int? = 0
    private var status: String? = null
    private lateinit var identifier: Identifier
    private var documentTypeHashMap: HashMap<String, DocumentType>? = null
    private val mListIdentifierType: MutableList<String> = ArrayList()
    private var mIdentifierTypeAdapter: ArrayAdapter<String>? = null
    private var mIdentifierStatusAdapter: ArrayAdapter<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            clientId = requireArguments().getInt(Constants.CLIENT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentIdentifierBinding.inflate(inflater, container, false)
        identifierStatus = binding.root.resources.getStringArray(R.array.status)
        viewModel = ViewModelProvider(this)[IdentifierDialogViewModel::class.java]
        showIdentifierSpinners()
        viewModel.loadClientIdentifierTemplate(clientId)

        viewModel.identifierDialogUiState.observe(viewLifecycleOwner) {
            when (it) {
                is IdentifierDialogUiState.ShowClientIdentifierTemplate -> {
                    showProgressbar(false)
                    showClientIdentifierTemplate(it.identifierTemplate)
                }

                is IdentifierDialogUiState.ShowError -> {
                    showProgressbar(false)
                    showErrorMessage(it.message)
                }

                is IdentifierDialogUiState.ShowIdentifierCreatedSuccessfully -> {
                    showProgressbar(false)
                    showIdentifierCreatedSuccessfully(it.identifierCreationResponse)
                }

                is IdentifierDialogUiState.ShowProgressbar -> showProgressbar(true)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreateIdentifier.setOnClickListener {
            onClickCreateIdentifier()
        }
    }

    private fun showIdentifierSpinners() {
        mIdentifierTypeAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, mListIdentifierType
        )
        mIdentifierTypeAdapter!!
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spIdentifierType.adapter = mIdentifierTypeAdapter
        binding.spIdentifierType.onItemSelectedListener = this
        mIdentifierStatusAdapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item, identifierStatus
        )
        mIdentifierStatusAdapter!!
            .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spIdentifierStatus.adapter = mIdentifierStatusAdapter
        binding.spIdentifierStatus.onItemSelectedListener = this
    }

    private fun onClickCreateIdentifier() {
        if (binding.etUniqueId.text.toString().trim { it <= ' ' } == "") {
            binding.etUniqueId.error = resources.getString(R.string.unique_id_required)
        } else if (mListIdentifierType.size == 0) {
            showError(R.string.empty_identifier_document_type)
        } else {
            hideKeyboard(binding.btnCreateIdentifier)
            val identifierPayload = IdentifierPayload()
            identifierPayload.documentTypeId = identifierDocumentTypeId
            identifierPayload.status = status
            identifierPayload.documentKey = binding.etUniqueId.text.toString()
            identifierPayload.description = binding.etDescription.text.toString()

            // Add the values in the identifier. It'll be sent to the calling Fragment
            // if the request is successful.
            identifier = Identifier()
            identifier.description = binding.etDescription.text.toString()
            identifier.documentKey = binding.etUniqueId.text.toString()
            identifier.documentType = documentTypeHashMap
                ?.get(binding.spIdentifierType.selectedItem.toString())
            viewModel.createClientIdentifier(clientId, identifierPayload)
        }
    }

    private fun showClientIdentifierTemplate(identifierTemplate: IdentifierTemplate) {
        this.identifierTemplate = identifierTemplate
        identifierTemplate.allowedDocumentTypes?.let {
            viewModel.getIdentifierDocumentTypeNames(
                it
            )
        }?.let {
            mListIdentifierType.addAll(
                it
            )
        }
        documentTypeHashMap = identifierTemplate.allowedDocumentTypes?.let {
            viewModel
                .mapDocumentTypesWithName(it)
        }
        mIdentifierTypeAdapter!!.notifyDataSetChanged()
    }

    private fun showIdentifierCreatedSuccessfully(
        identifierCreationResponse: IdentifierCreationResponse
    ) {
        Toast.makeText(
            activity, R.string.identifier_created_successfully,
            Toast.LENGTH_SHORT
        ).show()
        identifier.clientId = identifierCreationResponse.clientId
        identifier.id = identifierCreationResponse.resourceId
        if (clientIdentifierCreationListener != null) {
            clientIdentifierCreationListener!!.onClientIdentifierCreationSuccess(identifier)
        }
        dialog!!.dismiss()
    }

    private fun showErrorMessage(message: String) {
        if (clientIdentifierCreationListener != null) {
            clientIdentifierCreationListener!!.onClientIdentifierCreationFailure(message)
        } else {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showError(errorMessage: Int) {
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressbar(show: Boolean) {
        showProgress(show)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.sp_identifier_type -> identifierDocumentTypeId =
                identifierTemplate?.allowedDocumentTypes?.get(position)?.id

            R.id.sp_identifier_status -> status = identifierStatus[position]
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
    fun setOnClientIdentifierCreationListener(
        listener: ClientIdentifierCreationListener?
    ) {
        clientIdentifierCreationListener = listener
    }

    companion object {
        fun newInstance(clientId: Int): IdentifierDialogFragment {
            val documentDialogFragment = IdentifierDialogFragment()
            val args = Bundle()
            args.putInt(Constants.CLIENT_ID, clientId)
            documentDialogFragment.arguments = args
            return documentDialogFragment
        }
    }
}