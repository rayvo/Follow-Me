package com.cewit.fm1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cewit.fm1.models.City;
import com.cewit.fm1.models.Country;
import com.cewit.fm1.models.Place;
import com.cewit.fm1.models.Tour;
import com.cewit.fm1.models.Travel;
import com.cewit.fm1.models.User;
import com.cewit.fm1.util.ActivityHelper;
import com.cewit.fm1.util.DBHelper;
import com.cewit.fm1.util.DatePickerFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private DatabaseReference refCountries;

    List<Country> countries;
    List<City> cities;
    String countryId;

    //Layout
    Spinner spiCountries, spiCities;

    private ArrayAdapter<String> adCountry;
    private TableLayout tblInput;

    private Button btnSearch, btnAddCity, btnRemoveCity;

    String cityId;


    String[] arrCities;
    HashMap<String, String> hasCities;
    private View vEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //View Preparation
        spiCities = (Spinner) findViewById(R.id.spiCities);
        spiCountries = (Spinner) findViewById(R.id.spiCountries);
        tblInput = (TableLayout) findViewById(R.id.tblInput);

        tvStartDate = (TextView) findViewById(R.id.tvStartDate);
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                tvStartDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        tvStartTime = (TextView) findViewById(R.id.tvStartTime);
        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
// Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                tvStartTime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });


        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchTourResultActivity.class);
                intent.putExtra(ActivityHelper.CITY_ID, cityId);
                startActivity(intent);
            }
        });

        btnAddCity = (Button) findViewById(R.id.btnAddCity);
        btnAddCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<String> adCity = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, arrCities);
                adCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                vEmpty = new View(MainActivity.this);
                btnRemoveCity.setVisibility(TableLayout.VISIBLE);

                TableRow newRow = new TableRow(MainActivity.this);
                newRow.addView(vEmpty);

                Spinner spiAddedCity = new Spinner(MainActivity.this);
                spiAddedCity.setAdapter(adCity);
                spiAddedCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        cityId = hasCities.get(spiCities.getSelectedItem());
                        Log.d(TAG, "Selected cityId: " + cityId);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                newRow.addView(spiAddedCity);
                Button btnAddNewCity = new Button(MainActivity.this);
                btnAddNewCity.setText("+");
                Button btnRemoveNewCity = new Button(MainActivity.this);
                btnRemoveNewCity.setText("-");
                newRow.addView(btnAddNewCity);
                newRow.addView(btnRemoveNewCity);
                tblInput.addView(newRow);
            }
        });


        btnRemoveCity = (Button) findViewById(R.id.btnRemoveCity);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("greeting");

        countries = new ArrayList<Country>();
        refCountries = FirebaseDatabase.getInstance().getReference("countries");
        refCountries.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                countries.clear();
                for (DataSnapshot countrySnapshot : dataSnapshot.getChildren()) {
                    Country country = countrySnapshot.getValue(Country.class);
                    countries.add(country);
                }

                String[] spiarrCountries = new String[countries.size()];
                final HashMap<String, String> spihasCountries = new HashMap<String, String>();
                for (int i = 0; i < countries.size(); i++) {
                    Country country = countries.get(i);
                    spihasCountries.put(country.getName(), country.getId());
                    spiarrCountries[i] = country.getName();
                }
                //Set value to the spinner
                adCountry = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, spiarrCountries);
                adCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spiCountries.setAdapter(adCountry);
                int defCountry = adCountry.getPosition("KR");
                spiCountries.setSelection(defCountry);
                spiCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        countryId = spihasCountries.get(spiCountries.getSelectedItem());
                        Log.d(TAG, "Selected CountryId: " + countryId);
                        cities = new ArrayList<City>();
                        spiCities.setAdapter(null);
                        Log.i(TAG, "DUYVO: " + " countryId: " + countryId + " size: " + cities.size());
                        DatabaseReference refCities = FirebaseDatabase.getInstance().getReference("cities");
                        refCities.orderByChild("countryId").equalTo(countryId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                cities.clear();
                                for (DataSnapshot countrySnapshot : dataSnapshot.getChildren()) {
                                    City city = countrySnapshot.getValue(City.class);
                                    Log.i(TAG, "DUYVO: " + " city-countryID: " + city.getCountryId());
                                    cities.add(city);
                                }

                                //Update
                                if (cities.size() == 0) {
                                    Toast.makeText(MainActivity.this, "Cannot find available city in this country", Toast.LENGTH_LONG);
                                } else {
                                    arrCities = new String[cities.size()];
                                    hasCities = new HashMap<String, String>();
                                    for (int i = 0; i < cities.size(); i++) {
                                        City city = cities.get(i);
                                        hasCities.put(city.getName(), city.getId());
                                        arrCities[i] = city.getName();
                                    }

                                    ArrayAdapter<String> adCity = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, arrCities);
                                    adCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spiCities.setAdapter(adCity);
                                    spiCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            cityId = hasCities.get(spiCities.getSelectedItem());
                                            Log.d(TAG, "Selected cityId: " + cityId);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private TextView tvStartDate, tvStartTime;
    private int mYear, mMonth, mDay, mHour, mMinute;

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}
