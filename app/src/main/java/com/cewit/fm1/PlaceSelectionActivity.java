package com.cewit.fm1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import com.cewit.fm1.models.Accommodation;
import com.cewit.fm1.models.Place;
import com.cewit.fm1.util.ActivityHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Haseung has successfully got the code to work on his computer.
 */

public class PlaceSelectionActivity extends AppCompatActivity {

    private String TAG = PlaceSelectionActivity.class.getSimpleName();
    public List<Place> places;
    ListView list;
    PlaceCustomListView customListView;
    Button btnViewStarredOrAll;
    Spinner spnGu;
    Spinner spnType;
    Switch sGPS;

    Place forDB;

    int REQUEST_MODE;
    String cityId;
    String tourId;
    String curPlaceId;
    String strStartTime;

    ArrayAdapter<String> a1;
    String [] resType;
    String [] accomType;
    String [] tourType;
    String [] placeType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_list);

        Intent intent = this.getIntent();
        //Check if this is a refresh
        REQUEST_MODE = intent.getIntExtra(ActivityHelper.REFRESH_MODE, 0);
        cityId = intent.getStringExtra(ActivityHelper.CITY_ID);
        tourId = intent.getStringExtra(ActivityHelper.TOUR_ID);
        curPlaceId = intent.getStringExtra(ActivityHelper.CUR_PLACE_ID);
        strStartTime = intent.getStringExtra(ActivityHelper.START_TIME);

        places = new ArrayList<>();
        //hotelStarredList = new ArrayList<>();

        spnGu = findViewById(R.id.spnGu);
        spnType = findViewById(R.id.spnType);
        list = findViewById(R.id.lvPlaceList);
        btnViewStarredOrAll = findViewById(R.id.btnViewStarredOrAll);
        sGPS = findViewById(R.id.sGPS);


        // Set list adapter
//        customListView = new PlaceCustomListView(this, places,  sGPS.isChecked(), REQUEST_MODE, tourId, curPlaceId, strStartTime);
//        list.setAdapter(customListView);

        // Set spnGu adapter
        // TODO Properly set spnGu data elsewhere instead of manually inputting it
        String[] temp = new String[]{
                "View All", "Accommodation", "Tourism", "Restaurant"
        };
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, temp);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGu.setAdapter(a);

//        if()

        // Set spnType adapter
        // TODO Properly set spnType data elsewhere instead of manually inputting it
        accomType = new String[]{
                "View All", "Guest House", "Motel", "3-star Hotel", "4-star Hotel", "5-star Hotel", "Sauna"
        };
        resType = new String[]{
                "View All", "Chinese Restaurant", "Japanese Restaurant", "Korean Restaurant", "Thai Restaurant", "Vietnamese Restaurant", "Western Restaurant"
        };
        tourType = new String[]{
                "View All", "Airport", "Cave", "Beach", "Park", "Aquarium", "Mountain", "Cliff", "Industrial Tower", "Market", "Museum"
        };
        placeType = new String[]{
                "View All", "Guest House", "Motel", "3-star Hotel", "4-star Hotel", "5-star Hotel", "Sauna",
                "Chinese Restaurant", "Japanese Restaurant", "Korean Restaurant", "Thai Restaurant", "Vietnamese Restaurant", "Western Restaurant",
                "Airport", "Cave", "Beach", "Park", "Aquarium", "Mountain", "Cliff", "Industrial Tower", "Market", "Museum"
        };

        // Set Listeners
        btnViewStarredOrAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnViewStarredOrAll.getText().equals("View Starred Items")) {
                    btnViewStarredOrAll.setText("View All Items");
                    filterCurrentList( true, spnGu.getSelectedItem().toString(), spnType.getSelectedItem().toString(), sGPS.isChecked() );
                } else {
                    btnViewStarredOrAll.setText("View Starred Items");
                    filterCurrentList( false, spnGu.getSelectedItem().toString(), spnType.getSelectedItem().toString(), sGPS.isChecked() );
                }
            }
        });

        spnGu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = spnGu.getSelectedItem().toString();
//                System.out.println("------------------------------------------------------------------------------------------------: " + curPlaceId);
                if(selectedItem.toUpperCase().equals("RESTAURANT")) {
                    setAdapter(2);
                }
                else if(selectedItem.toUpperCase().equals("TOURISM")){
                    setAdapter(1);
                }
                else if(selectedItem.toUpperCase().equals("ACCOMMODATION")) {
                    setAdapter(0);
                }
                else
                    setAdapter(4);

                if( btnViewStarredOrAll.getText().equals("View Starred Items") ){
                    filterCurrentList(false, selectedItem, spnType.getSelectedItem().toString(), sGPS.isChecked());
                }
                else {
                    filterCurrentList(true, selectedItem, spnType.getSelectedItem().toString(), sGPS.isChecked());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = spnType.getSelectedItem().toString();
                if (btnViewStarredOrAll.getText().equals("View Starred Items")) {
                    filterCurrentList(false, spnGu.getSelectedItem().toString(), selectedItem, sGPS.isChecked());
                } else {
                    filterCurrentList(true, spnGu.getSelectedItem().toString(), selectedItem, sGPS.isChecked());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked ) {
                filterCurrentList(false, spnGu.getSelectedItem().toString(), spnType.getSelectedItem().toString(), isChecked);
            }
        });

        readPlaceData();

        //filterCurrentList(false, spnGu.getSelectedItem().toString(), spnType.getSelectedItem().toString(), sGPS.isChecked());
        if(curPlaceId.charAt(0) == 'r'){
            spnGu.setSelection(3);
            setAdapter(2);
            filterCurrentList(false, spnGu.getSelectedItem().toString(), spnType.getSelectedItem().toString(), sGPS.isChecked());

        } else if (curPlaceId.charAt(0)=='h') {
            spnGu.setSelection(1);
            setAdapter(0);
            filterCurrentList(false, spnGu.getSelectedItem().toString(), spnType.getSelectedItem().toString(), sGPS.isChecked());
        }else{
            spnGu.setSelection(2);
            setAdapter(1);
            String selectedItem = spnGu.getSelectedItem().toString();
            System.out.println("----------------------------------------------------------------------------------: " + selectedItem);
            filterCurrentList(false, selectedItem, spnType.getSelectedItem().toString(), sGPS.isChecked());
        }

    }

    public void filterCurrentList(boolean fav, String s, String t, boolean isChecked ){

//        PlaceCustomListView customListView = null;
        List<Place> temp = new ArrayList<>();

        if( !fav ){
            if( s.equals("View All") && t.equals("View All") ) {
                customListView = new PlaceCustomListView(this, places,  isChecked, REQUEST_MODE, tourId, curPlaceId , strStartTime);// hotelStarredList,
            } else if( s.equals("View All") ) {
                for (int i = 0; i < places.size(); i++) {
                    if (places.get(i).getType().equals(t)) {
                        temp.add(places.get(i));
                    }
                }
                customListView = new PlaceCustomListView(this, temp, isChecked, REQUEST_MODE, tourId, curPlaceId, strStartTime); //hotelStarredList,
            } else if( t.equals("View All") ){
                if(s.equals("Restaurant") || s.equals("Accommodation")){
                    for(int i = 0; i < places.size(); i++){
                        if(places.get(i).getType().toUpperCase().equals(s.toUpperCase()) ){
                            temp.add(places.get(i));
                        }
                    }
                }
                else if(s.equals("Tourism")){
                   for(int i = 0; i < places.size(); i++){
                       if(!places.get(i).getType().equals("Restaurant") && !places.get(i).getType().equals("Accommodation")){
                           temp.add(places.get(i));
                       }
                   }
                }
                customListView = new PlaceCustomListView(this, temp, isChecked, REQUEST_MODE, tourId, curPlaceId , strStartTime); //hotelStarredList,
            } else {
                if(s.equals("Restaurant")){
                    for(int i = 0; i < places.size(); i++) {
                        if(places.get(i).getResType().equals(t)) {
                            temp.add(places.get(i));
                        }
                    }
                }
                else if(s.equals("Accommodation")){
                        for(int i = 0; i < places.size(); i++) {
                            if(places.get(i).getAccType().equals(t)) {
                                temp.add(places.get(i));
                            }
                        }
                }
                else if(s.equals("Tourism")){
                    for(int i = 0; i < places.size(); i++) {
                        if(!places.get(i).getType().equals("Restaurant") && !places.get(i).getType().equals("Accommodation") && places.get(i).getType().equals(t)) {
                            temp.add(places.get(i));
                        }
                    }
                }
                customListView = new PlaceCustomListView(this, temp, isChecked, REQUEST_MODE, tourId, curPlaceId , strStartTime);// hotelStarredList,
            }
        } else {
            if( s.equals("View All") && t.equals("View All") ) {
                for(int i = 0; i < places.size(); i++){
                    if( places.get(i).isFavorite() ){
                        temp.add(places.get(i));
                    }
                }
                customListView = new PlaceCustomListView(this, temp, isChecked, REQUEST_MODE, tourId, curPlaceId, strStartTime ); //hotelStarredList,
            } else if( s.equals("View All") ) {
                for (int i = 0; i < places.size(); i++) {
                    if (places.get(i).isFavorite() && places.get(i).getType().equals(t)) {
                        temp.add(places.get(i));
                    }
                }
                customListView = new PlaceCustomListView(this, temp, isChecked, REQUEST_MODE, tourId, curPlaceId, strStartTime ); //hotelStarredList,
            } else if( t.equals("View All") ){
                if(s.equals("Restaurant") || s.equals("Accommodation")){
                    for(int i = 0; i < places.size(); i++){
                        if(places.get(i).getType().toUpperCase().equals(s.toUpperCase()) && places.get(i).isFavorite()){
                            temp.add(places.get(i));
                        }
                    }
                }
                else if(s.equals("Tourism")){
                    for(int i = 0; i < places.size(); i++){
                        if(!places.get(i).getType().equals("Restaurant") && !places.get(i).getType().equals("Accommodation") && places.get(i).isFavorite()){
                            temp.add(places.get(i));
                        }
                    }
                }
                customListView = new PlaceCustomListView(this, temp, isChecked, REQUEST_MODE, tourId, curPlaceId , strStartTime); //hotelStarredList,
            } else {
                if(s.equals("Restaurant")){
                    for(int i = 0; i < places.size(); i++) {
                        if(places.get(i).getResType().equals(t) && places.get(i).isFavorite()) {
                            temp.add(places.get(i));
                        }
                    }
                }
                else if(s.equals("Accommodation")){
                    for(int i = 0; i < places.size(); i++) {
                        if(places.get(i).getAccType().equals(t) && places.get(i).isFavorite()) {
                            temp.add(places.get(i));
                        }
                    }
                }
                else if(s.equals("Tourism")){
                    for(int i = 0; i < places.size(); i++) {
                        if(!places.get(i).getType().equals("Restaurant") && !places.get(i).getType().equals("Accommodation") && places.get(i).getType().equals(t) && places.get(i).isFavorite()) {
                            temp.add(places.get(i));
                        }
                    }
                }
                customListView = new PlaceCustomListView(this, temp, isChecked, REQUEST_MODE, tourId, curPlaceId , strStartTime);// hotelStarredList,
            }
        }

//        System.out.println(customListView.get)
        list.setAdapter(customListView);
    }

    private void setAdapter(int a){
        if(a==0){ //Accommodation
            a1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accomType);
            a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        else if(a == 1){ //Tourism
            a1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tourType);
            a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        else if(a==2){  //Restaurants
            a1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, resType);
            a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        else{
            a1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, placeType);
            a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        spnType.setAdapter(a1);
    }
    private void readPlaceData() {
        //Data Preparation
        DatabaseReference refPlaces = FirebaseDatabase.getInstance().getReference("places");
        refPlaces.orderByChild("cityId").equalTo(cityId).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot accomSnapshot : dataSnapshot.getChildren()) {
                    forDB = accomSnapshot.getValue(Place.class);
                    if(forDB !=null){
                        places.add(forDB);
                    }
                }
                PlaceCustomListView customListView = new PlaceCustomListView(PlaceSelectionActivity.this, places, sGPS.isChecked(), REQUEST_MODE, tourId, curPlaceId, strStartTime );
                list.setAdapter(customListView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
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

}






