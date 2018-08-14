package com.cewit.fm1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.cewit.fm1.models.Accommodation;

import java.util.ArrayList;
import java.util.List;

public class AccommodationListActivity extends AppCompatActivity {

    private String TAG = AccommodationListActivity.class.getSimpleName();

    List<Accommodation> hotelSamples;
    public List<Accommodation> hotelStarredList;
    ListView list;
    Button btnViewStarredOrAll;
    Spinner spnGu;
    Spinner spnType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accom_list);


        createHotelLists();

        initItems();

        //readHotelData();

        setListAdapter();

        setSpnGuAdapter();
        setSpnTypeAdapter();

        setListeners();

        updateListToAll();
        System.out.println("?????????????");

    }

    public void createHotelLists() {
        hotelSamples = new ArrayList<>();
        hotelStarredList = new ArrayList<>();
    }

    public void initItems() {
        spnGu = findViewById(R.id.spnGu);
        spnType = findViewById(R.id.spnType);
        list = findViewById(R.id.lvHotelList);
        btnViewStarredOrAll = findViewById(R.id.btnViewStarredOrAll);

    }

    public void setListAdapter() {
        AccommodationCustomListView customListView = new AccommodationCustomListView(this, hotelSamples, hotelStarredList);
        list.setAdapter(customListView);
    }

    //TODO Get actual city/province data elsewhere instead of manually inputting it
    public void setSpnGuAdapter() {
        String[] temp = new String[]{
                "Select Province", "연수구", "중구", "남동구"
        };
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, temp);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGu.setAdapter(a);
    }

    //TODO Get actual type data elsewhere instead of manually inputting it
    public void setSpnTypeAdapter() {
        String[] temp = new String[]{
                "Select Type", "Hotel", "Guest House", "Motel"
        };
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, temp);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnType.setAdapter(a);
    }

    public void setListeners() {
        btnViewStarredOrAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnViewStarredOrAll.getText().equals("View Starred Items")) {
                    btnViewStarredOrAll.setText("View All Items");
                    updateListToStarred();
                } else {
                    btnViewStarredOrAll.setText("View Starred Items");
                    updateListToAll();
                }
            }
        });

        spnGu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = spnGu.getSelectedItem().toString();
                updateListToFilters(selectedItem, spnType.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = spnType.getSelectedItem().toString();
                updateListToFilters(spnGu.getSelectedItem().toString(), selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public void updateListToStarred() {
        List<Accommodation> temp = new ArrayList<>();
        temp.addAll(hotelStarredList);

        hotelStarredList.clear();
        AccommodationCustomListView customListView = new AccommodationCustomListView(this, temp, hotelStarredList);
        list.setAdapter(customListView);
    }

    public void updateListToAll() {
        hotelStarredList.clear();
        AccommodationCustomListView customListView = new AccommodationCustomListView(this, hotelSamples, hotelStarredList);
        list.setAdapter(customListView);
    }

    public void updateListToFilters(String s, String t) {
        List<Accommodation> temp = new ArrayList<>();
        if (s.equals("Select Province") && t.equals("Select Type")) {
            if (btnViewStarredOrAll.getText().equals("View Starred Items")) {
                updateListToAll();
            } else {
                updateListToStarred();
            }
        } else if (s.equals("Select Province")) {
            for (int i = 0; i < hotelSamples.size(); i++) {
                if (hotelSamples.get(i).getType().equals(t)) {
                    temp.add(hotelSamples.get(i));
                }
            }
            hotelStarredList.clear();
            AccommodationCustomListView customListView = new AccommodationCustomListView(this, temp, hotelStarredList);
            list.setAdapter(customListView);
        } else if (t.equals("Select Type")) {
            for (int i = 0; i < hotelSamples.size(); i++) {
                if (hotelSamples.get(i).getAddress().contains(s)) {
                    temp.add(hotelSamples.get(i));
                }
            }
            hotelStarredList.clear();
            AccommodationCustomListView customListView = new AccommodationCustomListView(this, temp, hotelStarredList);
            list.setAdapter(customListView);
        } else {
            for (int i = 0; i < hotelSamples.size(); i++) {
                if (hotelSamples.get(i).getAddress().contains(s) && hotelSamples.get(i).getType().equals(t)) {
                    temp.add(hotelSamples.get(i));
                }
            }
            hotelStarredList.clear();
            AccommodationCustomListView customListView = new AccommodationCustomListView(this, temp, hotelStarredList);
            list.setAdapter(customListView);
        }
    }

    public void filterCurrentList(/**SOMETHING**/) {

    }

    /*
     * Currently reading from csv file.
     */
    /*private void readHotelData() {

        InputStream is = getResources().openRawResource(R.raw.hotels);
        BufferedReader br = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );

        String line;
        try {
            while ((line = br.readLine()) != null) {

                String[] tokens = line.split(",");

                Accommodation sample = new Accommodation(
                        tokens[0],
                        tokens[1],
                        tokens[2],
                        tokens[3],
                        tokens[4],
                        tokens[5],
                        tokens[6],
                        tokens[7],
                        tokens[8]
                );

                hotelSamples.add(sample);

            }
        } catch (IOException e) {
            Log.wtf("HotelListActivity", "Error reading hotels.csv file", e);
            e.printStackTrace();
        }
    }*/
}






