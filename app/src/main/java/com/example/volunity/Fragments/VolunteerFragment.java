package com.example.volunity.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunity.Adapter.ActivityAdapter2;
import com.example.volunity.Database_config.Activity.ActivityHelper;
import com.example.volunity.Database_config.City.CityHelper;
import com.example.volunity.Database_config.Province.ProvinceHelper;
import com.example.volunity.Models.Activity;
import com.example.volunity.R;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import com.example.volunity.Database_config.Activity.ActivityDBContract;


public class VolunteerFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private int userId;
    private RecyclerView rvActivities;
    private ActivityAdapter2 activityAdapter;
    private ActivityHelper activityHelper;
    private CityHelper cityHelper;
    private ProvinceHelper provinceHelper;
    private EditText etSearchActivity;


    public VolunteerFragment() {
        // Required empty public constructor
    }

    public static VolunteerFragment newInstance(int userId) {
        VolunteerFragment fragment = new VolunteerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt(ARG_USER_ID);
        }

        if (getContext() != null) {
            activityHelper = ActivityHelper.getInstance(getContext());
            cityHelper = CityHelper.getInstance(getContext());
            provinceHelper = ProvinceHelper.getInstance(getContext());
        }
    }

    private ArrayList<Activity> allActivities = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer, container, false);

        rvActivities = view.findViewById(R.id.rv_activities);
        rvActivities.setLayoutManager(new LinearLayoutManager(getContext()));

        EditText etSearchActivity = view.findViewById(R.id.etSearchActivity);
        etSearchActivity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterActivities(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        activityAdapter = new ActivityAdapter2(getContext(), cityHelper, provinceHelper, userId);
        rvActivities.setAdapter(activityAdapter);

        loadActivityData();

        return view;
    }


    private void loadActivityData() {
        ArrayList<Activity> activities = new ArrayList<>();
        Cursor activityCursor = null;

        try {
            activityHelper.open();
            activityCursor = activityHelper.queryAll();

            if (activityCursor != null && activityCursor.moveToFirst()) {
                do {
                    int id = activityCursor.getInt(activityCursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns._ID));
                    int organizerId = activityCursor.getInt(activityCursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.ORGANIZER_ID));
                    String image = activityCursor.getString(activityCursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.IMAGE));
                    String title = activityCursor.getString(activityCursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.TITLE));
                    String address = activityCursor.getString(activityCursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.ADDRESS));
                    int cityId = activityCursor.getInt(activityCursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.CITY_ID));
                    int provinceId = activityCursor.getInt(activityCursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.PROVINCE_ID));

                    LocalDate date = null;
                    String dateString = activityCursor.getString(activityCursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.DATE));
                    if (dateString != null) {
                        try {
                            date = LocalDate.parse(dateString);
                        } catch (DateTimeParseException e) {
                            Log.e("VolunteerFragment", "Error parsing date: " + dateString + " for activity ID: " + id, e);
                        }
                    }

                    Integer maxPeople = null;
                    if (!activityCursor.isNull(activityCursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.MAX_PEOPLE))) {
                        maxPeople = activityCursor.getInt(activityCursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.MAX_PEOPLE));
                    }

                    String description = activityCursor.getString(activityCursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.DESCRIPTION));
                    String category = activityCursor.getString(activityCursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.CATEGORY));

                    Timestamp createdAt = null;
                    String createdAtString = activityCursor.getString(activityCursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.CREATED_AT));
                    if (createdAtString != null) {
                        try {
                            createdAt = Timestamp.valueOf(createdAtString);
                        } catch (IllegalArgumentException e) {
                            Log.e("VolunteerFragment", "Error parsing createdAt: " + createdAtString + " for activity ID: " + id, e);
                        }
                    }

                    Timestamp updatedAt = null;
                    String updatedAtString = activityCursor.getString(activityCursor.getColumnIndexOrThrow(ActivityDBContract.ActivityColumns.UPDATED_AT));
                    if (updatedAtString != null) {
                        try {
                            updatedAt = Timestamp.valueOf(updatedAtString);
                        } catch (IllegalArgumentException e) {
                            Log.e("VolunteerFragment", "Error parsing updatedAt: " + updatedAtString + " for activity ID: " + id, e);
                        }
                    }

                    Activity activity = new Activity(
                            id,
                            organizerId,
                            image,
                            title,
                            address,
                            cityId,
                            provinceId,
                            date,
                            maxPeople,
                            description,
                            category,
                            createdAt,
                            updatedAt
                    );
                    activities.add(activity);

                } while (activityCursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("VolunteerFragment", "Error loading activity data: " + e.getMessage(), e);
        } finally {
            if (activityCursor != null) {
                activityCursor.close();
            }
        }


        allActivities.clear();
        allActivities.addAll(activities);


        if (etSearchActivity != null && etSearchActivity.getText() != null && etSearchActivity.getText().length() > 0) {
            filterActivities(etSearchActivity.getText().toString());
        } else {
            activityAdapter.setData(activities);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activityHelper != null) activityHelper.open();
        if (cityHelper != null) cityHelper.open();
        if (provinceHelper != null) provinceHelper.open();
        loadActivityData();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (activityHelper != null) activityHelper.close();
        if (cityHelper != null) cityHelper.close();
        if (provinceHelper != null) provinceHelper.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (activityHelper != null && activityHelper.isOpen()) activityHelper.close();
        if (cityHelper != null && cityHelper.isOpen()) cityHelper.close();
        if (provinceHelper != null && provinceHelper.isOpen()) provinceHelper.close();
    }

    private void filterActivities(String text) {
        ArrayList<Activity> filteredList = new ArrayList<>();
        for (Activity activity : allActivities) {
            if (activity.getTitle() != null && activity.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(activity);
            } else if (activity.getCategory() != null && activity.getCategory().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(activity);
            }
        }
        activityAdapter.setData(filteredList);
    }
}