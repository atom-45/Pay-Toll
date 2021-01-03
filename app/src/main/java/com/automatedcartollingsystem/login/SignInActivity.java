package com.automatedcartollingsystem.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
    private String email;
    private String password;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        TextView forgetPassword = findViewById(R.id.forgot_password_textview);
        progressBar = findViewById(R.id.progressBar);
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

            email = emailTextField.getText().toString().trim();
            password = passwordTextField.getText().toString().trim();


            if(email.equals("") && password.equals("")){

                Toast.makeText(this,
                        "Please complete the form.",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

            } else if(email.matches(Constants.EMAIL_PATTERN)) {
                Log.e("Value",Boolean.toString(!email.matches(Constants.EMAIL_PATTERN)));
                Toast.makeText(this,
                        "This is not a valid Email Address",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

            } else {
                new SignInTask().execute();
            }
        });

        findViewById(R.id.register_button).setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),RegistrationActivity.class)));

        forgetPassword.setOnClickListener(v -> Toast.makeText(SignInActivity.this,
                    "Email sent to your email address",Toast.LENGTH_SHORT).show());
    }

    private class SignInTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            RequestQueue requestQueue = Volley.newRequestQueue(SignInActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Constants.URL_USER_STRING, response -> {

                try {
                    Log.e("JSON RESPONSE", response);
                    JSONObject jsonObject = new JSONObject(response);
                    uVerification = jsonObject.getString("result");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(uVerification.equals("True")){
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignInActivity.this,
                            "Successfully logged in",Toast.LENGTH_LONG).show();
                    //finish();
                } else {
                    Toast.makeText(SignInActivity.this,
                            "Not registered or wrong password and/or email!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE); }
                }, error -> Toast.makeText(SignInActivity.this,
                        "Cannot sign into your account!",Toast.LENGTH_LONG).show()){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> mPar = new HashMap<>();
                        mPar.put("email",email);
                        mPar.put("password",password);
                        mPar.put("type","login");
                        return mPar;
                    }

                @Override
                public Priority getPriority() {
                    return Priority.IMMEDIATE;
                }
            };

                requestQueue.add(stringRequest);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}