package com.automatedcartollingsystem.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
    private String emailAddress;
    private String password;
    private String uVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        TextView forgetPassword = findViewById(R.id.forgot_password_textview);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        userVerification();
        // I am suspecting that the  forgetPassword  might not work. Actually this might not work.
       /* if(uVerification.equals("True")){
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(this,
                    "Successfully logged in",Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this,
                    "Not registered or wrong password!",Toast.LENGTH_LONG).show();

        }*/
        //Launches the Registration activity class
        findViewById(R.id.register_button).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));

        });

        forgetPassword.setOnClickListener(v -> {
            Toast.makeText(SignInActivity.this,
                        "Email sent to your email address",Toast.LENGTH_SHORT).show();

        });

    }


    //I find the user credentials redundant. but on the other hand it
    // means that the data is kept by another class.
    private void userVerification(){
        EditText email = findViewById(R.id.editTextEmailAddress);
        EditText password = findViewById(R.id.editTextPassword);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        userCredentials = new UserCredentials(email.getText().toString().trim(),
                password.getText().toString().trim());

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Constants.URL_USER_STRING, (Response.Listener<String>) response -> {
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
    }
}