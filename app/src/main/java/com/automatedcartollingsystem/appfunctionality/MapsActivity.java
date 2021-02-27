package com.automatedcartollingsystem.appfunctionality;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.automatedcartollingsystem.registration.ProfileActivity;
import com.example.automatedcartollingsystem.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.FindPlaceFromTextRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.FindPlaceFromText;
import com.google.maps.model.PlaceDetails;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.TrafficModel;
import com.google.maps.model.TravelMode;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.automatedcartollingsystem.models.Constants.*;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private CameraPosition cameraPosition;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GeoApiContext geoApiContext;
    private PlacesClient placesClient; //This accesses the GDB.
    private List<DirectionsApiRequest.Waypoint> waypointList;
    private List<LatLng> ltc;

    private ArrayList<String> routeRoads;
    private String concat_roads = "";
    private com.google.maps.model.LatLng origin;
    private com.google.maps.model.LatLng destination;


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


        geoApiContext = new GeoApiContext.Builder().
                apiKey(getString(R.string.google_maps_key)).build();

        //Pumulani
        //Carousel
        //Nyl Plaza
        //Kranskop Plaza
        DirectionsApiRequest.Waypoint[] waypoints = new DirectionsApiRequest.Waypoint[]{

                new DirectionsApiRequest.Waypoint(new com.google.maps.model.
                        LatLng(-25.639444, 28.275556), true),//Pumulani
                new DirectionsApiRequest.Waypoint(new com.google.maps.model.
                        LatLng(-25.322778, 28.297778), true), //Carousel
                new DirectionsApiRequest.Waypoint(new com.google.maps.model.
                        LatLng(-24.289722, 28.978889), true), //Nyl Plaza
                new DirectionsApiRequest.Waypoint(new com.google.maps.model.
                        LatLng(-24.781667, 28.471389), true) //Kranskop Plaza
        };


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

        getDeviceLocation();
        try {
            setRoute();
        } catch (InterruptedException | ApiException | IOException e) {
            e.printStackTrace();
        }


        mMap.setPadding(1,1,1,1);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // mMap.getUiSettings().setZoomGesturesEnabled(true);
        //mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
    }

    /* Found a new way to determine toll gate on a particular route.
     * but this means that I have to update my NationalRoads database to include lat and lbg pts.
     * I will make use of a php script to access the NationalRoads database to find the long lat
     *  coordinates.
     * To verify whether the tollgates belong on the route we verify or check whether their latlng
     * pts are within the  latlng bounds of origin and destination in that particular road.
     * ( It would be simpler to identify an outlier)
     */


    private com.google.maps.model.LatLng[] getOriDestCoordinates(String origin, String destination)
            throws IOException {

        //String a = getIntent().getStringExtra(Constants.START_DESTINATION);
        //String b = getIntent().getStringExtra(Constants.END_DESTINATION);
        com.google.maps.model.LatLng originCoord=null;
        com.google.maps.model.LatLng destiCoord= null;


        List<Address>  originList = new Geocoder(this,Locale.getDefault())
                .getFromLocationName(origin,6);

        List<Address> destinationList = new Geocoder(this,Locale.getDefault())
                .getFromLocationName(destination,6);

        Address originAddress = originList.get(0);
        Address destinationAddress = destinationList.get(0);

        if(originAddress.hasLongitude() && originAddress.hasLatitude())
        {
            if(destinationAddress.hasLatitude() && destinationAddress.hasLongitude())
            {
                 originCoord = new com.google.maps.model
                        .LatLng(originAddress.getLatitude(), originAddress.getLongitude()
                 );
                 destiCoord = new com.google.maps.model
                        .LatLng(destinationAddress.getLatitude(),destinationAddress.getLongitude()
                 );
            }
        }
        return new com.google.maps.model.LatLng[]{originCoord,destiCoord};
    }

    
    private void setRoute()
            throws InterruptedException, ApiException, IOException {

        /*
         * 1. get the names of location origin and destination ==Done
         * 2. Use geocoder to convert the location o and d into a latlng co-ordinates == Done
         * 3. plug them into DirectionsResult and obtain the routes/roads == Done
         * 4. pass them through the SearchTollTask class to get Toll co-ordinates that are in
         * between the route == Done.
         * Errors: Reverse directions don't work and Mahikeng to Polokwane.
         * Solutions : Unless we could simply use previous history to do the reverse directions
         *
         * Home : new com.google.maps.model.LatLng(-26.35669,28.20668)
         * Destination : new com.google.maps.model.LatLng(-23.91020,29.45364)
         */
        DirectionsResult directionsResult = DirectionsApi.newRequest(geoApiContext).
                mode(TravelMode.DRIVING).
                origin("Polokwane").
                destination("Durban").
                departureTimeNow().
                trafficModel(TrafficModel.OPTIMISTIC).
                alternatives(false).
                await();



        com.google.maps.model.LatLng[] oriDestCoordinates
                =  getOriDestCoordinates("Polokwane","Durban");

        DirectionsStep[] directionsStep = directionsResult.routes[0].legs[0].steps;
        
        FindPlaceFromText findPlaceFromText = PlacesApi.findPlaceFromText(geoApiContext,
                "Beit Bridge Border Post", FindPlaceFromTextRequest.InputType.TEXT_QUERY).await();



        origin = oriDestCoordinates[0];
        destination = oriDestCoordinates[1];

        TextView distanceText = findViewById(R.id.distance_detail);
        distanceText.setText(String.format("Distance: %s", directionsResult.routes[0].legs[0].distance));

        TextView timeText = findViewById(R.id.time_detail);
        timeText.setText(String.format("Duration: %s",directionsResult.routes[0].legs[0].duration));

        Log.e("Direction Result 1", Arrays.toString(directionsResult.routes));
        Log.e("Orgin_A", origin.toString());
        Log.e("Origin_B", destination.toString());

        String[] filteredSteps = filterDirectionSteps(directionsStep);
        filterRouteRoads(filteredSteps);
        setPolyline();


        //Log.e("Leg 1", String.valueOf(directionsResult.routes[0].legs[1]));
        //Log.e("Leg 2", String.valueOf(directionsResult.routes[0].legs[2]));
        //Log.e("Leg 3", String.valueOf(directionsResult.routes[0].legs[3]));
        //Log.e("Leg 4", String.valueOf(directionsResult.routes[0].legs[4]));

    }

    /**
     * @param directionsSteps Array of unfiltered steps obtained from Google DirectionSteps.
     * @return An array of filtered steps .
     */

    private String[] filterDirectionSteps(DirectionsStep[] directionsSteps){

        String[] filteredSteps = new String[directionsSteps.length];
        for(int i=0;i<directionsSteps.length;++i) {
            filteredSteps[i] = directionsSteps[i].htmlInstructions.trim()
                    .replace("<b>","")
                    .replace("</b>","")
                    .replace("/<wbr/>"," ")
                    .replace("</div>"," ")
                    .replace(">"," ")
                    .replace("<div","");

        }
        return filteredSteps;
    }

    /**
     * Filters an array consisting of string to extract road routes.
     * @param filteredSteps a filtered string array consisting of directions
     *                     and national routes/roads.
     */

    private void filterRouteRoads(String[] filteredSteps){
        routeRoads = new ArrayList<>();
        String concat = "";
        String[] stepsArray;

        for (String filteredStep : filteredSteps) {
            concat = concat.concat(" " + filteredStep);
        }
        stepsArray = concat.trim().split(" ");

        for (String s : stepsArray) {
            if((!s.equals(" ")) && !s.equals("")){
                if (s.substring(0,1).matches("[A-Z]") &&
                        s.substring(s.length() - 1).matches("[0-9]")) {
                    routeRoads.add(s);
                }
            }
        }
        routeRoads = removeDuplicates(routeRoads);
        if(routeRoads.size()==3){
            routeRoads.remove(2);
        }
        concat_roads = concatRoads(routeRoads);
    }

    /**
     * Concatenates an arraylist of route roads
     * @param routeRoads an ArrayList consisting of routes/roads
     * @return an untrimmed string of routes/roads
     */
    private String concatRoads(ArrayList<String> routeRoads){

        for (String s : TOLL_ROADS_ARRAY) {
            if (routeRoads.contains(s)) {
                concat_roads = concat_roads.concat(" "+s);
            }
        }
        return concat_roads;
    }

    /**
     * Removes routes/roads duplications from an ArrayList consisting of routes.
     * @param routes an ArrayList of containing a string of routes/roads.
     * @return an ArrayList of non-duplicated routes/roads.
     */
    private ArrayList<String> removeDuplicates(ArrayList<String> routes){
        ArrayList<String> r = new ArrayList<>();

        for (String s : TOLL_ROADS_ARRAY) {
            if (routes.contains(s)) {
                r.add(s);
            }
        }
        return  r;
    }

    /**
     * Stores points gathered from a JSONArray into
     * an ArrayList consisting of DirectionsApiRequest.Waypoint object.
     * @param jsonArray response array obtained from the database.
     * @return A list of DirectionsApiRequest.Waypoints objects consisting LatLng objects.
     * @throws JSONException when it cannot access the database.
     */

    private List<LatLng>  getWaypoints(JSONArray jsonArray)
            throws JSONException {
        ltc = new ArrayList<>();
        waypointList = new ArrayList<>();
        for(int i=0;i<jsonArray.length();++i){
            /*waypointList.add(
                    new DirectionsApiRequest.Waypoint(
                            new com.google.maps.model.LatLng(
                                    Double.parseDouble((String) jsonArray.getJSONArray(i).get(0)),
                                    Double.parseDouble((String) jsonArray.getJSONArray(i).get(1))
                            )
                    ,true)
            );*/
            ltc.add(new LatLng(Double.parseDouble((String) jsonArray.getJSONArray(i).get(0)),
                    Double.parseDouble((String) jsonArray.getJSONArray(i).get(1))));
        }
        return ltc;
    }

    /**
     * I Can change this method to
     */
    private void setPolyline(){

        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_TOLLROADS_STRING,
                response -> {

                    Log.e("Maps JSON RESPONSE",response);

                    if(response!=null){
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            ltc = getWaypoints(jsonArray);
                            Log.e("Waypoints",waypointList.toString());

                            for(int i=0;i<ltc.size();++i){
                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(ltc.get(i))
                                        .title("Toll "+i)

                                );
                            }

                            DirectionsApiRequest directionsApiRequest =
                                    new DirectionsApiRequest(geoApiContext);

                            directionsApiRequest.origin("Polokwane").destination("Durban").
                                    setCallback(new PendingResult.Callback<DirectionsResult>() {
                                @Override
                                public void onResult(DirectionsResult result) {
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        TextView tollNumberText =
                                                findViewById(R.id.toll_number_detail);
                                        tollNumberText.setText(
                                                String.format("No. of Tolls: %s",ltc.size()));

                                        for(DirectionsRoute route: result.routes){
                                            List<com.google.maps.model.LatLng> decodedPath =
                                                    PolylineEncoding.decode(
                                                            route.overviewPolyline.getEncodedPath()
                                                    );
                                            List<LatLng> newDecodedPath = new ArrayList<>();
                                            for(com.google.maps.model.LatLng latLng: decodedPath){
                                                newDecodedPath.add(new LatLng(latLng.lat,latLng.lng)
                                                );
                                            }
                                            Polyline polyline = mMap
                                                    .addPolyline(new PolylineOptions()
                                                            .addAll(newDecodedPath)
                                                    );
                                            polyline.setStartCap(new RoundCap());
                                            polyline.setEndCap(new RoundCap());
                                            polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
                                            polyline.setGeodesic(false);
                                            polyline.setColor(ContextCompat.
                                                    getColor(getApplicationContext(),
                                                    R.color.quantum_googblueA200));
                                            polyline.setJointType(JointType.ROUND);

                                        }
                                    });
                                }

                                @Override
                                public void onFailure(Throwable e) {
                                    e.printStackTrace();
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, Throwable::printStackTrace)
        {
            @Override
            protected Map<String, String> getParams() {

                Log.e("Concat Roads A",concat_roads.trim());
                Map<String,String> mPar = new HashMap<>();
                mPar.put("road_name",concat_roads.trim());
                mPar.put("latitude_o",String.valueOf(origin.lat));
                mPar.put("latitude_d",String.valueOf(destination.lat));
                mPar.put("longitude_o",String.valueOf(origin.lng));
                mPar.put("longitude_d",String.valueOf(destination.lng));


                return mPar;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void getDeviceLocation(){
        try{
            if (locationPermissionGranted){
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                //what does this listener below?
                locationResult.addOnCompleteListener(this, task -> {
                    lastKnownLocation = task.getResult();
                    //setting up maps camera position to current location of the device.
                    if(lastKnownLocation!=null){
                        Log.e("Last Known Location", String.valueOf(lastKnownLocation));
                        LatLng  currentLocation =  new LatLng(lastKnownLocation.getLatitude(),
                                lastKnownLocation.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, (float) 5.0));
                        mMap.addMarker(new MarkerOptions().position(currentLocation)
                                .title("Current Device Location")).setVisible(true);
                    } else {
                        Log.d(" Message 1","Current location is null. Using defaults.");
                        Log.e("Message 2","Exception: %s",task.getException());
                        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom());
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        }catch (SecurityException e){
            Log.e("Exception: %s",e.getMessage(),e);
        }
    }
}