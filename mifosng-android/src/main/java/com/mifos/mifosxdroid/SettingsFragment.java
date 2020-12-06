package com.mifos.mifosxdroid;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mifos.mifosxdroid.dialogfragments.syncsurveysdialog.SyncSurveysDialogFragment;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.Network;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayankjindal on 22/07/17.
 */

public class SettingsFragment extends PreferenceFragment {

    @BindView(R.id.settings_fragment_sync_button)
    ImageButton syncButton;

    private View rootView;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, rootView);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Network.isOnline(container.getContext())) {
                    SyncSurveysDialogFragment syncSurveysDialogFragment =
                            SyncSurveysDialogFragment.newInstance();
                    FragmentTransaction fragmentTransaction =
                            getFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(FragmentConstants.FRAG_SURVEYS_SYNC);
                    syncSurveysDialogFragment.setCancelable(false);
                    syncSurveysDialogFragment.show(fragmentTransaction,
                            getResources().getString(R.string.sync_clients));
                } else {
                    Toast.makeText(container.getContext(),
                          "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }
}