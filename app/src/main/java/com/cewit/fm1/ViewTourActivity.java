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


    int preferTransportType = 1; // default = car.


    String strCurrentPlaceId;
    String strNextPlaceId;

    private ViewPagerAdapter adapter;
    private TextView tvTourInfo;
    private TextView tvTourSummary;
    private int transType = 0;
    private String previousArrivalTime;

    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TourDiagramFragment(), "DIAGRAM");
        adapter.addFrag(new TourSummaryFragment(), "SUMMARY");
        viewPager.setAdapter(adapter);
    }


    //REFRESH VARIABLES:
    private boolean isTimeChanged = false;
    private int dayOfChangedTime;
    private int placeOfChangedTime;
    private int newChangedTime;
    private String newDepartureTime;
    private static List<String> changedTimes = new ArrayList<String>();


    private boolean isSkipRefresh = false;
    private static List<String> skippedPlaceIds = new ArrayList<String>();

    private boolean isPlaceChanged = false;
    private String prevPlaceId;
    private String newPlaceId;
    private String cityId;

    private boolean isPlaceAdded = false;

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

        //Check if this is a refresh
        int refreshMode = intent.getIntExtra(ActivityHelper.REFRESH_MODE, 0);
        switch (refreshMode) {
            case ActivityHelper.REFRESH_DEPARTURE_TIME_CHANGED:
                Log.d(TAG, "REFRESH_DEPARTURE_TIME_CHANGED");
                dayOfChangedTime = Integer.parseInt(intent.getStringExtra(ActivityHelper.DAY_OF_CHANGED_TIME));
                placeOfChangedTime = Integer.parseInt(intent.getStringExtra(ActivityHelper.PLACE_OF_CHANGED_TIME));
                if (placeOfChangedTime == 0) {
                    newDepartureTime = intent.getStringExtra(ActivityHelper.NEW_DEPARTURE_TIME);
                } else {
                    newChangedTime = intent.getIntExtra(ActivityHelper.NEW_CHANGED_TIME, 0);
                }
                changedTimes.add(dayOfChangedTime + ":" + placeOfChangedTime + ":" + newChangedTime);
                isTimeChanged = true;

                break;

            case ActivityHelper.REFRESH_SKIP:
                Log.d(TAG, "REFRESH_SKIP");
                String strSkippedPlaceId = intent.getStringExtra(ActivityHelper.PLACE_ID);
                skippedPlaceIds.add(strSkippedPlaceId);
                isSkipRefresh = true;
                break;

            case ActivityHelper.REFRESH_PLACE_CHANGED:
                Log.d(TAG, "REFRESH_PLACE_CHANGED");
                prevPlaceId = intent.getStringExtra(ActivityHelper.CUR_PLACE_ID);
                newPlaceId = intent.getStringExtra(ActivityHelper.NEW_PLACE_ID);
                isPlaceChanged = true;
                break;

            case ActivityHelper.REFRESH_PLACE_ADDED:
                Log.d(TAG, "REFRESH_PLACE_ADDED");
                prevPlaceId = intent.getStringExtra(ActivityHelper.CUR_PLACE_ID);
                newPlaceId = intent.getStringExtra(ActivityHelper.NEW_PLACE_ID);
                isPlaceAdded = true;
                break;

            default: //New Activity
        }

        //TODO will be changed later
        transType = 1;

        //Transport Changed Request
        int isBusSelected = 0, isCarSelected = 0;
        int isTransportChanged = intent.getIntExtra(ActivityHelper.REFRESH_TRANSPORT_CHANGED, 0);
        if (isTransportChanged == 0) { // no change, this is the original tour. All of transport types are selected
            isBusSelected = 1;
            isCarSelected = 1;
        } else { //Transport has been changed.
            isBusSelected = intent.getIntExtra(ActivityHelper.REFRESH_BUS_SELECTED, 0);
            isCarSelected = intent.getIntExtra(ActivityHelper.REFRESH_CAR_SELECTED, 1);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
                        cityId = tour.getCityId();
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

        //Check if this is a skip refresh
        if (isSkipRefresh) {
            updateData(ActivityHelper.REFRESH_SKIP);
        }

        //Check if this is a place change refresh
        if (isPlaceChanged) {
            updateData(ActivityHelper.REFRESH_PLACE_CHANGED);
        }

        //Check if this is a place add refresh
        if (isPlaceAdded) {
            updateData(ActivityHelper.REFRESH_PLACE_ADDED);
        }

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

    private void updateData(int updateMode) {
        switch (updateMode) {
            case ActivityHelper.REFRESH_SKIP:
                for (String skippedPlaceId : skippedPlaceIds) {
                    for (int i = 0; i < days.size(); i++) { //check each day
                        List<String> placeIds = days.get(strDay + (i + 1));
                        for (int j = 0; j < placeIds.size(); j++) {
                            if (placeIds.get(j).equals(skippedPlaceId)) {
                                days.get(strDay + (i + 1)).remove(j);
                                times.get(strDay + (i + 1)).remove(j);

                            }
                        }

                    }
                }
                break;
            case ActivityHelper.REFRESH_PLACE_CHANGED:
                for (int i = 0; i < days.size(); i++) { //check each day
                    List<String> placeIds = days.get(strDay + (i + 1));
                    for (int j = 0; j < placeIds.size(); j++) {
                        if (placeIds.get(j).equals(prevPlaceId)) {
                            days.get(strDay + (i + 1)).set(j, newPlaceId);
                            break;
                        }
                    }

                }
                break;

            case ActivityHelper.REFRESH_PLACE_ADDED:
                List<String> remainPlaceIds = new ArrayList<String>();
                boolean flag = false;

                for (int i = 0; i < days.size(); i++) { //check each day

                    List<String> placeIds = days.get(strDay + (i + 1));
                    int numberOfPlaceIds = placeIds.size();
                    String strTmp = "";
                    Integer intTmp = null;

                    for (int j = 0; j < numberOfPlaceIds; j++) {
                        if (!flag) {
                            if (placeIds.get(j).equals(prevPlaceId)) {
                                if (j == numberOfPlaceIds - 1) {
                                    days.get(strDay + (i + 1)).add(newPlaceId);
                                    times.get(strDay + (i + 1)).set(numberOfPlaceIds - 1, 40);
                                    times.get(strDay + (i + 1)).add(0); //TODO Will be modified later
                                    break;
                                }
                                //Increase the list by duplicating the last element.
                                String lastPlaceId = days.get(strDay + (i + 1)).get(numberOfPlaceIds - 1);
                                days.get(strDay + (i + 1)).add(lastPlaceId);

                                Integer lastTime = times.get(strDay + (i + 1)).get(numberOfPlaceIds - 1);
                                times.get(strDay + (i + 1)).add(lastTime);

                                //Backup the next element
                                strTmp = days.get(strDay + (i + 1)).get(j + 1);
                                intTmp = times.get(strDay + (i + 1)).get(j + 1);

                                //Add new value to the next element.
                                days.get(strDay + (i + 1)).set(j + 1, newPlaceId);
                                times.get(strDay + (i + 1)).set(j + 1, 60);
                                j++;

                                flag = true;
                                continue;
                            }
                        } else {
                            String strTmp2 = days.get(strDay + (i + 1)).get(j);
                            Integer intTmp2 = times.get(strDay + (i + 1)).get(j);
                            days.get(strDay + (i + 1)).set(j, strTmp);
                            times.get(strDay + (i + 1)).set(j, intTmp);
                            strTmp = strTmp2;
                            intTmp = intTmp2;
                        }

                    }
                    break;
                }
        }

    }

    private int totalRequiredTravels;

    private void createViews() {
        placeViewHash = new HashMap<String, List<PlaceView>>();
        travels = new ArrayList<Travel>();
        totalRequiredTravels = allPlaces.size() - 1 - (days.size() - 1);

        for (int i = 0; i < days.size(); i++) { //check each day
            List<String> placeIds = days.get(strDay + (i + 1));

            List<PlaceView> placeViews = new ArrayList<PlaceView>();

            Place nextPlace = null;
            for (int p = 0; p < placeIds.size(); p++) {

                Place place = getPlace(placeIds.get(p));
                PlaceView placeView = getPlaceView(place);
                placeViews.add(placeView);

                //For Transport used later
                if (p < placeIds.size() - 1) {
                    nextPlace = getPlace(placeIds.get(p + 1));
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
            if (strName.length() <= 3) {
                strName = "\n " + strName;

            }
            view.getTvName().setText(strName);
        }
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 0);
        view.getTvName().setLayoutParams(layoutParams);
        view.getTvName().getLayoutParams().width = 120;
        view.getTvName().setGravity(Gravity.CENTER);
        return view;
    }


    private String curPlaceId;
    private List<Integer> placeTimes;

    private void createTransportView() {
        transportViewHash = new HashMap<String, List<TransportView>>();

        //Check if this is a refresh of changing departure time
        if (isTimeChanged) {
            if (placeOfChangedTime == 0) strStartTime = newDepartureTime;
            else updateTimeSchedule();
        }

        for (int d = 0; d < days.size(); d++) { //check each day
            List<String> placeIds = days.get(strDay + (d + 1));
            placeTimes = times.get(strDay + (d + 1));
            int transportIndex = 0;
            String mDepartureTime = strStartTime;
            previousArrivalTime = strStartTime;
            List<TransportView> transportViews = new ArrayList<TransportView>();
            for (int p = 0; p < placeIds.size() - 1; p++) {
                String travelId = placeIds.get(p) + "_" + placeIds.get(p + 1);
                curPlaceId = placeIds.get(p);
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
                transportView.getTvFrom().setTag(new String(d + ":" + p + ":" + placeTimes.get(p)));

                transportView.getTvFrom().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get Current Time
                        final Calendar c = Calendar.getInstance();
                        int mHour = c.get(Calendar.HOUR_OF_DAY);
                        int mMinute = c.get(Calendar.MINUTE);
                        TextView tvFrom = (TextView) v;
                        final String prevDepartureTime = tvFrom.getText().toString();

                        String strTag = (String) tvFrom.getTag();
                        final String curDayIndex = strTag.substring(0, strTag.indexOf(":"));
                        final String curPlaceIndex = strTag.substring(strTag.indexOf(":") + 1, strTag.lastIndexOf(":"));
                        int curPlaceTime = Integer.parseInt(strTag.substring(strTag.lastIndexOf(":") + 1));

                        final String prevArrivalTime = Utility.computeTime(prevDepartureTime, curPlaceTime * (-1));

                        // Launch Time Picker Dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(ViewTourActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        //tvFrom.setText(hourOfDay + ":" + minute);
                                        view.setIs24HourView(true);
                                        String newDepartureTime = hourOfDay + ":" + minute;
                                        if (Utility.isValidTimeChanged(prevArrivalTime, newDepartureTime)) {
                                            int newPlaceTime = Utility.computeTimeDiffer(newDepartureTime, prevDepartureTime);

                                            ViewTourActivity.this.getIntent().putExtra(ActivityHelper.REFRESH_MODE, ActivityHelper.REFRESH_DEPARTURE_TIME_CHANGED);
                                            ViewTourActivity.this.getIntent().putExtra(ActivityHelper.DAY_OF_CHANGED_TIME, curDayIndex);
                                            ViewTourActivity.this.getIntent().putExtra(ActivityHelper.PLACE_OF_CHANGED_TIME, curPlaceIndex);
                                            if (curPlaceIndex.equals("0")) {
                                                ViewTourActivity.this.getIntent().putExtra(ActivityHelper.NEW_DEPARTURE_TIME, newDepartureTime);
                                            }
                                            ViewTourActivity.this.getIntent().putExtra(ActivityHelper.NEW_CHANGED_TIME, newPlaceTime);
                                            ViewTourActivity.this.finish();
                                            startActivity(ViewTourActivity.this.getIntent());

                                        } else {
                                            Toast.makeText(ViewTourActivity.this, "New time must be later than the previous arrival time ", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }, mHour, mMinute, true);

                        timePickerDialog.show();
                    }
                });

                transportView.getTvTo().setTag(new String(d + ":" + p + ":" + placeTimes.get(p)));
                transportView.getTvTo().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get Current Time
                        final Calendar c = Calendar.getInstance();
                        int mHour = c.get(Calendar.HOUR_OF_DAY);
                        int mMinute = c.get(Calendar.MINUTE);
                        TextView tvTo = (TextView) v;
                        final String prevDepartureTime = tvTo.getText().toString();

                        String strTag = (String) tvTo.getTag();
                        final String curDayIndex = strTag.substring(0, strTag.indexOf(":"));
                        final String curPlaceIndex = strTag.substring(strTag.indexOf(":") + 1, strTag.lastIndexOf(":"));
                        int curPlaceTime = Integer.parseInt(strTag.substring(strTag.lastIndexOf(":") + 1));

                        final String prevArrivalTime = Utility.computeTime(prevDepartureTime, curPlaceTime * (-1));

                        // Launch Time Picker Dialog
                        TimePickerDialog timePickerDialog = new TimePickerDialog(ViewTourActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {

                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {
                                        //tvFrom.setText(hourOfDay + ":" + minute);
                                        view.setIs24HourView(true);
                                        String newDepartureTime = hourOfDay + ":" + minute;
                                        if (Utility.isValidTimeChanged(prevArrivalTime, newDepartureTime)) {
                                            int newPlaceTime = Utility.computeTimeDiffer(newDepartureTime, prevDepartureTime);

                                            ViewTourActivity.this.getIntent().putExtra(ActivityHelper.REFRESH_MODE, ActivityHelper.REFRESH_DEPARTURE_TIME_CHANGED);
                                            ViewTourActivity.this.getIntent().putExtra(ActivityHelper.DAY_OF_CHANGED_TIME, curDayIndex);
                                            ViewTourActivity.this.getIntent().putExtra(ActivityHelper.PLACE_OF_CHANGED_TIME, curPlaceIndex);
                                            if (curPlaceIndex.equals("0")) {
                                                ViewTourActivity.this.getIntent().putExtra(ActivityHelper.NEW_DEPARTURE_TIME, newDepartureTime);
                                            }
                                            ViewTourActivity.this.getIntent().putExtra(ActivityHelper.NEW_CHANGED_TIME, newPlaceTime);
                                            ViewTourActivity.this.finish();
                                            startActivity(ViewTourActivity.this.getIntent());

                                        } else {
                                            Toast.makeText(ViewTourActivity.this, "New time must be later than the previous arrival time ", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                }, mHour, mMinute, true);

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

                transportView.setTag(curPlaceId);
                transportView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TransportView view = (TransportView) v;
                        showPopupMenuTransport(view);
                    }
                });


                mDepartureTime = Utility.computeTime(strArrivalTime, placeTimes.get(p + 1));
                previousArrivalTime = strArrivalTime;
                transportViews.add(transportView);
                transportIndex++;
            }
            transportViewHash.put(strDay + (d + 1), transportViews);
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

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void updateTimeSchedule() {
        for (int d = 0; d < times.size(); d++) {
            if (d == dayOfChangedTime) {
                for (int p = 0; p < times.get(strDay + (d + 1)).size(); p++) {
                    if (p == placeOfChangedTime) {
                        Log.d(TAG, "547: d/p " + d + "/" + p);
                        Log.d(TAG, "547: before " + times.get(strDay + (d + 1)).get(p));

                        if (placeOfChangedTime == 0) {
                            strStartTime = newDepartureTime;
                        }
                        int newTime = times.get(strDay + (d + 1)).get(p) + newChangedTime;

                        times.get(strDay + (d + 1)).set(p, newTime);
                        Log.d(TAG, "547: after " + times.get(strDay + (d + 1)).get(p));

                    }
                }
            }
        }
    }


    private Place curPlace;

    private void showPopupMenuPlace(PlaceView v) {
        PopupMenu menu = new PopupMenu(this, v);
        curPlace = v.getPlace();
        curPlaceId = curPlace.getId();


        final String strSite = curPlace.getSite();
        menu.getMenu().add(curPlace.getName());
        menu.getMenu().getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ViewTourActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(strSite)));
                return true;
            }
        });

        menu.getMenu().add(curPlace.getInfo());
        menu.getMenu().add(curPlace.getAddress());
        menu.getMenu().add(curPlace.getContact());
        menu.getMenu().add("Rate: " + curPlace.getRate());
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
                    ViewTourActivity.this.getIntent().putExtra(ActivityHelper.REFRESH_MODE, ActivityHelper.REFRESH_SKIP);
                    ViewTourActivity.this.getIntent().putExtra(ActivityHelper.PLACE_ID, curPlaceId);
                    ViewTourActivity.this.finish();
                    startActivity(ViewTourActivity.this.getIntent());

                }
                if (item.getTitle().equals("Change")) {
                    String placeType = curPlace.getType();
                    Intent intent;
                    if (placeType.equalsIgnoreCase("Accommodation")) {
                        intent = new Intent(ViewTourActivity.this.getApplicationContext(), AccommodationListActivity.class);
                    } else if (placeType.equalsIgnoreCase("Restaurant")) {
                        intent = new Intent(ViewTourActivity.this.getApplicationContext(), SelectPlaceActivity.class);
                    } else {
                        intent = new Intent(ViewTourActivity.this.getApplicationContext(), SelectPlaceActivity.class);
                    }
                    intent.putExtra(ActivityHelper.REFRESH_MODE, ActivityHelper.REFRESH_PLACE_CHANGED);
                    intent.putExtra(ActivityHelper.CITY_ID, cityId);
                    intent.putExtra(ActivityHelper.TOUR_ID, tourId);
                    intent.putExtra(ActivityHelper.CUR_PLACE_ID, curPlaceId);
                    startActivity(intent);
                    ViewTourActivity.this.finish();
                }
                return true;
            }
        });

        menu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            newPlaceId = data.getData().toString();
            Log.d(TAG, "715-New Place ID:" + newPlaceId);
        }
    }

    private void showPopupMenuTransport(View v) {
        TransportView transportView = (TransportView) v;
        PopupMenu menu = new PopupMenu(this.getApplicationContext(), v);

        final String curPlaceId = (String) transportView.getTag();


        HashMap<String, Transport> transportsHash = transportView.getTravel().getTransports();
        List<Transport> transports = new ArrayList<Transport>(transportsHash.values());
        String strOption = "";
        String transName, transDist, transCost, transTime;
        for (Transport transport : transports) {
            transName = transport.getName();
            if (transName == null) transName = "";

            transCost = transport.getCost() + "";
            transCost = transCost.substring(0, transCost.length() - 3) + "," +
                    transCost.substring(transCost.length() - 3);
            transCost = transCost + "원";

            transDist = ((float) transport.getDistance()) / 1000 + "Km";
            transTime = transport.getTime() + "분";

            strOption = transport.getType() + " " + transName + ": "
                    + transDist + "/" + transTime + "/" + transCost;
            menu.getMenu().add(strOption);
        }
        menu.getMenu().add("Add Place");
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(
                        ViewTourActivity.this.getApplicationContext(),
                        "You Clicked : " + item.getTitle(),
                        Toast.LENGTH_SHORT
                ).show();

                if (item.getTitle().equals("Add Place")) {
                    Intent intent = new Intent(ViewTourActivity.this.getApplicationContext(), SelectPlaceActivity.class);

                    intent.putExtra(ActivityHelper.REFRESH_MODE, ActivityHelper.REFRESH_PLACE_ADDED);
                    intent.putExtra(ActivityHelper.CITY_ID, cityId);
                    intent.putExtra(ActivityHelper.TOUR_ID, tourId);
                    intent.putExtra(ActivityHelper.CUR_PLACE_ID, curPlaceId);
                    startActivity(intent);
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
        if (allPlaces != null && allPlaces.size() > 0) {
            for (Place place : allPlaces) {
                if (place.getId().equals(placeId)) return place;
            }
        }
        return null;
    }

    private Travel getTravel(String travelId) {
        if (travels != null && travels.size() > 0) {
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
