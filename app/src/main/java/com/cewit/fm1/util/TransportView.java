package com.cewit.fm1.util;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cewit.fm1.R;
import com.cewit.fm1.models.Transport;
import com.cewit.fm1.models.Travel;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Taeyu Im on 18. 7. 26.
 * qvo@cs.stonybrook.edu
 */

public class TransportView extends LinearLayout {
    private static final String TAG = TransportView.class.getName();


    LinearLayout lilOutside;
    LinearLayout lilTop;
    private Travel travel;
    private TextView tvInfo;
    private TextView tvFrom;
    private TextView tvTo;
    private ImageView ivIcon;
    private View vLine;
    private Context context;
    private Activity activity;

    /* viewDirection
    0: to right
    1: down right
    2: to left
    3: down left
     */

    public TransportView(Activity mActivity, Travel mTravel, int viewDirection, String mDepartureTime, String mArrivalTime, String mInfo, boolean isCar) {
        super(mActivity.getApplicationContext(), null);
        if (mTravel == null) return;
        activity = mActivity;
        this.travel = mTravel;

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (viewDirection == 0) {
            inflater.inflate(R.layout.transport_view_0, this, true);
        } else if (viewDirection == 1) {
            inflater.inflate(R.layout.transport_view_1, this, true);
        } else if (viewDirection == 2) {
            inflater.inflate(R.layout.transport_view_2, this, true);
        } else {
            inflater.inflate(R.layout.transport_view_3, this, true);
        }


        lilOutside = (LinearLayout) getChildAt(0);
        lilTop = (LinearLayout) getChildAt(0).findViewById(R.id.lilTop);
        ivIcon = (ImageView) getChildAt(0).findViewById(R.id.ivIcon);
        tvFrom = (TextView) getChildAt(0).findViewById(R.id.tvFrom);
        tvTo = (TextView) getChildAt(0).findViewById(R.id.tvTo);
        tvInfo = (TextView) getChildAt(0).findViewById(R.id.tvInfo);
        vLine = (View) getChildAt(0).findViewById(R.id.vLine);



        tvFrom.setText(mDepartureTime);
        tvTo.setText(mArrivalTime);
        tvInfo.setText(mInfo);

        TransportViewType viewType = null;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        switch (viewDirection) {
            case 0: //right
                if (isCar) {
                    ivIcon.setImageResource(R.drawable.car_right);
                } else {
                    ivIcon.setImageResource(R.drawable.bus_right);
                }

                lilOutside.setLayoutParams(params);
                break;
            case 1: //down-right
                if (isCar) {
                    ivIcon.setImageResource(R.drawable.car_down_right);
                } else {
                    ivIcon.setImageResource(R.drawable.bus_down_right);
                }
                tvInfo.setRotation(90);
                ivIcon.setLayoutParams(params);
                break;
            case 2: //left
                if (isCar) {
                    ivIcon.setImageResource(R.drawable.car_left);
                } else {
                    ivIcon.setImageResource(R.drawable.bus_left);
                }
                tvFrom.setText(mArrivalTime);
                tvTo.setText(mDepartureTime);
                tvTo.setClickable(true);
                tvFrom.setClickable(false);

                break;
            case 3: //down-left
                if (isCar) {
                    ivIcon.setImageResource(R.drawable.car_down_left);
                } else {
                    ivIcon.setImageResource(R.drawable.bus_down_left);
                }
                tvInfo.setRotation(-90);
                break;
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        ivIcon.setLayoutParams(layoutParams);
        ivIcon.getLayoutParams().height = 120;
        ivIcon.getLayoutParams().width = 80;
    }

    public void setTvInfo(String strInfo) {
        this.tvInfo.setText(strInfo);
    }

    public ImageView getIvIcon() {
        return ivIcon;
    }

    public Travel getTravel() {
        return travel;
    }

    public TextView getTvInfo() {
        return tvInfo;
    }

    public void setTvInfo(TextView tvInfo) {
        this.tvInfo = tvInfo;
    }

    public enum TransportViewType {
        BUS_LEFT, BUS_RIGHT, BUS_DOWN_RIGHT, BUS_DOWN_LEFT, CAR_LEFT, CAR_DOWN_RIGHT, CAR_DOWN_LEFT, CAR_RIGHT
    }


    //TODO later will add the priority transport according to time, distance and cost.
    private void initLayoutOut(Travel travel, TransportViewType viewType) {
        //Calculate travel information
        String strInfo = "";
        Transport transport = travel.getTransports().get("bus01");

        //Select the icon
        switch (viewType) {
            case BUS_RIGHT:
                ivIcon.setImageResource(R.drawable.bus_right);
                transport = travel.getTransports().get("bus01");
                break;
            case BUS_DOWN_RIGHT:
                ivIcon.setImageResource(R.drawable.bus_down_right);
                transport = travel.getTransports().get("bus01");
                break;
            case BUS_DOWN_LEFT:
                ivIcon.setImageResource(R.drawable.bus_down_left);
                transport = travel.getTransports().get("bus01");
                break;

            case BUS_LEFT:
                ivIcon.setImageResource(R.drawable.bus_left);
                transport = travel.getTransports().get("bus01");
                break;

            case CAR_RIGHT:
                ivIcon.setImageResource(R.drawable.car_right);
                transport = travel.getTransports().get("car01");
                break;
            case CAR_DOWN_RIGHT:
                ivIcon.setImageResource(R.drawable.car_down_right);
                transport = travel.getTransports().get("car01");
                break;
            case CAR_DOWN_LEFT:
                ivIcon.setImageResource(R.drawable.car_down_left);
                transport = travel.getTransports().get("car01");
                break;
            case CAR_LEFT:
                ivIcon.setImageResource(R.drawable.car_left);
                transport = travel.getTransports().get("car01");
                break;
            //TODO will add more for other transports.
        }

        //strInfo = transport.getCost() + "/" + transport.getTime() + "/" + transport.getDistance();
        //tvInfo.setText(strInfo);
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public TextView getTvTo() {
        return tvTo;
    }

    public void setTvTo(TextView tvTo) {
        this.tvTo = tvTo;
    }

    public void setIvIcon(ImageView ivIcon) {
        this.ivIcon = ivIcon;
    }

    public View getvLine() {
        return vLine;
    }

    public void setvLine(View vLine) {
        this.vLine = vLine;
    }

    public TextView getTvFrom() {
        return tvFrom;
    }

    public void setTvFrom(TextView tvFrom) {
        this.tvFrom = tvFrom;
    }
}
