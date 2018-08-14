package com.cewit.fm1.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.cewit.fm1.MainActivity;
import com.cewit.fm1.models.City;
import com.cewit.fm1.models.Country;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Taeyu Im on 18. 7. 19.
 * qvo@cs.stonybrook.edu
 */

public class DBHelper {
    private static final String TAG = DBHelper.class.getName();

    private List<City> cities = new ArrayList<City>();


}
