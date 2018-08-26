package com.cewit.fm1;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cewit.fm1.models.Place;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Taeyu Im on 18. 5. 6.
 * qvo@cs.stonybrook.edu
 */

public class PlaceCustomListView extends ArrayAdapter<Place> {
    private List<Place> dataSet;
    private Activity context;

    public PlaceCustomListView(Activity context, List<Place> dataSet) {
        super(context, R.layout.place_listview, dataSet);
        this.context = context;
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r = convertView;
        ViewHolder viewHolder = null;
        if (r==null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.place_listview,null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder); //store view
        } else {
            viewHolder = (ViewHolder) r.getTag(); //get the store view
        }
        Place place = dataSet.get(position);
        if (place.getImageIds() != null && place.getImageIds().size() > 0) viewHolder.ivPlaceImage.setImageResource(place.getImageIds().get(0)); //First photo
        viewHolder.tvPlaceName.setText(place.getName());
        viewHolder.tvPlaceInfo.setText(place.getInfo());
        String placeSummary = "Contact: " + place.getContact() + "\nRate: " + place.getRate()
                + "\nDistance: TBD";
        viewHolder.tvSummary.setText(placeSummary);
        return r;

    }



    // View lookup cache
     class ViewHolder {
        TextView tvPlaceName;
        TextView tvPlaceInfo;
        TextView tvSummary;
        ImageView ivPlaceImage;
        ViewHolder(View v) {
            tvPlaceName=(TextView) v.findViewById(R.id.tvPlaceName);
            tvPlaceInfo=(TextView) v.findViewById(R.id.tvPlaceInfo);
            ivPlaceImage = (ImageView) v.findViewById(R.id.ivPlaceImage);
            tvSummary = (TextView) v.findViewById(R.id.tvSummary);
        }
    }

}
