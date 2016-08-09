package com.mifos.mifosxdroid.dialogfragments.syncclientsdialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.objects.client.Client;
import com.mifos.utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 *
 * Created by Rajan Maurya on 08/08/16.
 */
public class SyncClientsDialogFragment extends DialogFragment {


    @BindView(R.id.tv_sync_title)
    TextView tv_sync_title;

    @BindView(R.id.tv_client_name)
    TextView tv_syncing_client_name;

    @BindView(R.id.tv_total_clients)
    TextView tv_total_clients;

    @BindView(R.id.tv_syncing_client)
    TextView tv_syncing_client;

    @BindView(R.id.pb_sync_client)
    ProgressBar pb_syncing_client;

    @BindView(R.id.tv_total_progress)
    TextView tv_total_progress;

    @BindView(R.id.pb_total_sync_client)
    ProgressBar pb_total_sync_client;

    @BindView(R.id.tv_sync_failed)
    TextView tv_sync_failed;


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

    @OnClick(R.id.btn_cancel)
    void OnClickCancelButton() {
        getDialog().dismiss();
    }
}
