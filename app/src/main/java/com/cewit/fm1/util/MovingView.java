package com.cewit.fm1.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cewit.fm1.R;
import com.cewit.fm1.models.Place;

/**
 * Created by Taeyu Im on 18. 7. 26.
 * qvo@cs.stonybrook.edu
 */

public class MovingView extends LinearLayout {
    private Place place;
    private TextView tvFrom;
    private TextView tvTo;
    private TextView tvContent;


    public MovingView(Context context) {
        super(context, null);
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_moving_item, this, true);

        tvFrom = (TextView) getChildAt(0).findViewById(R.id.tvFrom);
        tvContent = (TextView) getChildAt(0).findViewById(R.id.tvContent);
        tvTo = (TextView) getChildAt(0).findViewById(R.id.tvTo);
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public TextView getTvFrom() {
        return tvFrom;
    }

    public void setTvFrom(TextView tvFrom) {
        this.tvFrom = tvFrom;
    }

    public TextView getTvTo() {
        return tvTo;
    }

    public void setTvTo(TextView tvTo) {
        this.tvTo = tvTo;
    }

    public TextView getTvContent() {
        return tvContent;
    }

    public void setTvContent(TextView tvContent) {
        this.tvContent = tvContent;
    }
}
