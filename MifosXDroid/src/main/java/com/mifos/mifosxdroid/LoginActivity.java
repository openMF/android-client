package com.mifos.mifosxdroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.objects.User;
import com.mifos.utils.MifosRestAdapter;
import com.mifos.utils.services.UserAuthService;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by ishankhanna on 08/02/14.
 */
public class LoginActivity extends ActionBarActivity implements Callback<User>, View.OnClickListener {

    SharedPreferences sharedPreferences;
    private EditText et_instanceURL;
    private EditText et_username;
    private EditText et_password;
    private Button bt_login;
    private String username;
    private String instanceURL;
    private String password;
    private MifosRestAdapter mifosRestAdapter;
    private UserAuthService userAuthService;
    private Context context;
    private String authenticationToken;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        authenticationToken = sharedPreferences.getString(User.AUTHENTICATION_KEY, "NA");

        setupUI();

    }


    /*
       Perform All UI Based Operations on activity creation
     */
    public void setupUI() {

        et_instanceURL = (EditText) findViewById(R.id.et_instanceURL);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);

        bt_login = (Button) findViewById(R.id.bt_login);
        bt_login.setOnClickListener(this);

        progressDialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Logging In");
        progressDialog.setCancelable(false);

    }

    public void login(String username, String password) {
        mifosRestAdapter = new MifosRestAdapter();

        userAuthService = mifosRestAdapter.getRestAdapter().create(UserAuthService.class);

        userAuthService.authenticate(username, password, this);
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
        Intent intent = new Intent(LoginActivity.this, DashboardFragmentActivity.class);
        saveAuthenticationKey("Basic " + user.getBase64EncodedAuthenticationKey());
        startActivity(intent);
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        progressDialog.dismiss();
        Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.bt_login:
                try {
                    if (validateUserInputs())
                        progressDialog.show();
                    login(username, password);
                } catch (ShortOfLengthException e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                }
        }

    }

    public void saveAuthenticationKey(String authenticationKey) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(User.AUTHENTICATION_KEY, authenticationKey);
        editor.commit();
        editor.apply();
    }
}
