package com.automatedcartollingsystem.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private String result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText nameEditText= findViewById(R.id.username);
        EditText mobile_numberEditText = findViewById(R.id.user_mobile_number);
        EditText emailAddressEditText = findViewById(R.id.user_email_address);
        EditText passwordEditText =   findViewById(R.id.password);
        EditText rePasswordEditText = findViewById(R.id.reenter_password);

        findViewById(R.id.register_button_2).setOnClickListener(v -> {

            String password =  passwordEditText.getText().toString().trim();
            String rePassword = rePasswordEditText.getText().toString().trim();
            String name = nameEditText.getText().toString();
            mobile_number = mobile_numberEditText.getText().toString().trim();
            emailAddress = emailAddressEditText.getText().toString().trim();
            //Feels like there might be an error on the second else if because I have to call the method 1st.

            user = new User(name, emailAddress,
                    mobile_number,"none",
                    0L, password,null);

            if(!password.equals(rePassword)){
                Toast.makeText(RegistrationActivity.this,
                        "Passwords do not match!",Toast.LENGTH_SHORT).show();

            } else if(name.equals("")||mobile_number.equals("")
                    ||emailAddress.equals("")||emailAddress.matches(Constants.EMAIL_PATTERN)){
                Toast.makeText(RegistrationActivity.this,
                        "Complete all Fields",Toast.LENGTH_SHORT).show();
            }
            else {
                //Usually we should launch an activity. Should remove the getText().clear and replace it with finish;
                postCredentials();
                if(result.equals("True")){ // Hope this won't produce null error at runtime.
                    Toast.makeText(RegistrationActivity.this,"You are already a registered user\nPlease sign in",
                            Toast.LENGTH_LONG).show();
                }
                Toast.makeText(RegistrationActivity.this,"You are successfully registered",Toast.LENGTH_LONG).show();
                //nameEditText.getText().clear();
                //mobile_numberEditText.getText().clear();
                //emailAddressEditText.getText().clear();
            }
            //passwordEditText.getText().clear();
            //rePasswordEditText.getText().clear();

        });
    }

    private void postCredentials(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_USER_STRING,
                response -> {
                    try {
                        Log.e("JSON RESPONSE", response);
                        JSONObject jsonObject = new JSONObject(response);
                        result = jsonObject.getString("result");

                        //This should return a boolean value from the php script.
                        //I leave this one open because I am not getting any responses from the database.
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> { Log.e("JSON ERROR",error.getMessage());
            Toast.makeText(this,"Cannot register!", Toast.LENGTH_LONG).show();})

        { //Convert account ID into integer in the php file. why doesnt java have independant generic hashmaps?
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> mPar = new HashMap<>();
                //This puts values into the user database table.
                mPar.put("name",user.getName());
                mPar.put("email",user.getEmail());
                mPar.put("mobile_number",user.getMobile_number());
                mPar.put("license_expiry_date",user.getLicense_exp());
                mPar.put("password",user.getPassword());
                mPar.put("account_id", Long.toString(user.getAccount_id()));
                mPar.put("type","register");

                return mPar;
            }
        };
        requestQueue.add(stringRequest);
    }
}