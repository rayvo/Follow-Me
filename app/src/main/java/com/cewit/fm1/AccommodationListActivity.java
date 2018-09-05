package com.cewit.fm1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import com.cewit.fm1.models.Accommodation;
import com.cewit.fm1.models.Tour;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.channels.AcceptPendingException;
import java.util.ArrayList;
import java.util.List;

/*
 * Haseung has successfully got the code to work on his computer.
 */

public class AccommodationListActivity extends AppCompatActivity {

    private String TAG = AccommodationListActivity.class.getSimpleName();

    public List<Accommodation> hotelSamples;
    //public List<Accommodation> hotelStarredList;
    ListView list;
    Button btnViewStarredOrAll;
    Spinner spnGu;
    Spinner spnType;
    Switch sGPS;

    Accommodation forDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accom_list);

        hotelSamples = new ArrayList<>();
        //hotelStarredList = new ArrayList<>();

        spnGu = findViewById(R.id.spnGu);
        spnType = findViewById(R.id.spnType);
        list = findViewById(R.id.lvHotelList);
        btnViewStarredOrAll = findViewById(R.id.btnViewStarredOrAll);
        sGPS = findViewById(R.id.sGPS);


        // Set list adapter
        AccommodationCustomListView customListView = new AccommodationCustomListView(this, hotelSamples,  sGPS.isChecked());// hotelStarredList,
        list.setAdapter(customListView);

        // Set spnGu adapter
        // TODO Properly set spnGu data elsewhere instead of manually inputting it
        String[] temp = new String[]{
                "View All", "서귀포시"
        };
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, temp);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGu.setAdapter(a);

        // Set spnType adapter
        // TODO Properly set spnType data elsewhere instead of manually inputting it
        temp = new String[]{
                "View All", "Hotel"
        };
        ArrayAdapter<String> a1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, temp);
        a1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(a1);

        // Set Listeners
        btnViewStarredOrAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnViewStarredOrAll.getText().equals("View Starred Items")) {
                    btnViewStarredOrAll.setText("View All Items");
                    filterCurrentList( true, spnGu.getSelectedItem().toString(), spnType.getSelectedItem().toString(), sGPS.isChecked() );
                } else {
                    btnViewStarredOrAll.setText("View Starred Items");
                    filterCurrentList( false, spnGu.getSelectedItem().toString(), spnType.getSelectedItem().toString(), sGPS.isChecked() );
                }
            }
        });

        spnGu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = spnGu.getSelectedItem().toString();
                if( btnViewStarredOrAll.getText().equals("View Starred Items") ){
                    filterCurrentList(false, selectedItem, spnType.getSelectedItem().toString(), sGPS.isChecked());
                } else {
                    filterCurrentList(true, selectedItem, spnType.getSelectedItem().toString(), sGPS.isChecked());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = spnType.getSelectedItem().toString();
                if (btnViewStarredOrAll.getText().equals("View Starred Items")) {
                    filterCurrentList(false, spnGu.getSelectedItem().toString(), selectedItem, sGPS.isChecked());
                } else {
                    filterCurrentList(true, spnGu.getSelectedItem().toString(), selectedItem, sGPS.isChecked());
    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sGPS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked ) {
                filterCurrentList(false, spnGu.getSelectedItem().toString(), spnType.getSelectedItem().toString(), isChecked);
            }
        });

        readAccomData();
        //filterCurrentList(false, spnGu.getSelectedItem().toString(), spnType.getSelectedItem().toString(), sGPS.isChecked());

    }


    public void filterCurrentList(boolean fav, String s, String t, boolean isChecked ){

        AccommodationCustomListView customListView = null;
        List<Accommodation> temp = new ArrayList<>();

        if( !fav ){
            if( s.equals("View All") && t.equals("View All") ) {
                customListView = new AccommodationCustomListView(this, hotelSamples,  isChecked );// hotelStarredList,
            } else if( s.equals("View All") ) {
                for (int i = 0; i < hotelSamples.size(); i++) {
                    if (hotelSamples.get(i).getType().equals(t)) {
                        temp.add(hotelSamples.get(i));
                    }
                }
                customListView = new AccommodationCustomListView(this, temp, isChecked); //hotelStarredList,
            } else if( t.equals("View All") ){
                for( int i = 0; i < hotelSamples.size(); i++ ){
                    if( hotelSamples.get(i).getAddress().contains(s) ){
                        temp.add(hotelSamples.get(i));
                    }
                }
                customListView = new AccommodationCustomListView(this, temp, isChecked ); //hotelStarredList,
            } else {
                for( int i = 0; i < hotelSamples.size(); i++ ){
                    if( hotelSamples.get(i).getAddress().contains(s) && hotelSamples.get(i).getType().equals(t) ){
                        temp.add(hotelSamples.get(i));
                    }
                }
                customListView = new AccommodationCustomListView(this, temp, isChecked );// hotelStarredList,
            }
        } else {
            if( s.equals("View All") && t.equals("View All") ) {
                for(int i = 0; i < hotelSamples.size(); i++){
                    if( hotelSamples.get(i).isFavorite() ){
                        temp.add(hotelSamples.get(i));
                    }
                }
                //temp.addAll(hotelStarredList);
                customListView = new AccommodationCustomListView(this, temp, isChecked ); //hotelStarredList,
            } else if( s.equals("View All") ) {
                for (int i = 0; i < hotelSamples.size(); i++) {
                    if (hotelSamples.get(i).isFavorite() && hotelSamples.get(i).getType().equals(t)) {
                        temp.add(hotelSamples.get(i));
                    }
                }
                customListView = new AccommodationCustomListView(this, temp, isChecked ); //hotelStarredList,
            } else if( t.equals("View All") ){
                for( int i = 0; i < hotelSamples.size(); i++ ){
                    if(hotelSamples.get(i).isFavorite() && hotelSamples.get(i).getAddress().contains(s) ){
                        temp.add(hotelSamples.get(i));
                    }
                }
                customListView = new AccommodationCustomListView(this, temp, isChecked ); // hotelStarredList, isChecked );
            } else {
                for( int i = 0; i < hotelSamples.size(); i++ ){
                    if( hotelSamples.get(i).isFavorite() && hotelSamples.get(i).getAddress().contains(s) && hotelSamples.get(i).getType().equals(t) ){
                        temp.add(hotelSamples.get(i));
                    }
                }
                customListView = new AccommodationCustomListView(this, temp, isChecked ); // hotelStarredList, isChecked );
            }
        }

        list.setAdapter(customListView);

    }

    private void readAccomData() {

        //Data Preparation
        DatabaseReference refPlaces = FirebaseDatabase.getInstance().getReference("places");
        refPlaces.orderByChild("type").equalTo("Accommodation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot accomSnapshot : dataSnapshot.getChildren()) {
                    forDB = accomSnapshot.getValue(Accommodation.class);
                    if(forDB !=null){
                        hotelSamples.add(forDB);
                    }
                }
                AccommodationCustomListView customListView = new AccommodationCustomListView(AccommodationListActivity.this, hotelSamples, sGPS.isChecked()); //hotelStarredList, sGPS.isChecked());
                list.setAdapter(customListView);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}






