package com.cewit.fm1;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
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

import com.cewit.fm1.models.Coordinate;
import com.cewit.fm1.models.Restaurant;
import com.cewit.fm1.util.ActivityHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantCustomListView extends ArrayAdapter<Restaurant> {
    private List<Restaurant> dataSet;
    private List<Restaurant> starSet;
    private Activity context;
    private Coordinate cords;

    int REQUEST_MODE;
    String tourId;
    String curPlaceId;

    public RestaurantCustomListView(Activity context, List<Restaurant> dataSet, List<Restaurant> starSet, Coordinate co, int rm, String tI, String cI){
        super(context, R.layout.restaurant_custom_list_view, dataSet);
        this.context = context;
        this.dataSet = dataSet;
        this.starSet = starSet;
        this.cords = co;
        this.REQUEST_MODE = rm;
        this.tourId = tI;
        this.curPlaceId = cI;
    }

//    private Restaurant res;
//    RestaurantListView.ViewHolder viewHolder;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //set the ViewHolder
        View r = convertView;
        final ViewHolder viewHolder;
        final Restaurant res = dataSet.get(position);
        if (r==null) {
            LayoutInflater layoutInflater = context.getLayoutInflater();
            r = layoutInflater.inflate(R.layout.restaurant_custom_list_view,null,true);
            viewHolder = new RestaurantCustomListView.ViewHolder(r);
            r.setTag(viewHolder); //store view
        } else {
            viewHolder = (RestaurantCustomListView.ViewHolder) r.getTag(); //get the store view
        }
        // ----------------------------- ADDING INFORMATION TO CUSTOM LIST VIEW ----------------------------- //

        // RESTAURANT NAME
        final String SITELINK = res.getSite();
        viewHolder.tvName.setText(res.getName());
        viewHolder.tvName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SITELINK)));
            }
        });

        //RESTAURANT ADDRESS
        viewHolder.tvLocation.setText(res.getAddress());
        long temp = res.getLng();

        final String hotelLng = Long.toString(res.getLng());
        final String hotelLat = Long.toString(res.getLat());
        final String hotelAdd = res.getAddress();
        viewHolder.tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gMapUri = Uri.parse("geo:" + hotelLat + "," + hotelLng + "?q=" + hotelAdd);
                Intent gMapIntent = new Intent(Intent.ACTION_VIEW, gMapUri);
                gMapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(gMapIntent);
            }
        });

        // RESTAURANT IMAGE
        final String imageID = res.getId();
        int hotelID = context.getResources().getIdentifier(imageID, "drawable", context.getPackageName());
        viewHolder.ivImage.setImageResource(hotelID);
        viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(SITELINK)));
            }
        });

        // RESTAURANT NUMBER
        final String PHONELINK = res.getContact();
        viewHolder.tvNumber.setText(res.getContact());
        viewHolder.tvNumber.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", PHONELINK, null)));
            }
        });

        //RESTAURANT CUISINE
        final String CUISINELINK = res.getResType();
        viewHolder.tvCuisine.setText(CUISINELINK);

        //RESTAURANT TIME
        final String TIMELINK = res.getOpenTime() + "~" + res.getCloseTime();
        viewHolder.tvTime.setText(TIMELINK);

        //RESTAURANT DISTANCE
        if(cords.getLongitude() == 0.0 && cords.getLatitude() == 0.0){
            viewHolder.tvCords.setText("");
        }
        else {
            double distance = distance(cords.getLatitude(), cords.getLongitude(), (double) res.getLat(), (double) res.getLng(), "K");
            distance = (double)Math.round(distance);

            viewHolder.tvCords.setText(Double.toString(distance/1000) + "km away");
        }

        //RESTAURANT SELECT BUTTON
        viewHolder.btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.restaurant_dialog);
                dialog.setTitle("Confirm Your Selection");

                TextView dResName = (TextView) dialog.findViewById(R.id.resName);
                TextView dResAddress = (TextView) dialog.findViewById(R.id.resAddress);
                TextView dResType = (TextView) dialog.findViewById(R.id.resType);
                TextView dResNumber = (TextView) dialog.findViewById(R.id.resNumber);
                TextView dResTime = (TextView) dialog.findViewById(R.id.resTime);
                ImageView dResImage = (ImageView) dialog.findViewById(R.id.resImage);

                dResName.setText(res.getName());
                dResAddress.setText(res.getAddress());
                dResType.setText(res.getResType());
                dResNumber.setText(res.getContact());
                dResTime.setText(res.getOpenTime() + "~" + res.getCloseTime());
                final String imageID = res.getId();
                int resID = context.getResources().getIdentifier(imageID, "drawable", context.getPackageName());
                dResImage.setImageResource(resID);

                Button confirm = (Button) dialog.findViewById(R.id.resConfirm);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        context.startActivity(new Intent(context, MainActivity.class));
                        Intent intent2 = new Intent( context, ViewTourActivity.class);
                        intent2.putExtra(ActivityHelper.REFRESH_MODE, REQUEST_MODE);
                        intent2.putExtra(ActivityHelper.NEW_PLACE_ID, res.getId());
                        intent2.putExtra(ActivityHelper.TOUR_ID, tourId);
                        intent2.putExtra(ActivityHelper.CUR_PLACE_ID, curPlaceId);
                        context.startActivity(intent2);
                    }
                });

                Button cancel = (Button) dialog.findViewById(R.id.resCancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                dialog.show();
            }
        });

        // RESTAURANT SELECT FAVORITE
        if(res.isFavorite()==false){
            viewHolder.ivFav.setImageResource(R.drawable.star_blank);
        }
        else {
            viewHolder.ivFav.setImageResource(R.drawable.star_filled);
        }

        viewHolder.ivFav.setClickable(true);
        viewHolder.ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(res.getName());
                System.out.println(Arrays.toString(new List[]{starSet}));
                if(starSet.contains(res)){
                    starSet.remove(res);
                    res.setFavorite(false);
                    viewHolder.ivFav.setImageResource(R.drawable.star_blank);
                    Toast.makeText(context, "Unstarred: " + res.getName(), Toast.LENGTH_SHORT).show();
                }
                else{
                    starSet.add(res);
                    res.setFavorite(true);
                    viewHolder.ivFav.setImageResource(R.drawable.star_filled);
                    Toast.makeText(context, "Starred: " + res.getName(), Toast.LENGTH_SHORT).show();
                }
                System.out.println(res.getName());
                System.out.println(Arrays.toString(new List[]{starSet}));
            }
        });

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

    class ViewHolder{
        TextView tvName;
        TextView tvNumber;
        ImageView ivImage;
        TextView tvTime;
        TextView tvCuisine;
        TextView tvLocation;
        Button btnSelect;
        ImageView ivFav;
        TextView tvCords;
        // Add whatever other things you added into xml

        ViewHolder(View v) {
            tvName = v.findViewById(R.id.tvResName);
            tvNumber = v.findViewById(R.id.tvResNumber);
            ivImage = v.findViewById(R.id.ivResImage);
            btnSelect = v.findViewById(R.id.btnResSelect);
            tvTime = v.findViewById(R.id.tvResTime);
            tvCuisine = v.findViewById(R.id.tvResType);
            tvLocation = v.findViewById(R.id.tvResAddress);
            ivFav = v.findViewById(R.id.ivResStar);
            tvCords = v.findViewById(R.id.tvResDistance);
            // Whatever you add above add here
        }
    }

}

