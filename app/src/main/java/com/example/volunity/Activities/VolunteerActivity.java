package com.example.volunity.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.volunity.Fragments.VolunteerFavFragment;
import com.example.volunity.Fragments.VolunteerFragment;
import com.example.volunity.R;
import com.example.volunity.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class VolunteerActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int loggedInUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.bottomNavigation.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.Home){
                    loadFragment(VolunteerFragment.newInstance(loggedInUserId));
                }else if(itemId == R.id.Favorite){
                    loadFragment(VolunteerFavFragment.newInstance(loggedInUserId));
                }
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(binding.fragmentContainer.getId(), fragment);
        fragmentTransaction.commit();
    }
}