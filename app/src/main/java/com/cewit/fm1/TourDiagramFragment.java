package com.cewit.fm1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Taeyu Im on 18. 8. 11.
 * qvo@cs.stonybrook.edu
 */

public class TourDiagramFragment extends Fragment {
    private static final String TAG = TourDiagramFragment.class.getName();


    private static final String TOUR = "TOUR";
    private Tour tour;
    private Activity activity;

    public TourDiagramFragment() {
        //This constructor should be left empty.
    }

    private void setActivity(Activity mActivity) {
        this.activity = mActivity;
    }
    public Tour getTour() {
        return tour;
    }
    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public static TourDiagramFragment newInstance(Activity mActivity, Tour mTour) {

        Bundle bundle = new Bundle();
        bundle.putSerializable(TOUR, mTour);

        TourDiagramFragment fragment = new TourDiagramFragment();
        fragment.setArguments(bundle);
        fragment.setActivity(mActivity);
        return fragment;
    }



    String strDay = "Day ";
    int countPlace = 0;
    int totalPlaces = 1;
    boolean isCircularTour = false;
    private List<Travel> travels;
    private List<String> sortedAllPlaceIds;
    private List<Place> allPlaces;
    private HashSet<String> allPlaceIds;
    private TableLayout tblTourDiagram;
    private HashMap<String, List<String>> days;
    private HashMap<String, List<Place>> detailDays;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tour_diagram_fragment_tab, container, false);
        tblTourDiagram = (TableLayout) rootView.findViewById(R.id.tblTourDiagram);
        tour = (Tour) getArguments().getSerializable(TOUR);
        if (tour == null) return null;

        days = tour.getDays();
        String tourInfo = tour.getInfo();
        String tourSummary = "(" + Utility.formatCost(tour.getTotalCost()) + "/"
                + Utility.formatDistance(tour.getTotalDistance()) + "/"
                + Utility.formatTime(tour.getTotalTime()) + ")";

        timeSchedules = tour.getTimes();
        startTime = tour.getStartTime();

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

    private List<Place> places;
    private PlaceView curPlaceView;
    private TransportView curTransportView;
    String strCurrentPlaceId;
    String strNextPlaceId;
    int preferTransportType = 1; // default = car.

    private HashMap<String, List<Integer>> timeSchedules;
    List<Integer> times;
    String startTime;

    private void updateView(HashMap<String, List<Place>> detailDays, List<Travel> travels) {
        //Prepare places
        //View Preparation
        int numDays = detailDays.size();
        for (int i = 0; i < numDays; i++) {
            places = detailDays.get("Day " + (i + 1));
            times = timeSchedules.get("Day " + (i+1));
            TableRow dayPartitionRow = new TableRow(activity.getApplicationContext());
            TableLayout.LayoutParams tableRowParamss1 =
                    new TableLayout.LayoutParams
                            (0, TableLayout.LayoutParams.WRAP_CONTENT);
            dayPartitionRow.setBackgroundColor(Color.DKGRAY);
            tableRowParamss1.setMargins(0, 0, 0, 10);
            dayPartitionRow.setLayoutParams(tableRowParamss1);
            TextView tvDayTitle = new TextView(activity.getApplicationContext());
            tvDayTitle.setText("Day " + (i + 1));
            tvDayTitle.setTextSize(22);
            tvDayTitle.setTextColor(Color.WHITE);

            /*TextView tvEmpty = new TextView(this);
            dayPartitionRow.addView(tvEmpty);*/
            dayPartitionRow.addView(tvDayTitle);
            tblTourDiagram.addView(dayPartitionRow);


            int totalPlaces = places.size();
            int numRow;
            int remainder = totalPlaces % 3;
            if (remainder != 0) numRow = ((totalPlaces + 2) / 3) * 2 - 1;
            else numRow = (totalPlaces / 3) * 2 - 1;


            Log.d("TY-TEST:", "total places: " + totalPlaces + ", remainder: " + remainder + ", numRow: " + numRow);

            int numCol = 5;
            int cellIndex = 0;
            int placeIndex = 0;
            boolean isEnd = false;
            boolean isSpecialRow = false;
            Travel curTravel = null;

            String strCurTime = startTime;
            String strPreTime = "";
            String strNextTime;
            for (int r = 0; r < numRow; r++) {
                TableRow row = new TableRow(activity.getApplicationContext());
                TableLayout.LayoutParams tableRowParamss =
                        new TableLayout.LayoutParams
                                (0, TableLayout.LayoutParams.WRAP_CONTENT);

                row.setBackgroundColor(Color.parseColor("#62b0ff"));
                tableRowParamss.setMargins(0, 0, 0, 10);
                row.setLayoutParams(tableRowParamss);

                int remain = (r + 2) % 4;
                List<View> revViews = new ArrayList<>();
                ;

                for (int c = 0; c < numCol; c++) {
                    int cellType = cellIndex % 20;
                    switch (cellType) {
                        case 0:
                        case 2:
                        case 4:
                            if (placeIndex < places.size()) {
                                Place curPlace = places.get(placeIndex);
                                /*String strShowTime = "";
                                if (!strPreTime.equals("")) {
                                    Place prevPlace = places.get(placeIndex-1);
                                    Transport transport = Utility.getTransport(travels, prevPlace.getId(), curPlace.getId(), preferTransportType);
                                    int transportTime = transport.getTime();
                                    strCurTime = Utility.computeTime(strPreTime, transportTime);
                                    String strNewStartTime = Utility.computeTime(strCurTime, times.get(placeIndex));
                                    strShowTime = strCurTime + "|" + strNewStartTime;
                                } else {
                                    strShowTime = strCurTime;
                                }
                                strPreTime = strCurTime;*/
                                curPlaceView = getPlaceView(curPlace);
                                row.addView(curPlaceView);
                                strCurrentPlaceId = curPlace.getId();
                                placeIndex++;
                            } else {
                                isEnd = true;
                                curPlaceView = getPlaceView(null); //get Empty View
                                revViews.add(curPlaceView);
                                row.addView(curPlaceView);
                            }

                            isSpecialRow = false;
                            break;

                        case 10: //reversed row
                        case 12:
                        case 14:
                            if (placeIndex >= places.size()) {
                                isEnd = true;
                                curPlaceView = getPlaceView(null); //get Empty View
                                revViews.add(curPlaceView);
                            } else {
                                Place curPlace = places.get(placeIndex);
                                /*String strShowTime = "";
                                if (!strPreTime.equals("")) {
                                    Place prevPlace = places.get(placeIndex-1);
                                    Transport transport = Utility.getTransport(travels, prevPlace.getId(), curPlace.getId(), preferTransportType);
                                    int transportTime = transport.getTime();
                                    strCurTime = Utility.computeTime(strPreTime, transportTime);
                                    String strNewStartTime = Utility.computeTime(strCurTime, times.get(placeIndex));
                                    strShowTime = strCurTime + "|" + strNewStartTime;
                                } else {
                                    strShowTime = strCurTime;
                                }
                                strPreTime = strCurTime;*/
                                curPlaceView = getPlaceView(curPlace);

                                revViews.add(curPlaceView);
                                strCurrentPlaceId = curPlace.getId();
                                placeIndex++;
                            }
                            isSpecialRow = false;
                            break;
                        case 1:
                        case 3:
                            if (!isEnd && (placeIndex < places.size())) {
                                strNextPlaceId = places.get(placeIndex).getId();
                                curTravel = Utility.getTravel(travels, strCurrentPlaceId, strNextPlaceId);
                                curTransportView = getTransportView(preferTransportType,  curTravel, 0);
                                row.addView(curTransportView);
                            } else {
                                curPlaceView = getPlaceView(null); //get Empty View
                                row.addView(curPlaceView);
                            }
                            isSpecialRow = false;
                            break;
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 16:
                        case 17:
                        case 18:
                        case 19:
                            curPlaceView = getPlaceView(null); //get Empty View
                            row.addView(curPlaceView);
                            isSpecialRow = true;
                            break;
                        case 9:
                            if (placeIndex < places.size()) {
                                strNextPlaceId = places.get(placeIndex).getId();
                                curTravel = Utility.getTravel(travels, strCurrentPlaceId, strNextPlaceId);
                                curTransportView = getTransportView(preferTransportType, curTravel, 1);
                                row.addView(curTransportView);
                            }
                            isSpecialRow = true;
                            break;
                        case 11: //reversed row
                        case 13:
                            if (!isEnd && (placeIndex < places.size())) {
                                strNextPlaceId = places.get(placeIndex).getId();
                                curTravel = Utility.getTravel(travels, strCurrentPlaceId, strNextPlaceId);
                                curTransportView = getTransportView(preferTransportType,  curTravel, 2);
                                revViews.add(curTransportView);
                            } else {
                                curPlaceView = getPlaceView(null); //get Empty View
                                revViews.add(curPlaceView);
                            }
                            break;
                        case 15:
                            if (!isEnd && (placeIndex < places.size())) {
                                strNextPlaceId = places.get(placeIndex).getId();
                                curTravel = Utility.getTravel(travels, strCurrentPlaceId, strNextPlaceId);
                                curTransportView = getTransportView(preferTransportType, curTravel, 3);
                                row.addView(curTransportView);
                            } else {
                                curPlaceView = getPlaceView(null); //get Empty View
                                revViews.add(curPlaceView);
                            }
                            isSpecialRow = true;
                            break;
                    } //end for switch
                    cellIndex++;

                } // end for col

                for (int v = revViews.size() - 1; v >= 0; v--) {
                    View iv = revViews.get(v);
                    if (iv.getParent() != null) {
                        ((ViewGroup) iv.getParent()).removeView(iv);
                    }
                    row.addView(iv);
                }
               /* if (isSpecialRow) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    params.setMargins(0, 500, 0, 0);
                    row.setLayoutParams(params);
                }*/
                tblTourDiagram.addView(row);
            } // end for row
        }
    }



    private int curPlaceId;

    private PlaceView getPlaceView(Place place) {
        PlaceView view = new PlaceView(activity.getApplicationContext());
        if (place != null) {
            //view.setTvInfo(place.getInfo()); //TY
            String strName = place.getName();
            view.setPlace(place);
           // view.getIvIcon().setImageResource(R.drawable.place);
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
            //view.getTvTime().setText(strTime);
            view.getTvName().setText(strName);
        }
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT);
       // view.getIvIcon().setLayoutParams(layoutParams);
        //view.getTvName().setLayoutParams(layoutParams);

        //view.getTvName().getLayoutParams().height = 80;
        //view.getTvName().getLayoutParams().width = 120;

        //view.getIvIcon().getLayoutParams().height = 80;
        //view.getIvIcon().getLayoutParams().width = 80;
        //view.getIvIcon().requestLayout();
        view.getTvName().getLayoutParams().width=120;
        view.getTvName().getLayoutParams().height=120;
       // view.getTvName().setLa
        return view;
    }

    private TransportView getTransportView(int transType, Travel travel, int direction) {
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
        TransportView view = new TransportView(activity.getApplicationContext(), travel, direction, isCar);
        if (travel != null) {
            HashMap<String, Transport> transports = travel.getTransports();
            String info = "";
            Transport transport;
            if (isCar) {
                transport = transports.get("car01");
                if (transport != null) {
                    info = info + Utility.formatDistance(transport.getDistance()) + "\n";
                    info = info + Utility.formatTime(transport.getTime()); // + "\n";
                    //info = info + Utility.formatCost(transport.getCost());

                }
            } else {
                transport = transports.get("bus01");
                if (transport != null) {
                    info = info + Utility.formatDistance(transport.getDistance()) + "\n";
                    info = info + Utility.formatTime(transport.getTime()); // + "\n";
                    //info = info + Utility.formatCost(transport.getCost());
                }
            }
            view.setTvInfo(info);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TransportView myView = (TransportView) v;
                    showPopupMenuTransport(myView);
                }
            });
        } else {
            //view.setIvIcon(null);
            //view.setTvInfo("");
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT);
        view.getIvIcon().setLayoutParams(layoutParams);
        view.getIvIcon().getLayoutParams().height = 120;
        view.getIvIcon().getLayoutParams().width = 80;

        view.getTvInfo().setLayoutParams(layoutParams);
        view.getTvInfo().getLayoutParams().height = 100;
        view.getTvInfo().getLayoutParams().width = 120;

        view.getTvFrom().setLayoutParams(layoutParams);
        view.getTvFrom().getLayoutParams().height = 100;
        view.getTvFrom().getLayoutParams().width = 80;

        view.getTvTo().setLayoutParams(layoutParams);
        view.getTvTo().getLayoutParams().height = 100;
        view.getTvTo().getLayoutParams().width = 80;

        view.getTvFrom().setText("8:00");
        view.getTvTo().setText("9:20");

        layoutParams.setMargins(0, 0, 0, 0);
        view.setGravity(Gravity.CENTER_HORIZONTAL);
        view.getIvIcon().requestLayout();
        return view;
    }

    private void showPopupMenuPlace(PlaceView v) {
        PopupMenu menu = new PopupMenu(activity, v);
        Place p = v.getPlace();
        curPlaceId = v.getId();
        menu.getMenu().add("Name: " + p.getName());
        menu.getMenu().add("Information: " + p.getInfo());
        menu.getMenu().add("Address: " + p.getAddress());
        menu.getMenu().add("Contact Phone: " + p.getContact());
        // menu.getMenu().add("Type: " + p.getType());
        menu.getMenu().add("Rate: " + p.getRate());
        menu.getMenu().add("Change");
        menu.getMenu().add("Skip");



       /* menu.getMenu().add("Theme Park");
        menu.getMenu().add("Museum");
        menu.getMenu().add("Famous Sightseeing Spots");
        menu.getMenu().add("Current Cultural Event");
        menu.getMenu().add("Island");*/

        //menu.getMenu().addSubMenu("Restaurant");

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(
                        activity.getApplicationContext(),
                        "You Clicked : " + item.getTitle(),
                        Toast.LENGTH_SHORT
                ).show();
                if (item.getTitle().equals("Skip")) {
                    Intent intent =  new Intent(activity.getApplicationContext(), ViewTourActivity.class);
                    intent.putExtra("SKIP", "curPlaceId");
                    activity.startActivities(new Intent[]{intent});
                }
                if (item.getTitle().equals("Change")) {
                    Intent intent = new Intent(activity.getApplicationContext(), ChangePlaceActivity.class);
                    intent.putExtra(ActivityHelper.TOUR_ID, tour.getId());
                    intent.putExtra(ActivityHelper.OLD_PLACE_ID, curPlaceId);
                    activity.startActivities(new Intent[]{intent});
                    activity.finish();
                }
                return true;
            }
        });

        menu.show();
    }

    private void showPopupMenuTransport(View v) {
        TransportView transportView = (TransportView) v;
        PopupMenu menu = new PopupMenu(activity.getApplicationContext(), v);

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

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(
                        activity.getApplicationContext(),
                        "You Clicked : " + item.getTitle(),
                        Toast.LENGTH_SHORT
                ).show();


                activity.startActivity(activity.getIntent());
                activity.finish();
                return true;
            }
        });

        menu.show();
    }

}
