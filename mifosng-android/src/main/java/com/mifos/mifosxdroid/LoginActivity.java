/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.mifosxdroid.online.DashboardFragmentActivity;
import com.mifos.objects.User;
import com.mifos.services.API;
import com.mifos.utils.Constants;

import org.apache.http.HttpStatus;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 08/02/14.
 */
public class LoginActivity extends ActionBarActivity implements Callback<User>{

    private static final String DOMAIN_NAME_REGEX_PATTERN = "^[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final String IP_ADDRESS_REGEX_PATTERN = "^(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))$";
    public static String PROTOCOL_HTTP = "http://";
    public static String PROTOCOL_HTTPS = "https://";
    public static String API_PATH = "/mifosng-provider/api/v1";
    SharedPreferences sharedPreferences;
    @InjectView(R.id.et_instanceURL) EditText et_instanceURL;
    @InjectView(R.id.et_username) EditText et_username;
    @InjectView(R.id.et_password) EditText et_password;
    @InjectView(R.id.bt_login) Button bt_login;
    @InjectView(R.id.tv_constructed_instance_url) TextView tv_constructed_instance_url;
    private String username;
    private String instanceURL;
    private String password;
    private Context context;
    private String authenticationToken;
    private ProgressDialog progressDialog;
    private String TAG = "LoginActivity";
    private Pattern domainNamePattern;
    private Matcher domainNameMatcher;
    private Pattern ipAddressPattern;
    private Matcher ipAddressMatcher;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String previouslyEnteredUrl = sharedPreferences.getString(Constants.INSTANCE_URL_KEY,
                getString(R.string.default_instance_url));
        authenticationToken = sharedPreferences.getString(User.AUTHENTICATION_KEY, "NA");

        ButterKnife.inject(this);
        setupUI();
        if(bt_login==null)
        {
            Log.i(TAG, "login button is null");
        }
        else {
            Log.i(TAG, "login button is not null");
        }

        domainNamePattern = Pattern.compile(DOMAIN_NAME_REGEX_PATTERN);
        ipAddressPattern = Pattern.compile(IP_ADDRESS_REGEX_PATTERN);

        tv_constructed_instance_url.setText(PROTOCOL_HTTPS + previouslyEnteredUrl + API_PATH);
        et_instanceURL.setText(previouslyEnteredUrl);

        et_instanceURL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {



            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {



            }

            @Override
            public void afterTextChanged(Editable editable) {

                String textUnderConstruction = PROTOCOL_HTTPS + editable.toString() + API_PATH;
                tv_constructed_instance_url.setText(textUnderConstruction);

                if(!validateURL(editable.toString())) {
                    tv_constructed_instance_url.setTextColor(getResources().getColor(R.color.red));
                } else {
                    tv_constructed_instance_url.setTextColor(getResources().getColor(R.color.deposit_green));
                }

            }
        });
    }

    public void setupUI()
    {
        progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Logging In");
        progressDialog.setCancelable(false);
    }

    public boolean validateUserInputs() throws ShortOfLengthException {

        //TODO Create All Validations Here for all input fields

        String urlInputValue = et_instanceURL.getEditableText().toString();
        try {

            if(!validateURL(urlInputValue)) {
                return false;
            }

            String validDomain = validateInstanceUrl(urlInputValue);
            Log.d("Filtered URL", validDomain);
            String constructedURL = PROTOCOL_HTTPS + validDomain + API_PATH;
            tv_constructed_instance_url.setText(constructedURL);
            URL url = new URL(constructedURL);
            instanceURL = url.toURI().toString();
            Log.d(TAG, "instance URL: " + instanceURL);
            API.setInstanceUrl(instanceURL);
            saveInstanceUrl(instanceURL);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Invalid instance URL: " + urlInputValue, e);
            throw new ShortOfLengthException("Instance URL", 5);
        } catch (URISyntaxException uriException) {
            Log.e(TAG, "Invalid instance URL: " + urlInputValue, uriException);
            throw new ShortOfLengthException("Instance URL", 5);
        }

        username = et_username.getEditableText().toString();
        if (username.length() < 5) {
            throw new ShortOfLengthException("Username", 5);
        }

        password = et_password.getEditableText().toString();
        if (password.length() < 6) {
            throw new ShortOfLengthException("Password", 6);
        }

        return true;
    }


    @Override
    public void success(User user, Response response) {
        progressDialog.dismiss();
        Toast.makeText(context, "Welcome " + user.getUsername(), Toast.LENGTH_SHORT).show();
        saveAuthenticationKey("Basic " + user.getBase64EncodedAuthenticationKey());
        Intent intent = new Intent(LoginActivity.this, DashboardFragmentActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        try {
            progressDialog.dismiss();
            if (retrofitError.getResponse().getStatus() == HttpStatus.SC_UNAUTHORIZED)
                Toast.makeText(context, getString(R.string.error_login_failed), Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Toast.makeText(context, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.bt_login)
    public void onLoginClick(Button button){
        login();
    }

    private void login() {
        try {
            if (validateUserInputs())
                progressDialog.show();

            API.userAuthService.authenticate(username, password, this);
        } catch (ShortOfLengthException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @OnEditorAction(R.id.et_password)
    public boolean passwordSubmitted(KeyEvent keyEvent) {
        if (keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            login();
            return true;
        }
        return false;
    }

    public void saveAuthenticationKey(String authenticationKey) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(User.AUTHENTICATION_KEY, authenticationKey);
        editor.commit();
        editor.apply();
    }

    public void saveInstanceUrl(String instanceURL) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.INSTANCE_URL_KEY, instanceURL);
        editor.commit();
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.offline_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.offline:
                startActivity(new Intent(this, OfflineCenterInputActivity.class));
                break;

            default: //DO NOTHING
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public String validateInstanceUrl(String url) {

        String filteredUrl;

        if(url.contains("https://")) {

            //Strip https:// from the URL
            filteredUrl = url.replace("https://","");

        }else if(url.contains("http://")) {

            //String http:// from the URL
            filteredUrl = url.replace("http://","");
        }else{

            //String URL doesn't include protocol
            filteredUrl = url;
        }

        if(filteredUrl.charAt(filteredUrl.length()-1) == '/') {
            filteredUrl = filteredUrl.replace("/","");
        }

        return filteredUrl;

    }

    public boolean validateURL(final String hex) {

        domainNameMatcher = domainNamePattern.matcher(hex);
        ipAddressMatcher = ipAddressPattern.matcher(hex);
        if (domainNameMatcher.matches()) return true;
        if (ipAddressMatcher.matches()) return true;
        return false;
    }

}
