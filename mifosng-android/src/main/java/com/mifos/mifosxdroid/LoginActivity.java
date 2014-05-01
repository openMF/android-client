package com.mifos.mifosxdroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.objects.User;
import com.mifos.utils.services.API;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 08/02/14.
 */
public class LoginActivity extends ActionBarActivity implements Callback<User> {

    SharedPreferences sharedPreferences;
    @InjectView(R.id.et_instanceURL) EditText et_instanceURL;
    @InjectView(R.id.et_username) EditText et_username;
    @InjectView(R.id.et_password) EditText et_password;
    @InjectView(R.id.bt_login) Button bt_login;

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
        authenticationToken = sharedPreferences.getString(User.AUTHENTICATION_KEY, "NA");

        ButterKnife.inject(this);
        setupUI();
        if(bt_login==null)
        {
            Log.i(tag,"login button is null");
        }
        else
            Log.i(tag,"login button is not null");
    }
    private void setTestLoginData()
    {
        et_username.setText("mifos");
        et_password.setText( "password");
    }
    public void setupUI() {

        progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Logging In");
        progressDialog.setCancelable(false);

    }

    public boolean validateUserInputs() throws ShortOfLengthException {

        //TODO Create All Validations Here for all input fields

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
    public void login(Button button){

        setTestLoginData();//TODO Delete it
        try {
            if (validateUserInputs())
                progressDialog.show();

            API.userAuthService.authenticate(username, password, this);

        } catch (ShortOfLengthException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void saveAuthenticationKey(String authenticationKey) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(User.AUTHENTICATION_KEY, authenticationKey);
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

        switch (item.getItemId()) {
            case R.id.offline:
                startActivity(new Intent(this, GroupActivity.class));
                break;

            default: //DO NOTHING
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
