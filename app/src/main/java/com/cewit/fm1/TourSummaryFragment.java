package com.cewit.fm1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cewit.fm1.models.Place;
import com.cewit.fm1.models.Tour;
import com.cewit.fm1.models.Transport;
import com.cewit.fm1.models.Travel;
import com.cewit.fm1.util.MovingView;
import com.cewit.fm1.util.PlaceView;
import com.cewit.fm1.util.TransportView;
import com.cewit.fm1.util.Utility;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Taeyu Im on 18. 8. 11.
 * qvo@cs.stonybrook.edu
 */

public class TourSummaryFragment extends Fragment {

    private static final String TAG = TourSummaryFragment.class.getName();


    private static final String TOUR = "TOUR";
    private Tour tour;
    private int preferTransportType;

    private Context context;
    private LinearLayout lilSummary;

    public TourSummaryFragment() {
        //This constructor should be left empty.
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    private void setContext(Context mContext) {
        this.context = mContext;
    }

    public static TourSummaryFragment newInstance(Activity mActivity, Tour mTour) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TOUR, mTour);

        TourSummaryFragment fragment = new TourSummaryFragment();
        fragment.setArguments(bundle);
        fragment.setContext(mActivity.getApplicationContext());
        return fragment;

    }


    List<String> placeIds;
    List<Integer> times;
    String startTime;

    String strDay = "Day ";
    int countPlace = 0;
    int totalPlaces = 1;
    boolean isCircularTour = false;
    private List<Travel> travels;
    private List<String> sortedAllPlaceIds;
    private List<Place> allPlaces;
    private HashSet<String> allPlaceIds;
    private HashMap<String, List<String>> days;
    private HashMap<String, List<Integer>> timeSchedules;
    private HashMap<String, List<Place>> detailDays;
    private List<Place> places;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tour_summary_fragment_tab, container, false);
        lilSummary = (LinearLayout) rootView.findViewById(R.id.lilSummary);
        lilSummary.setOrientation(LinearLayout.VERTICAL);
        tour = (Tour) getArguments().getSerializable(TOUR);
        preferTransportType = getArguments().getInt("prefer_transport");

        if (tour == null) return null;

        days = tour.getDays();
        if (days == null || days.size() ==0)  return null;

        timeSchedules = tour.getTimes();
        startTime = tour.getStartTime();

        for (int i = 0; i<days.size(); i++) {
             placeIds = days.get("Day " + (i+1));
             times = timeSchedules.get("Day " + (i+1));
        }

        //Check if the tour ended at the starting place or not
        if (days.get(strDay + 1).get(0).equals(days.get(strDay + days.size()).get(days.get(strDay + days.size()).size() - 1))) {
            totalPlaces = totalPlaces - 1;
            isCircularTour = true;
            Log.d(TAG, "The tour is ended at the starting place.");
        }

        travels = new ArrayList<Travel>();
        sortedAllPlaceIds = new ArrayList<String>();

        if (days != null && days.size() > 0) {
            Log.d(TAG, "Number of days: " + days.size());
            allPlaces = new ArrayList<Place>();
            allPlaceIds = new HashSet<>();
            for (int d = 1; d <= days.size(); d++) {
                List<String> placeIdsPerDay = days.get(strDay + d);
                if (placeIdsPerDay != null && placeIdsPerDay.size() > 0) {
                    Log.d(TAG, "Number of places in Day " + d + " is " + placeIdsPerDay.size());
                    totalPlaces = totalPlaces + placeIdsPerDay.size() - 1;
                    Log.d(TAG, "Total places in the tour is " + placeIdsPerDay.size());

                    for (int p = 0; p < placeIdsPerDay.size(); p++) {
                        String placeId = placeIdsPerDay.get(p);
                        if (!allPlaceIds.contains(placeId)) {
                            Log.d(TAG, "placeId: " + placeId);
                            allPlaceIds.add(placeId);
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
                                                detailDays = new HashMap<String, List<Place>>();
                                                HashSet<String> checkDoublePlaceIds = new HashSet<String>();
                                                for (int d = 0; d < days.size(); d++) {
                                                    List<String> innerPlaceIds = days.get(strDay + (d + 1));
                                                    List<Place> sortedPlacesPerDay = new ArrayList<Place>();
                                                    for (String innerPlaceId : innerPlaceIds) {
                                                        Log.d(TAG, "innerPlaceId: " + innerPlaceId);
                                                        for (Place innerPlace : allPlaces) {
                                                            if (innerPlaceId.equals(innerPlace.getId())) {
                                                                Log.d(TAG, "placeID: " + innerPlace.getId());
                                                                sortedPlacesPerDay.add(innerPlace);
                                                                if (!checkDoublePlaceIds.contains(innerPlace.getId())) {
                                                                    checkDoublePlaceIds.add(innerPlace.getId());
                                                                    sortedAllPlaceIds.add(innerPlaceId);
                                                                }
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    //Add to hash
                                                    detailDays.put(strDay + (d + 1), sortedPlacesPerDay);
                                                    Log.d(TAG, "The number of places for Day " + (d + 1) + " is " + sortedPlacesPerDay.size());
                                                }


                                                //Prepare travels
                                                String prePlaceId, curPlaceId;
                                                if (isCircularTour) {
                                                    sortedAllPlaceIds.add(sortedAllPlaceIds.get(0));
                                                }
                                                if (sortedAllPlaceIds.size() >= 2) {
                                                    prePlaceId = sortedAllPlaceIds.get(0);
                                                    for (int i = 1; i < sortedAllPlaceIds.size(); i++) {
                                                        curPlaceId = sortedAllPlaceIds.get(i);
                                                        String travelId = prePlaceId + "_" + curPlaceId;
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

                                                                if (travels.size() == sortedAllPlaceIds.size() - 1) {
                                                                    updateView(detailDays, travels);
                                                                }


                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                        prePlaceId = curPlaceId;
                                                    }
                                                } else {
                                                    Log.i(TAG, "Number of places must larger than 2.");
                                                }
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } // end of if place has already existed in the hashset
                    } //end of for places per day
                } // end of if
            } //end of for day

        } // end if day is not null



        return rootView;
    }

    private void updateView(HashMap<String, List<Place>> detailDays, List<Travel> travels) {
        //Prepare places
        //View Preparation

        int numDays = detailDays.size();
        /*
        for (int i = 0; i < numDays; i++) {

            times = timeSchedules.get("Day " + (i+1));
            TableRow  dayRow= new TableRow(this.context);
            TableLayout.LayoutParams tableRowParamss = new TableLayout.LayoutParams
                            (0, TableLayout.LayoutParams.WRAP_CONTENT);
            tableRowParamss.setMargins(0, 0, 0, 10);
            dayRow.setBackgroundColor(Color.DKGRAY);
            dayRow.setLayoutParams(tableRowParamss);

            //add Day-------------------------------------------------------------------------------
            TextView tvDayTitle = new TextView(this.context);
            tvDayTitle.setText("Day " + (i + 1));
            tvDayTitle.setTextSize(22);
            tvDayTitle.setTextColor(Color.WHITE);
            tvDayTitle.setBackgroundColor(Color.DKGRAY);
            lilSummary.addView(tvDayTitle);

            //add Detail Day Table -----------------------------------------------------------------
            LinearLayout lilTable = new LinearLayout(this.context);
            lilTable.setOrientation(LinearLayout.VERTICAL);



            places = detailDays.get("Day " + (i + 1));

            //Calculate number of row.
            int numOfRows = places.size()-1;
            for (Place place: places) {
                if (place.getType().contains("Restaurant")) {
                    numOfRows ++;
                }
            }

            String strFrom = startTime;
            String strTo;
            String strCurTime = startTime;
            String strNextTime;
            for (int p = 0; p<places.size()-1; p++) {
                //TableRow rowView = new TableRow(this.context);
                LinearLayout lilRow = new LinearLayout(this.context);

                LinearLayout.LayoutParams lilRowParamss = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                lilRow.setOrientation(LinearLayout.HORIZONTAL);
                lilRow.setWeightSum(1);
                lilRow.setBackgroundColor(Color.parseColor("#62b0ff"));

                Place curPlace = places.get(p);
                Place nextPlace = places.get(p+1);

                Transport transport = Utility.getTransport(travels,curPlace.getId(), nextPlace.getId(), preferTransportType);
                int transportTime = transport.getTime();
                strNextTime = Utility.computeTime(strCurTime, transportTime);
                MovingView mvTime = getMovingView(strCurTime,strNextTime);

                lilRow.addView(mvTime);
                //TODO add time spending at each place
                strCurTime = Utility.computeTime(strNextTime, times.get(p+1).intValue());



                strFrom = curPlace.getName();
                strTo = nextPlace.getName();
                MovingView mvPlace = getMovingView(strFrom, strTo);
                lilRow.addView(mvPlace);

                TextView tvContent = new TextView(this.context);
                if (transport != null) {
                    if (transport.getName() != null) {
                        tvContent.setText(transport.getType() + " " + transport.getName());
                    } else {
                        tvContent.setText(transport.getType());
                    }
                } else {
                    tvContent.setText("TBD");
                }
                tvContent.setTextSize(18);

                lilRow.addView(tvContent);

                EditText etCost = new EditText(this.context);
                etCost.setText(String.valueOf(transport.getCost()));
                etCost.setTextSize(18);
                lilRow.addView(etCost);

                lilTable.addView(lilRow);
            }
            lilSummary.addView(lilTable);

        } */

       for (int i = 0; i < numDays; i++) {

            times = timeSchedules.get("Day " + (i+1));
            TableRow  dayRow= new TableRow(this.context);
            TableLayout.LayoutParams tableRowParamss = new TableLayout.LayoutParams
                    (0, TableLayout.LayoutParams.WRAP_CONTENT);
            tableRowParamss.setMargins(0, 0, 0, 10);
            dayRow.setBackgroundColor(Color.DKGRAY);
            dayRow.setLayoutParams(tableRowParamss);

            //add Day-------------------------------------------------------------------------------
            TextView tvDayTitle = new TextView(this.context);
            tvDayTitle.setText("Day " + (i + 1));
            tvDayTitle.setTextSize(22);
            tvDayTitle.setTextColor(Color.WHITE);
            tvDayTitle.setBackgroundColor(Color.DKGRAY);
            lilSummary.addView(tvDayTitle);

            //add Detail Day Table -----------------------------------------------------------------
            TableLayout dayTable = new TableLayout(this.context);
            dayTable.setStretchAllColumns(true);
            places = detailDays.get("Day " + (i + 1));

            //Calculate number of row.
            int numOfRows = places.size()-1;
            for (Place place: places) {
                if (place.getType().contains("Restaurant")) {
                    numOfRows ++;
                }
            }

            String strFrom = startTime;
            String strTo;
            String strCurTime = startTime;
            String strNextTime;
            for (int p = 0; p<places.size()-1; p++) {
                TableRow rowView = new TableRow(this.context);
                rowView.setBackgroundColor(Color.parseColor("#62b0ff"));
                tableRowParamss.setMargins(0, 0, 0, 5);
                rowView.setLayoutParams(tableRowParamss);

                Place curPlace = places.get(p);
                Place nextPlace = places.get(p+1);

                Transport transport = Utility.getTransport(travels,curPlace.getId(), nextPlace.getId(), preferTransportType);
                int transportTime = transport.getTime();
                strNextTime = Utility.computeTime(strCurTime, transportTime);
                MovingView mvTime = getMovingView(strCurTime, strNextTime, "(" + Utility.formatTime(transportTime) + ")");
                rowView.addView(mvTime);
                //TODO add time spending at each place
                strCurTime = Utility.computeTime(strNextTime, times.get(p+1).intValue());



                strFrom = curPlace.getName();
                strTo = nextPlace.getName();
                MovingView mvPlace = getMovingView(strFrom, strTo, "(" + Utility.formatDistance(transport.getDistance()) + ")");
                rowView.addView(mvPlace);

                TextView tvContent = new TextView(this.context);
                if (transport != null) {
                    if (transport.getName() != null) {
                        tvContent.setText(transport.getType() + " " + transport.getName());
                    } else {
                        tvContent.setText(transport.getType());
                    }
                } else {
                    tvContent.setText("TBD");
                }
                tvContent.setTextSize(18);
                rowView.addView(tvContent);

                EditText etCost = new EditText(this.context);
                etCost.setText(String.valueOf(transport.getCost()));
                etCost.setTextSize(18);
                rowView.addView(etCost);

                dayTable.addView(rowView);
            }
            lilSummary.addView(dayTable);

        }

    }

    private MovingView getMovingView(String strFrom, String strTo, String strConent) {
        MovingView view = new MovingView(this.context);
        view.getTvFrom().setText(strFrom);
        view.getTvContent().setText(strConent);
        view.getTvContent().setTextSize(12);
        view.getTvTo().setText(strTo);


        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 0.2f);
        /*view.getIvIcon().setLayoutParams(layoutParams);
        view.getIvIcon().getLayoutParams().height = 50;
        view.getIvIcon().requestLayout();*/
        view.setGravity(Gravity.LEFT);
        return view;
    }
}
