package com.example.volunity.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volunity.Adapter.ActivityAdapter2;

import com.example.volunity.Database_config.Activity.ActivityHelper;
import com.example.volunity.Database_config.City.CityHelper;
import com.example.volunity.Database_config.Favorite.FavoriteDBContract;
import com.example.volunity.Database_config.Favorite.FavoriteHelper;
import com.example.volunity.Database_config.Province.ProvinceHelper;
import com.example.volunity.Models.Activity;
import com.example.volunity.R;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class VolunteerFavFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private int userId;

    private RecyclerView rvFavActivities;
    private ActivityAdapter2 activityAdapter;
    private ActivityHelper activityHelper;
    private FavoriteHelper favoriteHelper;
    private CityHelper cityHelper;
    private ProvinceHelper provinceHelper;

    public VolunteerFavFragment() {}

    public static VolunteerFavFragment newInstance(int userId) {
        VolunteerFavFragment fragment = new VolunteerFavFragment();
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
            favoriteHelper = FavoriteHelper.getInstance(getContext());
            cityHelper = CityHelper.getInstance(getContext());
            provinceHelper = ProvinceHelper.getInstance(getContext());
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_volunteer_fav, container, false);

        rvFavActivities = new RecyclerView(getContext());
        rvFavActivities.setId(View.generateViewId());
        rvFavActivities.setLayoutManager(new LinearLayoutManager(getContext()));
        ((ViewGroup) view).addView(rvFavActivities);

        activityAdapter = new ActivityAdapter2(getContext(), cityHelper, provinceHelper, userId);
        rvFavActivities.setAdapter(activityAdapter);

        loadFavoriteActivities();

        return view;
    }

    private void loadFavoriteActivities() {
        ArrayList<com.example.volunity.Models.Activity> favActivities = new ArrayList<>();
        Cursor favCursor = null;

        try {
            favoriteHelper.open();
            favCursor = favoriteHelper.queryByUserId(userId);

            if (favCursor != null && favCursor.moveToFirst()) {
                do {
                    int activityId = favCursor.getInt(favCursor.getColumnIndexOrThrow(FavoriteDBContract.FavoriteColumns.ACTIVITIES_ID));
                    // Query activity detail by activityId
                    activityHelper.open();
                    Cursor actCursor = activityHelper.search(activityId);
                    if (actCursor != null && actCursor.moveToFirst()) {
                        int id = actCursor.getInt(actCursor.getColumnIndexOrThrow(com.example.volunity.Database_config.Activity.ActivityDBContract.ActivityColumns._ID));
                        int organizerId = actCursor.getInt(actCursor.getColumnIndexOrThrow(com.example.volunity.Database_config.Activity.ActivityDBContract.ActivityColumns.ORGANIZER_ID));
                        String image = actCursor.getString(actCursor.getColumnIndexOrThrow(com.example.volunity.Database_config.Activity.ActivityDBContract.ActivityColumns.IMAGE));
                        String title = actCursor.getString(actCursor.getColumnIndexOrThrow(com.example.volunity.Database_config.Activity.ActivityDBContract.ActivityColumns.TITLE));
                        String address = actCursor.getString(actCursor.getColumnIndexOrThrow(com.example.volunity.Database_config.Activity.ActivityDBContract.ActivityColumns.ADDRESS));
                        int cityId = actCursor.getInt(actCursor.getColumnIndexOrThrow(com.example.volunity.Database_config.Activity.ActivityDBContract.ActivityColumns.CITY_ID));
                        int provinceId = actCursor.getInt(actCursor.getColumnIndexOrThrow(com.example.volunity.Database_config.Activity.ActivityDBContract.ActivityColumns.PROVINCE_ID));

                        LocalDate date = null;
                        String dateString = actCursor.getString(actCursor.getColumnIndexOrThrow(com.example.volunity.Database_config.Activity.ActivityDBContract.ActivityColumns.DATE));
                        if (dateString != null) {
                            try {
                                date = LocalDate.parse(dateString);
                            } catch (DateTimeParseException e) {
                                Log.e("VolunteerFavFragment", "Error parsing date: " + dateString + " for activity ID: " + id, e);
                            }
                        }

                        Integer maxPeople = null;
                        if (!actCursor.isNull(actCursor.getColumnIndexOrThrow(com.example.volunity.Database_config.Activity.ActivityDBContract.ActivityColumns.MAX_PEOPLE))) {
                            maxPeople = actCursor.getInt(actCursor.getColumnIndexOrThrow(com.example.volunity.Database_config.Activity.ActivityDBContract.ActivityColumns.MAX_PEOPLE));
                        }

                        String description = actCursor.getString(actCursor.getColumnIndexOrThrow(com.example.volunity.Database_config.Activity.ActivityDBContract.ActivityColumns.DESCRIPTION));
                        String category = actCursor.getString(actCursor.getColumnIndexOrThrow(com.example.volunity.Database_config.Activity.ActivityDBContract.ActivityColumns.CATEGORY));

                        Timestamp createdAt = null;
                        String createdAtString = actCursor.getString(actCursor.getColumnIndexOrThrow(com.example.volunity.Database_config.Activity.ActivityDBContract.ActivityColumns.CREATED_AT));
                        if (createdAtString != null) {
                            try {
                                createdAt = Timestamp.valueOf(createdAtString);
                            } catch (IllegalArgumentException e) {
                                Log.e("VolunteerFavFragment", "Error parsing createdAt: " + createdAtString + " for activity ID: " + id, e);
                            }
                        }

                        Timestamp updatedAt = null;
                        String updatedAtString = actCursor.getString(actCursor.getColumnIndexOrThrow(com.example.volunity.Database_config.Activity.ActivityDBContract.ActivityColumns.UPDATED_AT));
                        if (updatedAtString != null) {
                            try {
                                updatedAt = Timestamp.valueOf(updatedAtString);
                            } catch (IllegalArgumentException e) {
                                Log.e("VolunteerFavFragment", "Error parsing updatedAt: " + updatedAtString + " for activity ID: " + id, e);
                            }
                        }

                        com.example.volunity.Models.Activity activity = new com.example.volunity.Models.Activity(
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
                        favActivities.add(activity);
                        actCursor.close();
                    }
                } while (favCursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("VolunteerFavFragment", "Error loading favorite activities: " + e.getMessage(), e);
        } finally {
            if (favCursor != null) favCursor.close();
        }

        activityAdapter.setData(favActivities);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activityHelper != null) activityHelper.open();
        if (favoriteHelper != null) favoriteHelper.open();
        if (cityHelper != null) cityHelper.open();
        if (provinceHelper != null) provinceHelper.open();
        loadFavoriteActivities();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (activityHelper != null) activityHelper.close();
        if (favoriteHelper != null) favoriteHelper.close();
        if (cityHelper != null) cityHelper.close();
        if (provinceHelper != null) provinceHelper.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (activityHelper != null && activityHelper.isOpen()) activityHelper.close();
        if (favoriteHelper != null && favoriteHelper.isOpen()) favoriteHelper.close();
        if (cityHelper != null && cityHelper.isOpen()) cityHelper.close();
        if (provinceHelper != null && provinceHelper.isOpen()) provinceHelper.close();
    }
}