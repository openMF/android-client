package com.mifos.mifosxdroid.dialogfragments.identifierdialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.ProgressableDialogFragment
import com.mifos.mifosxdroid.databinding.DialogFragmentIdentifierBinding
import com.mifos.objects.noncore.DocumentType
import com.mifos.objects.noncore.Identifier
import com.mifos.objects.noncore.IdentifierCreationResponse
import com.mifos.objects.noncore.IdentifierPayload
import com.mifos.objects.noncore.IdentifierTemplate
import com.mifos.utils.Constants
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 01/10/16.
 */
class IdentifierDialogFragment : ProgressableDialogFragment(), IdentifierDialogMvpView,
    OnItemSelectedListener {

    private lateinit var binding: DialogFragmentIdentifierBinding

    lateinit var identifierStatus: Array<String>

    //    @JvmField
    @Inject
    lateinit var mIdentifierDialogPresenter: IdentifierDialogPresenter
    private var clientIdentifierCreationListener: ClientIdentifierCreationListener? = null
    private var clientId = 0
    private var identifierTemplate: IdentifierTemplate? = null
    private var identifierDocumentTypeId : Int? = 0
    private var status: String? = null
    private var identifier: Identifier? = null
    private var documentTypeHashMap: HashMap<String, DocumentType>? = null
    private val mListIdentifierType: MutableList<String> = ArrayList()
    private var mIdentifierTypeAdapter: ArrayAdapter<String>? = null
    private var mIdentifierStatusAdapter: ArrayAdapter<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MifosBaseActivity?)!!.activityComponent!!.inject(this)
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
        mIdentifierDialogPresenter.attachView(this)
        showIdentifierSpinners()
        mIdentifierDialogPresenter.loadClientIdentifierTemplate(clientId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreateIdentifier.setOnClickListener {
            onClickCreateIdentifier()
        }
    }

    override fun showIdentifierSpinners() {
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
            identifier!!.description = binding.etDescription.text.toString()
            identifier!!.documentKey = binding.etUniqueId.text.toString()
            identifier!!.documentType = documentTypeHashMap
                ?.get(binding.spIdentifierType.selectedItem.toString())
            mIdentifierDialogPresenter.createClientIdentifier(clientId, identifierPayload)
        }
    }

    override fun showClientIdentifierTemplate(identifierTemplate: IdentifierTemplate) {
        this.identifierTemplate = identifierTemplate
        identifierTemplate.allowedDocumentTypes?.let {
            mIdentifierDialogPresenter.getIdentifierDocumentTypeNames(
                it
            )
        }?.let {
            mListIdentifierType.addAll(
                it
            )
        }
        documentTypeHashMap = identifierTemplate.allowedDocumentTypes?.let {
            mIdentifierDialogPresenter
                .mapDocumentTypesWithName(it)
        }
        mIdentifierTypeAdapter!!.notifyDataSetChanged()
    }

    override fun showIdentifierCreatedSuccessfully(
        identifierCreationResponse: IdentifierCreationResponse
    ) {
        Toast.makeText(
            activity, R.string.identifier_created_successfully,
            Toast.LENGTH_SHORT
        ).show()
        identifier!!.clientId = identifierCreationResponse.clientId
        identifier!!.id = identifierCreationResponse.resourceId
        if (clientIdentifierCreationListener != null) {
            clientIdentifierCreationListener!!.onClientIdentifierCreationSuccess(identifier)
        }
        dialog!!.dismiss()
    }

    override fun showErrorMessage(message: String) {
        if (clientIdentifierCreationListener != null) {
            clientIdentifierCreationListener!!.onClientIdentifierCreationFailure(message)
        } else {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun showError(errorMessage: Int) {
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressbar(show: Boolean) {
        showProgress(show)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mIdentifierDialogPresenter.detachView()
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