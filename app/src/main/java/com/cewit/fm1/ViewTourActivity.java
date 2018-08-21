package com.cewit.fm1;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
 * Created by Taeyu Im on 18. 5. 7.
 * qvo@cs.stonybrook.edu
 */

public class ViewTourActivity extends AppCompatActivity {

    private static final String TAG = ViewTourActivity.class.getName();

    // Layout


    private TabLayout tabLayout;
    private ViewPager viewPager;

    private TextView tvTourInfo, tvTourSummary;
    private CheckBox chkBus, chkCarTaxi;

    private PlaceView curPlaceView;
    private TransportView curTransportView;

    //=================================================================
    private List<Place> places;

    private HashMap<String, List<String>> days;

    public HashMap<String, List<Place>> getDetailDays() {
        return detailDays;
    }

    public void setDetailDays(HashMap<String, List<Place>> detailDays) {
        this.detailDays = detailDays;
    }

    private  HashMap<String, List<Place>> detailDays;
    private List<Travel> travels;

    String strDay = "Day ";
    int countPlace = 0;
    private List<Place> allPlaces;
    private HashSet<String> allPlaceIds;
    private List<String> sortedAllPlaceIds;
    int totalPlaces = 1;
    boolean isCircularTour = false;

    private Intent intent;
    private String tourId;




    int preferTransportType = 1; // default = car.


    String strCurrentPlaceId;
    String strNextPlaceId;

    private ViewPagerAdapter adapter;

    private void addTabs(ViewPager viewPager, Tour mTour) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), mTour);
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



        //ViewPagerAdapter adapter = new ViewPagerAdapter(this.getApplicationContext(), getSupportFragmentManager());


        //Give the TabLayout the ViewPage



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
                    Tour tour = tourSnapshot.getValue(Tour.class);
                    if (tour != null) {

                        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), tour);
                        Fragment diagramFragment = TourDiagramFragment.newInstance(ViewTourActivity.this,tour);
                        Bundle bundle1 = new Bundle();
                        bundle1.putSerializable("TOUR", tour);
                        bundle1.putString("id", tour.getId());
                        bundle1.putInt("prefer_transport", preferTransportType);

                        diagramFragment.setArguments(bundle1);
                        adapter.addFrag(diagramFragment, "DIAGRAM VIEW");

                        Fragment summaryFragment = TourSummaryFragment.newInstance(ViewTourActivity.this,tour);
                        Bundle bundle2 = new Bundle();
                        bundle2.putSerializable("TOUR", tour);
                        bundle2.putString("id", tour.getId());
                        bundle2.putInt("prefer_transport", preferTransportType);

                        diagramFragment.setArguments(bundle2);
                        adapter.addFrag(summaryFragment, "SUMMARY");
                        viewPager.setAdapter(adapter);
                        viewPager.setOffscreenPageLimit(2);
                        tabLayout.setupWithViewPager(viewPager);
                    }
                }
            } //end data change of tour query

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }




    private void updateView(HashMap<String, List<Place>> detailDays, List<Travel> travels) {
        //Prepare places
        //View Preparation


        int numDays = detailDays.size();
        for (int i = 0; i < numDays; i++) {
            places = detailDays.get("Day " + (i + 1));
            TableRow dayPartitionRow = new TableRow(this);
            TableLayout.LayoutParams tableRowParamss1 =
                    new TableLayout.LayoutParams
                            (0, TableLayout.LayoutParams.WRAP_CONTENT);
            dayPartitionRow.setBackgroundColor(Color.DKGRAY);
            tableRowParamss1.setMargins(0, 0, 0, 10);
            dayPartitionRow.setLayoutParams(tableRowParamss1);
            TextView tvDayTitle = new TextView(this);
            tvDayTitle.setText("Day " + (i + 1));
            tvDayTitle.setTextSize(22);
            tvDayTitle.setTextColor(Color.WHITE);

            /*TextView tvEmpty = new TextView(this);
            dayPartitionRow.addView(tvEmpty);*/
            dayPartitionRow.addView(tvDayTitle);
            //tourDiagramTable.addView(dayPartitionRow);


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

            for (int r = 0; r < numRow; r++) {
                TableRow row = new TableRow(this);
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
                                Place place = places.get(placeIndex);
                                curPlaceView = getPlaceView(place);
                                row.addView(curPlaceView);
                                strCurrentPlaceId = place.getId();
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
                                Place place = places.get(placeIndex);
                                curPlaceView = getPlaceView(place);
                                revViews.add(curPlaceView);
                                strCurrentPlaceId = place.getId();
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
               // tourDiagramTable.addView(row);
            } // end for row
        }

        TableRow tr = new TableRow(this);
        TextView rang = new TextView(this);
        rang.setGravity(Gravity.CENTER_HORIZONTAL);
        //rang.setPadding(1, 1, 1, 1);
        rang.setText("01234567890");

        tr.addView(rang);
       // tourSummaryTable.addView(tr);//end for days
    }

    private PlaceView getPlaceView(Place place) {
        PlaceView view = new PlaceView(this.getApplicationContext());
        if (place != null) {
            //view.setTvInfo(place.getInfo()); //TY
            String strName = place.getName();
            view.setPlace(place);
            //view.getIvIcon().setImageResource(R.drawable.place);
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
            view.setTvName(strName); //TY
        } else {
            //view.setIvIcon(null);
            view.setTvName("");
        }
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT);
        //view.getIvIcon().setLayoutParams(layoutParams);
        //view.getIvIcon().getLayoutParams().height = 80;
        //view.getIvIcon().getLayoutParams().width = 80;
        //view.getIvIcon().requestLayout();
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
        TransportView view = new TransportView(this.getApplicationContext(), travel, direction, isCar);
        if (travel != null) {
            HashMap<String, Transport> transports = travel.getTransports();
            String info = "";
            Transport transport;
            if (isCar) {
                transport = transports.get("car01");
                if (transport != null) {
                    info = info + Utility.formatDistance(transport.getDistance()) + "\n";
                    info = info + Utility.formatTime(transport.getTime()) + "\n";
                    info = info + Utility.formatCost(transport.getCost());

                }
            } else {
                transport = transports.get("bus01");
                if (transport != null) {
                    info = info + Utility.formatDistance(transport.getDistance()) + "\n";
                    info = info + Utility.formatTime(transport.getTime()) + "\n";
                    info = info + Utility.formatCost(transport.getCost());
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
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT);
        view.getIvIcon().setLayoutParams(layoutParams);
        view.getIvIcon().getLayoutParams().height = 120;
        view.getIvIcon().getLayoutParams().width = 80;
        layoutParams.setMargins(0, 0, 0, 0);
        view.setGravity(Gravity.CENTER_HORIZONTAL);
        view.getIvIcon().requestLayout();
        return view;
    }

    private int curPlaceId;

    private void showPopupMenuPlace(PlaceView v) {
        setViewOnTouchColor(v);
        PopupMenu menu = new PopupMenu(ViewTourActivity.this, v);
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
                        ViewTourActivity.this,
                        "You Clicked : " + item.getTitle(),
                        Toast.LENGTH_SHORT
                ).show();
                if (item.getTitle().equals("Skip")) {
                    Intent intent = ViewTourActivity.this.getIntent();
                    intent.putExtra("SKIP", "curPlaceId");
                }
                if (item.getTitle().equals("Change")) {
                    Intent intent = new Intent(getApplicationContext(), ChangePlaceActivity.class);
                    intent.putExtra(ActivityHelper.TOUR_ID, tourId);
                    intent.putExtra(ActivityHelper.OLD_PLACE_ID, curPlaceId);
                    startActivities(new Intent[]{intent});
                    finish();
                }
                return true;
            }
        });

        menu.show();
    }

    private void showPopupMenuTransport(View v) {
        TransportView transportView = (TransportView) v;

        setViewOnTouchColor(v);
        PopupMenu menu = new PopupMenu(ViewTourActivity.this, v);

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
                        ViewTourActivity.this,
                        "You Clicked : " + item.getTitle(),
                        Toast.LENGTH_SHORT
                ).show();
                restartActivity();
                return true;
            }
        });

        menu.show();
    }

    private void setViewOnTouchColor(View v) {
        //v.setBackgroundColor(Color.rgb(255,0,0));
    }

    private void restartActivity() {
        this.startActivity(getIntent());
        this.finish();
    }

    private enum NodeType {
        FIRST_NODE,

        RIGHT_NODE,
        LEFT_NODE,
        DOWN_NODE,

        TOURISM_NODE,
        RESTAURANT_NODE,
        COFFEE_NODE,
        HOTEL_NODE,

        EMPTY_NODE
    }


    // View lookup cache
    class ViewHolder {

    }


    /*@NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewTourActivity.ViewHolder viewHolder = null;
        if (r == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.view_tour_listview, null, true);
            viewHolder = new ViewTourActivity.ViewHolder(r);
            r.setTag(viewHolder); //store view
        } else {
            viewHolder = (ViewTourActivity.ViewHolder) r.getTag(); //get the store view
        }

        Place place = places.get(position);
        viewHolder.tvPlaceName.setText(place.getName());
        String placeInfo = place.getOpenTime() + " (" + place.getEntranceFee() + "KRW)";
        viewHolder.tvPlaceInfo.setText(placeInfo);
        viewHolder.tvDescription.setText(place.getInfo());

        String travelInfo = "Bus";
        //TODO create travelInfo randomly.
        int busNum = (int) (Math.random() * ((500 - 100) + 1)) + 100;
        int travelTime = (int) (Math.random() * ((120 - 15) + 1)) + 15;
        travelInfo = travelInfo + " " + busNum + " (" + travelTime + "min)";
        viewHolder.tvTravelInfo.setText(travelInfo);
        // viewHolder.tvRatingStar.setText(place.getRatingStar());
        //viewHolder.tvOpenTime.setText(place.getOpenTime());
        viewHolder.ivCol1.setImageResource(place.getImageIds().get(0));
        return r;

    }*/
}
