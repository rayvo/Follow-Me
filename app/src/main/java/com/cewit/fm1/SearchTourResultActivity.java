package com.cewit.fm1;

/**
 * Created by Taeyu Im on 18. 5. 3.
 * qvo@cs.stonybrook.edu
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cewit.fm1.models.City;
import com.cewit.fm1.models.Country;
import com.cewit.fm1.models.Tour;
import com.cewit.fm1.models.Transport;
import com.cewit.fm1.models.Travel;
import com.cewit.fm1.util.ActivityHelper;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchTourResultActivity extends AppCompatActivity {
    private static final String TAG = SearchTourResultActivity.class.getName();



    //LIST VIEW
    ListView lst;
    //=================================================================


    private DatabaseReference refTours;

    // Database Helper

    List<Tour> tours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_tour_result);
        setTitle("Select A Tour!");
        // Inquire Data

        Intent intent = this.getIntent();
        String cityId = intent.getStringExtra(ActivityHelper.CITY_ID);

        //View Preparation
        lst = (ListView)findViewById(R.id.lstFoundTours);

        // Data Preparation
        tours = new ArrayList<Tour>();

        refTours = FirebaseDatabase.getInstance().getReference("tours");
        refTours.orderByChild("cityId").equalTo(cityId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Tour tour = dataSnapshot.getValue(Tour.class);
                tours.add(tour);
                Log.d(TAG,"Added tour:" + tour.getName());

                SearchTourResultCustomListView customListView = new SearchTourResultCustomListView(SearchTourResultActivity.this, tours);
                lst.setAdapter(customListView);
                lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Tour tour = tours.get(position);
                        //Toast.makeText(SearchTourResultActivity.this,"Tour:" + tour.getName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SearchTourResultActivity.this, ViewTourActivity.class);
                        intent.putExtra(ActivityHelper.TOUR_ID, tour.getId());
                        Log.d(TAG,"DUYVO-101: Seleted TourID:" + tour.getId() + "(" + tour.getName() + ")");
                        startActivity(intent);
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
        /*refTours.orderByChild("totalTime").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                tours.clear();
                Tour tour = dataSnapshot.getValue(Tour.class);
                tours.add(tour);


                //Update
                SearchTourResultCustomListView customListView = new SearchTourResultCustomListView(SearchTourResultActivity.this, tours);
                lst.setAdapter(customListView);
                lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Tour tour = tours.get(position);
                        //Toast.makeText(SearchTourResultActivity.this,"Tour:" + tour.getName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SearchTourResultActivity.this, ViewTourActivity.class);
                        intent.putExtra(ActivityHelper.TOUR_ID, tour.getId());
                        startActivity(intent);
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
