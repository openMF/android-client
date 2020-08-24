package com.mifos.mifosxdroid.online.savedcollectionsheetindividual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.core.MifosBaseFragment

/**
 * Created by aksh on 18/6/18.
 */
class SavedIndividualCollectionSheetFragment : MifosBaseFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_saved_collection_sheet, container, false)
    }
}