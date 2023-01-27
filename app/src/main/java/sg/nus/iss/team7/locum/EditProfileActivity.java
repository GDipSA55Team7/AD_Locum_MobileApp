package sg.nus.iss.team7.locum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import sg.nus.iss.team7.locum.Model.FreeLancer;
import sg.nus.iss.team7.locum.Utilities.UtilityConstants;

public class EditProfileActivity extends AppCompatActivity {

    EditText mName,mUserName,mEmail,mPassword,mContactNumber,mMedicalLicenseNumber;
    Button mSubmitBtn,mResetBtn;
    boolean nameIsValid = true ,usernameIsValid = true,passwordIsValid = true,emailIsValid = true,
            contactNumberIsValid = true,medicalLicenseNumberIsValid = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        initElements();

        //logged in get from shared Pref
        FreeLancer fl = readFromSharedPref();
        mName.setText(fl.getName());
        mUserName.setText(fl.getUserName());
        mEmail.setText(fl.getEmail());
        mPassword.setText(fl.getPassword());
        mContactNumber.setText(fl.getContact());
        mMedicalLicenseNumber.setText(fl.getMedicalLicenseNo());

        mSubmitBtn = findViewById(R.id.register);
        mResetBtn = findViewById(R.id.reset);


        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!allFieldsValid()){
                    Toast.makeText(getApplicationContext(),"Make sure all fields are valid",Toast.LENGTH_SHORT).show();
                }
                else{
                    //update
                    Toast.makeText(getApplicationContext(),"can proceed with update call",Toast.LENGTH_SHORT).show();
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

    private void initElements(){

        mName = findViewById(R.id.name);
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nameIsValid = updateErrorTxt(mName,"Name",3,20);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mUserName= findViewById(R.id.username);
        mUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                usernameIsValid = updateErrorTxt(mUserName,"userName",3,20);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mEmail = findViewById(R.id.email);
        mEmail.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = mEmail.getText().toString().trim();
                if(!validateEmail(email).equals("")){
                    mEmail.setError(validateEmail(email));
                    if(emailIsValid){
                        emailIsValid = false;
                    }
                }
                else{
                    if(!emailIsValid){
                        emailIsValid = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mPassword = findViewById(R.id.password);
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordIsValid = updateErrorTxt(mPassword,"Password",3,20);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mContactNumber = findViewById(R.id.contactNumber);
        mContactNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = mContactNumber.getText().toString().trim();
                if (input.matches("\\d{8}")) {
                    contactNumberIsValid = true;
                } else {
                    contactNumberIsValid = false;
                    mContactNumber.setError("Phone Number must 8 digits long");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mMedicalLicenseNumber = findViewById(R.id.medicalLicenseNumber);
        mMedicalLicenseNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String input = mMedicalLicenseNumber.getText().toString().trim();
                if (input.matches("^M[0-9]{5}[A-Z]$")) {
                    medicalLicenseNumberIsValid = true;
                } else {
                    medicalLicenseNumberIsValid = false;
                    mMedicalLicenseNumber.setError("Input must follow valid format E.g. M12345J");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private boolean updateErrorTxt(EditText editTxt, String fieldName, int minChar, int maxChar){

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

    private String validateEmail(String emailInput) {
        // for normal email  - String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String emailPattern = "[a-zA-Z0-9._-]+@u.nus.edu";
        if (emailInput.isEmpty()){
            return "Email cannot be empty";
        }
        if(!emailInput.matches(emailPattern)){
            return "Must be valid NUS email format E.g. ABC@u.nus.edu";
        }
        return "";
    }
    private FreeLancer readFromSharedPref(){
//        Gson gson = new Gson();
//        SharedPreferences sharedPreferences = getSharedPreferences(UtilityConstants.FREELANCER_SHARED_PREF, MODE_PRIVATE);
//        String json = sharedPreferences.getString(UtilityConstants.FREELANCER_DETAILS, "");
//        FreeLancer fl = gson.fromJson(json, FreeLancer.class);
//        return fl;

        //hardcode testing
        FreeLancer fl = new FreeLancer();
        fl.setName("johnTan");
        fl.setUserName("JT23");
        fl.setContact("92287435");
        fl.setEmail("a02@u.nus.edu");
        fl.setMedicalLicenseNo("M12345J");
        fl.setPassword("password");
        return fl;
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

    private boolean allFieldsValid(){
        return nameIsValid && usernameIsValid && passwordIsValid && emailIsValid && medicalLicenseNumberIsValid && contactNumberIsValid;
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

}