package com.cewit.fm1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cewit.fm1.models.Accommodation;
import com.cewit.fm1.models.Coordinate;
import com.cewit.fm1.models.Restaurant;
import com.cewit.fm1.util.ActivityHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.LocationSettingsRequest;
//import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantListActivity extends AppCompatActivity {
    List<Restaurant> restaurantList;
    List<Restaurant> filterList;
    List<Restaurant> starSet;
    ListView list;
    RestaurantCustomListView RestaurantListView;
    Activity context;
    Coordinate cords;

    Restaurant forDB;
    int REQUEST_MODE;
    String cityId;
    String tourId;
    String curPlaceId;
//    private FusedLocationProviderClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //initialize the activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_list);
        restaurantList = new ArrayList<>();
        filterList = new ArrayList<>();
        starSet = new ArrayList<>();
        list = findViewById(R.id.lvRestaurantList);
        context = this;
        cords = new Coordinate(0.0, 0.0);

        Intent intent = this.getIntent();
        //Check if this is a refresh
        REQUEST_MODE = intent.getIntExtra(ActivityHelper.REFRESH_MODE, 0);
        cityId = intent.getStringExtra(ActivityHelper.CITY_ID);
        tourId = intent.getStringExtra(ActivityHelper.TOUR_ID);
        curPlaceId = intent.getStringExtra(ActivityHelper.CUR_PLACE_ID);

        //run the custom list view
        RestaurantListView = new RestaurantCustomListView(this, restaurantList, starSet, cords, REQUEST_MODE, tourId, curPlaceId);
        list.setAdapter(RestaurantListView);

        //------------------- INIT CUISINE SPINNER ------------------- //
        final Spinner spinner = findViewById(R.id.spnCuisine);

        // Initializing a String Array
        String[] cuisine = new String[]{
                "Cuisine...",
                "All", "Asian Restaurant", "Chinese Restaurant", "Indian Restaurant", "Japanese Restaurant", "Korean Restaurant", "Thai Restaurant", "Vietnamese Restaurant", "Western Restaurant"
        };
        final List<String> cuisineList = new ArrayList<>(Arrays.asList(cuisine));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cuisineList){
            @Override
            public boolean isEnabled(int position){
                if(position == 0)  {
                    return false;
                }
                else{
                    return true;
                }
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = (String) parent.getItemAtPosition(position);
                // If user change the default selection
                // First item is disable and it is used for hint
                if(position > 0){
                    // Notify the selected item text
                    Toast.makeText(getApplicationContext(), "Selected : " + selectedItemText, Toast.LENGTH_SHORT).show();
                    if(!selectedItemText.toUpperCase().equals("ALL")) {
                        filterList.clear();
                        for (int i = 0; i < restaurantList.size(); i++) {
                            if (restaurantList.get(i).getResType().toUpperCase().equals(selectedItemText.toUpperCase())) {
                                filterList.add(restaurantList.get(i));
                            }
                        }
                        RestaurantListView =new RestaurantCustomListView(context, filterList, starSet, cords, REQUEST_MODE, tourId, curPlaceId);
                        list.setAdapter(RestaurantListView);
                    }
                    else{
                        RestaurantListView = new RestaurantCustomListView(context, restaurantList, starSet, cords, REQUEST_MODE, tourId, curPlaceId);
                        list.setAdapter(RestaurantListView);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //------------------- INIT FAVORITE BUTTON ------------------- //
        final ToggleButton favButton = (ToggleButton) findViewById(R.id.btnFav);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favButton.isChecked()){
                    if (spinner.getSelectedItemPosition() < 2) {
                        filterList.clear();
                        for (int i = 0; i < restaurantList.size(); i++) {
                            if (starSet.contains(restaurantList.get(i))) {
                                filterList.add(restaurantList.get(i));
                            }
                        }
                        RestaurantListView = new RestaurantCustomListView(context, filterList, starSet, cords, REQUEST_MODE, tourId, curPlaceId);
                        list.setAdapter(RestaurantListView);

                    } else {
                        List<Restaurant> temp = new ArrayList<>();
                        for (int i = 0; i < filterList.size(); i++) {
                            if (starSet.contains(filterList.get(i))) {
                                Restaurant a = filterList.get(i);
                                a.setFavorite(filterList.get(i).isFavorite());
                                temp.add(a);
                            }
                        }
                        RestaurantListView = new RestaurantCustomListView(context, temp, starSet, cords, REQUEST_MODE, tourId, curPlaceId);
                        list.setAdapter(RestaurantListView);
                    }
                }
                else{
                    if(spinner.getSelectedItemPosition()<2){
                        RestaurantListView = new RestaurantCustomListView(context, restaurantList, starSet, cords, REQUEST_MODE, tourId, curPlaceId);
                        list.setAdapter(RestaurantListView);
                    }
                    else{
                        RestaurantListView = new RestaurantCustomListView(context, filterList, starSet, cords, REQUEST_MODE, tourId, curPlaceId);
                        list.setAdapter(RestaurantListView);
                        //Hi

                        //why is this not commiting?
                        System.out.println("");
                    }

                }
            }
        });

//        //------------------- INIT GPS SWITCH ------------------- //
//        final Switch gps = findViewById(R.id.sGPS);
//        gps.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(gps.isChecked()){
//                    requestPermission();
//
//                    client = LocationServices.getFusedLocationProviderClient(context);
//
//                    if (ActivityCompat.checkSelfPermission(RestaurantListActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
//
//                        return;
//                    }
//                    if(isLocationServiceEnabled()){
//                        client.getLastLocation().addOnSuccessListener(RestaurantListActivity.this, new OnSuccessListener<Location>() {
//                            @Override
//                            public void onSuccess(Location location) {
//
//                                cords.setLatitude(location.getLatitude());
//                                cords.setLongitude(location.getLongitude());
//                                RestaurantListView = new RestaurantCustomListView(context, restaurantList, starSet, cords);
//                                list.setAdapter(RestaurantListView);
//
//                            }
//                        });
//                    }
//                    else{
//                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        context.startActivity(myIntent);
//                    }
//                }
//                else{
//                    cords.setLatitude(0.0);
//                    cords.setLongitude(0.0);
//                    RestaurantListView = new RestaurantCustomListView(context, restaurantList, starSet, cords);
//                    list.setAdapter(RestaurantListView);
//                }
//            }
//        });

        readResData();
    }

    private void readResData() {
        //Data Preparation
        DatabaseReference refPlaces = FirebaseDatabase.getInstance().getReference("places");
        refPlaces.orderByChild("type").equalTo("Restaurant").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot accomSnapshot : dataSnapshot.getChildren()) {
                    forDB = accomSnapshot.getValue(Restaurant.class);
                    if(forDB !=null){
                        restaurantList.add(forDB);
                    }
                }
                RestaurantCustomListView customListView = new RestaurantCustomListView(context, restaurantList, starSet, cords, REQUEST_MODE, tourId, curPlaceId);
                list.setAdapter(customListView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

    }

    public int getREQUEST_MODE() {
        return REQUEST_MODE;
    }

    public String getCityId() {
        return cityId;
    }

    public String getTourId() {
        return tourId;
    }

    public String getCurPlaceId() {
        return curPlaceId;
    }

//    private void requestPermission() {
//        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
//    }
//
//    public boolean isLocationServiceEnabled(){
//        LocationManager locationManager = null;
//        boolean gps_enabled= false,network_enabled = false;
//
//        if(locationManager ==null)
//            locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
//        try{
//            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        }catch(Exception ex){
//            //do nothing...
//        }
//
//        try{
//            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        }catch(Exception ex){
//            //do nothing...
//        }
//
//        return gps_enabled || network_enabled;
//
//    }


}
