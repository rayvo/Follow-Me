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
import java.util.List;

/*
 * Haseung has successfully got the code to work on his computer.
 */

public class PlaceSelectionActivity extends AppCompatActivity {

    private String TAG = PlaceSelectionActivity.class.getSimpleName();
    private String cityId;
    private String tourId;
    private String curPlaceId;
    private List<Place> places;
    private ListView lst;

    private int REQUEST_MODE;


    public List<Place> placeStarredList;
    ListView list;
    Button btnViewStarredOrAll;
    Spinner spnGu;
    Spinner spnType;
    Switch sGPS;
    String strStartTime;

    private Place place;


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

        places = new ArrayList<Place>();


        placeStarredList = new ArrayList<>();

        spnGu = findViewById(R.id.spnGu);
        spnType = findViewById(R.id.spnType);
        list = findViewById(R.id.lvPlaceList);
        btnViewStarredOrAll = findViewById(R.id.btnViewStarredOrAll);
        sGPS = findViewById(R.id.sGPS);


        // Set list adapter
        PlaceCustomListView customListView = new PlaceCustomListView(this, places, placeStarredList, sGPS.isChecked(), REQUEST_MODE, tourId, curPlaceId, strStartTime);
        list.setAdapter(customListView);

        // Set spnGu adapter
        // TODO Properly set spnGu data elsewhere instead of manually inputting it
        String[] temp = new String[]{
                "View All", "Accommodation", "Restaurant", "Park", "Beach", "Others"
        };
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, temp);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGu.setAdapter(a);

        // Set spnType adapter
        // TODO Properly set spnType data elsewhere instead of manually inputting it
        temp = new String[]{
                "View All", "Hotel", "Guest House", "Motel"
        };
        ArrayAdapter<String> a1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, temp);
        a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(a1);

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
                if( btnViewStarredOrAll.getText().equals("View Starred Items") ){
                    filterCurrentList(false, selectedItem, spnType.getSelectedItem().toString(), sGPS.isChecked());
                } else {
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

        readAccomData();
        //filterCurrentList(false, spnGu.getSelectedItem().toString(), spnType.getSelectedItem().toString(), sGPS.isChecked());

    }


    public void filterCurrentList(boolean fav, String s, String t, boolean isChecked ){

        PlaceCustomListView customListView = null;
        List<Place> temp = new ArrayList<>();

        if( !fav ){
            if( s.equals("View All") && t.equals("View All") ) {
                customListView = new PlaceCustomListView(this, places, placeStarredList, isChecked, REQUEST_MODE, tourId, curPlaceId, strStartTime );
            } else if( s.equals("View All") ) {
                for (int i = 0; i < places.size(); i++) {
                    if (places.get(i).getType().equals(t)) {
                        temp.add(places.get(i));
                    }
                }
                customListView = new PlaceCustomListView(this, temp, placeStarredList, isChecked, REQUEST_MODE, tourId, curPlaceId, strStartTime);
            } else if( t.equals("View All") ){
                for( int i = 0; i < places.size(); i++ ){
                    if( places.get(i).getAddress().contains(s) ){
                        temp.add(places.get(i));
                    }
                }
                customListView = new PlaceCustomListView(this, temp, placeStarredList, isChecked, REQUEST_MODE, tourId, curPlaceId , strStartTime);
            } else {
                for( int i = 0; i < places.size(); i++ ){
                    if( places.get(i).getAddress().contains(s) && places.get(i).getType().equals(t) ){
                        temp.add(places.get(i));
                    }
                }
                customListView = new PlaceCustomListView(this, temp, placeStarredList, isChecked , REQUEST_MODE, tourId, curPlaceId, strStartTime);
            }
        } else {
            if( s.equals("View All") && t.equals("View All") ) {
                temp.addAll(placeStarredList);
                customListView = new PlaceCustomListView(this, temp, placeStarredList, isChecked, REQUEST_MODE, tourId, curPlaceId , strStartTime);
            } else if( s.equals("View All") ) {
                for (int i = 0; i < placeStarredList.size(); i++) {
                    if (placeStarredList.get(i).getType().equals(t)) {
                        temp.add(placeStarredList.get(i));
                    }
                }
                customListView = new PlaceCustomListView(this, temp, placeStarredList, isChecked, REQUEST_MODE, tourId, curPlaceId , strStartTime);
            } else if( t.equals("View All") ){
                for( int i = 0; i < placeStarredList.size(); i++ ){
                    if( placeStarredList.get(i).getAddress().contains(s) ){
                        temp.add(placeStarredList.get(i));
                    }
                }
                customListView = new PlaceCustomListView(this, temp, placeStarredList, isChecked , REQUEST_MODE, tourId, curPlaceId, strStartTime);
            } else {
                for( int i = 0; i < placeStarredList.size(); i++ ){
                    if( placeStarredList.get(i).getAddress().contains(s) && placeStarredList.get(i).getType().equals(t) ){
                        temp.add(placeStarredList.get(i));
                    }
                }
                customListView = new PlaceCustomListView(this, temp, placeStarredList, isChecked, REQUEST_MODE, tourId, curPlaceId, strStartTime );
            }
        }

        list.setAdapter(customListView);

    }

    private void readAccomData() {

        //Data Preparation
        DatabaseReference refPlaces = FirebaseDatabase.getInstance().getReference("places");
        refPlaces.orderByChild("cityId").equalTo(cityId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot accomSnapshot : dataSnapshot.getChildren()) {
                    Place place = accomSnapshot.getValue(Place.class);
                    if(place !=null){
                        places.add(place);
                    }
                }
                PlaceCustomListView customListView = new PlaceCustomListView(PlaceSelectionActivity.this, places, placeStarredList, sGPS.isChecked(), REQUEST_MODE, tourId, curPlaceId, strStartTime);
                list.setAdapter(customListView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}






