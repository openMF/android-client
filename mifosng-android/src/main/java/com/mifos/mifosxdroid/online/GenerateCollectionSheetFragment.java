package com.mifos.mifosxdroid.online;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mifos.mifosxdroid.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class GenerateCollectionSheetFragment extends Fragment {


    View rootView;

    public GenerateCollectionSheetFragment() {
        // Required empty public constructor
    }

    public static GenerateCollectionSheetFragment newInstance() {

        GenerateCollectionSheetFragment generateCollectionSheetFragment = new GenerateCollectionSheetFragment();

        return generateCollectionSheetFragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       rootView = inflater.inflate(R.layout.fragment_generate_collection_sheet, container, false);
        return rootView;
    }


}
