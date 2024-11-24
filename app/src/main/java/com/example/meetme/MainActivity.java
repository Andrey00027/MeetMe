package com.example.meetme;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import ru.dimorinny.showcasecard.ShowCaseView;
import ru.dimorinny.showcasecard.position.ShowCasePosition;
import ru.dimorinny.showcasecard.position.ViewPosition;
import ru.dimorinny.showcasecard.radius.Radius;

public class MainActivity extends AppCompatActivity {

    Boolean firstStart;

    ImageButton like, dislike;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupTopNavigationView();

        like = findViewById(R.id.likeBtn);
        dislike = findViewById(R.id.dislikeBtn);

        like.setOnClickListener(v -> {

            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_animation));

            Drawable dislikeDrawable = dislike.getDrawable(); // Get the current drawable of the button
            if (dislikeDrawable != null) {
                dislikeDrawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.black), PorterDuff.Mode.SRC_IN);
            }

            // Change the color of the icon
            Drawable drawable = like.getDrawable(); // Get the current drawable of the button
            if (drawable != null) {
                drawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.smoke_red), PorterDuff.Mode.SRC_IN);
            }

            Toast.makeText(MainActivity.this, "Like pressed", Toast.LENGTH_SHORT).show();
        });

        dislike.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_animation));

            Drawable likeDrawable = like.getDrawable(); // Get the current drawable of the button
            if (likeDrawable != null) {
                likeDrawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.black), PorterDuff.Mode.SRC_IN);
            }

            Drawable drawable = dislike.getDrawable(); // Get the current drawable of the button
            if (drawable != null) {
                drawable.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.smoke_red), PorterDuff.Mode.SRC_IN);
            }

            Toast.makeText(MainActivity.this, "Dislike pressed", Toast.LENGTH_SHORT).show();
        });


    }

    private void setupTopNavigationView() {
        BottomNavigationView tvNv = findViewById(R.id.topNavViewBar);
        TopNavigationViewHelper.setupTopNavigationView(tvNv);
        TopNavigationViewHelper.enableNavigation(this, tvNv);
        Menu menu = tvNv.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        View profile_view = findViewById(R.id.ic_profile);
        View matched_view = findViewById(R.id.ic_matched);

        // Leer el valor de 'firstStart' desde SharedPreferences
        SharedPreferences newPref = getSharedPreferences("prefs", MODE_PRIVATE);
        firstStart = newPref.getBoolean("firstStart", true); // Valor predeterminado: true

        if (firstStart) {
            showToolTip_profile(new ViewPosition(profile_view));

            // Actualizar 'firstStart' para futuras ejecuciones
            SharedPreferences.Editor editor = newPref.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();
        }
    }

    private void showToolTip_profile(ShowCasePosition position)
    {
        new ShowCaseView.Builder(this).withTypedPosition(position).withTypedRadius(new Radius(186F))
                .withContent("First upload your profile picture and confirm it, otherwise the app won´t function correctly.").build().show(this);
    }

    public void dislikeBtn(View view) {
    }
}