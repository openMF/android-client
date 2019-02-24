package com.mifos.mifosxdroid.aboutus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mifos.mifosxdroid.AboutUsActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.license.LicenseFragment;
import com.mifos.mifosxdroid.privacypolicy.PrivacyPolicyFragment;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by Tarun on 23-02-19.
 */

public class AboutUsFragment extends MifosBaseFragment implements AboutUsMvpView {

    @Inject
    AboutUsPresenter presenter;

    public static AboutUsFragment newInstance(Bundle args) {
        AboutUsFragment fragment = new AboutUsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about_us, container, false);
        ButterKnife.bind(this, rootView);

        presenter.attachView(this);

        ((AboutUsActivity) getActivity()).getSupportActionBar().setTitle(R.string.about_us);

        Button btnPrivacyPolicy = rootView.findViewById(R.id.btn_privacy_policy);
        Button btnLicense = rootView.findViewById(R.id.btn_license);

        btnPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AboutUsActivity) getActivity()).
                        replaceFragment(PrivacyPolicyFragment.newInstance(null),
                                true, R.id.container);
            }
        });

        btnLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AboutUsActivity) getActivity()).
                        replaceFragment(LicenseFragment.newInstance(null),
                                true, R.id.container);
            }
        });

        TextView tvCopyright = rootView.findViewById(R.id.tv_copyright);
        tvCopyright.setText(getString(R.string.copyright_mifos,
                String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));

        return rootView;
    }

    @Override
    public void showProgressbar(boolean b) {

    }
}
