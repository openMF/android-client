package com.mifos.mifosxdroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.mifos.mifosxdroid.core.MifosBaseActivity
import com.mifos.mifosxdroid.databinding.ActivityAboutBinding

class AboutActivity : MifosBaseActivity() {

    private lateinit var binding: ActivityAboutBinding

    var contributors = "https://github.com/openMF/android-client/graphs/contributors"
    var gitHub = "https://github.com/openMF/android-client"
    var twitter = "https://twitter.com/mifos"
    var license = "https://github.com/openMF/android-client/blob/master/LICENSE.md"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showBackButton()

        binding.contributor.setOnClickListener {
            link(contributors)
        }
        binding.GitHub.setOnClickListener {
            link(gitHub)
        }
        binding.twitter.setOnClickListener {
            link(twitter)
        }
        binding.llAboutLicense.setOnClickListener {
            link(license)
        }
    }

    fun link(url: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}