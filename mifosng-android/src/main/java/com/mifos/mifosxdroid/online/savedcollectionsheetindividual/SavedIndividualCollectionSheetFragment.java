package com.mifos.mifosxdroid.online.savedcollectionsheetindividual;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseFragment;

/**
 * Created by aksh on 18/6/18.
 */

public class SavedIndividualCollectionSheetFragment extends MifosBaseFragment {

    public SavedIndividualCollectionSheetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved_collection_sheet, container, false);
    }
}
