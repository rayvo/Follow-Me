package com.cewit.fm1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cewit.fm1.models.Accommodation;
import com.cewit.fm1.models.Tour;
import com.cewit.fm1.util.ActivityHelper;

import java.util.List;

/**
 * Created by Taeyu Im on 18. 8. 14.
 * qvo@cs.stonybrook.edu
 */

public class AccommodationCustomListView extends ArrayAdapter<Accommodation> {
    private List<Accommodation> dataSet;
    //private List<Accommodation> favorites;
    private Activity context;
    private boolean isGPSOn;
    int REQUEST_MODE;
//    String cityId;
    String tourId;
    String curPlaceId;



    public AccommodationCustomListView(@NonNull Activity context, List<Accommodation> dataSet,  boolean isGPSOn, int rm, String ti, String cpi) {
        super(context, R.layout.accom_custom_list_view, dataSet);
        this.context = context;
        this.dataSet = dataSet;
        this.isGPSOn = isGPSOn;
        REQUEST_MODE = rm;
        tourId = ti;
        curPlaceId = cpi;
    }

    //private Accommodation accom;
    AccommodationCustomListView.ViewHolder viewHolder;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // set the ViewHolder
        View r = convertView;
        if (r == null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.accom_custom_list_view, null, true);
            viewHolder = new AccommodationCustomListView.ViewHolder(r);
            r.setTag(viewHolder);
        } else {
            viewHolder = (AccommodationCustomListView.ViewHolder) r.getTag();
        }

        final Accommodation accom = dataSet.get(position);

        // Edit viewHolder to have proper Accom Info
        final String strSite = accom.getSite();
        viewHolder.tvAccomName.setText(accom.getName());
        viewHolder.tvAccomName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(strSite)));
            }
        });

        viewHolder.tvAccomAddress.setText(accom.getAddress());
        viewHolder.tvAccomNumber.setText(accom.getContact());
        viewHolder.tvAccomType.setText(accom.getAccType());

        // TODO Properly set up gps and find distance
        viewHolder.tvAccomDistance.setText("XX km");
        if(isGPSOn){
            viewHolder.tvAccomDistance.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvAccomDistance.setVisibility(View.INVISIBLE);
        }

        int accomId = context.getResources().getIdentifier(accom.getId(), "drawable", context.getPackageName());
        viewHolder.ivAccomImage.setImageResource(accomId);
        viewHolder.ivAccomImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gMapUri = Uri.parse("geo:" + accom.getLat() + "," + accom.getLng() + "?q=" + accom.getLat() + "," + accom.getLng() + "(" + accom.getName() + ")");
                Intent gMapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(gMapUri)));
                gMapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(gMapIntent);

            }
        });


        final ImageView ivhf = viewHolder.ivAccomStar;
        if (accom.isFavorite()) {
            viewHolder.ivAccomStar.setImageResource(R.drawable.star_filled);
        } else {
            viewHolder.ivAccomStar.setImageResource(R.drawable.star_blank);
        }

        viewHolder.ivAccomStar.setClickable(true);
        viewHolder.ivAccomStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accom.isFavorite()){ //favorites.contains(accom)) {
                    //favorites.remove(accom);
                    accom.setFavorite(false);
                    ivhf.setImageResource(R.drawable.star_blank);
                } else {
                    //favorites.add(accom);
                    accom.setFavorite(true);
                    ivhf.setImageResource(R.drawable.star_filled);
                }
            }
        });

        // TODO when changing context send back proper info
        final String accomName = dataSet.get(position).getName();
        viewHolder.btnAccomSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Select " + accomName + "?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                Intent intent = new Intent(context, ViewTourActivity.class);
//                                //TODO IMPORTANT Add more intent extras here and go back properly
//                                intent.putExtra("HOTEL_NAME", accomName);
//                                context.startActivity(intent);

                                Intent intent2 = new Intent( context, ViewTourActivity.class);
                                intent2.putExtra(ActivityHelper.REFRESH_MODE, REQUEST_MODE);
                                intent2.putExtra(ActivityHelper.NEW_PLACE_ID, accom.getId());
                                intent2.putExtra(ActivityHelper.TOUR_ID, tourId);
                                intent2.putExtra(ActivityHelper.CUR_PLACE_ID, curPlaceId);
                                context.startActivity(intent2);
                                //context.finish();

                                //dialog.cancel();
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
        TextView tvAccomName;
        TextView tvAccomAddress;
        TextView tvAccomNumber;
        TextView tvAccomType;
        TextView tvAccomDistance;
        ImageView ivAccomImage;
        ImageView ivAccomStar;
        Button btnAccomSelect;

        ViewHolder(View v) {
            tvAccomName = v.findViewById(R.id.tvAccomName);
            tvAccomAddress = v.findViewById(R.id.tvAccomAddress);
            tvAccomNumber = v.findViewById(R.id.tvAccomNumber);
            tvAccomType = v.findViewById(R.id.tvAccomType);
            tvAccomDistance = v.findViewById(R.id.tvAccomDistance);
            ivAccomImage = v.findViewById(R.id.ivAccomImage);
            ivAccomStar = v.findViewById(R.id.ivAccomStar);
            btnAccomSelect = v.findViewById(R.id.btnAccomSelect);
        }
    }

}
