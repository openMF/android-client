package com.mifos.mifosxdroid.online;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mifos.mifosxdroid.R;
import com.mifos.objects.db.CollectionSheet;
import com.mifos.services.API;
import com.mifos.services.data.Payload;
import com.mifos.utils.Constants;

import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CollectionSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class CollectionSheetFragment extends Fragment {


    public static final String COLLECTION_SHEET_ONLINE = "Collection Sheet Online";
    private int centerId; // Center for which collection sheet is being generated
    private String dateOfCollection; // Date of Meeting on which collection has to be done.
    private int calendarInstanceId;
    View rootView;

    public static CollectionSheetFragment newInstance(int centerId, String dateOfCollection, int calendarInstanceId) {
        CollectionSheetFragment fragment = new CollectionSheetFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CENTER_ID, centerId);
        args.putString(Constants.DATE_OF_COLLECTION, dateOfCollection);
        args.putInt(Constants.CALENDAR_INSTANCE_ID, calendarInstanceId);
        fragment.setArguments(args);
        return fragment;
    }
    public CollectionSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            centerId = getArguments().getInt(Constants.CENTER_ID);
            dateOfCollection = getArguments().getString(Constants.DATE_OF_COLLECTION);
            calendarInstanceId = getArguments().getInt(Constants.CALENDAR_INSTANCE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_collection_sheet, container, false);

        ButterKnife.inject(this, rootView);

        fetchCollectionSheet();

        return rootView;
    }


    public void fetchCollectionSheet() {

        Payload payload = new Payload();
        payload.setCalendarId(calendarInstanceId);
        payload.setTransactionDate(dateOfCollection);
        //payload.setDateFormat();

        API.centerService.getCollectionSheet(centerId, payload, new Callback<CollectionSheet>() {
            @Override
            public void success(CollectionSheet collectionSheet, Response response) {

                Log.i(COLLECTION_SHEET_ONLINE, "Received");

            }

            @Override
            public void failure(RetrofitError retrofitError) {

                Log.i(COLLECTION_SHEET_ONLINE, retrofitError.getLocalizedMessage());


            }
        });


    }


}
