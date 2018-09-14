package com.cewit.fm1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TimeUtils;
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
import com.cewit.fm1.util.ContentView;
import com.cewit.fm1.util.CostView;
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



    private Activity activity;

    private HashMap<String, List<MovingView>> mvTimeViewHash;
    private HashMap<String, List<MovingView>> mvPlaceViewHash;
    private HashMap<String, List<ContentView>> contentViewHash;
    private HashMap<String, List<CostView>> costViewHash;
    private String[] daySummaries;

    private Context context;
    private LinearLayout lilSummary;

    public TourSummaryFragment() {
        //This constructor should be left empty.
    }

    private void setContext(Context mContext) {
        this.context = mContext;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public HashMap<String, List<ContentView>> getContentViewHash() {
        return contentViewHash;
    }

    public void setContentViewHash(HashMap<String, List<ContentView>> contentViewHash) {
        this.contentViewHash = contentViewHash;
    }

    public HashMap<String, List<CostView>> getCostViewHash() {
        return costViewHash;
    }

    public void setCostViewHash(HashMap<String, List<CostView>> costViewHash) {
        this.costViewHash = costViewHash;
    }

    public HashMap<String, List<MovingView>> getMvTimeViewHash() {
        return mvTimeViewHash;
    }

    public void setMvTimeViewHash(HashMap<String, List<MovingView>> mvTimeViewHash) {
        this.mvTimeViewHash = mvTimeViewHash;
    }

    public HashMap<String, List<MovingView>> getMvPlaceViewHash() {
        return mvPlaceViewHash;
    }

    public void setMvPlaceViewHash(HashMap<String, List<MovingView>> mvPlaceViewHash) {
        this.mvPlaceViewHash = mvPlaceViewHash;
    }


    public static TourSummaryFragment newInstance(Activity mActivity, HashMap<String, List<MovingView>> mMvTimeViewHash, HashMap<String, List<MovingView>> mMvPlaceViewHash, HashMap<String, List<ContentView>> mContentViewHash, HashMap<String, List<CostView>> mCostViewHash) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("MV_TIME_VIEW_HASH", mMvTimeViewHash);
        bundle.putSerializable("MV_PLACE_VIEW_HASH", mMvPlaceViewHash);
        bundle.putSerializable("CONTENT_VIEW_HASH", mContentViewHash);
        bundle.putSerializable("COST_VIEW_HASH", mCostViewHash);

        TourSummaryFragment fragment = new TourSummaryFragment();
        fragment.setArguments(bundle);
        fragment.setContext(mActivity.getApplicationContext());

        fragment.setArguments(bundle);
        fragment.setActivity(mActivity);

        fragment.setMvTimeViewHash(mMvTimeViewHash);
        fragment.setMvPlaceViewHash(mMvPlaceViewHash);
        fragment.setContentViewHash(mContentViewHash);
        fragment.setCostViewHash(mCostViewHash);

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "createViews(): started");

        View rootView = inflater.inflate(R.layout.tour_summary_fragment_tab, container, false);
        lilSummary = (LinearLayout) rootView.findViewById(R.id.lilSummary);
        lilSummary.setOrientation(LinearLayout.VERTICAL);

        daySummaries = getArguments().getStringArray("DAY_SUMMARY");
        mvTimeViewHash = (HashMap<String, List<MovingView>>) getArguments().getSerializable("MV_TIME_VIEW_HASH");
        mvPlaceViewHash = (HashMap<String, List<MovingView>>) getArguments().getSerializable("MV_PLACE_VIEW_HASH");
        contentViewHash = (HashMap<String, List<ContentView>>) getArguments().getSerializable("CONTENT_VIEW_HASH");
        costViewHash = (HashMap<String, List<CostView>>) getArguments().getSerializable("COST_VIEW_HASH");

        if (mvTimeViewHash == null || mvTimeViewHash.size() == 0) return null;
        updateView();

        return rootView;
    }

    private void updateView() {
        Log.d(TAG, "updateView(): started");

        //Prepare places
        //View Preparation
        for (int i = 0; i < mvTimeViewHash.size(); i++) {
            List<MovingView> mvTimeViews = mvTimeViewHash.get("Day " + (i + 1));
            List<MovingView> mvPlaceViews = mvPlaceViewHash.get("Day " + (i + 1));
            List<ContentView> contentViews = contentViewHash.get("Day " + (i + 1));
            List<CostView> costViews = costViewHash.get("Day " + (i + 1));

            TableRow dayRow = new TableRow(this.context);
            TableLayout.LayoutParams tableRowParamss = new TableLayout.LayoutParams
                    (0, TableLayout.LayoutParams.WRAP_CONTENT);
            tableRowParamss.setMargins(0, 0, 0, 10);
            dayRow.setBackgroundColor(Color.DKGRAY);
            dayRow.setLayoutParams(tableRowParamss);

            //add Day-------------------------------------------------------------------------------
            TextView tvDayTitle = new TextView(this.context);
            tvDayTitle.setText("DAY " + (i + 1) + ":\t\t\t" + daySummaries[i]);
            tvDayTitle.setTextSize(22);
            tvDayTitle.setTextColor(Color.WHITE);
            tvDayTitle.setBackgroundColor(Color.DKGRAY);
            lilSummary.addView(tvDayTitle);

            //add Detail Day Table -----------------------------------------------------------------
            TableLayout tblDaySummary = new TableLayout(this.context);
            tblDaySummary.setStretchAllColumns(true);

            TableRow rowView = new TableRow(this.context);
            rowView.setBackgroundColor(Color.parseColor("#62b0ff"));
            tableRowParamss.setMargins(0, 0, 0, 5);
            rowView.setLayoutParams(tableRowParamss);
            TextView tvTime = new TextView(this.context);
            tvTime.setText("시간");

            TextView tvItem = new TextView(this.context);
            tvItem.setText("항목");

            TextView tvContent = new TextView(this.context);
            tvContent.setText("내용");

            TextView tvFee = new TextView(this.context);
            tvFee.setText("금액");

            tvTime.setTextSize(20);
            tvItem.setTextSize(20);
            tvContent.setTextSize(20);
            tvFee.setTextSize(20);

            tvTime.setTypeface(tvTime.getTypeface(), Typeface.BOLD_ITALIC);
            tvItem.setTypeface(tvItem.getTypeface(), Typeface.BOLD_ITALIC);
            tvContent.setTypeface(tvContent.getTypeface(), Typeface.BOLD_ITALIC);
            tvFee.setTypeface(tvFee.getTypeface(), Typeface.BOLD_ITALIC);

            tvTime.setTextColor(Color.BLUE);
            tvItem.setTextColor(Color.BLUE);
            tvContent.setTextColor(Color.BLUE);
            tvFee.setTextColor(Color.BLUE);

            rowView.addView(tvTime);
            rowView.addView(tvItem);
            rowView.addView(tvContent);
            rowView.addView(tvFee);

            tblDaySummary.addView(rowView);

            for (int j = 0; j < mvTimeViews.size(); j++) {
                rowView = new TableRow(this.context);
                rowView.setBackgroundColor(Color.parseColor("#62b0ff"));
                tableRowParamss.setMargins(0, 0, 0, 5);
                rowView.setLayoutParams(tableRowParamss);


                if(mvTimeViews.get(j).getParent()!=null) {
                    ((ViewGroup)mvTimeViews.get(j).getParent()).removeView(mvTimeViews.get(j));
                }
                rowView.addView(mvTimeViews.get(j));

                if(mvPlaceViews.get(j).getParent()!=null) {
                    ((ViewGroup)mvPlaceViews.get(j).getParent()).removeView(mvPlaceViews.get(j));
                }
                rowView.addView(mvPlaceViews.get(j));

                if(contentViews.get(j).getParent()!=null) {
                    ((ViewGroup)contentViews.get(j).getParent()).removeView(contentViews.get(j));
                }
                rowView.addView(contentViews.get(j));

                if(costViews.get(j).getParent()!=null) {
                    ((ViewGroup)costViews.get(j).getParent()).removeView(costViews.get(j));
                }
                rowView.addView(costViews.get(j));
                tblDaySummary.addView(rowView);
            }
            lilSummary.addView(tblDaySummary);
        }
    }
}
