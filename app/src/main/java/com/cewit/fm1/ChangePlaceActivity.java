package com.cewit.fm1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cewit.fm1.models.Place;
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

/**
 * Created by Taeyu Im on 18. 8. 3.
 * qvo@cs.stonybrook.edu
 */

public class ChangePlaceActivity extends AppCompatActivity {

    private static final String TAG = ChangePlaceActivity.class.getName();

    ListView lst;
    List<Place> places;
    private DatabaseReference refTours;
    String tourId;
    String oldPlaceId;
    String newPlaceId;
    HashMap<String, List<String>> days;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_place_view);
        setTitle("Select A Place!");

        Intent intent = this.getIntent();
        tourId = intent.getStringExtra(ActivityHelper.TOUR_ID);
        oldPlaceId = intent.getStringExtra(ActivityHelper.OLD_PLACE_ID);

        //View Preparation
        places = new ArrayList<Place>();
        lst = (ListView) findViewById(R.id.lstFoundPlaces);
        refTours = FirebaseDatabase.getInstance().getReference("places");
        refTours.orderByChild("id").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Place place = dataSnapshot.getValue(Place.class);
                places.add(place);
                Log.d(TAG, "Added place:" + place.getName());

                ChangePlaceCustomListView customListView = new ChangePlaceCustomListView(ChangePlaceActivity.this, places);
                lst.setAdapter(customListView);
                lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Place place = places.get(position);
                        //Toast.makeText(SearchTourResultActivity.this,"Tour:" + tour.getName(), Toast.LENGTH_SHORT).show();
                        newPlaceId = place.getId();

                        DatabaseReference refTours = FirebaseDatabase.getInstance().getReference("tours");
                        refTours.orderByChild("id").equalTo(tourId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot tourSnapshot : dataSnapshot.getChildren()) {
                                    Tour tour = tourSnapshot.getValue(Tour.class);
                                    days = tour.getDays();
                                    List<List<String>> listOfPlacesPerDay = new ArrayList<List<String>>(days.values());
                                    HashMap<String, List<String>> newDays = new HashMap<String, List<String>>();
                                    String strDay = "Day ";
                                    for (int i = 0; i < listOfPlacesPerDay.size(); i++) {
                                        List<String> placeIdsPerDay = listOfPlacesPerDay.get(i);
                                        for (int j = 0; j<placeIdsPerDay.size(); j++) {
                                            if (placeIdsPerDay.get(j).equals(oldPlaceId)) {
                                                placeIdsPerDay.remove(j);
                                                placeIdsPerDay.add(j,newPlaceId);
                                                break;
                                            }
                                        }
                                        newDays.put(strDay + (i+1),placeIdsPerDay);
                                    }

                                   // saveTour(tour.getName() + "(Modified)", tour.getInfo(), newDays, tour.getTimes(), tour.getStartTime(), tour.getImageIds(), tour.getCityId());

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

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


    }
    private String tourName, travelFrom, travelTo;
    private int  totalTime, totalCost, totalDistance;
    private List<String> tourImageIds;
    private String tourInfo;
    private HashMap<String, List<String>> tourDays;
    private ArrayList<Travel> travelsPerDay;

    private HashMap<String, List<Integer>> times;
    private String startTime;
    private String cityId;

    private void saveTour(String mTourName, String mTourInfo, HashMap<String, List<String>> mTourDays, HashMap<String, List<Integer>> mTimes, String mStartTime, List<String> mTourImageIds, String mCityId ) {
        tourId = refTours.push().getKey();

        tourName= mTourName;
        tourInfo = mTourInfo;
        tourDays = mTourDays;
        times = mTimes;
        startTime = mStartTime;
        tourImageIds = mTourImageIds;
        cityId = mCityId;

        totalTime = 0;
        totalCost = 0;
        totalDistance = 0;

        for (int i = 0; i < mTourDays.size(); i++) { //Loop each day
            travelsPerDay = new ArrayList<Travel>();
            System.out.println("Day: " + (i+1));

            List<String> placeIdsPerDay = mTourDays.get("Day " + String.valueOf(i + 1)); //Schedule with key started from day 1
            for (int p = 0; p < placeIdsPerDay.size(); p++) { //create travels between places
                if (p < placeIdsPerDay.size() - 1) { //not the last place
                    //Find the travel from placeIds.get(i) to placeIds.get(i+1)
                    travelFrom = placeIdsPerDay.get(p);
                    travelTo = placeIdsPerDay.get(p + 1);
                    String travelFromTo = travelFrom + "_" + travelTo;
                    System.out.println("Obtaining one travel from: " + travelFrom + " to:" + travelTo);

                    DatabaseReference refTravels = FirebaseDatabase.getInstance().getReference("travels");
                    refTravels.orderByChild("id").equalTo(travelFromTo).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            Travel travel = dataSnapshot.getValue(Travel.class);

                            if (travel != null) {
                                //found travel from travelFrom to travelTo
                                System.out.println("DUYVO-268: Travel ID:" + travel.getId());
                                if (travel.getTransports() != null && travel.getTransports().size() > 0) {
                                    Transport transport = travel.getTransports().get("car01");
                                    if (transport != null) {
                                        totalCost = totalCost + transport.getCost(); // default the first transport is used
                                        totalDistance = totalDistance + transport.getDistance();
                                        totalTime = totalTime + transport.getTime();
                                    }
                                }
                                travelsPerDay.add(travel);

                                //System.out.println("DUYVO-274: TravelID:" + travel.getId() + " TotalCost:" + totalCost + ", TotalDistance:" + totalDistance);
                                Tour tour = new Tour(tourId, tourName, tourInfo, totalTime, totalCost, totalDistance, tourDays, times, startTime, tourImageIds, cityId);
                                refTours = FirebaseDatabase.getInstance().getReference("tours");
                                refTours.child(tourId).setValue(tour);
                                Toast.makeText(ChangePlaceActivity.this, "Tour changed", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Tour " + tourName + " changed");

                                Intent intent = new Intent(ChangePlaceActivity.this, ViewTourActivity.class);
                                intent.putExtra(ActivityHelper.TOUR_ID, tourId);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.d(TAG, "Could not find any travel from " + travelFrom + " to " + travelTo);
                            }
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

                }
                //tourSchedule.put("Day " + String.valueOf(i + 1), travelsPerDay);
            }
        }

       /* Tour tour = new Tour(id, name, info, totalTime, totalCost, totalDistance, days, imageIds);
        refTours = mDatabase.getReference("tours");
        refTours.child(id).setValue(tour);
        Toast.makeText(this, "Tour added", Toast.LENGTH_LONG).show();*/
    }

}
