package com.automatedcartollingsystem.appfunctionality;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.automatedcartollingsystem.models.Constants;
import com.example.automatedcartollingsystem.R;

import org.json.JSONException;

import java.util.Locale;

public class NavigationActivity extends AppCompatActivity {

    /**
     * My Navigation xml file or activity needs to be redesigned.
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);


        EditText startDestination = findViewById(R.id.start_editText);
        EditText endDestination = findViewById(R.id.end_editText);

        findViewById(R.id.set_button).setOnClickListener(v -> {
            String start = startDestination.getText().toString().trim();
            String end = endDestination.getText().toString().trim();

            if(start.equals(end) || start.equals("")||end.equals("")){
                Toast.makeText(getApplicationContext(),
                        "From and To destinations cannot be the same",Toast.LENGTH_SHORT)
                        .show();
            } else{
                startActivity(new Intent(getApplicationContext(),MapsActivity.class).
                        putExtra(Constants.START_DESTINATION,start).
                        putExtra(Constants.END_DESTINATION,end));
                finish();
            }
        });
    }
}