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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.Model.FreeLancer;

public class RegisterActivity extends AppCompatActivity {

    EditText mName,mUserName,mPassword,mEmail,mContactNumber,mMedicalLicenseNumber;
    Button mRegister,mReset;
    Map<String,Boolean> mapFieldToValidStatus = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initListeners();
        mRegister = findViewById(R.id.register);
        mReset = findViewById(R.id.reset);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allFieldsValid()){
                    FreeLancer fl = new FreeLancer();
                    fl.setName(mName.getText().toString().trim());
                    fl.setUsername(mUserName.getText().toString().trim());
                    fl.setPassword(mPassword.getText().toString().trim());
                    fl.setEmail(mEmail.getText().toString().trim());
                    fl.setContact(mContactNumber.getText().toString().trim());
                    fl.setMedicalLicenseNo(mMedicalLicenseNumber.getText().toString().trim());

                    Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
                    ApiMethods api = retrofit.create(ApiMethods.class);

                    Call<FreeLancer> registerFLCall = api.registerFreeLancer(fl);
                    registerFLCall.enqueue(new Callback<FreeLancer>() {
                        @Override
                        public void onResponse(Call<FreeLancer> call, Response<FreeLancer> response) {
                            if(response.isSuccessful()){
                                if(response.code() == 201){
                                    FreeLancer returnedFL = response.body();
                                    Toast.makeText(getApplicationContext(),"Register successful, welcome " + returnedFL.getName(),Toast.LENGTH_SHORT).show();

                                    //if register is successful, store in shared Pref
                                    storeFLDetailsInSharedPref(returnedFL);

                                    //redirect
                                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }
                                else if  (response.code() == 409) {
                                    FreeLancer invalidFL = response.body();
                                    String errString =  invalidFL.getErrorsFieldString();

                                    String displayErrorTxt = "";
                                    if(errString.contains("username")){
                                        displayErrorTxt += "UserName has been taken.Please choose another unique username\n";
                                    }
                                    if(errString.contains("email")){
                                        displayErrorTxt += "Email has been taken.Please choose another unique email\n";

                                    }

                                    if(errString.contains("medical")){
                                        displayErrorTxt += "MedicalLicenseNumber has been taken.Please provide valid MedicalLicenseNumber\n";
                                    }

                                    createDialogForRegisterFailed(displayErrorTxt);
                                }
                            }
                            else {
                                int statusCode = response.code();
                                if (statusCode == 500) {
                                    createDialogForRegisterFailed("Internal Server Error");
                                    //Toast.makeText(getApplicationContext(), "INTERNAL SERVER ERROR", Toast.LENGTH_SHORT).show();
                                }


                            }
                        }
                        @Override
                        public void onFailure(Call<FreeLancer> call, Throwable t) {
                            if (t instanceof IOException) {
                                createDialogForRegisterFailed("Network Failure");
                                // Toast.makeText(LoginActivity.this, "Network Failure ", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                createDialogForRegisterFailed("JSON Parsing Issue");
                                // Toast.makeText(LoginActivity.this, "JSON Parsing Issue", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    //Toast.makeText(getApplicationContext(),"Make sure all fields are valid",Toast.LENGTH_SHORT).show();
                    createDialogForRegisterFailed("Make sure all fields are valid");
                }
            }
        });

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout =  findViewById(R.id.linearlayoutRegisterActivity);
                clearAllFields(linearLayout);
            }
        });
    }


    private void initListeners(){

        mName = findViewById(R.id.name);
        mName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        mUserName= findViewById(R.id.username);
        mUserName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        mEmail = findViewById(R.id.email);
        mEmail.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        mPassword = findViewById(R.id.password);
        mPassword.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mPassword.setTransformationMethod(new PasswordTransformationMethod());

        mContactNumber = findViewById(R.id.contactNumber);

        mMedicalLicenseNumber = findViewById(R.id.medicalLicenseNumber);
        mMedicalLicenseNumber.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        listenerForLengthValidation(mName,"Name",1,10);
        listenerForLengthValidation(mUserName,"UserName",3,12);
        listenerForLengthValidation(mPassword,"Password",5,15);

        // regex for normal email  - String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        //String validEmailRegex = "[a-zA-Z0-9._-]+@u.nus.edu";
        String validEmailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";
        String validMedicalLicenseNumberRegex = "^M[0-9]{5}[A-Z]$";
        String validContactNumberRegex = "\\d{8}";

        listenerForRegexValidation(mEmail,"Email",validEmailRegex);
        listenerForRegexValidation(mMedicalLicenseNumber,"MedicalLicenseNumber",validMedicalLicenseNumberRegex);
        listenerForRegexValidation(mContactNumber,"ContactNumber",validContactNumberRegex);

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
    private void listenerForRegexValidation(final EditText editTxt,final String fieldName,final String validPattern){
        editTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Boolean fieldIsValid = validateWithRegex(editTxt,fieldName,validPattern);
                mapFieldToValidStatus.put(fieldName,fieldIsValid);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private boolean validateWithRegex(EditText editTxt,final String fieldName, final String validRegexPattern) {

        String fieldInput = editTxt.getText().toString().trim();

        if (fieldInput.isEmpty()){
            editTxt.setError( fieldName + " cannot be empty");
            return false;
        }
        else if(!fieldInput.matches(validRegexPattern)){

            switch(fieldName){
                case "Email":
                    editTxt.setError("Must be valid  email format E.G. ABC@gmail.com");
                    break;
                case "ContactNumber":
                    editTxt.setError("Phone Number must 8 digits long");
                    break;
                case "MedicalLicenseNumber":
                    editTxt.setError("Input must follow valid format E.g. M12345J");
                    break;
                default:
                    break;
            }
            return false;
        }
        return true;
    }

    private boolean allFieldsValid(){
        boolean isValid = true;
        //contains false
        if(mapFieldToValidStatus.values().isEmpty()){
            isValid = false;
        }
        for (Boolean b : mapFieldToValidStatus.values()){
            if(b == Boolean.FALSE){
                isValid = false;
            }
        }
        return isValid;
    }
    private void clearAllFields(ViewGroup group) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }
            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearAllFields((ViewGroup)view);
        }
    }

    private void createDialogForRegisterFailed(String msg){
        new AlertDialog.Builder(RegisterActivity.this)
                .setIcon(R.drawable.ic_exit_application)
                .setTitle("Register Failed")
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void storeFLDetailsInSharedPref(FreeLancer freeLancer){
        Gson gson = new Gson();
        String json = gson.toJson(freeLancer);
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.Freelancer_Shared_Pref), MODE_PRIVATE);
        sharedPreferences.edit().putString(getResources().getString(R.string.Freelancer_Details), json).apply();
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



}