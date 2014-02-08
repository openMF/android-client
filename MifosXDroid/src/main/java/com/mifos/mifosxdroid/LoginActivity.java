package com.mifos.mifosxdroid;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.EditText;

import com.mifos.exceptions.ShortOfLengthException;
import com.mifos.objects.User;
import com.mifos.utils.MifosRestAdapter;
import com.mifos.utils.UserAuthService;

import retrofit.client.Response;

/**
 * Created by ishankhanna on 08/02/14.
 */
public class LoginActivity extends ActionBarActivity {

    private EditText et_instanceURL;    //Text Input for instance URl
    private EditText et_username;       //Text Input for Username
    private EditText et_password;       //Text Input for Password

    private String username;
    private String instanceURL;
    private String password;

    private MifosRestAdapter mifosRestAdapter;
    private UserAuthService userAuthService;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mifosRestAdapter = new MifosRestAdapter();

        userAuthService = mifosRestAdapter.getRestAdapter().create(UserAuthService.class);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        try{

            User user = userAuthService.authenticate("mifos","password");
            Log.i("User",user.toString());

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    /*
        Method to Perform All UI Based Operations on activity creation
     */
    public void setupUI(){

        et_instanceURL = (EditText) findViewById(R.id.et_instanceURL);
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);

    }

    public boolean validateUserInputs() throws ShortOfLengthException{

        //TODO Create All Validations Here for all input fields

        username = et_username.getEditableText().toString();
        if(username.length()<8)
        {
            throw new ShortOfLengthException("Username",8);
        }

        password = et_password.getEditableText().toString();
        if(password.length()<6)
        {
            throw new ShortOfLengthException("Password",6);
        }


        return true;
    }

}
