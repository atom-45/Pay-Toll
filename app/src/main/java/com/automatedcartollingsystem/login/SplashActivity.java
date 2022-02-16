package com.automatedcartollingsystem.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.automatedcartollingsystem.appfunctionality.NavigationActivity;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    
    //Basically this code below does almost nothing when it comes to controlling the time needed to display the splash screen, though it allows the time to launch a different activity at a given time period.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_splash);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                finish();
            }
        },1000);

    }
}
