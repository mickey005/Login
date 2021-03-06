package com.example.udacity.surfconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.accountkit.AccessToken;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends AppCompatActivity {

    public  static int APP_REQUEST_CODE=1;
    LoginButton fbloginButton;
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FontHelper.setCustomTypeface(findViewById(R.id.view_root));

        fbloginButton =(LoginButton)findViewById(R.id.facebook_login_button);
        fbloginButton.setReadPermissions("email");
        //login  Button callback registration

        callbackManager = CallbackManager.Factory.create();
        fbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                launchAccountActivity();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

                //display error

                String toastMessage =error.getMessage();
                Toast.makeText(LoginActivity.this,toastMessage,Toast.LENGTH_LONG).show();

            }
        });

        //check for an existing access token
        AccessToken accessToken = AccountKit.getCurrentAccessToken();
        com.facebook.AccessToken loginToken = com.facebook.AccessToken.getCurrentAccessToken();

        if (accessToken!= null||loginToken==null){
            //if previously logged in , proceed to the account activity

            launchAccountActivity();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //forward the result to the callback manger for login
        callbackManager.onActivityResult(requestCode,resultCode,data);

        //confirm that this response matches your request

        if(requestCode ==APP_REQUEST_CODE){
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (loginResult.getError()!=null){
                //display error
                String toastMessage = loginResult.getError().getErrorType().getMessage();
                Toast.makeText(this,toastMessage,Toast.LENGTH_LONG).show();
            }else if (loginResult.getAccessToken()!=null){
                //on successful login proceed to the account activity
                launchAccountActivity();
            }
        }
    }

    private void onLogin(final LoginType loginType){
        //create intent for the account kit activity
        final  Intent intent = new Intent(this, AccountKitActivity.class);

        //configure login type and response type

        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder=
                new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType,
                        AccountKitActivity.ResponseType.TOKEN);
        final AccountKitConfiguration configuration = configurationBuilder.build();
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, configuration);
        startActivityForResult(intent,APP_REQUEST_CODE);
    }

    public void onPhoneLogIn(View view){
        onLogin(LoginType.PHONE);
    }

    public void onEmailLogin(View view){
        onLogin(LoginType.EMAIL);
    }
    private void launchAccountActivity() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
        finish();
    }


}
