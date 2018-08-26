package com.cewit.fm1;

/**
 * Created by Taeyu Im on 18. 5. 2.
 * qvo@cs.stonybrook.edu
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cewit.fm1.models.Tour;
import com.cewit.fm1.models.Transport;
import com.cewit.fm1.models.Travel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SplashScreen extends AppCompatActivity {
    private static final String TAG = SplashScreen.class.getSimpleName();

    private static boolean isFirstLaunch;

    // Database Helper
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();


    private DatabaseReference refTours;

    private List<Travel> travels = new ArrayList<Travel>();


    //Layout
    private ImageView ivMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        //Layout
        ivMain = (ImageView) findViewById(R.id.ivMainImage);
        ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivities(new Intent[]{intent});
                finish();
            }
        });
        //DB Initialization
        mDatabase = FirebaseDatabase.getInstance();
        refTours = mDatabase.getReference("tours");


        /*final DatabaseReference refTravels = mDatabase.getReference("travels");
        refTravels.orderByChild("from").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Travel travel = dataSnapshot.getValue(Travel.class);
                HashMap<String, Transport> transports = travel.getTransports();
                System.out.println("DUYVO-100:" + dataSnapshot.getKey() + ":" + travel.getTo());
                if (transports != null && transports.size() > 0) {
                    for (int i = 0; i < transports.size(); i++) {
                        System.out.println("DUYVO-103:");
                    }
                }
                travels.add(travel);
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
        });*/



        /*tours = new ArrayList<Tour>();



        checkFirstLaunch();
        if (isFirstLaunch) {
            setDefaultPreferences();
            initializeDB();
        }

        */// TODO: Uncommented Later
        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(500);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivities(new Intent[]{intent});
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        myThread.start();
        //clearTours();
        //iniTours(); //TODO
        //iniCities();
        //initTours();
    }

    private void initTours() {
        String tourName = "Jeju Two-day Trip";
        String tourInfo = "A 2-day tour of Jeju's most popular attractions";

        HashMap<String, List<String>> tourDays = new HashMap<String, List<String>>();
        List<String> placeIds1 = new ArrayList<String>();
        placeIds1.add("jeju001");
        placeIds1.add("jeju002");
        placeIds1.add("r_jeju001");
        placeIds1.add("jeju003");
        placeIds1.add("jeju005");
        placeIds1.add("r_jeju007");
        placeIds1.add("h_jeju001");
        tourDays.put("Day 1", placeIds1);

        List<String> placeIds2 = new ArrayList<String>();
        placeIds2.add("h_jeju001");
        placeIds2.add("jeju006");
        placeIds2.add("r_jeju004");
        placeIds2.add("jeju007");
        placeIds2.add("jeju012");
        placeIds2.add("r_jeju006");
        placeIds2.add("jeju001");
        tourDays.put("Day 2", placeIds2);


        HashMap<String, List<Integer>> tourTimes = new HashMap<String, List<Integer>>();
        List<Integer> times1 = new ArrayList<Integer>();
        times1.add(0);
        times1.add(80);
        times1.add(45);
        times1.add(60);
        times1.add(50);
        times1.add(45);
        times1.add(0);
        tourTimes.put("Day 1", times1);


        List<Integer> times2 = new ArrayList<Integer>();
        times2.add(0);
        times2.add(120);
        times2.add(45);
        times2.add(100);
        times2.add(50);
        times2.add(45);
        times2.add(0);
        tourTimes.put("Day 2", times2);

        String strStartTime = "10:00";
        String strCityId = "-LJ6r2bh6mlgxXJHghyU";
        List<String> tourImageIds = new ArrayList<String>();
        tourImageIds.add(R.drawable.jeju_island + "");


        saveTour(tourName, tourInfo, tourDays, tourTimes, strStartTime, tourImageIds, strCityId);
    }


   /* ValueEventListener placesValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            places.clear(); // clear if it contains previous places
            for(DataSnapshot placeSnapshot: dataSnapshot.getChildren()) {
                Place place = placeSnapshot.getValue(Place.class);
                places.add(place);
            }
            //TODO with places, usually update adapter view
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };*/


    /*
      Schedule contains day number and list of placeIds for the day
    */

    List<Travel> travelsPerDay;
    HashMap<String, List<String>> tourDays;
    HashMap<String, List<Integer>> tourTimes;
    int totalTime = 0;
    int totalCost = 0;
    int totalDistance = 0;
    List<String> tourImageIds;
    String cityId;
    String tourId;
    String tourInfo;
    String tourName;
    String travelFrom;
    String travelTo;
    private String startTime;

    private void saveTour(String mTourName, String mTourInfo, HashMap<String, List<String>> mTourDays, HashMap<String, List<Integer>> mTourTimes, String mStartTime, List<String> mTourImageIds, String mCityId) {
        tourId = refTours.push().getKey();
        tourImageIds = mTourImageIds;
        cityId = mCityId;
        tourInfo = mTourInfo;
        tourName = mTourName;
        tourDays = mTourDays;
         totalTime = 0;
         totalCost = 0;
         totalDistance = 0;
         tourTimes = mTourTimes;
         startTime = mStartTime;


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

                                Tour tour = new Tour(tourId, tourName, tourInfo, totalTime, totalCost, totalDistance, tourDays, tourTimes, startTime, tourImageIds, cityId);
                                refTours = mDatabase.getReference("tours");
                                refTours.child(tourId).setValue(tour);
                                Toast.makeText(SplashScreen.this, "Tour added", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Tour " + tourName + " added");
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
