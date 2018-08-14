package com.cewit.fm1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cewit.fm1.models.Accommodation;
import com.cewit.fm1.models.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantCustomListView extends ArrayAdapter<Restaurant> {
    private List<Restaurant> dataSet;
    public List<Restaurant> starSet;
    private Activity context;

    private Restaurant restaurant;
    RestaurantCustomListView.ViewHolder viewHolder;

    public RestaurantCustomListView(Activity context, List<Restaurant> dataSet, List<Restaurant> starSet){
        super(context, R.layout.restaurant_custom_list_view, dataSet);
        this.context = context;
        this.dataSet = dataSet;
        this.starSet = starSet;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get ViewHolder
        View r = convertView;
        ViewHolder viewHolder = null;
        if (r==null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.restaurant_custom_list_view,null,true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder); //store view
        } else {
            viewHolder = (ViewHolder) r.getTag(); //get the store view
        }
        if(position<dataSet.size()) {
            restaurant = dataSet.get(position);

            // RESTAURANT NAME
            viewHolder.tvName.setText(restaurant.getName());
            viewHolder.tvName.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(restaurant.getSite())));
                }
            });

            //RESTAURANT ADDRESS
            viewHolder.tvLocation.setText(restaurant.getAddress());
            viewHolder.tvLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gMapUri = Uri.parse("geo:" + restaurant.getLat() + "," + restaurant.getLng() + "?q=" + restaurant.getAddress());
                    Intent gMapIntent = new Intent(Intent.ACTION_VIEW, gMapUri);
                    gMapIntent.setPackage("com.google.android.apps.maps");
                    context.startActivity(gMapIntent);
                }
            });

            // RESTAURANT IMAGE
            //TEMPORARY CONVERSION TO STRING OF ID
            int hotelID = context.getResources().getIdentifier(restaurant.getId(), "drawable", context.getPackageName());
            viewHolder.ivImage.setImageResource(hotelID);
            viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(restaurant.getSite())));
                }
            });

            // RESTAURANT NUMBER
            viewHolder.tvNumber.setText(restaurant.getContact());
            viewHolder.tvNumber.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("Tel", restaurant.getContact(), null)));
                }
            });

            //RESTAURANT CUISINE +   RESTAURANT OPEN/CLOSE
            viewHolder.tvCuisine.setText(restaurant.getResType() + ", " + restaurant.getOpenTime() + "~" + restaurant.getCloseTime());

            // RESTAURANT SELECT BUTTON
            viewHolder.btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, MainActivity.class));
                    // TODO Change this so it sends proper info back to where it needs to go
                }
            });
            // RESTAURANT SELECT FAVORITE

            viewHolder.tvFav.setClickable(true);
//            viewHolder.tvFav
            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.tvFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("yay");
                    if(starSet.contains(restaurant)){
                        starSet.remove(restaurant);
                        restaurant.setFavorite(false);
                        finalViewHolder.tvFav.setBackgroundResource(R.drawable.star_blank);
                        Toast.makeText(context, "Unstarred: " + restaurant.getName(), Toast.LENGTH_SHORT).show();

                    }
                    else{
                        starSet.add(restaurant);
                        restaurant.setFavorite(true);
                        finalViewHolder.tvFav.setBackgroundResource(R.drawable.star_filled);
                        Toast.makeText(context, "Starred: " + restaurant.getName(), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
        return r;
    }

    class ViewHolder{
        TextView tvName;
        TextView tvNumber;
        ImageView ivImage;
        TextView tvCuisine;
        TextView tvLocation;
        Button btnSelect;
        ImageButton tvFav;
        // Add whatever other things you added into xml

        ViewHolder(View v) {
            tvName = v.findViewById(R.id.tvName);
            tvNumber = v.findViewById(R.id.tvNumber);
            ivImage = v.findViewById(R.id.ivImage);
            btnSelect = v.findViewById(R.id.btnSelect);
            tvCuisine = v.findViewById(R.id.tvCuisine);
            tvLocation = v.findViewById(R.id.tvLocation);
            tvFav = v.findViewById(R.id.tvFav);
        }
    }

}
