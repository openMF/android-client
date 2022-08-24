package com.mifos.mifosxdroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import com.mifos.mifosxdroid.core.MifosBaseActivity
import java.util.*

class AboutActivity : MifosBaseActivity() {
    private val websiteLink = "https://openmf.github.io/mobileapps.github.io/"
    private val contributors = "https://github.com/openMF/android-client/graphs/contributors"
    private val gitHub = "https://github.com/openMF/android-client"
    private val twitter = "https://twitter.com/mifos"
    private val license = "https://github.com/openMF/android-client/blob/master/LICENSE.md"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        showBackButton()

        findViewById<TextView>(R.id.app_version).text = BuildConfig.VERSION_NAME
        findViewById<TextView>(R.id.tv_copy_right).text =  getString(R.string.copy_right_mifos, Calendar.getInstance()[Calendar.YEAR].toString())

        findViewById<View>(R.id.about_website_container).setOnClickListener {
            link(websiteLink)
        }
        findViewById<View>(R.id.twitter_link_container).setOnClickListener {
            link(twitter)
        }
        findViewById<View>(R.id.about_sources_container).setOnClickListener {
            link(gitHub)
        }
        findViewById<View>(R.id.self_license_container).setOnClickListener {
            link(license)
        }
        findViewById<View>(R.id.contributor).setOnClickListener {
            link(contributors)
        }
    }


    fun link(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}