package com.automatedcartollingsystem.appfunctionality;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.automatedcartollingsystem.registration.ProfileActivity;
import com.example.automatedcartollingsystem.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.GeoApiContext;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private boolean locationPermissionGranted;
    //private GoogleMap googleMap;
    private Location lastKnownLocation;
    private CameraPosition cameraPosition;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GeoApiContext geoApiContext;
    private PlacesClient placesClient; //This accesses the GDB.


    private  static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    List<Address> addresses1,addresses2;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    public MapsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView
                .OnNavigationItemSelectedListener() {
            Intent intent;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.account_history_icon:
                        intent = new Intent(getApplicationContext(),TransactionHistoryActivity.class);
                        break;

                    case R.id.traffic_update_icon:
                        intent = new Intent(getApplicationContext(),TrafficUpdateActivity.class);
                        break;

                    case R.id.navigation_icon:
                        intent = new Intent(getApplicationContext(),NavigationActivity.class);
                        break;

                    case R.id.profile_icon:
                        intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        break;

                    default:
                        throw new IllegalArgumentException("Unexpected value: " + item.getItemId());
                }
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //getDeviceLocation();


        /*final EditText editText = findViewById(R.id.editTextTextPersonName2);
        final EditText editText1 = findViewById(R.id.editTextTextPersonName);
        final Geocoder geocoder = new Geocoder(this);


        //I can add a FAB such that when the user clicks on it, it can show traffic.
        //I have to play around with this.
        findViewById(R.id.floatingActionButton5).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMap.setTrafficEnabled(true);
                        Snackbar.make(view,"Traffic enabled",Snackbar.LENGTH_SHORT).
                                setAction("Action",null).show();
                    }
                }
        );

        findViewById(R.id.button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String startLocation = editText.getText().toString();
                        String endLocation = editText1.getText().toString();
                        try {
                            addresses1 = geocoder.getFromLocationName(startLocation,1);
                            addresses2 = geocoder.getFromLocationName(endLocation,1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Address address = addresses1.get(0);
                        Address address2 = addresses2.get(0);
                        com.google.maps.model.LatLng origin =
                                new com.google.maps.model.LatLng(address.getLatitude(),address.getLongitude());

                        com.google.maps.model.LatLng destination =
                                new com.google.maps.model.LatLng(address2.getLatitude(),address2.getLongitude());
                        try {
                            getRoute(origin,destination,mMap);
                        } catch (InterruptedException | IOException | ApiException e) {
                            e.printStackTrace();
                        }

                        //mMap.addMarker(new MarkerOptions().position(origin).title(startLocation));
                        // mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));


                    }
                }
        );

        mMap.setPadding(1,1,1,1);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // mMap.getUiSettings().setZoomGesturesEnabled(true);
        //mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);**/
    }




    /*private void getRoute(com.google.maps.model.LatLng origin,
                          com.google.maps.model.LatLng destination,
                          final GoogleMap map) throws InterruptedException, ApiException, IOException {



        //Using the Google Directions api to calculate directions
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.origin(origin);
        directions.alternatives(false);


        /* DirectionsApiRequest directionsApiRequest = DirectionsApi.newRequest(geoApiContext);
        directionsApiRequest.mode(TravelMode.DRIVING);
        directionsApiRequest.origin(origin);
        directionsApiRequest.destination(destination);
        directionsApiRequest.departureTimeNow();
        final DirectionsResult result = directionsApiRequest.await();
        String timeToDestination = result.routes[0].legs[0].duration.humanReadable;
        String distanceTraveled = result.routes[0].legs[0].distance.humanReadable;
        textView.setText(String.format("Total distance: %skm\nTotal time: %s", distanceTraveled, timeToDestination));
        new Handler(Looper.getMainLooper()).post(() -> {
            //Log.d("tag", "run: results routes: "+result.routes.length);
            for (DirectionsRoute route: result.routes){
                List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding
                        .decode(route.overviewPolyline.getEncodedPath());
                List<LatLng> newDecodedPath = new ArrayList<>();
                for(com.google.maps.model.LatLng latLng: decodedPath){
                    newDecodedPath.add(new LatLng(latLng.lat,latLng.lng));
                }
                Polyline polyline = map.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                polyline.setStartCap(new RoundCap());
                polyline.setEndCap(new RoundCap());
                polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
                polyline.setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
                polyline.setJointType(JointType.ROUND);
            }
        });
        //Retrieving the details of our destination.

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(final DirectionsResult result) {
                Log.e("Route","Routes: "+result.routes[0].toString());
                Log.e("Duration","Duration: "+result.routes[0].legs[0].duration);
                Log.e("Distance","Distance: "+result.routes[0].legs[0].distance);
                Log.e("Waypoints","Waypoints: "+result.geocodedWaypoints[0].toString());

                String timeToDestination = result.routes[0].legs[0].duration.humanReadable;
                String distanceTraveled = result.routes[0].legs[0].distance.humanReadable;
                final TextView textView = findViewById(R.id.textView);


                new Handler(Looper.getMainLooper()).post(() -> {
                    //Log.d("tag", "run: results routes: "+result.routes.length);
                    textView.setText(String.format("Total distance: %s\nTotal time: %s", distanceTraveled, timeToDestination));
                    for (DirectionsRoute route: result.routes){
                        List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding
                                .decode(route.overviewPolyline.getEncodedPath());
                        List<LatLng> newDecodedPath = new ArrayList<>();

                        for(com.google.maps.model.LatLng latLng: decodedPath){
                            newDecodedPath.add(new LatLng(latLng.lat,latLng.lng));
                        }
                        Polyline polyline = map.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                        polyline.setStartCap(new RoundCap());
                        polyline.setEndCap(new RoundCap());
                        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
                        polyline.setColor(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
                        polyline.setJointType(JointType.ROUND);
                    }
                });
            }
            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();

            }
        });

        //.add(origin, destination));

    } */

    private void getDeviceLocation(){
        try{
            if (locationPermissionGranted){
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                //what does this listener below?
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        lastKnownLocation = task.getResult();
                        //setting up maps camera position to current location of the device.
                        if(lastKnownLocation!=null){
                            LatLng  currentLocation =  new LatLng(lastKnownLocation.getLatitude(),
                                    lastKnownLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                            mMap.addMarker(new MarkerOptions().position(currentLocation)
                                    .title("Current Device Location"));
                        } else{
                            Log.d(" Message 1","Current location is null. Using defaults.");
                            Log.e("Message 2","Exception: %s",task.getException());
                            //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom());
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e("Exception: %s",e.getMessage(),e);
        }
    }
}