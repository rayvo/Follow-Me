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

import com.cewit.fm1.models.Tour;
import com.cewit.fm1.util.Utility;

import java.util.List;
import java.util.Random;

/**
 * Created by Taeyu Im on 18. 5. 6.
 * qvo@cs.stonybrook.edu
 */

public class SearchTourResultCustomListView extends ArrayAdapter<Tour> {
    private List<Tour> dataSet;
    private Activity context;

    public SearchTourResultCustomListView(Activity context, List<Tour> dataSet) {
        super(context, R.layout.search_tour_result_listview, dataSet);
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
            r = layoutInflater.inflate(R.layout.search_tour_result_listview,null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder); //store view
        } else {
            viewHolder = (ViewHolder) r.getTag(); //get the store view
        }
        Tour tour = dataSet.get(position);

        int imageId = R.drawable.jeju_island;

        String strImageId = "jeju003";

            Random rand = new Random();
            int ra = rand.nextInt() % 3;
            switch (ra) {
                case 0:
                    strImageId = "jeju001";
                    break;
                case 1:
                    strImageId = "jeju002";
                    break;
                case 2:
                    strImageId = "jeju000";
                    break;
                case 3:
                    strImageId = "jeju004";
                    break;
            }
        if (context.getResources().getIdentifier(strImageId, "drawable", context.getPackageName()) != 0) {
            imageId =context.getResources().getIdentifier(strImageId, "drawable", context.getPackageName());
        }
        viewHolder.ivTourImage.setImageResource(imageId);


        if(tour.getImageIds() != null && tour.getImageIds().size()>0) {
//             viewHolder.ivTourImage.setImageResource(Integer.parseInt(tour.getImageIds().get(0))); //TODO will be checked later
            viewHolder.ivTourImage.setImageResource(R.drawable.jeju_island);
        }
        viewHolder.tvTourName.setText(tour.getName());
        viewHolder.tvTourDescription.setText(tour.getInfo());
        String tourSummary = Utility.formatDistance(tour.getTotalDistance()) + "/" + Utility.formatTime(tour.getTotalTime());
        viewHolder.tvTourSummary.setText(tourSummary);
        return r;

    }


    // View lookup cache
     class ViewHolder{
        ImageView ivTourImage;
        TextView tvTourName;
        TextView tvTourDescription;
        TextView tvTourSummary;

        ViewHolder(View v) {
            tvTourName=(TextView) v.findViewById(R.id.tvTourName);
            tvTourDescription=(TextView) v.findViewById(R.id.tvDescription);
            ivTourImage = (ImageView) v.findViewById(R.id.ivTourImage);
            tvTourSummary = (TextView) v.findViewById(R.id.tvSummary);
        }
    }

}
