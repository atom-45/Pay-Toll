package com.automatedcartollingsystem.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.automatedcartollingsystem.appfunctionality.MapsActivity;
import com.automatedcartollingsystem.models.Account;
import com.automatedcartollingsystem.models.Constants;
import com.automatedcartollingsystem.models.Registration;
import com.automatedcartollingsystem.models.User;
import com.example.automatedcartollingsystem.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RegistrationActivity extends AppCompatActivity {

    private User user;
    private String name;
    private String password;
    private Account account;
    private String mobile_number;
    private String emailAddress;
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
        RegistrationActivity registrationActivity = new RegistrationActivity();

        findViewById(R.id.register_button_2).setOnClickListener(v -> {

            password =  passwordEditText.getText().toString().trim();
            String rePassword = rePasswordEditText.getText().toString().trim();
            name = nameEditText.getText().toString();
            mobile_number = mobile_numberEditText.getText().toString().trim();
            emailAddress = emailAddressEditText.getText().toString().trim();

            if(!password.equals(rePassword)){
                Toast.makeText(RegistrationActivity.this,
                        "Passwords do not match!",Toast.LENGTH_SHORT).show();
                passwordEditText.getText().clear();
                rePasswordEditText.getText().clear();

            } else if(name.equals("")||mobile_number.equals("")
                    ||emailAddress.equals("")||emailAddress.matches(Constants.EMAIL_PATTERN)){
                Toast.makeText(RegistrationActivity.this,
                        "Complete all Fields",Toast.LENGTH_SHORT).show();
            } else {
                new AccountTask().execute(name);
            }
        });
    }


    //It can only work iff it uses the same WIFI it cannot work using mobile data. for that the URL should be from a server/domain.
    private class RegistrationTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... id) {
            RequestQueue requestQueue = Volley.newRequestQueue(RegistrationActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_USER_STRING,
                    response -> {
                        try {
                            Log.e("JSON RESPONSE", response);
                            JSONObject jsonObject = new JSONObject(response);
                            result = jsonObject.getString("result");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(result.equals("True")){
                            Toast.makeText(RegistrationActivity.this,
                                    "You are successfully registered",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrationActivity.this, MapsActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegistrationActivity.this,
                                    "You are not registered",Toast.LENGTH_SHORT).show();

                        }
                    }, error -> { //Log.e("JSON ERROR",error.getMessage());
                Toast.makeText(RegistrationActivity.this,"Cannot register!", Toast.LENGTH_SHORT).show();})

            { //Convert account ID into integer in the php file. why doesnt java have independant generic hashmaps?
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> mPar = new HashMap<>();
                    //This puts values into the user database table.
                    mPar.put("name",name);
                    mPar.put("email",emailAddress);
                    mPar.put("mobile_number",mobile_number);
                    mPar.put("license_expiry_date","None");
                    mPar.put("password",password);
                    mPar.put("account_id", Integer.toString(id[0]));
                    mPar.put("type","register");

                    return mPar;
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

    private class AccountTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... voids) {

            RequestQueue requestQueue = Volley.newRequestQueue(RegistrationActivity.this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_ACCOUNT_STRING,
                    response -> {

                        try {
                            Log.e("JSON RESPONSE", response);
                            JSONObject jsonObject = new JSONObject(response);

                            account = new Account(Integer.parseInt(jsonObject.getString("account_id")),
                                    Double.parseDouble(jsonObject.getString("account_balance")),
                                    Long.parseLong(jsonObject.getString("account_number")),
                                    "Standard Bank", jsonObject.getString("name"),
                                    jsonObject.getString("account_type"),
                                    jsonObject.getString("account_email")
                            );

                            new RegistrationTask().execute(account.getAccount_id());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> { Log.e("JSON ERROR",error.getMessage());
                Toast.makeText(RegistrationActivity.this,"Account not verified", Toast.LENGTH_SHORT).show();})

            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String,String> mPar = new HashMap<>();
                    //This puts values into the user database table.
                    mPar.put("name",voids[0]);
                    mPar.put("request","get");

                    return mPar;
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

    /*
     * Key Problems: TextView spacing that would prevent the account from not being verified.
     *               Extracting values from the database.
     *               The zero value issue on the error log: Here is what I think, the reason why I am
     *               getting the zero first before the actual number is because initially account_id is assigned
     *               a zero value before being assigned the proper and correct value in on created.
     */
}