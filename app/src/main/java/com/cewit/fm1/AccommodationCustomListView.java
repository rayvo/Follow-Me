package com.cewit.fm1;

import android.app.Activity;
import android.content.Context;
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

import java.util.List;

/**
 * Created by Taeyu Im on 18. 8. 14.
 * qvo@cs.stonybrook.edu
 */

public class AccommodationCustomListView extends ArrayAdapter<Accommodation> {
    private List<Accommodation> dataSet;
    private List<Accommodation> favorites;
    private Activity context;

    public AccommodationCustomListView(@NonNull Activity context, List<Accommodation> dataSet, List<Accommodation> favorites) {
        super(context, R.layout.accom_custom_list_view, dataSet);
        this.context = context;
        this.dataSet = dataSet;
        this.favorites = favorites;
    }

    private Accommodation accom;
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

        accom = dataSet.get(position);

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
            viewHolder.ivAccomStar.setImageResource(R.drawable.star_blank);
            if (favorites.contains(accom)) {
                favorites.remove(accom);
            }
        } else {
            viewHolder.ivAccomStar.setImageResource(R.drawable.star_filled);
            if (!favorites.contains(accom)) {
                favorites.add(accom);
            }
        }

        viewHolder.ivAccomStar.setClickable(true);
        viewHolder.ivAccomStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favorites.contains(accom)) {
                    favorites.remove(accom);
                    accom.setFavorite(false);
                    ivhf.setImageResource(R.drawable.star_blank);
                } else {
                    favorites.add(accom);
                    accom.setFavorite(true);
                    ivhf.setImageResource(R.drawable.star_filled);
                }
            }
        });

        // TODO when changing context send back proper info
        viewHolder.btnAccomSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MainActivity.class)); //TODO will return to the view tour activity.
            }
        });


        return r;
    }


    class ViewHolder {
        TextView tvAccomName;
        TextView tvAccomAddress;
        TextView tvAccomNumber;
        TextView tvAccomType;
        ImageView ivAccomImage;
        ImageView ivAccomStar;
        Button btnAccomSelect;

        ViewHolder(View v) {
            tvAccomName = v.findViewById(R.id.tvAccomName);
            tvAccomAddress = v.findViewById(R.id.tvAccomAddress);
            tvAccomNumber = v.findViewById(R.id.tvAccomNumber);
            tvAccomType = v.findViewById(R.id.tvAccomType);
            ivAccomImage = v.findViewById(R.id.ivAccomImage);
            ivAccomStar = v.findViewById(R.id.ivAccomStar);
            btnAccomSelect = v.findViewById(R.id.btnAccomSelect);
        }
    }

}
