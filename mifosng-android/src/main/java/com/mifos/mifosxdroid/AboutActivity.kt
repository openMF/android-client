package com.mifos.mifosxdroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.databinding.ActivityAboutBinding

class AboutActivity : MifosBaseActivity() {

    lateinit var binding: ActivityAboutBinding
    var contributors = "https://github.com/openMF/android-client/graphs/contributors"
    var gitHub = "https://github.com/openMF/android-client"
    var twitter = "https://twitter.com/mifos"
    var license = "https://github.com/openMF/android-client/blob/master/LICENSE.md"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
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