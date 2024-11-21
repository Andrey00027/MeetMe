package com.example.meetme;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button like, dislike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        like = findViewById(R.id.likeBtn);
        dislike = findViewById(R.id.dislikeBtn);

        like.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_animation));
            Toast.makeText(MainActivity.this, "Like pressed", Toast.LENGTH_SHORT).show();
        });

        dislike.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_animation));
            Toast.makeText(MainActivity.this, "Dislike pressed", Toast.LENGTH_SHORT).show();
        });
    }

    public void dislikeBtn(View view) {
    }
}