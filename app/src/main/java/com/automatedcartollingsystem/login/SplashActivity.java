package com.automatedcartollingsystem.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.automatedcartollingsystem.appfunctionality.NavigationActivity;

public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();

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
        },2000);

    }
}

/*
    <LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:map="http://schemas.android.com/apk/res-auto"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <fragment
        android:id="@+id/map"
        android:name="com.google.android.gms.maps.SupportMapFragment"
        android:layout_width="match_parent"
        android:layout_height="550dp"
        map:layout_constraintEnd_toEndOf="parent"
        map:layout_constraintHorizontal_bias="0.0"
        map:layout_constraintStart_toStartOf="parent"
        map:layout_constraintTop_toTopOf="parent"
        tools:context="com.automatedcartollingsystem.appfunctionality.MapsFragment" />

    <com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/bottom_nav_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:menu="@menu/bottom_navigation_items"
        map:layout_constraintBottom_toBottomOf="parent"
        map:layout_constraintEnd_toEndOf="parent"
        map:layout_constraintHorizontal_bias="0.5"
        map:layout_constraintStart_toStartOf="parent"
        map:layout_constraintTop_toBottomOf="@+id/map"
        map:layout_constraintVertical_bias="0.5" />

</LinearLayout>

 */