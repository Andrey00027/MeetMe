package com.example.meetme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetme.Cards.ArrayAdapter;
import com.example.meetme.Cards.Card;
import com.example.meetme.ChatRoom.SendNotification;
import com.example.meetme.Matched.MatchedActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import ru.dimorinny.showcasecard.ShowCaseView;
import ru.dimorinny.showcasecard.position.ShowCasePosition;
import ru.dimorinny.showcasecard.position.ViewPosition;
import ru.dimorinny.showcasecard.radius.Radius;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Card cards_data[];
    private ArrayAdapter arrayAdapter;
    private int i;
    private  String tag;
    private FirebaseAuth mAuth;
    private ProgressBar spinner;
    boolean firstStart;
    private String currentUId, notification, sendMessageText;
    private boolean activityStarted;
    private DatabaseReference usersDb;


    ListView listView;
    List<Card> rowItems;

    ImageButton like, dislike;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        activityStarted = true;
        setContentView(R.layout.activity_main);
        spinner = (ProgressBar)findViewById(R.id.progressBarMain);
        spinner.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        firstStart = prefs.getBoolean("firstStart", true);
        setupTopNavigationView();
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }
        tag = "MainActivity";
        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        if(mAuth != null && mAuth.getCurrentUser() != null)
            currentUId = mAuth.getCurrentUser().getUid();
        else{
            Log.d(tag, "Authorization failed");
            Toast.makeText(getApplicationContext(), "Auth failed", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        like = findViewById(R.id.btnLikeMain);
        dislike = findViewById(R.id.btnDislikeMain);
        Log.d(tag, "onCreate " + currentUId);
        checkUserSex();
        rowItems = new ArrayList<Card>();
        arrayAdapter = new ArrayAdapter(this, R.layout.item, rowItems );
        final SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener()
        {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d(tag, "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }
            @Override
            public void onLeftCardExit(Object dataObject) {
                Card obj = (Card) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
                Toast.makeText(MainActivity.this, "Left", Toast.LENGTH_SHORT).show();

                //Display a banner when no cards are available to display
                TextView tv = (TextView)findViewById(R.id.txvNoCardsMain);
                if(rowItems.size() == 0) {
                    tv.setVisibility(View.VISIBLE);
                } else {
                    tv.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onRightCardExit(Object dataObject) {
                Card obj = (Card) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();

                //Display a banner when no cards are available to display
                TextView tv = (TextView)findViewById(R.id.txvNoCardsMain);
                if(rowItems.size() == 0) {
                    tv.setVisibility(View.VISIBLE);
                } else {
                    tv.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                if (view != null) {
                    ImageButton btnLike = findViewById(R.id.btnLikeMain);
                    ImageButton btnDislike = findViewById(R.id.btnDislikeMain);

                    if (btnLike != null) {
                        btnLike.setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                        Intent i = new Intent(MainActivity.this, MatchedActivity.class);
                        startActivity(i);
                    } else {
                        Log.e("button", "btnLikeMain is null");
                    }

                    if (btnDislike != null) {
                        btnDislike.setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
                    } else {
                        Log.e("button", "btnDislikeMain is null");
                    }
                } else {
                    Log.e("button", "Selected view is null");
                }
            }
        });
        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Item Clicked", Toast.LENGTH_SHORT).show();
                Log.e("button", "btnDislikeMain is null");
            }
        });
    }
    private void setupTopNavigationView() {
        BottomNavigationView tvNv = findViewById(R.id.top_navigation_view);
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

    private void showToolTip_profile(ShowCasePosition position) {
        new ShowCaseView.Builder(MainActivity.this)
                .withTypedPosition(position)
                .withTypedRadius(new Radius(186F))
                .withContent("First time upload your profile picture and click on Confirm other wise your app will not work")
                .build()
                .show(MainActivity.this);
    }
    private void showToolTip_matches(ShowCasePosition position) {
        new ShowCaseView.Builder(MainActivity.this)
                .withTypedPosition(position)
                .withTypedRadius(new Radius(186F))
                .withContent("Find you matches and begin chatting!")
                .build()
                .show(MainActivity.this);
    }
    public void DislikeBtnMain(View v) {
        if (rowItems.size() != 0) {
            Card card_item = rowItems.get(0);
            String userId = card_item.getUserId();
            usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
            Toast.makeText(MainActivity.this, "Left", Toast.LENGTH_SHORT).show();
            rowItems.remove(0);
            arrayAdapter.notifyDataSetChanged();
            //Display a banner when no cards are available to display
            TextView tv = (TextView)findViewById(R.id.txvNoCardsMain);
            if(rowItems.size() == 0) {
                tv.setVisibility(View.VISIBLE);
            } else {
                tv.setVisibility(View.INVISIBLE);
            }
            Intent btnClick = new Intent(MainActivity.this, btnDislikeActivity.class);
            btnClick.putExtra("url", card_item.getProfileUrl());
            startActivity(btnClick);
        }
    }
    public void LikeBtnMain(View v) {
        if (rowItems.size() != 0) {
            Card card_item = rowItems.get(0);
            String userId = card_item.getUserId();
            //check matches
            usersDb.child(userId).child("connections").child("yeps").child(currentUId).setValue(true);
            isConnectionMatch(userId);
            Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_SHORT).show();
            rowItems.remove(0);
            arrayAdapter.notifyDataSetChanged();
            //Display a banner when no cards are available to display
            TextView tv = (TextView)findViewById(R.id.txvNoCardsMain);
            if(rowItems.size() == 0) {
                tv.setVisibility(View.VISIBLE);
            } else {
                tv.setVisibility(View.INVISIBLE);
            }
            Intent btnClick = new Intent(MainActivity.this, btnLikeActivity.class);
            btnClick.putExtra("url", card_item.getProfileUrl());
            startActivity(btnClick);
        }
    }
    private void isConnectionMatch(final String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("yeps").child(userId);
        usersDb.child(currentUId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    sendMessageText = dataSnapshot.getValue().toString();
                else
                    sendMessageText = "";
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        if(!currentUId.equals(userId)) {
            currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(MainActivity.this, "New Connection", Toast.LENGTH_LONG).show();
                        String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                        Map mapLastTimeStamp = new HashMap<>();
                        long now  = System.currentTimeMillis();
                        String timeStamp = Long.toString(now);
                        mapLastTimeStamp.put("lastTimeStamp", timeStamp);
                        usersDb.child(dataSnapshot.getKey()).child("connections").child("matched").child(currentUId).child("ChatId").setValue(key);
                        usersDb.child(dataSnapshot.getKey()).child("connections").child("matched").child(currentUId).updateChildren(mapLastTimeStamp);
                        usersDb.child(currentUId).child("connections").child("matched").child(dataSnapshot.getKey()).child("ChatId").setValue(key);
                        usersDb.child(currentUId).child("connections").child("matched").child(dataSnapshot.getKey()).updateChildren(mapLastTimeStamp);
                        notification = " ";
                        DatabaseReference notificationID = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("notificationKey");
                        notificationID.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    notification = snapshot.getValue().toString();
                                    Log.d("sendChat", notification);
                                    new SendNotification("You have a new match!", "", notification, null, null );
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
    private String userSex, userGive;
    private String oppositeUserNeed, oppositeUserSex;
    public void checkUserSex(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("CardSearch", dataSnapshot.toString());

                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("sex").getValue() != null){
                         Log.d("CardSearch", "exists coloumn called");
                        userSex = dataSnapshot.child("sex").getValue().toString();
                        Log.d("CardSearch", "datachange called");
                        oppositeUserSex = userSex;
                        getOppositeSexUsers(oppositeUserSex);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.d("DatabaseError", "datachange not called");
            }
        });
    }

    public void getOppositeSexUsers(final String oppositeUserSex){
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("CardSearch", "first child added");
                if (dataSnapshot.exists() && !dataSnapshot.getKey().equals(currentUId)) {
                    //Log.d("CardSearch", "getOppositeSex called");
                    Log.d("CardSearch", "exists child");
                    String profileImageUrl = "default";
                        if (!dataSnapshot.child("profileImageUrl").getValue().equals("default")) {
                            profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                        }
                        Card item = new Card(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl, dataSnapshot.child("age").getValue().toString(),
                                dataSnapshot.child("phone").getValue().toString(), dataSnapshot.child("hobbies").getValue().toString(), dataSnapshot.child("occupation").getValue().toString(),
                                dataSnapshot.child("socialStatus").getValue().toString());
                        rowItems.add(item);
                        arrayAdapter.notifyDataSetChanged();
                }
                //spinner.setVisibility(View.GONE);
                //Display a banner when no cards are available to display
                TextView tv = (TextView)findViewById(R.id.txvNoCardsMain);
                if(rowItems.size() == 0) {
                    tv.setVisibility(View.VISIBLE);
                } else {
                    tv.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        return;
    }

}