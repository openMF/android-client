package com.mifos.mifosxdroid.online.activate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.core.util.Toaster.show
import com.mifos.mifosxdroid.databinding.FragmentActivateClientBinding
import com.mifos.objects.client.ActivatePayload
import com.mifos.utils.Constants
import com.mifos.utils.DatePickerConstrainType
import com.mifos.utils.FragmentConstants
import com.mifos.utils.getDatePickerDialog
import com.mifos.utils.getTodayFormatted
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 09/02/17.
 */
@AndroidEntryPoint
class ActivateFragment : MifosBaseFragment(), ActivateMvpView {

    private lateinit var binding: FragmentActivateClientBinding
    private val arg: ActivateFragmentArgs by navArgs()

    @Inject
    lateinit var activatePresenter: ActivatePresenter
    private var id = 0
    private var activateType: String? = null
    private var activationDate: Instant = Instant.now()
    private val submissionDatePickerDialog by lazy {
        getDatePickerDialog(activationDate, DatePickerConstrainType.ONLY_FUTURE_DAYS) {
            val formattedDate = SimpleDateFormat("dd MM yyyy", Locale.getDefault()).format(it)
            activationDate = Instant.ofEpochMilli(it)
            binding.activateDateFieldContainer.editText?.setText(formattedDate)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = arg.clientId
        activateType = arg.activationType
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentActivateClientBinding.inflate(inflater, container, false)
        activatePresenter.attachView(this)
        showUserInterface()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnActivate.setOnClickListener {
            onClickActivationButton()
        }

        binding.activateDateFieldContainer.setEndIconOnClickListener {
            submissionDatePickerDialog.show(
                requireActivity().supportFragmentManager,
                FragmentConstants.DFRAG_DATE_PICKER
            )
        }
    }

    override fun showUserInterface() {
        setToolbarTitle(getString(R.string.activate))
        binding.activateDateFieldContainer.editText?.setText(getTodayFormatted())
    }

    private fun onClickActivationButton() {
        val clientActivate = ActivatePayload(activationDate.toString())
        activate(clientActivate)
    }

    fun activate(clientActivate: ActivatePayload?) {
        when (activateType) {
            Constants.ACTIVATE_CLIENT -> activatePresenter.activateClient(id, clientActivate)
            Constants.ACTIVATE_CENTER -> activatePresenter.activateCenter(id, clientActivate)
            Constants.ACTIVATE_GROUP -> activatePresenter.activateGroup(id, clientActivate)
            else -> {}
        }
    }

    override fun showActivatedSuccessfully(message: Int) {
        Toast.makeText(
            activity, getString(message), Toast.LENGTH_SHORT
        ).show()
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun showError(errorMessage: String) {
        show(binding.root, errorMessage, Toaster.INDEFINITE)
    }

    override fun showProgressbar(show: Boolean) {
        if (show) {
            showMifosProgressDialog()
        } else {
            hideMifosProgressDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activatePresenter.detachView()
    }
}