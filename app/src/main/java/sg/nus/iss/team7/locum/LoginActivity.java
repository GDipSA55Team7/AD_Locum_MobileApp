package sg.nus.iss.team7.locum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.FireBase.FirebaseTokenUtils;
import sg.nus.iss.team7.locum.Model.FreeLancer;
import sg.nus.iss.team7.locum.Utilities.SharedPrefUtility;

public class LoginActivity extends AppCompatActivity {

    EditText mUserName,mPassword;
    Button mLoginBtn,mRegisterBtn;
    Map<String,Boolean> mapFieldToValidStatus = new HashMap<>();
    String notificationTargetUserName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //If Logged In, direct to MainActivity
        if(isLoggedIn()) {
            FreeLancer loggedInFL = SharedPrefUtility.readFromSharedPref(getApplicationContext());
            FirebaseTokenUtils.sendTokenToServerOnLogin(loggedInFL.getUsername(),getApplicationContext());
            launchMainActivity();
        }

        initElementsAndListeners();

        mLoginBtn.setOnClickListener(v -> {
                    String usernameInput = mUserName.getText().toString().trim();
                    String passwordInput = mPassword.getText().toString().trim();

                    //check if fields are empty
                    if (usernameInput.isEmpty()) {
                        mUserName.setError(getResources().getString(R.string.UserName));
                    }
                    if (passwordInput.isEmpty()) {
                        mPassword.setError(getResources().getString(R.string.Password));
                    }
                    if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setIcon(R.drawable.ic_exit_application)
                                .setTitle(getResources().getString(R.string.LoginFailed))
                                .setMessage(getResources().getString(R.string.LoginFailedUserNameAndPasswordEmpty))
                                .setCancelable(true)
                                .setPositiveButton(getResources().getString(R.string.Ok), (dialog, id) -> dialog.dismiss())
                                .show();
                    }

                    if (!usernameInput.isEmpty() && !passwordInput.isEmpty()) {

                        Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
                        ApiMethods api = retrofit.create(ApiMethods.class);

                        //coming from notifications, loginuser must match notification target user
                        Intent intentNotificationUser = getIntent();
                        if (intentNotificationUser.hasExtra("notificationTargetUserName")) {
                            notificationTargetUserName = intentNotificationUser.getStringExtra("notificationTargetUserName");
                            if (notificationTargetUserName != null && !notificationTargetUserName.equals("")) {
                                // loginusername do not match notificationtargetusername
                                if (!usernameInput.equals(notificationTargetUserName)) {
                                    createDialogForLoginFailed("Notification not meant the username you tried to login with");
                                }

                                //matches, proceed with login
                                else {
                                    Log.e("Try to login From Notification", "loginusername matches notificationtarget, logging in as  " + notificationTargetUserName);
                                    FreeLancer checkFLlogin = new FreeLancer();
                                    checkFLlogin.setUsername(usernameInput);
                                    checkFLlogin.setPassword(passwordInput);

                                    Call<FreeLancer> loginFLCall = api.loginFreeLancer(checkFLlogin);
                                    loginFLCall.enqueue(new Callback<FreeLancer>() {
                                        @Override
                                        public void onResponse(@NonNull Call<FreeLancer> call, @NonNull Response<FreeLancer> response) {
                                            if (response.isSuccessful() && response.code() == 200) {
                                                FreeLancer existingFL = response.body();
                                                if (existingFL != null && existingFL.getName() != null) {
                                                    Log.e("Try to login From Notification", "login success ");
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.LoginSuccess) + existingFL.getName(), Toast.LENGTH_SHORT).show();

                                                    //if login is successful, store in shared Pref
                                                    SharedPrefUtility.storeFLDetailsInSharedPref(getApplicationContext(), existingFL);

                                                    //register device token with springboot server
                                                    FirebaseTokenUtils.sendTokenToServerOnLogin(existingFL.getUsername(),getApplicationContext());

                                                    Log.e("Try to login From Notification", "route from LoginActivity to JobDetailsActivity and pass jobId");
                                                    //send jobid to jobDetailsActivity retrieve jobdetails
                                                    Integer jobId = intentNotificationUser.getIntExtra("itemId", 0);
                                                    Intent intent = new Intent(LoginActivity.this, JobDetailActivity.class);
                                                    intent.putExtra("itemId", jobId);
                                                    startActivity(intent);
                                                }
                                            }
                                            else {
                                                int statusCode = response.code();
                                                if (statusCode == 500) {
                                                    createDialogForLoginFailed(getResources().getString(R.string.InternalServerError));
                                                } else if (statusCode == 404) {
                                                    createDialogForLoginFailed(getResources().getString(R.string.NoSuchRegisteredUser));
                                                }
                                            }
                                        }
                                        @Override
                                        public void onFailure(@NonNull Call<FreeLancer> call, @NonNull Throwable t) {
                                            if (t instanceof IOException) {
                                                createDialogForLoginFailed(getResources().getString(R.string.NetworkFailure));
                                            } else {
                                                createDialogForLoginFailed(getResources().getString(R.string.JSONParsingIssue));
                                            }
                                        }
                                });
                            }
                        }
                    }

                    //standard login (not from notification)
                    else if (notificationTargetUserName.equals("")) {
                        Log.e("standard login", "username : " + notificationTargetUserName);
                        FreeLancer checkFLlogin = new FreeLancer();
                        checkFLlogin.setUsername(usernameInput);
                        checkFLlogin.setPassword(passwordInput);

                        Call<FreeLancer> loginFLCall = api.loginFreeLancer(checkFLlogin);
                        loginFLCall.enqueue(new Callback<FreeLancer>() {
                            @Override
                            public void onResponse(@NonNull Call<FreeLancer> call, @NonNull Response<FreeLancer> response) {
                                if (response.isSuccessful() && response.code() == 200) {
                                    FreeLancer existingFL = response.body();
                                    if (existingFL != null && existingFL.getName() != null) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.LoginSuccess) + existingFL.getName(), Toast.LENGTH_SHORT).show();

                                        //if login is successful, store in shared Pref
                                        SharedPrefUtility.storeFLDetailsInSharedPref(getApplicationContext(),existingFL);

                                        //register device token with springboot server
                                        FirebaseTokenUtils.sendTokenToServerOnLogin(existingFL.getUsername(),getApplicationContext());

                                        //go to mainactivity for standardlogin
                                        launchMainActivity();
                                    }
                                }
                                else {
                                    int statusCode = response.code();
                                    if (statusCode == 500) {
                                        createDialogForLoginFailed(getResources().getString(R.string.InternalServerError));
                                    } else if (statusCode == 404) {
                                        createDialogForLoginFailed(getResources().getString(R.string.NoSuchRegisteredUser));
                                    }
                                }
                            }
                            @Override
                            public void onFailure(@NonNull Call<FreeLancer> call, @NonNull Throwable t) {
                                if (t instanceof IOException) {
                                    createDialogForLoginFailed(getResources().getString(R.string.NetworkFailure));
                                } else {
                                    createDialogForLoginFailed(getResources().getString(R.string.JSONParsingIssue));
                                }
                            }
                        });
                    }
                }
            });
        mRegisterBtn.setOnClickListener(v -> launchRegisterActivity());
    }
    // Comfirmation prompt for exiting app
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_exit_application)
                .setTitle(getResources().getString(R.string.LeavingApplication))
                .setMessage(getResources().getString(R.string.LeavingApplicationPrompt))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.Yes), (dialog, id) -> LoginActivity.super.onBackPressed())
                .setNegativeButton(getResources().getString(R.string.No), null)
                .show();
    }
    @Override
    protected void onResume() {
        if(isLoggedIn()){
            launchMainActivity();
        }
        super.onResume();
    }
    // Hide softkeyboard on element loses focus
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
    private boolean isLoggedIn(){
        SharedPreferences userDetailsSharedPref = getSharedPreferences(getResources().getString(R.string.Freelancer_Shared_Pref),MODE_PRIVATE);
        return userDetailsSharedPref.contains(getResources().getString(R.string.Freelancer_Details));
    }
    private void initElementsAndListeners(){
        mUserName = findViewById(R.id.username);
        mUserName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mPassword = findViewById(R.id.password);
        mPassword.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mPassword.setTransformationMethod(new PasswordTransformationMethod());

        listenerForLengthValidation(mUserName,getResources().getString(R.string.UserName),3,12);
        listenerForLengthValidation(mPassword,getResources().getString(R.string.Password),5,15);

        mLoginBtn = findViewById(R.id.login);
        mRegisterBtn = findViewById(R.id.register);
    }
    private void launchMainActivity(){
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void launchRegisterActivity(){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    private boolean validateLength(EditText editTxt, String fieldName, int minChar, int maxChar){

        boolean fieldIsValid = true;
        String checkFieldStr = editTxt.getText().toString().trim();

        if(checkFieldStr.isEmpty()){
            editTxt.setError(fieldName +getResources().getString(R.string.MustNotBeEmpty));
            if(fieldIsValid){
                fieldIsValid = false;
            }
        }
        else if (checkFieldStr.length() < minChar || checkFieldStr.length() > maxChar){
            editTxt.setError(fieldName + " must be between " +minChar + " and " + maxChar + " characters");
            if(fieldIsValid){
                fieldIsValid = false;
            }
        }
        return fieldIsValid;
    }
    private void listenerForLengthValidation(final EditText editTxt,final String fieldName,final int minChar,final int maxChar){
        editTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Boolean fieldIsValid = validateLength(editTxt, fieldName, minChar, maxChar);
                mapFieldToValidStatus.put(fieldName, fieldIsValid);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private void createDialogForLoginFailed(String msg){
        new AlertDialog.Builder(LoginActivity.this)
                .setIcon(R.drawable.ic_exit_application)
                .setTitle(getResources().getString(R.string.LoginFailed))
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.Ok), (dialog, id) -> dialog.dismiss())
                .show();
    }
}
