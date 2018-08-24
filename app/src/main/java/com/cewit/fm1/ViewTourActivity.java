package com.cewit.fm1;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cewit.fm1.models.Place;
import com.cewit.fm1.models.Tour;
import com.cewit.fm1.models.Transport;
import com.cewit.fm1.models.Travel;
import com.cewit.fm1.util.ActivityHelper;
import com.cewit.fm1.util.PlaceView;
import com.cewit.fm1.util.TransportView;
import com.cewit.fm1.util.Utility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Taeyu Im on 18. 5. 7.
 * qvo@cs.stonybrook.edu
 */

public class ViewTourActivity extends AppCompatActivity {

    private static final String TAG = ViewTourActivity.class.getName();

    // Layout


    private Tour tour;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private CheckBox chkBus, chkCarTaxi;

    //=================================================================

    private HashMap<String, List<String>> days;
    private HashMap<String, List<PlaceView>> placeViewHash;
    private HashMap<String, List<TransportView>> transportViewHash;
    private String strStartTime;
    private HashMap<String, List<Integer>> times;
    private int curPlaceId;

    private List<Travel> travels;

    String strDay = "Day ";
    int countPlace = 0;
    private List<Place> allPlaces;
    private HashSet<String> allPlaceIds;
    private List<String> sortedAllPlaceIds;
    int totalPlaces = 0;
    boolean isCircularTour = false;

    private Intent intent;
    private String tourId;
    private boolean isTimeChanged = false;
    private int placeIndexChanged;


    int preferTransportType = 1; // default = car.


    String strCurrentPlaceId;
    String strNextPlaceId;

    private ViewPagerAdapter adapter;
    private TextView tvTourInfo;
    private TextView tvTourSummary;
    private int transType = 0;

    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TourDiagramFragment(), "DIAGRAM");
        adapter.addFrag(new TourSummaryFragment(), "SUMMARY");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_tour);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ;
        //Check the request
        intent = this.getIntent();
        tourId = intent.getStringExtra(ActivityHelper.TOUR_ID);
        //Skip Request
        String strSkipId = intent.getStringExtra(ActivityHelper.REFRESH_SKIP);
        if (strSkipId != null && strSkipId.length() > 0) {
        } else {
        }
        //Transport Changed Request
        int isBusSelected = 0, isCarSelected = 0;
        int isTransportChanged = intent.getIntExtra(ActivityHelper.REFRESH_TRANSPORT_CHANGE, 0);
        if (isTransportChanged == 0) { // no change, this is the original tour. All of transport types are selected
            isBusSelected = 1;
            isCarSelected = 1;
        } else { //Transport has been changed.
            isBusSelected = intent.getIntExtra(ActivityHelper.REFRESH_BUS_SELECTED, 0);
            isCarSelected = intent.getIntExtra(ActivityHelper.REFRESH_CAR_SELECTED, 1);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Departure Time Changed Request
        String strChangedDepartureTime = intent.getStringExtra("CHANGE_DEPARTURE_TIME");
        if (strChangedDepartureTime != null) {
            //Create a temp tour with new modification time
            placeIndexChanged = intent.getIntExtra("PLACE_INDEX", 0);
            isTimeChanged = true;
            //Load the new tour instead the current one
        }

        //Give the TabLayout the ViewPage
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Data Preparation
        tvTourInfo = (TextView) findViewById(R.id.tvTourInfo);
        tvTourSummary = (TextView) findViewById(R.id.tvTourSummary);

        chkBus = (CheckBox) findViewById(R.id.chkBus);
        chkCarTaxi = (CheckBox) findViewById(R.id.chkCar);
        //View Preparation

        if (isCarSelected == 1) {
            preferTransportType = 1;
            chkCarTaxi.setChecked(true);
        } else {
            chkCarTaxi.setChecked(false);
        }
        if (isBusSelected == 1) {
            preferTransportType = 2;
            chkBus.setChecked(true);
        } else {
            chkBus.setChecked(false);
        }
        if (isBusSelected + isCarSelected == 2) preferTransportType = 3;


        chkBus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int isBus = 0;
                if (isChecked) {
                    isBus = 1;
                } else {
                    isBus = 0;
                }
                intent.putExtra(ActivityHelper.REFRESH_BUS_SELECTED, isBus);
                restartActivity();
            }
        });

        chkCarTaxi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int isCar = 0;
                if (isChecked) {
                    isCar = 1;
                } else {
                    isCar = 0;
                }
                intent.putExtra(ActivityHelper.REFRESH_CAR_SELECTED, isCar);
                restartActivity();
            }
        });

        //Data Preparation
        DatabaseReference refTours = FirebaseDatabase.getInstance().getReference("tours");
        refTours.orderByChild("id").equalTo(tourId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot tourSnapshot : dataSnapshot.getChildren()) {
                    tour = tourSnapshot.getValue(Tour.class);
                    if (tour != null) {
                        //Create placeViewHash and transportViewHash
                        createViewHash(tour);
                    }
                }
            } //end data change of tour query

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void createViewHash(Tour tour) {
        days = tour.getDays();
        times = tour.getTimes();
        strStartTime = tour.getStartTime();
        allPlaces = new ArrayList<Place>();

        if (days != null && days.size() > 0) {
            Log.d(TAG, "Number of days: " + days.size());
            for (int i = 0; i < days.size(); i++) { //check each day
                List<String> placeIds = days.get(strDay + (i + 1));
                totalPlaces = totalPlaces + placeIds.size();
                if (placeIds != null && placeIds.size() > 0) {
                    Log.d(TAG, "Number of places in Day " + (i + 1) + " is " + placeIds.size());
                    for (int p = 0; p < placeIds.size(); p++) {
                        String placeId = placeIds.get(p);
                        Log.d(TAG, "placeId: " + placeId);
                        DatabaseReference refPlaces = FirebaseDatabase.getInstance().getReference("places");
                        refPlaces.orderByChild("id").equalTo(placeId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                                if (iterator.hasNext()) {
                                    Place place = iterator.next().getValue(Place.class);
                                    if (place != null) {
                                        Log.d(TAG, "Added PlaceId:" + place.getId());
                                        allPlaces.add(place);
                                        countPlace = countPlace + 1;
                                        if (countPlace == totalPlaces) { //obtained all of the places in all days
                                            createViews();
                                        }
                                    } // if place != null
                                } //loop
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                }
            }
        }
    }

    private int totalRequiredTravels;
    private void createViews() {
        placeViewHash = new HashMap<String,List<PlaceView>>();
        travels = new ArrayList<Travel>();
        totalRequiredTravels = allPlaces.size() - 1 - (days.size()-1);
        for (int i = 0; i < days.size(); i++) { //check each day
            List<String> placeIds = days.get(strDay + (i + 1));

            List<PlaceView> placeViews = new ArrayList<PlaceView>();

            Place nextPlace = null;
            for (int p = 0; p < placeIds.size(); p++) {
                Place place = getPlace(placeIds.get(p));
                PlaceView placeView = getPlaceView(place);
                placeViews.add(placeView);

                //For Transport used later
                if (p<placeIds.size()-1) {
                    nextPlace = getPlace(placeIds.get(p+1));
                    String travelId = place.getId() + "_" + nextPlace.getId();
                    DatabaseReference refPlaces = FirebaseDatabase.getInstance().getReference("travels");
                    refPlaces.orderByChild("id").equalTo(travelId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                            if (iterator.hasNext()) {
                                Travel travel = iterator.next().getValue(Travel.class);
                                if (travel != null) {
                                    travels.add(travel);
                                    Log.d(TAG, "Added Travel with Id:" + travel.getId());
                                }
                            }

                            if (travels.size() == totalRequiredTravels) {
                                createTransportView();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
            placeViewHash.put(strDay + (i + 1), placeViews);
        }
    }

    private PlaceView getPlaceView(Place place) {
        PlaceView view = new PlaceView(this.getApplicationContext());
        if (place != null) {
            String strName = place.getName();
            view.setPlace(place);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlaceView myView = (PlaceView) v;
                    showPopupMenuPlace(myView);
                }
            });

            if (strName.length() > 15) {
                strName = strName.substring(0, 14) + "...";
            }
            if (strName.length()<=3) {
                strName = "\n " + strName;

            }
            view.getTvName().setText(strName);
        }
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,20,0,0);
        view.getTvName().setLayoutParams(layoutParams);
        view.getTvName().getLayoutParams().width=120;
        view.getTvName().setGravity(Gravity.CENTER);
        return view;
    }


    private String curTravelId;
    private void createTransportView() {
        transportViewHash = new HashMap<String,List<TransportView>>();

        for (int i = 0; i < days.size(); i++) { //check each day
            List<String> placeIds = days.get(strDay + (i + 1));
            List<Integer> placeTimes = times.get(strDay + (i+1));
            int transportIndex = 0;
            String mDepartureTime = strStartTime;
            List<TransportView> transportViews = new ArrayList<TransportView>();
            for (int p = 0; p < placeIds.size()-1; p++) {
                String travelId = placeIds.get(p) + "_" + placeIds.get(p+1);
                curTravelId = travelId;
                Travel travel = getTravel(travelId);

                //Select transport type
                boolean isCar = true;
                switch (transType) {
                    case 1: // car
                        isCar = true;
                        break;
                    case 2: // bus
                        isCar = false;
                        break;

                    default: //mix bus and car
                        int random = (Math.random() < 0.5) ? 0 : 1;
                        if (random == 0) {
                            isCar = true;
                        } else {
                            isCar = false;
                        }
                }

                HashMap<String, Transport> transports = travel.getTransports();
                String info = "";
                Transport transport;
                int transTime = 0;
                String strArrivalTime = "";

                if (isCar) {
                    transport = transports.get("car01");
                    if (transport != null) {
                        info = info + Utility.formatDistance(transport.getDistance()) + "/";
                        transTime = transport.getTime();
                        info = info + Utility.formatTime(transTime);
                        strArrivalTime = Utility.computeTime(mDepartureTime, transTime);
                    }
                } else {
                    transport = transports.get("bus01");
                    if (transport != null) {
                        info = info + Utility.formatDistance(transport.getDistance()) + "/";
                        transTime = transport.getTime();
                        info = info + Utility.formatTime(transTime);
                        strArrivalTime = Utility.computeTime(mDepartureTime, transTime);
                    }
                }

                int direction = computeDirection(transportIndex);
                TransportView transportView = new TransportView(this, travel, direction, mDepartureTime, strArrivalTime, info, isCar);

                transportView.getTvFrom().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get Current Time
                        final Calendar c = Calendar.getInstance();
                        int mHour = c.get(Calendar.HOUR_OF_DAY);
                        int mMinute = c.get(Calendar.MINUTE);

                        // Launch Time Picker Dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(ViewTourActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        //tvFrom.setText(hourOfDay + ":" + minute);
                                        ViewTourActivity.this.getIntent().putExtra("CHANGE_DEPARTURE_TIME", hourOfDay + ":" + minute);
                                        ViewTourActivity.this.getIntent().putExtra("TRAVEL_ID", curTravelId);
                                        ViewTourActivity.this.finish();
                                        startActivity(ViewTourActivity.this.getIntent());
                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                });

                transportView.getTvTo().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get Current Time
                        final Calendar c = Calendar.getInstance();
                        int mHour = c.get(Calendar.HOUR_OF_DAY);
                        int mMinute = c.get(Calendar.MINUTE);

                        // Launch Time Picker Dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(ViewTourActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        ViewTourActivity.this.getIntent().putExtra("CHANGE_DEPARTURE_TIME", hourOfDay + ":" + minute);
                                        ViewTourActivity.this.getIntent().putExtra("TRAVEL_ID", curTravelId);
                                        ViewTourActivity.this.finish();
                                        startActivity(ViewTourActivity.this.getIntent());
                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    }
                });
                if (direction == 2) {
                    transportView.getTvTo().setClickable(true);
                    transportView.getTvFrom().setClickable(false);
                } else {
                    transportView.getTvTo().setClickable(false);
                    transportView.getTvFrom().setClickable(true);
                }


                mDepartureTime = Utility.computeTime(strArrivalTime, placeTimes.get(p));
                transportViews.add(transportView);
                transportIndex++;
            }
            transportViewHash.put(strDay + (i+1), transportViews);
        }

        Fragment diagramFragment = TourDiagramFragment.newInstance(ViewTourActivity.this, placeViewHash, transportViewHash);
        Bundle bundle1 = new Bundle();
        bundle1.putSerializable("PLACE_VIEW_HASH", placeViewHash);
        bundle1.putSerializable("TRANSPORT_VIEW_HASH", transportViewHash);

        diagramFragment.setArguments(bundle1);
        adapter.addFrag(diagramFragment, "DIAGRAM VIEW");

        Fragment summaryFragment = TourSummaryFragment.newInstance(ViewTourActivity.this, tour);
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("TOUR", tour);
        bundle2.putString("id", tour.getId());
        bundle2.putInt("prefer_transport", preferTransportType);

        summaryFragment.setArguments(bundle2);
        adapter.addFrag(summaryFragment, "SUMMARY");


        Log.d(TAG, "487:" + placeViewHash.size()  + ", Day 1: " + placeViewHash.get(strDay + "1").size());
        Log.d(TAG, "488:" + placeViewHash.size()  + ", Day 2: " + placeViewHash.get(strDay + "2").size());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void showPopupMenuPlace(PlaceView v) {
        PopupMenu menu = new PopupMenu(this, v);
        Place p = v.getPlace();
        curPlaceId = v.getId();


        final String strSite = p.getSite();
        menu.getMenu().add(p.getName());
        menu.getMenu().getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ViewTourActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(strSite)));
                return true;
            }
        });

        menu.getMenu().add(p.getInfo());
        menu.getMenu().add(p.getAddress());
        menu.getMenu().add(p.getContact());
        menu.getMenu().add("Rate: " + p.getRate());
        menu.getMenu().add("Change");
        menu.getMenu().add("Skip");

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(
                        ViewTourActivity.this.getApplicationContext(),
                        "You Clicked : " + item.getTitle(),
                        Toast.LENGTH_SHORT
                ).show();
                if (item.getTitle().equals("Skip")) {
                    Intent intent =  new Intent(ViewTourActivity.this.getApplicationContext(), ViewTourActivity.class);
                    intent.putExtra("SKIP", "curPlaceId");
                    ViewTourActivity.this.startActivities(new Intent[]{intent});
                }
                if (item.getTitle().equals("Change")) {
                    Intent intent = new Intent(ViewTourActivity.this.getApplicationContext(), ChangePlaceActivity.class);
                    intent.putExtra(ActivityHelper.TOUR_ID, tour.getId());
                    intent.putExtra(ActivityHelper.OLD_PLACE_ID, curPlaceId);
                    ViewTourActivity.this.startActivities(new Intent[]{intent});
                    ViewTourActivity.this.finish();
                }
                return true;
            }
        });

        menu.show();
    }

    private int computeDirection(int transportIndex) {
        int remain = transportIndex % 6;
        switch (remain) {
            case 0:
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
            case 4:
                return 2;
            case 5:
                return 3;
        }
        return 0;
    }

    private Place getPlace(String placeId) {
        if(allPlaces != null && allPlaces.size()>0) {
            for (Place place : allPlaces) {
                if (place.getId().equals(placeId)) return place;
            }
        }
        return null;
    }

    private Travel getTravel(String travelId) {
        if(travels != null && travels.size()>0) {
            for (Travel travel : travels) {
                if (travel.getId().equals(travelId)) return travel;
            }
        }
        return null;
    }

    private void restartActivity() {
        this.startActivity(getIntent());
        this.finish();
    }

}
