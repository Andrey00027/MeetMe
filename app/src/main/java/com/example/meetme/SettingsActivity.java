package com.example.meetme;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;


public class SettingsActivity extends AppCompatActivity {

    private static final int SELECT_PICTURE = 1;
    private static String path = "";
    private ScrollView scrollView;
    private View editText;

    Toolbar toolBarSettings;

    private EditText nNameField, mAgeField, mSexField, mPhoneField, mHobbiesField, mOccupationField, mSocialStatusField;
    private ProgressBar spinner;
    private ImageButton mBack;
    private ImageView mProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private Button btnCancel, btnSave;

    private String profileImageUrl, userId, userName, userAge, userSex, userPhone, userHobbies, userOccupation, userSocialStatus;
    private Uri resultUri;

    private String selectedImagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        scrollView = findViewById(R.id.scroll_Settings);
        editText = findViewById(R.id.edtxt_SocialStatus);
        spinner = findViewById(R.id.progressBarSettings);
        spinner.setVisibility(View.GONE);

        toolBarSettings = findViewById(R.id.toolbarSettings);

        mProfileImage = findViewById(R.id.imvProfileImage);
        mBack = findViewById(R.id.imbBackSettings);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        nNameField = findViewById(R.id.edtxt_Name);
        mAgeField = findViewById(R.id.edtxt_Age);
        mPhoneField = findViewById(R.id.edtxt_Phone);
        mSexField = findViewById(R.id.edtxt_Sex);
        mHobbiesField = findViewById(R.id.edtxt_Hobbies);
        mOccupationField = findViewById(R.id.edtxt_Occupation);
        mSocialStatusField = findViewById(R.id.edtxt_SocialStatus);

        btnCancel = findViewById(R.id.btnCancelSettings);
        btnSave = findViewById(R.id.btnSaveSettings);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null && mAuth.getCurrentUser()!= null)
        {
            userId = mAuth.getCurrentUser().getUid();
        }
        else
        {
            finish();
        }

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);


        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                Rect rect = new Rect();
                scrollView.getWindowVisibleDisplayFrame(rect);
                int screenHeight = scrollView.getRootView().getHeight();
                int keypadHeight = screenHeight - rect.bottom;

                if (keypadHeight > screenHeight * 0.15)
                {
                    int[] location = new int[2];
                    editText.getLocationOnScreen(location);
                    int scrollToPosition = location[1] - rect.top - (editText.getHeight() / 2);

                    scrollView.post(() -> scrollView.smoothScrollTo(0, scrollToPosition));
                }
            }
        });

        getUserInfo();

        mProfileImage.setOnClickListener(v ->
        {
            if (!checkPermission())
            {
                Toast.makeText(SettingsActivity.this,"Permission required to access the gallery", Toast.LENGTH_SHORT).show();
                requestPermissions();

            }
            else
            {
//                Intent i = new Intent(Intent.ACTION_PICK);
//                i.setType("image/*");
//                startActivityForResult(i, 100);

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 1);
            }
        });

        btnSave.setOnClickListener(v -> {
            saveUserInformation();
            //Intent i = new Intent(SettingsActivity.this, MainActivity.class);
            //startActivity(i);
            //finish();
        });

        btnCancel.setOnClickListener(v -> {
            Intent i = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });

        setSupportActionBar(toolBarSettings);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;

    }

    private boolean checkPermission()
    {

        int result = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermissions()
    {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Open image picker if permission granted
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, 1);
            } else {
                // Show message if permission denied
                Toast.makeText(this, "Please allow access to continue", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.ContactUs)
        {
            new AlertDialog.Builder(this).setTitle("Contact Us").setMessage("Contact us here: andreidumitrache027@gmail.com")
                    .setNegativeButton("Dismiss", null).setIcon(android.R.drawable.ic_dialog_info).show();
        }
        else if(item.getItemId() == R.id.logout) {
            spinner.setVisibility(View.VISIBLE);
            mAuth.signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            spinner.setVisibility(View.GONE);
        } else if (item.getItemId() == R.id.deleteAccount)
        {
            new AlertDialog.Builder(this).setTitle("Are you sure?").setMessage("Deleting your account will result in completely removing your account from the system")
                   .setNegativeButton("Dismiss", null).setPositiveButton("Delete", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    spinner.setVisibility(View.VISIBLE);
                                    if(task.isSuccessful())
                                    {
                                        deleteUserAccount(userId);
                                        Toast.makeText(SettingsActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                                        startActivity(i);
                                        finish();
                                        spinner.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        Toast.makeText(SettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                        Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                                        startActivity(i);
                                        finish();
                                        spinner.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }

        return super.onOptionsItemSelected(item);

    }

    public void deleteMatch(String matchId, String chatId)
    {

        DatabaseReference matchId_in_UserId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("connections").child("matches").child(matchId);

        DatabaseReference userId_in_matchId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("connections").child("matches").child(userId);

        DatabaseReference yeps_in_matchId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("connections").child("yeps").child(userId);

        DatabaseReference yeps_in_UserId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("connections").child("yeps").child(matchId);

        DatabaseReference matchId_chat_dbReference = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);

        matchId_chat_dbReference.removeValue();
        matchId_in_UserId_dbReference.removeValue();
        userId_in_matchId_dbReference.removeValue();
        yeps_in_matchId_dbReference.removeValue();
        yeps_in_UserId_dbReference.removeValue();

    }

    private void deleteUserAccount(String userId)
    {

        DatabaseReference curUser_ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        DatabaseReference curUser_matches_ref = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userId).child("connections").child("matches");

        curUser_matches_ref.addListenerForSingleValueEvent( new ValueEventListener()
        {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    for(DataSnapshot matchSnapshot : snapshot.getChildren())
                    {
                        deleteMatch(matchSnapshot.getKey(), matchSnapshot.child("ChatId").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }

        });

        curUser_ref.removeValue();
        curUser_matches_ref.removeValue();

    }

    private void getUserInfo() {
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();

                    if (map.get("name") != null) {
                        userName = map.get("name").toString();
                        nNameField.setText(userName);
                    }
                    if (map.get("age") != null) {
                        userAge = map.get("age").toString();
                        mAgeField.setText(userAge);
                    }
                    if (map.get("phone") != null) {
                        userPhone = map.get("phone").toString();
                        mPhoneField.setText(userPhone);
                    }
                    if (map.get("sex") != null) {
                        userSex = map.get("sex").toString();
                        mSexField.setText(userSex);
                    }
                    if (map.get("hobbies") != null) {
                        userHobbies = map.get("hobbies").toString();
                        mHobbiesField.setText(userHobbies);
                    }
                    if (map.get("occupation") != null) {
                        userOccupation = map.get("occupation").toString();
                        mOccupationField.setText(userOccupation);
                    }
                    if (map.get("socialStatus") != null) {
                        userSocialStatus = map.get("socialStatus").toString();
                        mSocialStatusField.setText(userSocialStatus);
                    }
                    if (map.get("profileImageUrl") != null) {
                        profileImageUrl = map.get("profileImageUrl").toString();
                        if (profileImageUrl.equals("default")) {
                            Glide.with(SettingsActivity.this).load(R.drawable.no_image).into(mProfileImage);
                        } else {
                            Glide.with(SettingsActivity.this).load(profileImageUrl).into(mProfileImage);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsActivity.this, "Failed to load user information", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void saveUserInformation() {
        // Collect user information
        userName = nNameField.getText().toString();
        userAge = mAgeField.getText().toString();
        userPhone = mPhoneField.getText().toString();
        userSex = mSexField.getText().toString();
        userHobbies = mHobbiesField.getText().toString();
        userOccupation = mOccupationField.getText().toString();
        userSocialStatus = mSocialStatusField.getText().toString();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("name", userName);
        userInfo.put("age", userAge);
        userInfo.put("phone", userPhone);
        userInfo.put("sex", userSex);
        userInfo.put("hobbies", userHobbies);
        userInfo.put("occupation", userOccupation);
        userInfo.put("socialStatus", userSocialStatus);
        mUserDatabase.updateChildren(userInfo);
//        mUserDatabase.updateChildren(userInfo);


        if (resultUri != null) {
            StorageReference filepath = FirebaseStorage.getInstance()
                    .getReference("profileImages")
                    .child(userId);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = filepath.putBytes(data);
                uploadTask.addOnFailureListener(e -> {
                    Log.e("ImageUpload", "Upload failed: " + e.getMessage());
                    Toast.makeText(this, "Image upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                });
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    filepath.getDownloadUrl().addOnSuccessListener(uri -> {
                        Log.d("ImageUpload", "Upload successful: " + uri.toString());
                        String uploadedImageUrl = uri.toString();
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                        // Update user info with the downloadable URL
                        userInfo.put("profileImageUrl", uploadedImageUrl);
                        mUserDatabase.updateChildren(userInfo);

                        // Update the image view with Glide
                        Glide.with(SettingsActivity.this)
                                .load(uploadedImageUrl)
                                .placeholder(R.drawable.no_image) // Optional placeholder
                                .into(mProfileImage);
                    });
                });
            } catch (IOException e) {
                Log.e("ImageUpload", "Error processing image: " + e.getMessage());
            }
        } else {
            Log.d("ImageUpload", "No image selected for upload.");
        }

        String uploadedImageUrl = path;
//        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child("XlonUbMLs1N6XLu6CzHMad9Mrzw2");

        userInfo.put("profileImageUrl", uploadedImageUrl);


    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            resultUri  = data.getData();

            if (resultUri  != null) {

                Glide.with(this)
                        .load(resultUri )
                        .placeholder(R.drawable.no_image) // Optional placeholder
                        .into(mProfileImage);
            }
        }
        else{
            Toast.makeText(this, "You haven't picked any image.", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public String getRealPathFromURI(Context context, Uri uri) {
        String result = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            if (idx != -1) {
                result = cursor.getString(idx);
            }
            cursor.close();
        }
        return result;
    }
}















