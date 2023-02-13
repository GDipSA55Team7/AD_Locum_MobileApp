package sg.nus.iss.team7.locum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
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
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.e("FromNotification", String.valueOf(getIntent().getBooleanExtra("fromNotification",false)));

        //If Logged In,update deviceToken and direct to MainActivity
        if(isLoggedIn() && getIntent().getBooleanExtra("fromNotification",false) == false ) {
            FreeLancer loggedInFL = SharedPrefUtility.readFromSharedPref(getApplicationContext());
            FirebaseTokenUtils.getDeviceToken(getApplicationContext(), new FirebaseTokenUtils.OnTokenReceivedListener() {
                @Override
                public void onTokenReceived(String token) {
                    loggedInFL.setDeviceToken(token);
                    Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
                    ApiMethods api = retrofit.create(ApiMethods.class);
                    Log.e("checkFL", loggedInFL.getDeviceToken());
                    Call<FreeLancer> loginFLCall = api.loginFreeLancerAndUpdateToken(loggedInFL);
                    loginFLCall.enqueue(new Callback<FreeLancer>() {
                        @Override
                        public void onResponse(@NonNull Call<FreeLancer> call, @NonNull Response<FreeLancer> response) {
                            if (response.isSuccessful() && response.code() == 200) {
                                FreeLancer existingFL = response.body();
                                if (existingFL != null && existingFL.getName() != null) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.LoginSuccess) + existingFL.getName(), Toast.LENGTH_SHORT).show();
                                    Log.e("standard login", loggedInFL.getUsername() + " login successful , token registered : " +  token);
                                    launchMainActivity();
                                }
                            } else {
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
            });
        }
        //not logged in (can be from notification redirection)
        else{
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
                            //Coming from notifications, loginuser must match notification target user
                            FreeLancer checkFLlogin = new FreeLancer();
                            checkFLlogin.setUsername(usernameInput);
                            checkFLlogin.setPassword(passwordInput);


                            //standard login (not from notification)
                                Log.e("standard login", "username : " + usernameInput);
                                FirebaseTokenUtils.getDeviceToken(getApplicationContext(), new FirebaseTokenUtils.OnTokenReceivedListener() {
                                    @Override
                                    public void onTokenReceived(String token) {
                                        checkFLlogin.setDeviceToken(token);
                                        Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
                                        ApiMethods api = retrofit.create(ApiMethods.class);
                                        Log.e("checkFL", checkFLlogin.getDeviceToken());
                                        Call<FreeLancer> loginFLCall = api.loginFreeLancerAndUpdateToken(checkFLlogin);
                                        loginFLCall.enqueue(new Callback<FreeLancer>() {
                                            @Override
                                            public void onResponse(@NonNull Call<FreeLancer> call, @NonNull Response<FreeLancer> response) {
                                                if (response.isSuccessful() && response.code() == 200) {
                                                    FreeLancer existingFL = response.body();
                                                    if (existingFL != null && existingFL.getName() != null) {
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.LoginSuccess) + existingFL.getName(), Toast.LENGTH_SHORT).show();
                                                        Log.e("standard login", usernameInput + "login successful , token registered : " +  token);

                                                        //if login is successful, store in shared Pref
                                                        SharedPrefUtility.storeFLDetailsInSharedPref(getApplicationContext(), existingFL);

                                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                                        //by default , Pixel 4 XL API 29 normal notification permissions are enabled
                                                        if (notificationManager.areNotificationsEnabled()) {
                                                            launchMainActivity();
                                                        }
                                                        //depends on phone S20 requires manual setting of permissions
                                                        else {
                                                            // Notifications are disabled, prompt the user to enable them
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                                            builder.setMessage(
                                                                            "The \"show notification\" is a normal permission so there is no need to request for permission at runtime, " +
                                                                                    "Android will automatically request for runtime permission when creating notifications.However," +
                                                                                    "for some reason, the first push notification will not be displayed when using Android's permission prompt." +
                                                                                    "As such, User has to set \"show notification\" permission manually in app settings. "
                                                                    )
                                                                    .setCancelable(false)
                                                                    .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int id) {
                                                                            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                                                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                                                            startActivity(intent);
                                                                        }
                                                                    });
                                                            AlertDialog alert = builder.create();
                                                            alert.show();
                                                        }
                                                    }
                                                } else {
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
                                });
                            }
                    });
            mRegisterBtn.setOnClickListener(v -> launchRegisterActivity());
        }
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
    protected void onPause() {
        super.onPause();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
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
    private void launchJobDetailsActivity(Integer jobId){
        Intent intent = new Intent(LoginActivity.this, JobDetailActivity.class);
        intent.putExtra("itemId", jobId);
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
        dialog = new AlertDialog.Builder(LoginActivity.this)
                .setIcon(R.drawable.ic_exit_application)
                .setTitle(getResources().getString(R.string.LoginFailed))
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.Ok), (dialog, id) -> dialog.dismiss())
                .show();
    }

    //intent from jobDetailsActivity(By Notification)
    @Override
    protected void onNewIntent(Intent intentFromNotification) {
        super.onNewIntent(intentFromNotification);
        setIntent(intentFromNotification);

        mLoginBtn.setOnClickListener(v -> {

        String usernameInput = mUserName.getText().toString().trim();
        String passwordInput = mPassword.getText().toString().trim();
        FreeLancer checkFLlogin = new FreeLancer();
        checkFLlogin.setUsername(usernameInput);
        checkFLlogin.setPassword(passwordInput);


        if (intentFromNotification.hasExtra("notificationTargetUserName") && !usernameInput.isEmpty() && !passwordInput.isEmpty()) {
            String notificationTargetUserName = intentFromNotification.getStringExtra("notificationTargetUserName");
            Log.e("onNewIntent","usertarget : " + notificationTargetUserName );
            Log.e("jobid","jobId : " + String.valueOf(intentFromNotification.getIntExtra("itemId",-1)));
            if (notificationTargetUserName != null && !notificationTargetUserName.equals("") && !usernameInput.equals(notificationTargetUserName)) {
                // loginusername do not match notificationtargetusername
                createDialogForLoginFailed("Notification not meant the username you tried to login with");
            }
            //matches, proceed with login
            else {
                Log.e("Try to login From Notification", "loginusername matches notificationtarget, logging in as  " + notificationTargetUserName);
                FirebaseTokenUtils.getDeviceToken(getApplicationContext(), new FirebaseTokenUtils.OnTokenReceivedListener() {
                    @Override
                    public void onTokenReceived(String token) {
                        checkFLlogin.setDeviceToken(token);

                        Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
                        ApiMethods api = retrofit.create(ApiMethods.class);
                        Log.e("checkFL", checkFLlogin.getDeviceToken());
                        Call<FreeLancer> loginFLCall = api.loginFreeLancerAndUpdateToken(checkFLlogin);
                        loginFLCall.enqueue(new Callback<FreeLancer>() {
                            @Override
                            public void onResponse(@NonNull Call<FreeLancer> call, @NonNull Response<FreeLancer> response) {
                                if (response.isSuccessful() && response.code() == 200) {
                                    FreeLancer existingFL = response.body();
                                    if (existingFL != null && existingFL.getName() != null) {
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.LoginSuccess) + existingFL.getName(), Toast.LENGTH_SHORT).show();
                                        Log.e("login from notification", usernameInput + "login successful , token registered : " +  token);

                                        //if login is successful, store in shared Pref
                                        SharedPrefUtility.storeFLDetailsInSharedPref(getApplicationContext(), existingFL);

                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                                        //"Show Notifications" permissions must be enabled

                                        // Pixel 4 XL API 29- By Default, normal "show notification" permissions are enabled
                                        if (notificationManager.areNotificationsEnabled()) {
                                            launchJobDetailsActivity(intentFromNotification.getIntExtra("itemId",-1));
                                        }
                                        //Depends on phone, S20 "show Notifications" permissions are disabled
                                        // redirect the user to app settings to enable permissions
                                        else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                            builder.setMessage(
                                                            "The \"show notification\" is a normal permission so there is no need to request for permission at runtime, " +
                                                                    "Android will automatically request for runtime permission when creating notifications.However," +
                                                                    "for some reason, the first push notification will not be displayed when using Android's permission prompt." +
                                                                    "As such, User has to set \"show notification\" permission manually in app settings. "
                                                    )
                                                    .setCancelable(false)
                                                    .setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                                            startActivity(intent);
                                                        }
                                                    });
                                            AlertDialog alert = builder.create();
                                            alert.show();
                                        }

                                    }
                                } else {
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
                });

            }
            }
        });
    }
}