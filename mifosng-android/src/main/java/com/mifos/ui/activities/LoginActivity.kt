/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mifos.api.BaseApiManager
import com.mifos.mifosxdroid.HomeActivity
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.core.util.Toaster
import com.mifos.mifosxdroid.databinding.ActivityLoginBinding
import com.mifos.mifosxdroid.passcode.PassCodeActivity
import com.mifos.states.LoginUiState
import com.mifos.utils.Constants
import com.mifos.utils.Network
import com.mifos.utils.PrefManager
import com.mifos.utils.PrefManager.savePostAuthenticationResponse
import com.mifos.utils.PrefManager.saveToken
import com.mifos.utils.ValidationUtil
import com.mifos.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.apache.fineract.client.models.PostAuthenticationResponse
import javax.inject.Inject

/**
 * Created by ishankhanna on 08/02/14.
 */
@AndroidEntryPoint
class LoginActivity : MifosBaseActivity(){

    private lateinit var binding: ActivityLoginBinding

    private lateinit var viewModel: LoginViewModel

    @Inject
    lateinit var baseApiManager: org.mifos.core.apimanager.BaseApiManager

    private lateinit var username: String
    private lateinit var instanceURL: String
    private lateinit var password: String
    private lateinit var domain: String
    private var isValidUrl = false
    private val urlWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val port = if (binding.etInstancePort.editableText.toString()
                    .isEmpty()
            ) null else Integer.valueOf(binding.etInstancePort.editableText.toString())
            instanceURL = ValidationUtil.getInstanceUrl(binding.etInstanceURL.text.toString(), port)
            isValidUrl = ValidationUtil.isValidUrl(instanceURL)
            binding.tvConstructedInstanceUrl.text = instanceURL
            domain = binding.etInstanceURL.editableText.toString()
            if (domain.isEmpty() || domain.contains(" ")) {
                isValidUrl = false
            }
            binding.tvConstructedInstanceUrl.setTextColor(
                if (isValidUrl) ContextCompat.getColor(
                    applicationContext, R.color.green_light
                ) else ContextCompat.getColor(
                    applicationContext, R.color.red_light
                )
            )
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        title = null
        setContentView(binding.root)
        binding.etInstancePort.inputType = InputType.TYPE_CLASS_NUMBER
        if (PrefManager.getPort() != "80") binding.etInstancePort.setText(PrefManager.getPort())
        binding.etInstanceURL.setText(PrefManager.getInstanceDomain())
        binding.etInstanceURL.addTextChangedListener(urlWatcher)
        binding.etInstancePort.addTextChangedListener(urlWatcher)
        urlWatcher.afterTextChanged(Editable.Factory.getInstance().newEditable(""))

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.btLogin.setOnClickListener {
            hideKeyboard(binding.btLogin)
            login()
        }

        binding.etPassword.setOnEditorActionListener { _, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN)) {
                login()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        viewModel.loginUiState.observe(this){
            when(it) {
                is LoginUiState.ShowProgress -> showProgressbar(it.state)
                is LoginUiState.ShowLoginSuccessful -> {
                    hideProgress()
                    onLoginSuccessful(it.user)
                }
                is LoginUiState.ShowError -> {
                    hideProgress()
                    onLoginError(it.message)
                }
            }
        }
    }

    private fun validateUserInputs(): Boolean {
        domain = binding.etInstanceURL.editableText.toString()
        if (domain.isEmpty() || domain.contains(" ")) {
            showToastMessage(getString(R.string.error_invalid_url))
            return false
        }
        if (!isValidUrl) {
            showToastMessage(getString(R.string.error_invalid_connection))
            return false
        }
        username = binding.etUsername.editableText.toString()
        password = binding.etPassword.editableText.toString()
        if (username.isEmpty() || password.isEmpty()) {
            showToastMessage(getString(R.string.error_enter_credentials))
            return false
        } else {
            if (username.length < 5) {
                showToastMessage(getString(R.string.error_username_length))
                return false
            }
            if (password.length < 6) {
                showToastMessage(getString(R.string.error_password_length))
                return false
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_login, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.mItem_connection_settings -> {
                binding.llConnectionSettings.visibility =
                    if (binding.llConnectionSettings.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showToastMessage(message: String) {
        Toaster.show(findViewById(android.R.id.content), message, Toaster.LONG)
    }

    private fun onLoginSuccessful(user: PostAuthenticationResponse) {

        PrefManager.usernamePassword = Pair(username,password)
        // Saving userID
        PrefManager.setUserId(user.userId!!.toInt())
        // Saving user's token
        saveToken("Basic " + user.base64EncodedAuthenticationKey)
        // Saving user
        savePostAuthenticationResponse(user)
        Toast.makeText(
            this, getString(R.string.toast_welcome) + " " + user.username, Toast.LENGTH_SHORT
        ).show()
        if (PrefManager.getPassCodeStatus()) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            val intent = Intent(this, PassCodeActivity::class.java)
            intent.putExtra(Constants.INTIAL_LOGIN, true)
            startActivity(intent)
        }
        finish()
    }

    private fun onLoginError(errorMessage: String) {
        showToastMessage(errorMessage)
    }

    private fun showProgressbar(show: Boolean) {
        if (show) {
            showProgress(getString(R.string.logging_in))
        } else {
            hideProgress()
        }
    }


    private fun login() {
        if (!validateUserInputs()) {
            return
        }
        // Saving tenant
        PrefManager.setTenant(binding.etTenantIdentifier.editableText.toString())
        // Saving InstanceURL for next usages
        PrefManager.setInstanceUrl(instanceURL)
        // Saving domain name
        PrefManager.setInstanceDomain(binding.etInstanceURL.editableText.toString())
        // Saving port
        PrefManager.setPort(binding.etInstancePort.editableText.toString())
        // Updating Services
        BaseApiManager.createService()
        baseApiManager.createService(username,password,PrefManager.getInstanceUrl(),PrefManager.getTenant(),true)
        if (Network.isOnline(this)) {
            viewModel.login(username, password)
        } else {
            showToastMessage(getString(R.string.error_not_connected_internet))
        }
    }

}