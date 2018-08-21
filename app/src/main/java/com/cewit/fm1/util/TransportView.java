package com.cewit.fm1.util;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cewit.fm1.R;
import com.cewit.fm1.models.Transport;
import com.cewit.fm1.models.Travel;

/**
 * Created by Taeyu Im on 18. 7. 26.
 * qvo@cs.stonybrook.edu
 */

public class TransportView extends LinearLayout {
    private static final String TAG = TransportView.class.getName();


    LinearLayout layoutTransport;
    private Travel travel;
    private TextView tvInfo;
    private TextView tvFrom;
    private TextView tvTo;
    private ImageView ivIcon;
    private View vLine;

    /* viewDirection
    0: to right
    1: down right
    2: to left
    3: down left
     */

    public TransportView(Context context, Travel travel, int viewDirection, boolean isCar) {
        super(context, null);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_tour_transport_item, this, true);

        layoutTransport = (LinearLayout) getChildAt(0);
        ivIcon = (ImageView) getChildAt(0).findViewById(R.id.ivIcon);
        tvFrom = (TextView) getChildAt(0).findViewById(R.id.tvFrom);
        tvTo = (TextView) getChildAt(0).findViewById(R.id.tvTo);
        tvInfo = (TextView) getChildAt(0).findViewById(R.id.tvInfo);
        vLine = (View) getChildAt(0).findViewById(R.id.vLine);

        //setOrientation(LinearLayout.VERTICAL);
        //setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutParams params ;
        switch (viewDirection) {
            case 0: //right
                //Nothing changed
                params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                params.setMargins(0, 0, 0, 0);

                layoutTransport.setOrientation(LinearLayout.VERTICAL);
                layoutTransport.setGravity(Gravity.CENTER_HORIZONTAL);
                layoutTransport.setLayoutParams(params);
                break;
            case 1: //down-right
                params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                params.setMargins(0, 0, 0, 0);

                layoutTransport.setGravity(Gravity.CENTER_VERTICAL);
                layoutTransport.setOrientation(LinearLayout.HORIZONTAL);
                layoutTransport.setLayoutParams(params);

                if (tvInfo.getParent() != null) {
                    ((ViewGroup) tvInfo.getParent()).removeView(tvInfo);
                }
                if (vLine.getParent() != null) {
                    ((ViewGroup) vLine.getParent()).removeView(vLine);
                }
                if (ivIcon.getParent() != null) {
                    ((ViewGroup) ivIcon.getParent()).removeView(ivIcon);
                }
                float scale = getContext().getResources().getDisplayMetrics().density;
                int tvInfoWidthPixels = (int) (40 * scale + 0.5f);
                LayoutParams tvInfoParams = new LayoutParams(tvInfoWidthPixels, LayoutParams.MATCH_PARENT);

                tvInfo.setHorizontallyScrolling(false);
                tvInfo.setSingleLine(false);
                tvInfo.setLayoutParams(tvInfoParams);

                layoutTransport.addView(tvInfo);

                int lineViewPixels = (int) (3 * scale + 0.5f); //convert from 3dp
                LayoutParams vLineParams = new LayoutParams(lineViewPixels, LayoutParams.MATCH_PARENT);
                params.setMargins(0, 0, 0, 0);
                vLine.setLayoutParams(vLineParams);
                layoutTransport.addView(vLine);

                layoutTransport.addView(ivIcon);
                break;
            case 2: //left
                params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                params.setMargins(0, 0, 0, 0);

                layoutTransport.setOrientation(LinearLayout.VERTICAL);
                layoutTransport.setGravity(Gravity.CENTER_HORIZONTAL);
                layoutTransport.setLayoutParams(params);
                break;
            case 3: //down-left
                params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                layoutTransport.setGravity(Gravity.CENTER_VERTICAL);
                layoutTransport.setOrientation(LinearLayout.HORIZONTAL);

                if (tvInfo.getParent() != null) {
                    ((ViewGroup) tvInfo.getParent()).removeView(tvInfo);
                }
                if (vLine.getParent() != null) {
                    ((ViewGroup) vLine.getParent()).removeView(vLine);
                }
                if (ivIcon.getParent() != null) {
                    ((ViewGroup) ivIcon.getParent()).removeView(ivIcon);
                }

                layoutTransport.addView(ivIcon);

                scale = getContext().getResources().getDisplayMetrics().density;
                lineViewPixels = (int) (3 * scale + 0.5f); //convert from 3dp
                vLineParams = new LayoutParams(lineViewPixels, LayoutParams.MATCH_PARENT);
                params.setMargins(0, 0, 0, 0);
                vLine.setLayoutParams(vLineParams);
                layoutTransport.addView(vLine);

                tvInfoWidthPixels = (int) (40 * scale + 0.5f);
                tvInfoParams = new LayoutParams(tvInfoWidthPixels, LayoutParams.MATCH_PARENT);
                tvInfoParams.setMargins(0,0,-50,0);
                tvInfo.setHorizontallyScrolling(false);
                tvInfo.setSingleLine(false);
                tvInfo.setLayoutParams(tvInfoParams);
                layoutTransport.addView(tvInfo);
        }

        if (travel != null) {
            this.travel = travel;
            TransportViewType viewType = null;
            if (isCar) {
                switch (viewDirection) {
                    case 0: //right
                        viewType = TransportViewType.CAR_RIGHT;
                        break;
                    case 1: //down-right
                        viewType = TransportViewType.CAR_DOWN_RIGHT;
                        break;
                    case 2: //left
                        viewType = TransportViewType.CAR_LEFT;
                        break;
                    case 3: //down-left
                        viewType = TransportViewType.CAR_DOWN_LEFT;
                        break;
                }
            } else { //bus
                switch (viewDirection) {
                    case 0: //right
                        viewType = TransportViewType.BUS_RIGHT;
                        break;
                    case 1: //down-right
                        viewType = TransportViewType.BUS_DOWN_RIGHT;
                        break;
                    case 2: //left
                        viewType = TransportViewType.BUS_LEFT;
                        break;
                    case 3: //down-left
                        viewType = TransportViewType.BUS_DOWN_LEFT;
                        break;
                }
            }
            initLayoutOut(travel, viewType);
        } else {
            Log.i(TAG, "Received a null value of place.");
        }
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
}
