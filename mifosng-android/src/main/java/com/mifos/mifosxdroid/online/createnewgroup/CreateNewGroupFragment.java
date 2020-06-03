/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.createnewgroup;

/**
 * Created by nellyk on 1/22/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.exceptions.InvalidTextInputException;
import com.mifos.exceptions.RequiredFieldException;
import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.GroupsActivity;
import com.mifos.mifosxdroid.uihelpers.MFDatePicker;
import com.mifos.objects.group.GroupPayload;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.response.SaveResponse;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.MifosResponseHandler;
import com.mifos.utils.Network;
import com.mifos.utils.PrefManager;
import com.mifos.utils.ValidationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

//TODO Show Image and Text after successful or Failed during creation of Group and
//TODO A button to Continue or Finish the GroupCreation.
public class CreateNewGroupFragment extends ProgressableFragment
        implements MFDatePicker.OnDatePickListener, CreateNewGroupMvpView,
        AdapterView.OnItemSelectedListener {


    private final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.et_group_name)
    EditText et_groupName;

    @BindView(R.id.et_group_external_id)
    EditText et_groupexternalId;

    @BindView(R.id.cb_group_active_status)
    CheckBox cb_groupActiveStatus;

    @BindView(R.id.tv_group_submission_date)
    TextView tv_submissionDate;

    @BindView(R.id.tv_group_activationDate)
    TextView tv_activationDate;

    @BindView(R.id.sp_group_offices)
    Spinner sp_offices;

    @BindView(R.id.btn_submit)
    Button bt_submit;

    @BindView(R.id.layout_submission)
    LinearLayout layout_submission;

    @Inject
    CreateNewGroupPresenter mCreateNewGroupPresenter;

    String activationdateString;
    int officeId;
    Boolean result = true;
    View rootView;
    String dateofsubmissionstring;
    private DialogFragment mfDatePicker;
    private DialogFragment newDatePicker;

    private List<String> mListOffices = new ArrayList<>();
    private List<Office> officeList;
    private ArrayAdapter<String> mOfficesAdapter;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.sp_group_offices) {
            officeId = officeList.get(position).getId();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public CreateNewGroupFragment() {
        // Required empty public constructor
    }

    public static CreateNewGroupFragment newInstance() {
        CreateNewGroupFragment createNewGroupFragment = new CreateNewGroupFragment();
        return createNewGroupFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_create_new_group, null);

        ButterKnife.bind(this, rootView);
        mCreateNewGroupPresenter.attachView(this);

        inflateOfficesSpinner();
        inflateSubmissionDate();
        inflateActivationDate();

        mCreateNewGroupPresenter.loadOffices();

        //client active checkbox onCheckedListener
        cb_groupActiveStatus.setOnCheckedChangeListener(new CompoundButton
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    layout_submission.setVisibility(View.VISIBLE);
                } else {
                    layout_submission.setVisibility(View.GONE);
                }

            }
        });

        activationdateString = tv_activationDate.getText().toString();
        activationdateString = DateHelper.getDateAsStringUsedForCollectionSheetPayload
                (activationdateString).replace("-", " ");
        dateofsubmissionstring = tv_submissionDate.getText().toString();
        dateofsubmissionstring = DateHelper.getDateAsStringUsedForDateofBirth
                (dateofsubmissionstring).replace("-", " ");

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Network.isOnline(getContext())) {
                    GroupPayload groupPayload = new GroupPayload();

                    groupPayload.setName(et_groupName.getEditableText().toString());
                    groupPayload.setExternalId(et_groupexternalId.getEditableText().toString());
                    groupPayload.setActive(cb_groupActiveStatus.isChecked());
                    groupPayload.setActivationDate(activationdateString);
                    groupPayload.setSubmissionDate(dateofsubmissionstring);
                    groupPayload.setOfficeId(officeId);
                    groupPayload.setDateFormat("dd MMMM yyyy");
                    groupPayload.setLocale("en");

                    initiateGroupCreation(groupPayload);
                } else {
                    Toaster.show(rootView, R.string.error_network_not_available, Toaster.LONG);
                }
            }
        });

        return rootView;
    }

    private void initiateGroupCreation(GroupPayload groupPayload) {
        //TextField validations
        if (!isGroupNameValid()) {
            return;
        }

        mCreateNewGroupPresenter.createGroup(groupPayload);
    }


    private void inflateOfficesSpinner() {
        mOfficesAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item,
                mListOffices);
        mOfficesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_offices.setAdapter(mOfficesAdapter);
        sp_offices.setOnItemSelectedListener(this);
    }

    public void inflateSubmissionDate() {
        mfDatePicker = MFDatePicker.newInsance(this);

        tv_submissionDate.setText(MFDatePicker.getDatePickedAsString());

        tv_submissionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mfDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                        .DFRAG_DATE_PICKER);
            }
        });

    }

    public void inflateActivationDate() {
        newDatePicker = MFDatePicker.newInsance(this);

        tv_activationDate.setText(MFDatePicker.getDatePickedAsString());

        tv_activationDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newDatePicker.show(getActivity().getSupportFragmentManager(), FragmentConstants
                        .DFRAG_DATE_PICKER);
            }

        });


    }

    public void onDatePicked(String date) {
        tv_submissionDate.setText(date);
        tv_activationDate.setText(date);

    }

    public boolean isGroupNameValid() {
        result = true;
        try {
            if (TextUtils.isEmpty(et_groupName.getEditableText().toString())) {
                throw new RequiredFieldException(getResources().getString(R.string.group_name),
                        getResources().getString(R.string.error_cannot_be_empty));
            }

            if (et_groupName.getEditableText().toString().trim().length() < 4 && et_groupName
                    .getEditableText().toString().trim().length() > 0) {
                throw new ShortOfLengthException(getResources().getString(R.string.group_name), 4);
            }
            if (!ValidationUtil.isNameValid(et_groupName.getEditableText().toString())) {
                throw new InvalidTextInputException(getResources().getString(R.string.group_name)
                        , getResources().getString(R.string.error_should_contain_only),
                        InvalidTextInputException.TYPE_ALPHABETS);
            }
        } catch (InvalidTextInputException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        } catch (ShortOfLengthException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        } catch (RequiredFieldException e) {
            e.notifyUserWithToast(getActivity());
            result = false;
        }

        return result;
    }


    @Override
    public void showOffices(List<Office> offices) {
        officeList = offices;
        for (Office office : offices) {
            mListOffices.add(office.getName());
        }
        Collections.sort(mListOffices);
        mOfficesAdapter.notifyDataSetChanged();
    }

    @Override
    public void showGroupCreatedSuccessfully(SaveResponse saveResponse) {
        Toast.makeText(getActivity(), "Group " + MifosResponseHandler.getResponse(),
                Toast.LENGTH_LONG).show();
        getActivity().getSupportFragmentManager().popBackStack();
        if (PrefManager.getUserStatus() == Constants.USER_ONLINE) {
            Intent groupActivityIntent = new Intent(getActivity(), GroupsActivity.class);
            groupActivityIntent.putExtra(Constants.GROUP_ID, saveResponse.getGroupId());
            startActivity(groupActivityIntent);
        }
    }

    @Override
    public void showFetchingError(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCreateNewGroupPresenter.detachView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}