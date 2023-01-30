package sg.nus.iss.team7.locum;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class EditProfileActivity extends AppCompatActivity {

    EditText mName,mEmail,mPassword,mContactNumber,mMedicalLicenseNumber;
    Button mSubmitBtn,mResetBtn;
    Map<String,Boolean> mapFieldToValidStatus = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initListeners();

        //update fields with existing profile data
        FreeLancer fl = readFromSharedPref();
        if(fl != null){
            displayExistingFreeLancerDetails(fl);
        }

        mSubmitBtn = findViewById(R.id.register);
        mResetBtn = findViewById(R.id.reset);

        mSubmitBtn.setOnClickListener(v -> {
            if(!allFieldsValid()){
                createDialogForValidationFailed(getResources().getString(R.string.AllFieldsAreValid));
            }
            //proceed to update
            else{

                Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
                ApiMethods api = retrofit.create(ApiMethods.class);

                if (fl != null) {
                    fl.setName(mName.getText().toString().trim());
                }
                if (fl != null) {
                    fl.setEmail(mEmail.getText().toString().trim());
                }
                if (fl != null) {
                    fl.setPassword(mPassword.getText().toString().trim());
                }
                if (fl != null) {
                    fl.setContact(mContactNumber.getText().toString().trim());
                }
                if (fl != null) {
                    fl.setMedicalLicenseNo(mMedicalLicenseNumber.getText().toString().trim());
                }

                Call<FreeLancer> updateFLCall = api.updateFreeLancer(fl);
                updateFLCall.enqueue(new Callback<FreeLancer>() {
                    @Override
                    public void onResponse(@NonNull Call<FreeLancer> call, @NonNull Response<FreeLancer> response) {
                        if(response.isSuccessful()){
                            if(response.code() == 200){
                                Toast.makeText(getApplicationContext(),"Update Success ",Toast.LENGTH_SHORT).show();
                                //if register is successful, store in shared Pref
                                storeFLDetailsInSharedPref(fl);
                                //redirect
                                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        else {
                            int statusCode = response.code();
                            if (statusCode == 500) {
                                createDialogForEditFailed(getResources().getString(R.string.InternalServerError));
                            }
                            else if  ( statusCode == 406) {
                                FreeLancer invalidFL = null;
                                if (response.errorBody() != null) {
                                    invalidFL = new Gson().fromJson( response.errorBody().charStream(), FreeLancer.class);
                                }

                                if(invalidFL != null){
                                    String errString =  invalidFL.getErrorsFieldString();

                                    String displayErrorTxt = "These fields have already been taken/registered :";
                                    if(!errString.isEmpty()){
                                        if(errString.contains("Email")){
                                            displayErrorTxt += " Email,";
                                        }

                                        if(errString.contains("Medical")){
                                            displayErrorTxt += " MedicalLicenseNumber,";
                                        }
                                        displayErrorTxt = displayErrorTxt.substring(0, displayErrorTxt.length() - 1);
                                        createDialogForEditFailed(displayErrorTxt);
                                    }
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<FreeLancer> call, @NonNull Throwable t) {
                        if (t instanceof IOException) {
                            createDialogForEditFailed(getResources().getString(R.string.NetworkFailure));
                        }
                        else {
                            createDialogForEditFailed(getResources().getString(R.string.JSONParsingIssue));
                        }
                    }
                });
            }
        });
        mResetBtn.setOnClickListener(v -> {
            LinearLayout linearLayout =  findViewById(R.id.linearlayoutEditProfileActivity);
            clearAllFields(linearLayout);
        });
    }

    private void initListeners(){

        mName = findViewById(R.id.name);
        mName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        mEmail = findViewById(R.id.email);
        mEmail.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        mPassword = findViewById(R.id.password);
        mPassword.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mPassword.setTransformationMethod(new PasswordTransformationMethod());

        mContactNumber = findViewById(R.id.contactNumber);

        mMedicalLicenseNumber = findViewById(R.id.medicalLicenseNumber);
        mMedicalLicenseNumber.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        listenerForLengthValidation(mName,getResources().getString(R.string.Name),1,10);
        listenerForLengthValidation(mPassword,getResources().getString(R.string.Password),5,15);

        String validEmailRegex = getResources().getString(R.string.ValidEmailRegex);
        String validMedicalLicenseNumberRegex = getResources().getString(R.string.ValidMedicalLicenseNumberRegex);
        String validContactNumberRegex = getResources().getString(R.string.ValidContactNumberRegex);

        listenerForRegexValidation(mEmail,getResources().getString(R.string.Email),validEmailRegex);
        listenerForRegexValidation(mMedicalLicenseNumber,getResources().getString(R.string.MedicalLicenseNumber),validMedicalLicenseNumberRegex);
        listenerForRegexValidation(mContactNumber,getResources().getString(R.string.ContactNumber),validContactNumberRegex);

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

    private FreeLancer readFromSharedPref(){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.Freelancer_Shared_Pref), MODE_PRIVATE);
        String json = sharedPreferences.getString(getResources().getString(R.string.Freelancer_Details), "");
        FreeLancer fl = gson.fromJson(json, FreeLancer.class);
        return fl;
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
            if (b == Boolean.FALSE) {
                isValid = false;
                break;
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

    private void createDialogForValidationFailed(String msg){
        new AlertDialog.Builder(EditProfileActivity.this)
                .setIcon(R.drawable.ic_exit_application)
                .setTitle(getResources().getString(R.string.SubmitFailed))
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.Ok), (dialog, id) -> dialog.dismiss())
                .show();
    }

    private void storeFLDetailsInSharedPref(FreeLancer freeLancer){
        Gson gson = new Gson();
        String json = gson.toJson(freeLancer);
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.Freelancer_Shared_Pref), MODE_PRIVATE);
        sharedPreferences.edit().putString(getResources().getString(R.string.Freelancer_Details), json).apply();
    }

    private void createDialogForEditFailed(String msg){
        new AlertDialog.Builder(EditProfileActivity.this)
                .setIcon(R.drawable.ic_exit_application)
                .setTitle(getResources().getString(R.string.SubmitChangesFailed))
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.Ok), (dialog, id) -> dialog.dismiss())
                .show();
    }

    private void displayExistingFreeLancerDetails(FreeLancer fl){
        if(fl.getName() != null){
            mName.setText(fl.getName());
        }
        if(fl.getEmail() != null) {
            mEmail.setText(fl.getEmail());
        }
        if(fl.getPassword() != null) {
            mPassword.setText(fl.getPassword());
        }
        if(fl.getContact() != null) {
            mContactNumber.setText(fl.getContact());
        }
        if(fl.getMedicalLicenseNo() != null) {
            mMedicalLicenseNumber.setText(fl.getMedicalLicenseNo());
        }
    }
}
