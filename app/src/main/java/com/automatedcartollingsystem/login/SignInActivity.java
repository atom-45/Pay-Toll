package com.automatedcartollingsystem.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.automatedcartollingsystem.login.data.UserCredentials;
import com.automatedcartollingsystem.models.Constants;
import com.automatedcartollingsystem.registration.RegistrationActivity;
import com.example.automatedcartollingsystem.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    private UserCredentials userCredentials;
    private String uVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        TextView forgetPassword = findViewById(R.id.forgot_password_textview);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        EditText emailTextField = findViewById(R.id.editTextEmailAddress);
        EditText passwordTextField = findViewById(R.id.editTextPassword);

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
             && ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) !=
                PackageManager.PERMISSION_GRANTED) {

             ActivityCompat.requestPermissions(this, new String[]
                     {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                     6854);

        }
        //This should include the network or internet verification
        progressBar.setVisibility(View.INVISIBLE);
        findViewById(R.id.sign_in_button).setOnClickListener(v -> {
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);

            String email = emailTextField.getText().toString().trim();
            String password = passwordTextField.getText().toString().trim();
            userVerification(email, password);

            if("True".equals(uVerification)){//This is a Yoda condition
                Toast.makeText(SignInActivity.this,
                        "Successfully logged in",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SignInActivity.this,
                        "Not registered or wrong password and/or email!",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        findViewById(R.id.register_button).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));
            finish();
        });

        forgetPassword.setOnClickListener(v -> Toast.makeText(SignInActivity.this,
                    "Email sent to your email address",Toast.LENGTH_SHORT).show());
    }

    //I find the user credentials redundant. but on the other hand it
    // means that the data is kept by another class/object.
    private void userVerification(String email, String password){

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        if(!email.equals("") && !password.equals("") && email.matches(Constants.EMAIL_PATTERN)){
            userCredentials = new UserCredentials(email,password);
            Log.e("Email",email);
            Log.e("Password",password);

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Constants.URL_USER_STRING, response -> {
                Log.e("JSON RESPONSE", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    uVerification = jsonObject.getString("boolSignIn");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                Toast.makeText(this,
                        "Cannot sign into your account!",Toast.LENGTH_LONG).show();
                Log.e("JSON ERROR",error.getMessage());

            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError{
                    Map<String, String> mPar = new HashMap<>();
                    mPar.put("Email",userCredentials.getEmail());
                    mPar.put("Password",userCredentials.getPassword());
                    return mPar;
                }
            };
            requestQueue.add(stringRequest);

        } else {
            Toast.makeText(this,
                    "Password is wrong or email address is invalid",Toast.LENGTH_SHORT).show();
        }
    }
}