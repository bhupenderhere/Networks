package com.example.networks.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.networks.R;
import com.example.networks.Utility;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class user_home extends AppCompatActivity {
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;

    private ViewPager2 viewPager;
    private final String[] labels = new String[]{"Notice", "eBook", "Gallery", "Faculty"};

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
        setContentView(R.layout.user_home);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // If User Is Null -> Move To Login
            startActivity(new Intent(getApplicationContext(), user_login.class));
            finish();
        }

        // Sets The ActionBar Style
        Utility.activityActionBar("Networks", Objects.requireNonNull(getSupportActionBar()), this);

        init();

        new TabLayoutMediator(findViewById(R.id.tab_layout), findViewById(R.id.view_pager), (tab, position) -> tab.setText(labels[position])).attach();

        viewPager.setCurrentItem(0, true);

        // Sets The Drawer Layout
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.start, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        // Navigation Drawer
        ((NavigationView) findViewById(R.id.navigation_drawer)).setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.user_navigation_profile) {
                startActivity(new Intent(this, com.example.networks.User.Activities.user_profile.class));
                this.finish();
            } else
                return false;

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        actionBarDrawerToggle.onOptionsItemSelected(item);
        return true;
    }

    private void init() {
        viewPager = findViewById(R.id.view_pager);
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(this);
        viewPager.setAdapter(adapter);
    }

    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new com.example.networks.User.Navigation.Notice.notice_fragment();
                case 1:
                    return new com.example.networks.User.Navigation.Ebook.ebook_fragment();
                case 2:
                    return new com.example.networks.User.Navigation.Gallery.gallery_fragment();
                case 3:
                    return new com.example.networks.User.Navigation.Faculty.faculty_fragment();
            }
            return new com.example.networks.User.Navigation.Notice.notice_fragment();
        }

        @Override
        public int getItemCount() {
            return labels.length;
        }
    }
}