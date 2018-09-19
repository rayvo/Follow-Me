package com.cewit.fm1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cewit.fm1.models.Place;
import com.cewit.fm1.models.Tour;
import com.cewit.fm1.util.ActivityHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Taeyu Im on 18. 9. 14.
 * qvo@cs.stonybrook.edu
 */

public class ViewPlaceActivity extends AppCompatActivity {

    private static final String TAG = ViewPlaceActivity.class.getName();

    private Intent intent;
    private String placeId;
    private String tourId;
    private String cityId;
    private String startTime;

    private TextView tvTitle;
    private TextView tvAddress;
    private TextView tvInfo;
    private TextView tvNumber;
    private TextView tvType;
    private ImageView ivImage;
    private Button btnSkip;
    private Button btnChange;
    private  Button btnExit;
    private Place place;

    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_place);

        context = this.getApplicationContext();
        intent = this.getIntent();
        tourId = intent.getStringExtra(ActivityHelper.TOUR_ID);
        cityId = intent.getStringExtra(ActivityHelper.CITY_ID);
        placeId = intent.getStringExtra(ActivityHelper.PLACE_ID);
        startTime = intent.getStringExtra(ActivityHelper.START_TIME);

        tvTitle = findViewById(R.id.tvName);
        tvInfo = findViewById(R.id.tvInfo);
        tvAddress = findViewById(R.id.tvAddress);
        tvNumber = findViewById(R.id.tvNumber);
        tvType = findViewById(R.id.tvType);
        ivImage = findViewById(R.id.ivImage);

        btnSkip = findViewById(R.id.btnSkip);
        btnChange = findViewById(R.id.btnChange);
        btnExit = findViewById(R.id.btnExit);

        //Data Preparation
        DatabaseReference refPlaces = FirebaseDatabase.getInstance().getReference("places");
        refPlaces.orderByChild("id").equalTo(placeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot tourSnapshot : dataSnapshot.getChildren()) {
                     place = tourSnapshot.getValue(Place.class);
                    if (place != null) {
                        tvTitle.setText(place.getName());
                        tvTitle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(place.getSite())));
                            }
                        });
                        //tvInfo.setText(place.getInfo());

//                        ivImage.setImageResource(R.drawable.jeju000);
                        int imageId = R.mipmap.ic_launcher;
                        if (context.getResources().getIdentifier(place.getId(), "drawable", context.getPackageName()) != 0) {
                            imageId =context.getResources().getIdentifier(place.getId(), "drawable", context.getPackageName());
                        }
                        ivImage.setImageResource(imageId);
                        ivImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(place.getSite())));
                            }
                        });

                        final String hotelLng = Long.toString(place.getLng());
                        final String hotelLat = Long.toString(place.getLat());
                        final String hotelAdd = place.getAddress();
                        tvAddress.setText(place.getAddress());
                        tvAddress.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri gMapUri = Uri.parse("geo:" + hotelLat + "," + hotelLng + "?q=" + hotelAdd);
                                Intent gMapIntent = new Intent(Intent.ACTION_VIEW, gMapUri);
                                gMapIntent.setPackage("com.google.android.apps.maps");
                                context.startActivity(gMapIntent);
                            }
                        });
                        tvNumber.setText(place.getContact());
                        tvType.setText(place.getType());
                        tvInfo.setText(place.getInfo());
                    }
                }
            } //end data change of tour query

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewPlaceActivity.this.getApplicationContext(), ViewTourActivity.class);
                //intent.putExtra(ActivityHelper.PLACE_ID, placeId);
                intent.putExtra(ActivityHelper.TOUR_ID, tourId);
                intent.putExtra(ActivityHelper.START_TIME, startTime);
                startActivity(intent);
                finish();
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewPlaceActivity.this);
                    builder1.setMessage("Would you like to skip this place?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(context, ViewTourActivity.class);
                                    intent.putExtra(ActivityHelper.REFRESH_MODE, ActivityHelper.REFRESH_SKIP);
                                    intent.putExtra(ActivityHelper.TOUR_ID, tourId);
                                    intent.putExtra(ActivityHelper.START_TIME, startTime);
                                    intent.putExtra(ActivityHelper.PLACE_ID, placeId);
                                    startActivity(intent);
                                    finish();
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

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewPlaceActivity.this);
                builder1.setMessage("Would you like to change this place?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String placeType = place.getType();
                                Intent intent;
//                                if (placeType.equalsIgnoreCase("Accommodation")) {
//                                    intent = new Intent(context, AccommodationListActivity.class);
//                                } else if (placeType.equalsIgnoreCase("Restaurant")) {
//                                    intent = new Intent(context, RestaurantListActivity.class);
//                                } else {
//                                    intent = new Intent(context, PlaceSelectionActivity.class);
//                                }
                                intent = new Intent(context, PlaceSelectionActivity.class);
                                intent.putExtra(ActivityHelper.REFRESH_MODE, ActivityHelper.REFRESH_PLACE_CHANGED);
                                intent.putExtra(ActivityHelper.CITY_ID, cityId);
                                intent.putExtra(ActivityHelper.TOUR_ID, tourId);
                                intent.putExtra(ActivityHelper.START_TIME, startTime);
                                intent.putExtra(ActivityHelper.CUR_PLACE_ID, placeId);
                                startActivity(intent);
                                finish();
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
    }
}
