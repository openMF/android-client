package com.mifos.mifosxdroid.passcode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mifos.utils.Constants

class PassCodeActivity : AppCompatActivity() {

    private var currPassCode: String? = null
    private var isToUpdatePassCode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        findViewById<NestedScrollView>(com.mifos.mobile.passcode.R.id.cl_rootview).setBackgroundColor(
//            android.R.attr.colorBackground
//        )

        intent?.let {
            currPassCode = it.getStringExtra(Constants.CURR_PASSWORD)
            isToUpdatePassCode = it.getBooleanExtra(Constants.IS_TO_UPDATE_PASS_CODE, false)
        }
    }

//    override fun showToaster(view: View?, msg: Int) {
//        Toaster.show(view, msg, Toaster.SHORT)
//    }
//
//    override fun startLoginActivity() {
//        startActivity(Intent(this, AndroidClientActivity::class.java))
//        finish()
//    }
//
//    override fun getLogo(): Int {
//        return R.drawable.mifos_logo
//    }
//
//    override fun getEncryptionType(): Int {
//        return EncryptionUtil.FINERACT_CN
//    }
//
//    override fun startNextActivity() {
//        startActivity(Intent(this, AndroidClientActivity::class.java))
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        if (isToUpdatePassCode && !currPassCode.isNullOrEmpty()) {
//            PasscodePreferencesHelper(this).apply {
//                savePassCode(currPassCode)
//            }
//        }
//        finish()
//    }
}
