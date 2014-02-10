package com.mifos.mifosxdroid;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by ishankhanna on 09/02/14.
 */
public class ClientListFragment extends Fragment{

    private View rootView;

    private AutoCompleteTextView actv_clientSearch;
    private TextView tv_fullName;
    private TextView tv_accountNumber;
    private TextView tv_externalId;
    private TextView tv_activationDate;
    private TextView tv_office;
    private TextView tv_group;
    private TextView tv_loanOfficer;
    private TextView tv_loanCycle;

    private Button bt_showClientDetails;

    private ArrayAdapter<String> clientNames;

    public ClientListFragment(ArrayAdapter<String> clientNames){

        this.clientNames = clientNames;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = (View) inflater.inflate(R.layout.fragment_client,null);

        setupUI();



        return rootView;
    }

    public void setupUI()
    {

        actv_clientSearch = (AutoCompleteTextView) rootView.findViewById(R.id.actv_clientSearch);
        //TODO Add Client names to this box
        actv_clientSearch.setAdapter(this.clientNames);

        tv_fullName = (TextView) rootView.findViewById(R.id.tv_fullName);
        tv_accountNumber = (TextView) rootView.findViewById(R.id.tv_accountNumber);
        tv_externalId = (TextView) rootView.findViewById(R.id.tv_externalId);
        tv_activationDate = (TextView) rootView.findViewById(R.id.tv_activationDate);
        tv_office = (TextView) rootView.findViewById(R.id.tv_office);
        tv_group = (TextView) rootView.findViewById(R.id.tv_group);
        tv_loanOfficer = (TextView) rootView.findViewById(R.id.tv_loanOfficer);
        tv_loanCycle = (TextView) rootView.findViewById(R.id.tv_loanCycle);

        bt_showClientDetails = (Button) rootView.findViewById(R.id.bt_showClientDetails);
        bt_showClientDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });

    }

}
