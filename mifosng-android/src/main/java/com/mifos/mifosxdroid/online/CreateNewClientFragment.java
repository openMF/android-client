package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.client.Client;
import com.mifos.objects.organisation.Office;
import com.mifos.services.data.ClientPayload;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.MifosApplication;
import com.mifos.utils.SafeUIBlockingUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by dikshabhatia on 13/06/15.
 */


public class CreateNewClientFragment extends Fragment implements MFDatePicker.OnDatePickListener {



    @InjectView(R.id.et_client_first_name)
    EditText et_clientFirstName;
    @InjectView(R.id.tv_client_first_name)
    TextView tv_clientFirstName;
    @InjectView(R.id.et_client_last_name)
    EditText et_clientLastName;
    @InjectView(R.id.tv_client_last_name)
    TextView tv_clientLastName;
    @InjectView(R.id.cb_client_active_status)
    CheckBox cb_clientActiveStatus;
    @InjectView(R.id.tv_submission_date)
    TextView tv_submissionDate;
    @InjectView(R.id.sp_offices)
    Spinner sp_offices;
    @InjectView(R.id.bt_submit)
    Button bt_submit;

    int officeId;

    private DialogFragment mfDatePicker;
    View rootView;
    String dateString;
    private HashMap<String, Integer> officeNameIdHashMap = new HashMap<String, Integer>();
    SafeUIBlockingUtility safeUIBlockingUtility;

    private OnFragmentInteractionListener mListener;


    public static CreateNewClientFragment newInstance() {
        CreateNewClientFragment createNewClientFragment = new CreateNewClientFragment();

        return createNewClientFragment;
    }

    public CreateNewClientFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_create_new_client,null);
        ButterKnife.inject(this, rootView);

        inflateSubmissionDate();

        cb_clientActiveStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                tv_submissionDate.setVisibility(View.VISIBLE);

            }
        });

        dateString = tv_submissionDate.getText().toString();
        Log.d("current date", DateHelper.getCurrentDateAsListOfIntegers().toString());
        dateString=DateHelper.getDateAsStringUsedForCollectionSheetPayload(dateString).replace("-", " ");




                ((MifosApplication) getActivity().getApplicationContext()).api.officeService.getAllOffices(new Callback<List<Office>>() {

                @Override
                public void success(List<Office> offices, Response response) {
                final List<String> officeList = new ArrayList<String>();

                     for (Office office : offices) {
                     officeList.add(office.getName());
                      officeNameIdHashMap.put(office.getName(), office.getId());
                     }
                     ArrayAdapter<String> officeAdapter = new ArrayAdapter<String>(getActivity(),
                     android.R.layout.simple_spinner_item, officeList);
                     officeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                      sp_offices.setAdapter(officeAdapter);
                      sp_offices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                     @Override
                     public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                      officeId = officeNameIdHashMap.get(officeList.get(i));
                      Log.d("officeId " + officeList.get(i), String.valueOf(officeId));

                    }

                     @Override
                     public void onNothingSelected(AdapterView<?> adapterView) {

                     }
              });
           }
                   @Override
                     public void failure(RetrofitError error) {
                   }
                }
                );

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClientPayload clientPayload = new ClientPayload();

                clientPayload.setFirstname(et_clientFirstName.getText().toString());
                clientPayload.setLastname(et_clientLastName.getText().toString());
                clientPayload.setActive(cb_clientActiveStatus.isChecked());
                clientPayload.setActivationDate(dateString);
                clientPayload.setOfficeId(officeId);

                initiateClientCreation(clientPayload);


            }
        });

        return rootView;

    }

    private void initiateClientCreation(ClientPayload clientPayload) {

        //TODO Validations

        //Date validation : check for date less than or equal to current date
        int i=checkDateValidation();

        if(i==-1)
        {
            Toast.makeText(getActivity()," Please enter  a valid date ",Toast.LENGTH_LONG).show();
        }
        else {

            Toast.makeText(getActivity(), "Submit data", Toast.LENGTH_LONG).show();
            ((MifosApplication) getActivity().getApplicationContext()).api.clientService.createClient(clientPayload, new Callback<Client>() {
                @Override
                public void success(Client client, Response response) {


                }

                @Override
                public void failure(RetrofitError error) {


                }
            }); }

   }

    public void inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        tv_submissionDate.setText(MFDatePicker.getDatePickedAsString());

        tv_submissionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants.DFRAG_DATE_PICKER);
            }
        });


    }

    @Override
    public void onDatePicked(String date) {

        tv_submissionDate.setText(date);
    }

    public int checkDateValidation(){
        int i;
        List<Integer> date1= new ArrayList<>();
        List<Integer> date2= new ArrayList<>();
        date1 =DateHelper.getCurrentDateAsListOfIntegers();
        date2 =DateHelper.getDateList(tv_submissionDate.getText().toString(), "-");

        Collections.reverse(date2);
        Log.d("reverse list ", date2.toString());
        i=DateHelper.dateComparator(date1, date2);
        Log.d(" date1 date 2", " " + date1.toString() + date2.toString() + " " + i);

        return i;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
