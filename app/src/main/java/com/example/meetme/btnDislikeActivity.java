package com.example.meetme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class btnDislikeActivity extends AppCompatActivity
{

    private static final String TAG = "BtnDislikeActivity";
    private static final int ACTIVITY_NUM = 1;
    private Context mContext = btnDislikeActivity.this;
    private ImageView dislike;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_btn_dislike);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupTopNavigationView();
        dislike = findViewById(R.id.btnDislikeMain);

        Intent intent = getIntent();
        String profileUrl = intent.getStringExtra("url");

        switch (profileUrl)
        {
            case "default":
                Glide.with(mContext).load(R.drawable.profile_icon).into(dislike);
                break;
            default:
                Glide.with(mContext).load(profileUrl).into(dislike);
                break;
        }

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(2000);
                    Intent i = new Intent(btnDislikeActivity.this, MainActivity.class);
                    //i.putExtra("activity_num", ACTIVITY_NUM);
                    startActivity(i);
                    //finish();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void setupTopNavigationView()
    {
        BottomNavigationView tvNv = findViewById(R.id.topNavViewBar);
        TopNavigationViewHelper.setupTopNavigationView(tvNv);
        TopNavigationViewHelper.enableNavigation(mContext, tvNv);
        Menu menu = tvNv.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    public void DislikeBtn(View view)
    {

    }
}