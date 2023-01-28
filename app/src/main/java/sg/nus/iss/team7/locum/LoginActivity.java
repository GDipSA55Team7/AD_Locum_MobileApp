package sg.nus.iss.team7.locum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
import sg.nus.iss.team7.locum.Model.FreeLancer;
import sg.nus.iss.team7.locum.Utilities.UtilityConstants;

public class LoginActivity extends AppCompatActivity {

    EditText mUserName,mPassword;
    Button mLoginBtn,mRegisterBtn;
    Map<String,Boolean> mapFieldToValidStatus = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(isLoggedIn()) {
            // go to another activity

        }
        initElementsAndListeners();
        setLoginAnimation();


       mLoginBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String usernameInput = mUserName.getText().toString().trim();
               String passwordInput = mPassword.getText().toString().trim();

               //check if fields are empty
               if(usernameInput.isEmpty()){
                   mUserName.setError("Username cannot be empty");
               }
               if(passwordInput.isEmpty()){
                   mPassword.setError("Password cannot be empty");
               }
               if(usernameInput.isEmpty() || passwordInput.isEmpty()){
                   //Toast.makeText(getApplicationContext(),"Make sure all fields are valid ",Toast.LENGTH_SHORT).show();
                   new AlertDialog.Builder(LoginActivity.this)
                           .setIcon(R.drawable.ic_exit_application)
                           .setTitle("Login Failed")
                           .setMessage("Pls check that both username and password are not empty")
                           .setCancelable(true)
                           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int id) {
                                   dialog.dismiss();
                               }
                           })
                           .show();
               }
               if(!usernameInput.isEmpty() && !passwordInput.isEmpty()){
                   Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
                   ApiMethods api = retrofit.create(ApiMethods.class);

                   FreeLancer checkFLlogin = new FreeLancer();
                   checkFLlogin.setUsername(usernameInput);
                   checkFLlogin.setPassword(passwordInput);
                   Call<FreeLancer> loginFLCall = api.loginFreeLancer(checkFLlogin);
                   loginFLCall.enqueue(new Callback<FreeLancer>() {
                       @Override
                       public void onResponse(Call<FreeLancer> call, Response<FreeLancer> response) {
                           if(response.isSuccessful()){
                               FreeLancer existingFL = response.body();
                               Toast.makeText(getApplicationContext(),"Login successful, welcome " + existingFL.getName(),Toast.LENGTH_SHORT).show();
                               //if login is successful, store in shared Pref
                               storeFLDetailsInSharedPref(existingFL);

                               //for testing editProfile API call redirect to editProfileActivity
                               Intent intent = new Intent(LoginActivity.this,EditProfileActivity.class);
                               startActivity(intent);
                           }
                           else {
                               int statusCode = response.code();
                               if (statusCode == 500) {
                                   createDialogForLoginFailed("Internal Server Error");
                                   //Toast.makeText(getApplicationContext(), "INTERNAL SERVER ERROR", Toast.LENGTH_SHORT).show();
                               }
                               else if (statusCode == 404){
                                   createDialogForLoginFailed("No Such Registered User");
                                   //Toast.makeText(getApplicationContext(), "No Such Registered User", Toast.LENGTH_SHORT).show();
                               }
                           }
                       }
                       @Override
                       public void onFailure(Call<FreeLancer> call, Throwable t) {
                           if (t instanceof IOException) {
                               createDialogForLoginFailed("Network Failure");
                               // Toast.makeText(LoginActivity.this, "Network Failure ", Toast.LENGTH_SHORT).show();
                           }
                           else {
                               createDialogForLoginFailed("JSON Parsing Issue");
                              // Toast.makeText(LoginActivity.this, "JSON Parsing Issue", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
               }
           }
       });

       mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchRegisterActivity();
            }
        });
    }

    // Comfirmation prompt for exiting app
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_exit_application)
                .setTitle("Leaving Application")
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        LoginActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
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

    private void setLoginAnimation(){
        int width=1000;
        int height=1000;
        LottieAnimationView lottieanimation=findViewById(R.id.doctor_login);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lottieanimation.getLayoutParams();
        params.width = width;
        params.height = height;
    }
    private boolean isLoggedIn(){
        SharedPreferences userDetailsSharedPref = getSharedPreferences(UtilityConstants.FREELANCER_SHARED_PREF,MODE_PRIVATE);
        return userDetailsSharedPref.contains(UtilityConstants.FREELANCER_DETAILS);
    }
    private void initElementsAndListeners(){
        mUserName = findViewById(R.id.username);
        mUserName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mPassword = findViewById(R.id.password);
        mPassword.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mPassword.setTransformationMethod(new PasswordTransformationMethod());


        listenerForLengthValidation(mUserName,"UserName",3,12);
        listenerForLengthValidation(mPassword,"Password",5,15);

        mLoginBtn = findViewById(R.id.login);
        mRegisterBtn = findViewById(R.id.register);
    }
    private boolean validateLength(EditText editTxt, String fieldName, int minChar, int maxChar){

        boolean fieldIsValid = true;
        String checkFieldStr = editTxt.getText().toString().trim();

        if(checkFieldStr.isEmpty()){
            editTxt.setError(fieldName +" must not be empty");
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
    private void launchRegisterActivity(){
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
    private void storeFLDetailsInSharedPref(FreeLancer freeLancer){
        Gson gson = new Gson();
        String json = gson.toJson(freeLancer);
        SharedPreferences sharedPreferences = getSharedPreferences(UtilityConstants.FREELANCER_SHARED_PREF, MODE_PRIVATE);
        sharedPreferences.edit().putString(UtilityConstants.FREELANCER_DETAILS, json).apply();
    }

    private void createDialogForLoginFailed(String msg){
        new AlertDialog.Builder(LoginActivity.this)
                .setIcon(R.drawable.ic_exit_application)
                .setTitle("Login Failed")
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}