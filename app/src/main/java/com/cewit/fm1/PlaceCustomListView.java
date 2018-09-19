package com.cewit.fm1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cewit.fm1.models.Accommodation;
import com.cewit.fm1.models.Place;
import com.cewit.fm1.util.ActivityHelper;

import java.util.List;

/**
 * Created by Taeyu Im on 18. 8. 14.
 * qvo@cs.stonybrook.edu
 */

public class PlaceCustomListView extends ArrayAdapter<Place> {

    private List<Place> dataSet;
    private Activity context;
    private boolean isGPSOn;
    int REQUEST_MODE;
    //    String cityId;
    String tourId;
    String curPlaceId;
    String strStartTime;



    public PlaceCustomListView(@NonNull Activity context, List<Place> dataSet,  boolean isGPSOn, int mRequestMode, String mTourId, String mCurPlaceId, String mStartTime) {
        super(context, R.layout.place_custom_list_view, dataSet);
        this.context = context;
        this.dataSet = dataSet;
        this.isGPSOn = isGPSOn;
        this.REQUEST_MODE = mRequestMode;
        this.tourId = mTourId;
        this.curPlaceId = mCurPlaceId;
        this.strStartTime = mStartTime;
    }

    //private Place;
    PlaceCustomListView.ViewHolder viewHolder;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // set the ViewHolder
        View r = convertView;
        if (r == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.place_custom_list_view, null, true);
            viewHolder = new PlaceCustomListView.ViewHolder(r);
            r.setTag(viewHolder);
        } else {
            viewHolder = (PlaceCustomListView.ViewHolder) r.getTag();
        }

        final Place place = dataSet.get(position);

        // Edit viewHolder to have proper Accom Info
        final String strSite = place.getSite();
        viewHolder.tvPlaceName.setText(place.getName());
        viewHolder.tvPlaceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(strSite)));
            }
        });


        viewHolder.tvPlaceNumber.setText(place.getContact());

        if(place.getType().equals("Restaurant")) {
            viewHolder.tvPlaceType.setText(place.getResType());
        }
        else if(place.getType().equals("Accommodation")){
            viewHolder.tvPlaceType.setText(place.getAccType());
        }
        else{
            viewHolder.tvPlaceType.setText(place.getType());
        }

        final String hotelLng = Long.toString(place.getLng());
        final String hotelLat = Long.toString(place.getLat());
        final String hotelAdd = place.getAddress();
        viewHolder.tvPlaceAddress.setText(place.getAddress());
        viewHolder.tvPlaceAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gMapUri = Uri.parse("geo:" + hotelLat + "," + hotelLng + "?q=" + hotelAdd);
                Intent gMapIntent = new Intent(Intent.ACTION_VIEW, gMapUri);
                gMapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(gMapIntent);
            }
        });

        int placeId = context.getResources().getIdentifier(place.getId(), "drawable", context.getPackageName());
        viewHolder.ivPlaceImage.setImageResource(placeId);
        viewHolder.ivPlaceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(strSite)));
            }
        });


        final ImageView ivhf = viewHolder.ivPlaceStar;
        if (place.isFavorite()) {
            viewHolder.ivPlaceStar.setImageResource(R.drawable.star_filled);
        } else {
            viewHolder.ivPlaceStar.setImageResource(R.drawable.star_blank);
        }

        viewHolder.ivPlaceStar.setClickable(true);
        viewHolder.ivPlaceStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (place.isFavorite()){ //favorites.contains(place)) {
                    //favorites.remove(place);
                    place.setFavorite(false);
                    ivhf.setImageResource(R.drawable.star_blank);
                    Toast.makeText(context, "Unstarred: " + place.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    //favorites.add(place);
                    place.setFavorite(true);
                    ivhf.setImageResource(R.drawable.star_filled);
                    Toast.makeText(context, "Starred: " + place.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // TODO when changing context send back proper info
        final String placeName = dataSet.get(position).getName();
        viewHolder.btnPlaceSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Select " + placeName + "?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(context, ViewTourActivity.class);
                                intent.putExtra(ActivityHelper.REFRESH_MODE, REQUEST_MODE);
                                intent.putExtra(ActivityHelper.NEW_PLACE_ID, place.getId());
                                intent.putExtra(ActivityHelper.CUR_PLACE_ID, curPlaceId);
                                intent.putExtra(ActivityHelper.TOUR_ID, tourId);
                                intent.putExtra(ActivityHelper.START_TIME, strStartTime);
                                context.startActivity(intent);
                                context.finish();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

        // TODO Properly set up gps and find distance
        double tempLat = 37.375307;
        double tempLng = 126.66802800000005;
        double distance = distance(tempLat, tempLng, (double) place.getLat(), (double) place.getLng(), "K");
        distance = (double)Math.round(distance);

        viewHolder.tvPlaceDistance.setText(Double.toString(distance/1000) + "km");
        if(isGPSOn){
            viewHolder.tvPlaceDistance.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvPlaceDistance.setVisibility(View.INVISIBLE);
        }

        return r;
    }

    //unit is "k"
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
            dist = dist * 1000;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    class ViewHolder {
        TextView tvPlaceName;
        TextView tvPlaceAddress;
        TextView tvPlaceNumber;
        TextView tvPlaceType;
        TextView tvPlaceDistance;
        ImageView ivPlaceImage;
        ImageView ivPlaceStar;
        Button btnPlaceSelect;

        ViewHolder(View v) {
            tvPlaceName = v.findViewById(R.id.tvPlaceName);
            tvPlaceAddress = v.findViewById(R.id.tvPlaceAddress);
            tvPlaceNumber = v.findViewById(R.id.tvPlaceNumber);
            tvPlaceType = v.findViewById(R.id.tvPlaceType);
            tvPlaceDistance = v.findViewById(R.id.tvPlaceDistance);
            ivPlaceImage = v.findViewById(R.id.ivPlaceImage);
            ivPlaceStar = v.findViewById(R.id.ivPlaceStar);
            btnPlaceSelect = v.findViewById(R.id.btnPlaceSelect);
        }
    }
}
