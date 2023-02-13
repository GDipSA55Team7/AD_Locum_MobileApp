package sg.nus.iss.team7.locum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class RegisterActivity extends AppCompatActivity {

    EditText mName,mUserName,mPassword,mEmail,mContactNumber,mMedicalLicenseNumber;
    Button mRegister,mReset;
    Map<String,Boolean> mapFieldToValidStatus = new HashMap<>();
    FreeLancer fl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initListenersAndFieldValidationListener();

        mRegister = findViewById(R.id.register);
        mReset = findViewById(R.id.reset);

        mRegister.setOnClickListener(v -> {
            if(allFieldsValid()){
                //listener for receiving token
                FirebaseTokenUtils.getDeviceToken(getApplicationContext(), token -> {
                    //onTokenReceived(String token), set token on freelancer object to be send in API call
                    setFreeLancerDetails(token);
                    Log.e("Sending DeviceToken", fl.getDeviceToken());

                    //Register API Call
                    Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
                    ApiMethods api = retrofit.create(ApiMethods.class);
                    Call<FreeLancer> loginFLCall = api.registerFreeLancer(fl);
                    loginFLCall.enqueue(new Callback<FreeLancer>() {
                        @Override
                        public void onResponse(@NonNull Call<FreeLancer> call, @NonNull Response<FreeLancer> response) {
                            if (response.isSuccessful() && response.code() == 201) {
                                FreeLancer returnedFL = response.body();
                                if (returnedFL != null && returnedFL.getName() != null) {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.RegisterSuccess) + returnedFL.getName(),Toast.LENGTH_SHORT).show();
                                    //Store freeLancer Details into shared Pref
                                    SharedPrefUtility.storeFLDetailsInSharedPref(getApplicationContext(),returnedFL);
                                    launchMainActivity();
                                }
                            }
                            //Register API Call failed
                            else {
                                int statusCode = response.code();
                                if (statusCode == 500) {
                                    createDialogForRegisterFailed(getResources().getString(R.string.InternalServerError));
                                }
                                //Server-side Validation Error - non-unique Fields(username,Email,medicalLicenseNo)
                                else if  ( statusCode == 406) {
                                    FreeLancer invalidFL = null;
                                    if (response.errorBody() != null) {
                                        //converting a JSON response string from an API to FreeLancer class object using GSON
                                        invalidFL = new Gson().fromJson( response.errorBody().charStream(), FreeLancer.class);
                                    }
                                    //display fields that have failed validation
                                    if(invalidFL != null){
                                        String errString =  invalidFL.getErrorsFieldString();
                                        String displayErrorTxt = "These fields have already been taken/registered :";
                                        if(!errString.isEmpty()){
                                            if(errString.contains("Username")){
                                                displayErrorTxt += " UserName,";
                                            }
                                            if(errString.contains("Email")){
                                                displayErrorTxt += " Email,";
                                            }
                                            if(errString.contains("Medical")){
                                                displayErrorTxt += " MedicalLicenseNumber,";
                                            }
                                            displayErrorTxt = displayErrorTxt.substring(0, displayErrorTxt.length() - 1);
                                            createDialogForRegisterFailed(displayErrorTxt);
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<FreeLancer> call, @NonNull Throwable t) {
                            if (t instanceof IOException) {
                                createDialogForRegisterFailed(getResources().getString(R.string.NetworkFailure));
                            }
                            else {
                                createDialogForRegisterFailed(getResources().getString(R.string.JSONParsingIssue));
                            }
                        }
                    });
                });
            }
            else{
                createDialogForRegisterFailed(getResources().getString(R.string.AllFieldsAreValid));
            }
        });

        mReset.setOnClickListener(v -> {
            LinearLayout linearLayout =  findViewById(R.id.linearlayoutRegisterActivity);
            clearAllFields(linearLayout);
        });
    }

    private void initListenersAndFieldValidationListener(){

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

        listenerForLengthValidation(mName,getResources().getString(R.string.Name),1,10);
        listenerForLengthValidation(mUserName,getResources().getString(R.string.UserName),3,12);
        listenerForLengthValidation(mPassword,getResources().getString(R.string.Password),5,15);

        String validEmailRegex = getResources().getString(R.string.ValidEmailRegex);
        String validMedicalLicenseNumberRegex = getResources().getString(R.string.ValidMedicalLicenseNumberRegex);
        String validContactNumberRegex = getResources().getString(R.string.ValidContactNumberRegex);

        listenerForRegexValidation(mEmail,getResources().getString(R.string.Email),validEmailRegex);
        listenerForRegexValidation(mMedicalLicenseNumber,getResources().getString(R.string.MedicalLicenseNumber),validMedicalLicenseNumberRegex);
        listenerForRegexValidation(mContactNumber,getResources().getString(R.string.ContactNumber),validContactNumberRegex);
    }

    private boolean validateLength(EditText editTxt, String fieldName, int minChar, int maxChar){

        boolean fieldIsValid = true;
        String checkFieldStr = editTxt.getText().toString().trim();

        if(checkFieldStr.isEmpty()){
            editTxt.setError(fieldName + getResources().getString(R.string.MustNotBeEmpty));
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
            editTxt.setError( fieldName + getResources().getString(R.string.MustNotBeEmpty));
            return false;
        }
        else if(!fieldInput.matches(validRegexPattern)){

            switch(fieldName){

                case "Email":
                    editTxt.setError("Must be valid " + getResources().getString(R.string.Email) + getResources().getString(R.string.EmailValidation));
                    break;
                case "ContactNumber":
                    editTxt.setError( getResources().getString(R.string.ContactNumber)  + getResources().getString(R.string.ContactNumberValidation));
                    break;
                case "MedicalLicenseNumber":
                    editTxt.setError("Must be valid " + getResources().getString(R.string.MedicalLicenseNumber) + getResources().getString(R.string.MedicalLicenseNumberValidation));
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
                .setTitle(getResources().getString(R.string.RegisterFailed))
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.Ok), (dialog, id) -> dialog.dismiss())
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

    private void launchMainActivity(){
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setFreeLancerDetails(String deviceToken){
        fl = new FreeLancer();
        fl.setName(mName.getText().toString().trim());
        fl.setUsername(mUserName.getText().toString().trim());
        fl.setPassword(mPassword.getText().toString().trim());
        fl.setEmail(mEmail.getText().toString().trim());
        fl.setContact(mContactNumber.getText().toString().trim());
        fl.setMedicalLicenseNo(mMedicalLicenseNumber.getText().toString().trim());
        fl.setErrorsFieldString("");
        fl.setId("");
        fl.setDeviceToken(deviceToken);
    }
}
