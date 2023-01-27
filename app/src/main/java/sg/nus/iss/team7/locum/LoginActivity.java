package sg.nus.iss.team7.locum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.Model.FreeLancer;
import sg.nus.iss.team7.locum.Utilities.UtilityConstants;

public class LoginActivity extends AppCompatActivity {

    EditText mUsername,mPassword;
    Button mLoginBtn,mRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(isLoggedIn()) {
            // go to view job postings/ history
        }

       mUsername = findViewById(R.id.username);
       mPassword = findViewById(R.id.password);
       mLoginBtn = findViewById(R.id.login);
       mRegisterBtn = findViewById(R.id.register);

       mLoginBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String username = mUsername.getText().toString().trim();
               String password = mPassword.getText().toString().trim();

               //check if fields are empty
               if(username.isEmpty()){
                   mUsername.setError("Username cannot be empty");
               }
               if(password.isEmpty()){
                   mPassword.setError("Password cannot be empty");
               }

               if(!username.isEmpty() && !password.isEmpty()){

                   Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
                   ApiMethods api = retrofit.create(ApiMethods.class);

                   FreeLancer checkFLlogin = new FreeLancer();
                   checkFLlogin.setUserName(username);
                   checkFLlogin.setPassword(password);
                   Call<FreeLancer> loginFLCall = api.loginFreeLancer(checkFLlogin);
                   loginFLCall.enqueue(new Callback<FreeLancer>() {
                       @Override
                       public void onResponse(Call<FreeLancer> call, Response<FreeLancer> response) {
                           if(response.isSuccessful()){
                               FreeLancer validatedFL = response.body();
                               Toast.makeText(getApplicationContext(),"Login successful, welcome " + validatedFL.getName(),Toast.LENGTH_SHORT).show();
                               storeFLDetailsInSharedPref(validatedFL);

                           }
                           else {
                               int statusCode = response.code();
                               if (statusCode == 500) {
                                   Toast.makeText(getApplicationContext(), "INTERNAL SERVER ERROR", Toast.LENGTH_SHORT).show();
                               }
                               else if (statusCode == 404){
                                   Toast.makeText(getApplicationContext(), "No Such Registered User", Toast.LENGTH_SHORT).show();
                               }
                           }
                       }
                       @Override
                       public void onFailure(Call<FreeLancer> call, Throwable t) {
                           if (t instanceof IOException) {
                               Toast.makeText(LoginActivity.this, "Network Failure ", Toast.LENGTH_SHORT).show();
                           }
                           else {
                               Toast.makeText(LoginActivity.this, "JSON Parsing Issue", Toast.LENGTH_SHORT).show();
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
    //comfirmation dialog for leaving app
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

    //hide softkeyboard on lose focus
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
        SharedPreferences userDetailsSharedPref = getSharedPreferences(UtilityConstants.FREELANCER_SHARED_PREF,MODE_PRIVATE);
            return userDetailsSharedPref.contains(UtilityConstants.FREELANCER_DETAILS);
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
}