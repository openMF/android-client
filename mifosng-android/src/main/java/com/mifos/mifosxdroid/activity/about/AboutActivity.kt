package com.mifos.mifosxdroid.activity.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.mifos.mifosxdroid.BuildConfig
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.databinding.ActivityAboutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutActivity : MifosBaseActivity() {

    private lateinit var binding: ActivityAboutBinding

    private val websiteLink = "https://openmf.github.io/mobileapps.github.io/"
    private val contributors = "https://github.com/openMF/android-client/graphs/contributors"
    private val gitHub = "https://github.com/openMF/android-client"
    private val twitter = "https://twitter.com/mifos"
    private val license = "https://github.com/openMF/android-client/blob/master/LICENSE.md"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showBackButton()

        binding.appVersion.text = BuildConfig.VERSION_NAME

        binding.contributor.setOnClickListener {
            link(contributors)
        }
        binding.sourcesText.setOnClickListener {
            link(gitHub)
        }
        binding.twitterLinkContainer.setOnClickListener {
            link(twitter)
        }
        binding.selfLicenseContainer.setOnClickListener {
            link(license)
        }
        binding.aboutWebsiteContainer.setOnClickListener {
            link(websiteLink)
        }
    }

    private fun link(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}