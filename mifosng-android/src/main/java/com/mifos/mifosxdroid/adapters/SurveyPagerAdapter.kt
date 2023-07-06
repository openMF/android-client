/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * Created by Nasim Banu on 28,January,2016.
 */
class SurveyPagerAdapter(fm: FragmentManager?, private val fragments: List<Fragment>) :
    FragmentStatePagerAdapter(
        fm!!
    ) {
    var currentPage = 0

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}