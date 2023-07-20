package com.example.networks.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.networks.Admin.Navigation.About.about_fragment;
import com.example.networks.Admin.Navigation.Home.home_fragment;
import com.example.networks.Admin.Navigation.Profile.profile_fragment;
import com.example.networks.R;
import com.example.networks.Utility;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class admin_home extends AppCompatActivity {
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_home);

        // Sets The ActionBar Style
        Utility.activityActionBar("Home", Objects.requireNonNull(getSupportActionBar()), this);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // If Admin Is Null -> Move To Login
            startActivity(new Intent(getApplicationContext(), com.example.networks.Admin.admin_login.class));
            this.finish();
        }

        // Home Bottom Navigation
        ((BottomNavigationView) findViewById(R.id.bottomNavigationView)).setOnItemSelectedListener(item -> {
            Fragment fragment;
            if (item.getItemId() == R.id.admin_navigation_home)
                fragment = new home_fragment();
            else if (item.getItemId() == R.id.admin_navigation_about)
                fragment = new about_fragment();
            else if (item.getItemId() == R.id.admin_navigation_profile)
                fragment = new profile_fragment();
            else
                return false;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, Objects.requireNonNull(fragment)).commit();
            return true;
        });


        // Sets The Drawer Layout
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.start, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        // Navigation Drawer
        ((NavigationView) findViewById(R.id.navigation_drawer)).setNavigationItemSelectedListener(item -> {
            Fragment fragment;
            if (item.getItemId() == R.id.admin_navigation_home)
                fragment = new home_fragment();
            else if (item.getItemId() == R.id.admin_navigation_about)
                fragment = new about_fragment();
            else if (item.getItemId() == R.id.admin_navigation_profile)
                fragment = new profile_fragment();
            else
                return false;

            int selectedItemId;
            if (fragment instanceof home_fragment)
                selectedItemId = R.id.admin_navigation_home;
            else if (fragment instanceof about_fragment)
                selectedItemId = R.id.admin_navigation_about;
            else
                selectedItemId = R.id.admin_navigation_profile;


            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            Menu menu = bottomNavigationView.getMenu();
            if (selectedItemId != -1) {
                MenuItem selectedItem = menu.findItem(selectedItemId);
                selectedItem.setChecked(true);
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, Objects.requireNonNull(fragment)).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        actionBarDrawerToggle.onOptionsItemSelected(item);
        return true;
    }
}