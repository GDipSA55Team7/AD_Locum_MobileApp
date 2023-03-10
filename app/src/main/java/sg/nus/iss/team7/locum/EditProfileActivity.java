package sg.nus.iss.team7.locum;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sg.nus.iss.team7.locum.APICommunication.ApiMethods;
import sg.nus.iss.team7.locum.APICommunication.RetroFitClient;
import sg.nus.iss.team7.locum.Model.FreeLancer;

public class EditProfileActivity extends AppCompatActivity {

    EditText mName,mUserName,mEmail,mPassword,mContactNumber,mMedicalLicenseNumber;
    Button mSubmitBtn,mResetBtn;
    Map<String,Boolean> mapFieldToValidStatus = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initListeners();

        //update fields with existing profile data
        FreeLancer fl = readFromSharedPref();
        displayExistingFreeLancerDetails(fl);

        mSubmitBtn = findViewById(R.id.register);
        mResetBtn = findViewById(R.id.reset);

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!allFieldsValid()){
                    //Toast.makeText(getApplicationContext(),"Make sure all fields are valid",Toast.LENGTH_SHORT).show();
                    createDialogForValidationFailed("Make sure all fields are valid");
                }
                else{
                    //update
                    //Toast.makeText(getApplicationContext(),"can proceed with update call",Toast.LENGTH_SHORT).show();

                    Retrofit retrofit = RetroFitClient.getClient(RetroFitClient.BASE_URL);
                    ApiMethods api = retrofit.create(ApiMethods.class);

                    fl.setName(mName.getText().toString().trim());
                    fl.setUsername(mUserName.getText().toString().trim());
                    fl.setEmail(mEmail.getText().toString().trim());
                    fl.setPassword(mPassword.getText().toString().trim());
                    fl.setContact(mContactNumber.getText().toString().trim());
                    fl.setMedicalLicenseNo(mMedicalLicenseNumber.getText().toString().trim());

                    Call<ResponseBody> updateFLCall = api.updateFreeLancer(fl);
                    updateFLCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.isSuccessful()){
                                if(response.code() == 200){
                                    //FreeLancer validatedFL = response.body();
                                    Toast.makeText(getApplicationContext(),"Details updated ",Toast.LENGTH_SHORT).show();
                                    //if register is successful, store in shared Pref
                                    storeFLDetailsInSharedPref(fl);
                                    //redirect
                                }
                                else if(response.code() == 404){
                                    createDialogForSubmitFailed("NOT FOUND");
                                }
                            }
                            else {
                                int statusCode = response.code();
                                if (statusCode == 500) {
                                    createDialogForSubmitFailed("Internal Server Error");
                                    //Toast.makeText(getApplicationContext(), "INTERNAL SERVER ERROR", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            if (t instanceof IOException) {
                                createDialogForSubmitFailed("Network Failure");
                                // Toast.makeText(LoginActivity.this, "Network Failure ", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                createDialogForSubmitFailed("JSON Parsing Issue");
                                // Toast.makeText(LoginActivity.this, "JSON Parsing Issue", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        mResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout =  findViewById(R.id.linearlayoutEditProfileActivity);
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
        String validEmailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}";;
        String validMedicalLicenseNumberRegex = "^M[0-9]{5}[A-Z]$";
        String validContactNumberRegex = "\\d{8}";

        listenerForRegexValidation(mEmail,"Email",validEmailRegex);
        listenerForRegexValidation(mMedicalLicenseNumber,"MedicalLicenseNumber",validMedicalLicenseNumberRegex);
        listenerForRegexValidation(mContactNumber,"ContactNumber",validContactNumberRegex);

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

        //hardcode testing
//        FreeLancer fl = new FreeLancer();
//        fl.setName("johnTan");
//        fl.setUsername("JT23");
//        fl.setContact("92287435");
//        fl.setEmail("a02@u.nus.edu");
//        fl.setMedicalLicenseNo("M12345J");
//        fl.setPassword("password");
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

    private void createDialogForValidationFailed(String msg){
        new AlertDialog.Builder(EditProfileActivity.this)
                .setIcon(R.drawable.ic_exit_application)
                .setTitle("Submit Failed")
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

    private void createDialogForSubmitFailed(String msg){
        new AlertDialog.Builder(EditProfileActivity.this)
                .setIcon(R.drawable.ic_exit_application)
                .setTitle("Submit Changes Failed")
                .setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void displayExistingFreeLancerDetails(FreeLancer fl){
        mName.setText(fl.getName());
        mUserName.setText(fl.getUsername());
        mEmail.setText(fl.getEmail());
        mPassword.setText(fl.getPassword());
        mContactNumber.setText(fl.getContact());
        mMedicalLicenseNumber.setText(fl.getMedicalLicenseNo());
    }

}