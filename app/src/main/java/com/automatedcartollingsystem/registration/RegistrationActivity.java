package com.automatedcartollingsystem.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.automatedcartollingsystem.models.Constants;
import com.automatedcartollingsystem.models.User;
import com.example.automatedcartollingsystem.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private User user;
    private String mobile_number;
    private String emailAddress;
    private String userVerification;
    private String passwordVerification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setLogo(R.drawable.app_logo);
        //getActionBar().setDisplayShowTitleEnabled(false);
        //getActionBar().setLogo(R.drawable.app_logo);
        //getActionBar().setDisplayUseLogoEnabled(true);

        EditText nameEditText= findViewById(R.id.username);
        EditText mobile_numberEditText = findViewById(R.id.user_mobile_number);
        EditText emailAddressEditText = findViewById(R.id.user_email_address);
        EditText passwordEditText =   findViewById(R.id.password);
        EditText rePasswordEditText = findViewById(R.id.reenter_password);

        String name = nameEditText.toString();
        mobile_number = mobile_numberEditText.toString().trim();
        emailAddress = emailAddressEditText.toString().trim();
        String password =  passwordEditText.toString().trim();
        String rePassword = rePasswordEditText.toString().trim();

        user = new User(name, emailAddress,
                mobile_number,null,
                0L, password,null);


        //Feels like there might be an error on the second else if because I have to call the method 1st.
        if(!password.equals(rePassword)){
            Toast.makeText(this,"Passwords do not match!",Toast.LENGTH_LONG).show();
        }
        else {
            //Usually we should launch an activity.
            postCredentials();
            if(userVerification.equals("True")){
                Toast.makeText(this,"You are already a registered user\nPlease sign in",
                        Toast.LENGTH_LONG).show();
            }
            Toast.makeText(this,"You are successfully registered",Toast.LENGTH_LONG).show();
            nameEditText.clearComposingText();
            mobile_numberEditText.clearComposingText();
            emailAddressEditText.clearComposingText();
        }
        passwordEditText.clearComposingText();
        rePasswordEditText.clearComposingText();
    }

    private void postCredentials(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_USER_STRING,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        passwordVerification = jsonObject.getString("boolPass");
                        userVerification = jsonObject.getString("boolUserVeri");
                        Log.e("Password verification", passwordVerification);
                        Log.e("User Verification",userVerification);
                        //This should return a boolean value from the php script.
                        //I leave this one open because I am not getting any responses from the database.
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> { Log.e("JSON ERROR",error.getMessage());
            Toast.makeText(this,"Cannot register!", Toast.LENGTH_LONG).show();})

        { //Convert account ID into integer in the php file. why doesnt java have independant generic hashmaps?
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> mPar = new HashMap<>();
                //This puts values into the user database table.
                mPar.put("name",user.getName());
                mPar.put("email",user.getEmail());
                mPar.put("mobile_number",user.getMobile_number());
                mPar.put("license_expiry_date",user.getLicense_exp());
                mPar.put("password",user.getPassword());
                mPar.put("account_id", Long.toString(user.getAccount_id()));
                
                return mPar;
            }
        };
        requestQueue.add(stringRequest);
    }
}