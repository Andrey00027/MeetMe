package com.example.meetme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;


public class TopNavigationViewHelper
{

    private static final String TAG = "TopNavigationViewHelper";

    public static void setupTopNavigationView(BottomNavigationView tv)
    {
        Log.d(TAG, "setupTopNavigationView: Setting up top navigation view");
    }

    public static void enableNavigation(final Context context , BottomNavigationView view)
    {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.ic_profile) {
                    if (!(context instanceof SettingsActivity)) {
                        Intent intent_Profile = new Intent(context, SettingsActivity.class);
                        context.startActivity(intent_Profile);
                    }
                    return true;
                } else if (itemId == R.id.ic_matched) {
                    if (!(context instanceof MatchedActivity)) {
                        Intent intent_Matched = new Intent(context, MatchedActivity.class);
                        context.startActivity(intent_Matched);
                    }
                    return true;
                } else if (itemId == R.id.ic_home) {
                    if (!(context instanceof MainActivity)) {
                        Intent intent_Home = new Intent(context, MainActivity.class);
                        context.startActivity(intent_Home);
                    }
                    return true;
                }
                return false;
            }
        });
    }



}
