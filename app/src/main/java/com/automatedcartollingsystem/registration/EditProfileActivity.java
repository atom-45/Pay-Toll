package com.automatedcartollingsystem.registration;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.automatedcartollingsystem.models.Constants;
import com.example.automatedcartollingsystem.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {


    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        EditText carBrandEditText = findViewById(R.id.inputCarBrandEditText);
        EditText carModelEditText = findViewById(R.id.inputCarModelEditText);
        EditText carRegEditText = findViewById(R.id.inputCarRegistrationEditText);


        findViewById(R.id.update_button).setOnClickListener(v -> {

            String carBrandText = carBrandEditText.getText().toString().trim();
            String carModelText = carModelEditText.getText().toString().trim();
            String carRegText = carRegEditText.getText().toString().trim();

            if(!carBrandText.equals("")|| !carModelText.equals("")
            || !carRegText.equals("")){
                //Main problem is getting the data
                putCarData(requestQueue,carBrandText,carModelText,carRegText);

            } else {
                Toast.makeText(this,"Incomplete!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //
    private void putCarData(RequestQueue requestQueue,String carBrand,String carModel,String carReg){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_CAR_STRING,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        result = jsonObject.getString("result");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(result!=null && result.equals("True")){
                        Toast.makeText(this,"Successfully updated your details",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(EditProfileActivity.this,
                                ProfileActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this,"Something went wrong, try again",
                                Toast.LENGTH_SHORT).show();
                    }

                }, Throwable::printStackTrace)
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> parameters = new HashMap<>();

                parameters.put("brand",carBrand);
                parameters.put("model",carModel);
                parameters.put("registration",carReg);
                parameters.put("user_id",ProfileActivity.user_id);
                parameters.put("type","edit");

                return parameters;
            }
        };
        requestQueue.add(stringRequest);
    }
}
