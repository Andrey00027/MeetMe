package com.example.meetme;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private View editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Enable edge-to-edge experience if applicable
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Reference your ScrollView and the EditText here
        scrollView = findViewById(R.id.scroll_Settings); // Replace with your ScrollView ID
        editText = findViewById(R.id.edtxt_SocialStatus);  // Replace with the specific EditText ID

        // Adjust scroll position when the keyboard is visible
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                scrollView.getWindowVisibleDisplayFrame(rect);
                int screenHeight = scrollView.getRootView().getHeight();
                int keypadHeight = screenHeight - rect.bottom;

                if (keypadHeight > screenHeight * 0.15) {
                    // Keyboard is visible, calculate the scroll offset
                    int[] location = new int[2];
                    editText.getLocationOnScreen(location);
                    int scrollToPosition = location[1] - rect.top - (editText.getHeight() / 2);

                    scrollView.post(() -> scrollView.smoothScrollTo(0, scrollToPosition));
                }
            }
        });
    }
}
