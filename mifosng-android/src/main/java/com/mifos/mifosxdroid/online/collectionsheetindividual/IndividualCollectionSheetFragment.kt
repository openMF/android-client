package com.mifos.mifosxdroid.online.collectionsheetindividual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment
import com.mifos.mifosxdroid.databinding.FragmentIndividualRecyclerBinding
import com.mifos.mifosxdroid.online.savedcollectionsheetindividual.SavedIndividualCollectionSheetFragment
import com.mifos.utils.Constants

/**
 * Created by Tarun on 05-07-2017.
 */
class IndividualCollectionSheetFragment : MifosBaseFragment() {

    private lateinit var binding: FragmentIndividualRecyclerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentIndividualRecyclerBinding.inflate(inflater,container,false)

        setToolbarTitle(getStringMessage(R.string.individual_collection_sheet))
        setupViewPager(binding.viewpager)
        binding.tabs.setupWithViewPager(binding.viewpager)
        return binding.root
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