package com.cewit.fm1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.cewit.fm1.models.Transport;
import com.cewit.fm1.util.ActivityHelper;
import com.cewit.fm1.util.PlaceView;
import com.cewit.fm1.util.TransportView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Taeyu Im on 18. 8. 11.
 * qvo@cs.stonybrook.edu
 */

public class TourDiagramFragment extends Fragment {
    private static final String TAG = TourDiagramFragment.class.getName();

    //From the Activity
    private HashMap<String, List<TransportView>> transportViewHash;
    private HashMap<String, List<PlaceView>> placeViewHash;
    private String[] daySummaries;
    private Activity activity;

    private void setActivity(Activity mActivity) {
        this.activity = mActivity;
    }

    public void setTransportViewHash(HashMap<String, List<TransportView>> transportViewHash) {
        this.transportViewHash = transportViewHash;
    }

    public void setPlaceViewHash(HashMap<String, List<PlaceView>> placeViewHash) {
        this.placeViewHash = placeViewHash;
    }

    //Layout
    private LinearLayout lilDiagram;

    public static TourDiagramFragment newInstance(Activity mActivity, HashMap<String, List<PlaceView>> mPlaceViewHash, HashMap<String, List<TransportView>> mTransportViewHash) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("PLACE_VIEW_HASH", mPlaceViewHash);
        bundle.putSerializable("TRANSPORT_VIEW_HASH", mTransportViewHash);

        TourDiagramFragment fragment = new TourDiagramFragment();
        fragment.setArguments(bundle);
        fragment.setActivity(mActivity);
        fragment.setTransportViewHash(mTransportViewHash);
        fragment.setPlaceViewHash(mPlaceViewHash);
        return fragment;
    }

    private String strDay = "Day ";
    private PlaceView curPlaceView;
    private TransportView curTransportView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tour_diagram_fragment_tab, container, false);
        lilDiagram = (LinearLayout) rootView.findViewById(R.id.lilDiagram);
        lilDiagram.setOrientation(LinearLayout.VERTICAL);

        daySummaries = getArguments().getStringArray("DAY_SUMMARY");

        placeViewHash = (HashMap<String, List<PlaceView>>)getArguments().getSerializable("PLACE_VIEW_HASH");
        transportViewHash = (HashMap<String, List<TransportView>>)getArguments().getSerializable("TRANSPORT_VIEW_HASH");

        if (placeViewHash != null && placeViewHash.size() > 0) {
            for (int i = 0; i < placeViewHash.size(); i++) { //get data for each day.
                List<PlaceView> placeViews = placeViewHash.get(strDay + (i+1));
                List<TransportView> transportViews = transportViewHash.get(strDay + (i+1));

                TableRow dayPartitionRow = new TableRow(activity.getApplicationContext());
                TableLayout.LayoutParams tableRowParamss1 = new TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT);
                dayPartitionRow.setBackgroundColor(Color.DKGRAY);
                tableRowParamss1.setMargins(0, 0, 0, 0);
                dayPartitionRow.setLayoutParams(tableRowParamss1);

                //add Day-------------------------------------------------------------------------------
                TextView tvDayTitle = new TextView(this.activity.getApplicationContext());
                tvDayTitle.setText(strDay + (i + 1));
                tvDayTitle.setTextSize(22);
                tvDayTitle.setTextColor(Color.WHITE);
                tvDayTitle.setBackgroundColor(Color.DKGRAY);


                TextView tvDaySummary = new TextView(this.activity.getApplicationContext());
                tvDaySummary.setText("DAY " + (i+1) + ":\t\t\t" + daySummaries[i]);
                tvDaySummary.setTextSize(22);
                tvDaySummary.setTextColor(Color.WHITE);
                tvDaySummary.setBackgroundColor(Color.DKGRAY);

                lilDiagram.addView(tvDaySummary);

                //add diagram for this day
                TableLayout tblDayDiagram = new TableLayout(this.activity.getApplicationContext());
                tblDayDiagram.setStretchAllColumns(true);
                tblDayDiagram.setBackgroundColor(Color.parseColor("#62b0ff"));

                //compute the layout
                int totalPlacesPerDay = placeViews.size();
                int numRow = 0;
                int remainder = totalPlacesPerDay % 3;
                if (remainder != 0) numRow = ((totalPlacesPerDay + 2) / 3) * 2 - 1;
                else numRow = (totalPlacesPerDay / 3) * 2 - 1;

                int numCol = 5;

                int cellIndex = 0;
                int placeIndex = 0;
                int transportIndex = 0;
                boolean isEnd = false;

                for (int r = 0; r < numRow; r++) { //create each row
                    TableRow row = new TableRow(activity.getApplicationContext());
                    TableLayout.LayoutParams tableRowParamss = new TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT);
                    row.setBackgroundColor(Color.parseColor("#62b0ff"));
                    tableRowParamss.setMargins(0, 0, 0, 0);
                    row.setLayoutParams(tableRowParamss);


                    List<View> revViews = new ArrayList<>();

                    for (int c = 0; c < numCol; c++) {
                        int cellType = cellIndex % 20;
                        switch (cellType) {
                            case 0:
                            case 2:
                            case 4:
                                if (placeIndex < placeViews.size()) {
                                    curPlaceView = placeViews.get(placeIndex);
                                    row.addView(curPlaceView);
                                    placeIndex++;
                                } else {
                                    isEnd = true;
                                    curPlaceView = getEmptyView(); //get Empty View
                                    revViews.add(curPlaceView);
                                    row.addView(curPlaceView);
                                }
                                break;

                            case 10: //reversed row
                            case 12:
                            case 14:
                                if (placeIndex >= placeViews.size()) {
                                    isEnd = true;
                                    curPlaceView = getEmptyView(); //get Empty View
                                    revViews.add(curPlaceView);
                                } else {
                                    curPlaceView = placeViews.get(placeIndex);
                                    revViews.add(curPlaceView);
                                    placeIndex++;
                                }
                                break;
                            case 1:
                            case 3:
                                if (!isEnd && (placeIndex < placeViews.size())) {
                                    curTransportView = transportViews.get(transportIndex);
                                    row.addView(curTransportView);
                                    transportIndex++;
                                } else {
                                    curPlaceView = getEmptyView(); //get Empty View
                                    row.addView(curPlaceView);
                                }
                                break;
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 16:
                            case 17:
                            case 18:
                            case 19:
                                curPlaceView = getEmptyView(); //get Empty View
                                row.addView(curPlaceView);
                                break;
                            case 9:
                                if (placeIndex < placeViews.size()) {
                                    curTransportView = transportViews.get(transportIndex);
                                    row.addView(curTransportView);
                                    transportIndex++;
                                }
                                break;
                            case 11: //reversed row
                            case 13:
                                if (!isEnd && (placeIndex < placeViews.size())) {
                                    curTransportView = transportViews.get(transportIndex);
                                    revViews.add(curTransportView);
                                    transportIndex++;
                                } else {
                                    curPlaceView = getEmptyView(); //get Empty View
                                    revViews.add(curPlaceView);
                                }
                                break;
                            case 15:
                                if (!isEnd && (placeIndex < placeViews.size())) {
                                    curTransportView = transportViews.get(transportIndex);
                                    row.addView(curTransportView);
                                    transportIndex++;
                                } else {
                                    curPlaceView = getEmptyView(); //get Empty View
                                    revViews.add(curPlaceView);
                                }
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

                    tblDayDiagram.addView(row);
                } // end for row
                lilDiagram.addView(tblDayDiagram);
            } //end for day

        } //end for hash checking

        return rootView;
    }


    public TourDiagramFragment() {
        //This constructor should be left empty.
    }

    private PlaceView getEmptyView() {
        PlaceView view = new PlaceView(activity.getApplicationContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,20,0,0);
        view.getTvName().setLayoutParams(layoutParams);
        view.getTvName().getLayoutParams().width=120;
        view.getTvName().setGravity(Gravity.CENTER);
        return view;
    }
}
