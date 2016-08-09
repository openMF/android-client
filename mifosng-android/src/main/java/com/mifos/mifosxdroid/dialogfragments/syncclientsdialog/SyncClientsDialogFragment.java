package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.objects.client.Client;
import com.mifos.utils.Constants;

import butterknife.ButterKnife;

/**
 *
 * Created by Rajan Maurya on 08/08/16.
 */
public class SyncClientsDialogFragment extends DialogFragment {


    private View rootView;

    private Client mClient;

    public static SyncClientsDialogFragment newInstance(Client client) {
        SyncClientsDialogFragment syncClientsDialogFragment = new SyncClientsDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.CLIENT, client);
        syncClientsDialogFragment.setArguments(args);
        return syncClientsDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null)
            mClient = getArguments().getParcelable(Constants.CLIENT);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_fragment_sync_clients, container, false);
        ButterKnife.bind(this, rootView);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);


        return rootView;
    }
}
