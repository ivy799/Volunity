package com.example.volunity.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
// Import other necessary views if you have them, e.g., TextView, ImageView
// import android.widget.TextView;

import com.example.volunity.R;

public class VolunteerProfileFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";
    private int userId;

    public VolunteerProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId The ID of the logged-in user.
     * @return A new instance of fragment VolunteerProfileFragment.
     */
    public static VolunteerProfileFragment newInstance(int userId) {
        VolunteerProfileFragment fragment = new VolunteerProfileFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_volunteer_profile, container, false);

        // You can now use 'userId' in this fragment to load user-specific data,
        // for example, displaying profile information for 'userId'.
        // Example:
        // TextView profileName = view.findViewById(R.id.tv_profile_name);
        // if (profileName != null) {
        //     profileName.setText("Profil Pengguna ID: " + userId);
        // }

        return view;
    }
}