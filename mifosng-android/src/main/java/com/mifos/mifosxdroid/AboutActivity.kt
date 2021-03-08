package com.mifos.mifosxdroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.mifos.mifosxdroid.core.MifosBaseActivity

class AboutActivity : MifosBaseActivity() {
    var contributors = "https://github.com/openMF/android-client/graphs/contributors"
    var gitHub = "https://github.com/openMF/android-client"
    var twitter = "https://twitter.com/mifos"
    var license = "https://github.com/openMF/android-client/blob/master/LICENSE.md"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        showBackButton()
    }

    fun goToWeb(view: View?) {
        link(contributors)
    }

    fun goToGit(view: View?) {
        link(gitHub)
    }

    fun goToTwitter(view: View?) {
        link(twitter)
    }

    fun goToLicense(view: View?) {
        link(license)
    }

    fun link(url: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}