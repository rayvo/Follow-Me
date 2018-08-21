package com.cewit.fm1.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cewit.fm1.R;
import com.cewit.fm1.models.Place;

/**
 * Created by Taeyu Im on 18. 7. 26.
 * qvo@cs.stonybrook.edu
 */

public class PlaceView extends LinearLayout {

    private Place place;
    private TextView tvName;
    //private ImageView ivIcon;


    public PlaceView(Context context) {
        super(context, null);
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_tour_place_item, this, true);

        //ivIcon = (ImageView) getChildAt(0).findViewById(R.id.ivIcon);
        tvName = (TextView) getChildAt(0).findViewById(R.id.tvName);
    }

    public void setTvName(String strName) {
        tvName.setText(strName);
    }


   /* public ImageView getIvIcon() {
        return ivIcon;
    }

    public void setIvIcon(ImageView ivIcon) {
        this.ivIcon = ivIcon;
    }*/

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public TextView getTvName() {
        return tvName;
    }

    public void setTvName(TextView tvName) {
        this.tvName = tvName;
    }

}
