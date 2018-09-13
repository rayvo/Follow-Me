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

import com.cewit.fm1.models.Accommodation;
import com.cewit.fm1.models.Place;
import com.cewit.fm1.util.ActivityHelper;

import java.util.List;

/**
 * Created by Taeyu Im on 18. 8. 14.
 * qvo@cs.stonybrook.edu
 */

public class PlaceCustomListView extends ArrayAdapter<Place> {
    private static final String TAG = PlaceCustomListView.class.getName();

    private int REQUEST_MODE = 0;
    private List<Place> dataSet;
    private List<Place> favorites;
    private Activity context;
    private boolean isGPSOn;
    private String tourId;
    private String curPlaceId;
    private String strStartTime;



    public PlaceCustomListView(@NonNull Activity context, List<Place> dataSet, List<Place> favorites, boolean isGPSOn, int mRequestMode, String mTourId, String mCurPlaceId, String mStartTime) {
        super(context, R.layout.place_custom_list_view, dataSet);
        this.context = context;
        this.dataSet = dataSet;
        this.favorites = favorites;
        this.isGPSOn = isGPSOn;
        this.REQUEST_MODE = mRequestMode;
        this.tourId = mTourId;
        this.curPlaceId = mCurPlaceId;
        this.strStartTime = mStartTime;
    }

    private Place place;
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

        place = dataSet.get(position);

        // Edit viewHolder to have proper Place Info
        final String strSite = place.getSite();
        viewHolder.tvPlaceName.setText(place.getName());
        viewHolder.tvPlaceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(strSite)));
            }
        });

        viewHolder.tvPlaceAddress.setText(place.getAddress());
        viewHolder.tvPlaceNumber.setText(place.getContact());
        viewHolder.tvPlaceType.setText(place.getAccType());

        // TODO Properly set up gps and find distance
        viewHolder.tvPlaceDistance.setText("XX km");
        if(isGPSOn){
            viewHolder.tvPlaceDistance.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvPlaceDistance.setVisibility(View.INVISIBLE);
        }

        int accomId = context.getResources().getIdentifier(place.getId(), "drawable", context.getPackageName());
        try{
            //viewHolder.ivPlaceImage.setImageResource(accomId);
        } catch (Exception ex) {
            Log.e(TAG,ex.toString());
        }
        viewHolder.ivPlaceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gMapUri = Uri.parse("geo:" + place.getLat() + "," + place.getLng() + "?q=" + place.getLat() + "," + place.getLng() + "(" + place.getName() + ")");
                Intent gMapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(gMapUri)));
                gMapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(gMapIntent);

            }
        });


        final ImageView ivhf = viewHolder.ivPlaceStar;
        if (place.isFavorite()) {
            viewHolder.ivPlaceStar.setImageResource(R.drawable.star_filled);
            if (!favorites.contains(place)) {
                favorites.add(place);
            }
        } else {
            viewHolder.ivPlaceStar.setImageResource(R.drawable.star_blank);
            if (favorites.contains(place)) {
                favorites.remove(place);
            }
        }

        viewHolder.ivPlaceStar.setClickable(true);
        viewHolder.ivPlaceStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favorites.contains(place)) {
                    favorites.remove(place);
                    place.setFavorite(false);
                    ivhf.setImageResource(R.drawable.star_blank);
                } else {
                    favorites.add(place);
                    place.setFavorite(true);
                    ivhf.setImageResource(R.drawable.star_filled);
                }
            }
        });

        // TODO when changing context send back proper info
        final String placeId = place.getId();
        final String placeName = place.getName();
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
                                intent.putExtra(ActivityHelper.NEW_PLACE_ID, placeId);
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

                //context.startActivity(new Intent(context, MainActivity.class)); //TODO will return to the view tour activity.
            }
        });


        return r;
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
