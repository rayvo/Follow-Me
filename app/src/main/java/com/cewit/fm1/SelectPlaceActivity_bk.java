package com.cewit.fm1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cewit.fm1.models.Place;
import com.cewit.fm1.util.ActivityHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taeyu Im on 18. 8. 25.
 * qvo@cs.stonybrook.edu
 */

public class SelectPlaceActivity_bk extends Activity {
    private static final String TAG = SelectPlaceActivity_bk.class.getName();

    private String cityId;
    private String tourId;
    private String curPlaceId;
    private List<Place> places;
    private ListView lst;

    private int REQUEST_MODE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_place_view);
        setTitle("Select A Place!");

        Intent intent = this.getIntent();

        //Check if this is a refresh
        REQUEST_MODE = intent.getIntExtra(ActivityHelper.REFRESH_MODE, 0);
        cityId = intent.getStringExtra(ActivityHelper.CITY_ID);
        tourId = intent.getStringExtra(ActivityHelper.TOUR_ID);
        curPlaceId = intent.getStringExtra(ActivityHelper.CUR_PLACE_ID);

        places = new ArrayList<Place>();

        //View Preparation
        lst = (ListView)findViewById(R.id.lstFoundPlaces);

        DatabaseReference refPlaces = FirebaseDatabase.getInstance().getReference("places");
        /*refPlaces.orderByChild("cityId").equalTo(cityId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Place place = dataSnapshot.getValue(Place.class);
                places.add(place);
                Log.d(TAG,"Added place:" + place.getName());

                PlaceCustomListView customListView = new PlaceCustomListView(SelectPlaceActivity_bk.this, places);
                lst.setAdapter(customListView);
                lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Place place = places.get(position);
                        Intent intent = new Intent(SelectPlaceActivity_bk.this, ViewTourActivity.class);
                        intent.putExtra(ActivityHelper.REFRESH_MODE, REQUEST_MODE);
                        intent.putExtra(ActivityHelper.NEW_PLACE_ID, place.getId());
                        intent.putExtra(ActivityHelper.TOUR_ID, tourId);
                        intent.putExtra(ActivityHelper.CUR_PLACE_ID, curPlaceId);
                        startActivity(intent);
                        finish();
                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/
    }
}
