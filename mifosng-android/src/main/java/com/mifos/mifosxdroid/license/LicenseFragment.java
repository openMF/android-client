package com.mifos.mifosxdroid.license;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mifos.mifosxdroid.AboutUsActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;

/**
 * Created by Tarun on 23-02-19.
 */

public class LicenseFragment extends MifosBaseFragment implements LicenseMvpView {

    @Inject
    LicensePresenter presenter;

    public static LicenseFragment newInstance(@Nullable Bundle args) {
        LicenseFragment fragment = new LicenseFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_privacy_policy, container, false);
        setHasOptionsMenu(true);
        ButterKnife.bind(this, rootView);
        presenter.attachView(this);

        setHasOptionsMenu(true);
        ((AboutUsActivity) getActivity()).getSupportActionBar()
                .setTitle(getString(R.string.license));

        WebView webViewPrivacyPolicy = rootView.findViewById(R.id.webview_privacy_policy);
        webViewPrivacyPolicy.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showProgressbar(true);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                showProgressbar(false);
                super.onPageFinished(view, url);
            }
        });
        webViewPrivacyPolicy.loadUrl(getString(R.string.url_license));

        return rootView;
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            showMifosProgressDialog();
        } else {
            hideMifosProgressDialog();
        }
    }
}
