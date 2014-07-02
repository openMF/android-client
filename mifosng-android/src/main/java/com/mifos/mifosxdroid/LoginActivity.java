package com.mifos.mifosxdroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

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

    public static String INSTANCE_URL_KEY = "instanceURL";
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
    private String tag = getClass().getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String previouslyEnteredUrl = sharedPreferences.getString(INSTANCE_URL_KEY,
                getString(R.string.default_instance_url));
        authenticationToken = sharedPreferences.getString(User.AUTHENTICATION_KEY, "NA");

        ButterKnife.inject(this);
        setupUI();
        if(bt_login==null)
        {
            Log.i(tag, "login button is null");
        }
        else {
            Log.i(tag, "login button is not null");
        }
        tv_constructed_instance_url.setText(PROTOCOL_HTTPS + previouslyEnteredUrl + API_PATH);
        et_instanceURL.setText(previouslyEnteredUrl);
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
            String validDomain = validateInstanceUrl(urlInputValue);
            Log.d("Filtered URL", validDomain);
            String constructedURL = PROTOCOL_HTTPS + validDomain + API_PATH;
            tv_constructed_instance_url.setText(constructedURL);
            URL url = new URL(constructedURL);
            instanceURL = url.toURI().toString();
            Log.d(tag, "instance URL: " + instanceURL);
            API.setInstanceUrl(instanceURL);
            saveInstanceUrl(validDomain);
        } catch (MalformedURLException e) {
            Log.e(tag, "Invalid instance URL: " + urlInputValue, e);
            throw new ShortOfLengthException("Instance URL", 5);
        } catch (URISyntaxException uriException) {
            Log.e(tag, "Invalid instance URL: " + urlInputValue, uriException);
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
        progressDialog.dismiss();
        Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show();
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
        editor.putString(INSTANCE_URL_KEY, instanceURL);
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
        Log.d(tag, "onOptionsItemSelected: " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.offline:
                startActivity(new Intent(this, GroupActivity.class));
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

}
