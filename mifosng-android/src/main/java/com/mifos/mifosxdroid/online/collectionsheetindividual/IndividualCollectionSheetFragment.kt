package com.mifos.mifosxdroid.online.collectionsheetindividual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.tabs.TabLayout
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.online.savedcollectionsheetindividual.SavedIndividualCollectionSheetFragment
import com.mifos.utils.Constants
import java.util.*

/**
 * Created by Tarun on 05-07-2017.
 */
class IndividualCollectionSheetFragment : MifosBaseFragment() {
    @JvmField
    @BindView(R.id.viewpager)
    var viewPager: ViewPager? = null

    @JvmField
    @BindView(R.id.tabs)
    var tabLayout: TabLayout? = null
    private lateinit var rootView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_individual_recycler, container, false)
        ButterKnife.bind(this, rootView)
        setToolbarTitle(getStringMessage(R.string.individual_collection_sheet))
        setupViewPager(viewPager)
        tabLayout!!.setupWithViewPager(viewPager)
        return rootView
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        adapter.addFragment(NewIndividualCollectionSheetFragment(), Constants.NEW)
        adapter.addFragment(SavedIndividualCollectionSheetFragment(), Constants.SAVED)
        viewPager!!.adapter = adapter
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager?) : FragmentPagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }

    companion object {
        fun newInstance(): IndividualCollectionSheetFragment {
            val args = Bundle()
            val fragment = IndividualCollectionSheetFragment()
            fragment.arguments = args
            return fragment
        }
    }
}