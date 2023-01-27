package sg.nus.iss.team7.locum;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class RegisterActivity extends AppCompatActivity {

    EditText mName,mUserName,mPassword,mEmail,mContactNumber,mMedicalLicenseNumber;
    Button mRegister,mReset;
    boolean nameIsValid ,usernameIsValid,passwordIsValid,emailIsValid,
            contactNumberIsValid,medicalLicenseNumberIsValid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initElements();
        mRegister = findViewById(R.id.register);
        mReset = findViewById(R.id.reset);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allFieldsValid()){
                    Toast.makeText(getApplicationContext(),"Proceed with register",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Make sure all fields are valid",Toast.LENGTH_SHORT).show();
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