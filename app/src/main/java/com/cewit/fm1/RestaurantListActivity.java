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

import com.cewit.fm1.models.Coordinate;
import com.cewit.fm1.models.Restaurant;
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
        //read in data
        readData();

        //run the custom list view
        RestaurantListView = new RestaurantCustomListView(this, restaurantList, starSet, cords);
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
                        RestaurantListView = new RestaurantCustomListView(context, filterList,starSet,cords);
                        list.setAdapter(RestaurantListView);
                    }
                    else{
                        RestaurantListView = new RestaurantCustomListView(context, restaurantList,starSet, cords);
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
                        RestaurantListView = new RestaurantCustomListView(context, filterList, starSet, cords);
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
                        RestaurantListView = new RestaurantCustomListView(context, temp, starSet, cords);
                        list.setAdapter(RestaurantListView);
                    }
                }
                else{
                    if(spinner.getSelectedItemPosition()<2){
                        RestaurantListView = new RestaurantCustomListView(context, restaurantList, starSet, cords);
                        list.setAdapter(RestaurantListView);
                    }
                    else{
                        RestaurantListView = new RestaurantCustomListView(context, filterList, starSet, cords);
                        list.setAdapter(RestaurantListView);
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

    private void readData(){

        InputStream inputStream = getResources().openRawResource(R.raw.resdata);
        int i = 001;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine = reader.readLine();
            csvLine = "";
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");

                String id = row[0];
                String name = row[1];
                String address = row[2];
                String cityId = row[3];
                String contact = row[4];
                String site = row[5];
                String info = row[6];
                String email = row[7];
                long lat = Long.parseLong(row[8]);
                long lng = Long.parseLong(row[9]);
//                long lat = row[8];
//                long ?lng = row[9];
                int rate = Integer.parseInt(row[10]);
                String openTime = row[11];
                String closeTime = row[12];
                int entranceFee = Integer.parseInt(row[13]);
                List<Integer> imageIds = new ArrayList<>();
                imageIds.add(Integer.parseInt(row[14]));
                String type = row[15];
                boolean isFavorite = Boolean.parseBoolean(row[16]);
                String resType = row[17];

                Restaurant tempRestaurant = new Restaurant(id, name, address, cityId, contact, site, info, email, lat, lng, rate, openTime, closeTime, entranceFee, imageIds, type, isFavorite, resType);
                restaurantList.add(tempRestaurant);
                i++;
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        } finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
    }

}
