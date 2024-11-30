package com.example.meetme.Matched;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.ImageView;

import com.example.meetme.MainActivity;
import com.example.meetme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MatchedActivity extends AppCompatActivity
{

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchedAdapter;
    private RecyclerView.LayoutManager mMatchedLayoutManager;
    private ImageView mBack;
    private DatabaseReference current;
    private ValueEventListener listen;
    private HashMap<String, Integer> mList = new HashMap<>();
    private String currentUserId, mLastTimeStamp, mLastMessage, lastSeen;
    DatabaseReference mCurrUserIdInsideMatchedConnections, mCheckLastSeen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_matched);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mBack = findViewById(R.id.imbBackMatched);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = findViewById(R.id.recyclerViewMatched);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchedLayoutManager = new LinearLayoutManager(MatchedActivity.this);
        mRecyclerView.setLayoutManager(mMatchedLayoutManager);
        mMatchedAdapter = new MatchedAdapter(getDataSetMatched(), MatchedActivity.this);
        mRecyclerView.setAdapter(mMatchedAdapter);

        mBack.setOnClickListener(v -> {
            Intent intent = new Intent(MatchedActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        getUserMatchId();

        mLastMessage = mLastTimeStamp = lastSeen = "";
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void getLastMessageInfo(DatabaseReference userDb){
        mCurrUserIdInsideMatchedConnections = userDb.child("connections").child("matched").child(currentUserId);

        mCurrUserIdInsideMatchedConnections.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("lastMessage").getValue() != null && snapshot.child("lastTimeStamp").getValue() != null && snapshot.child("lastSend").getValue()!= null )
                    {
                        mLastMessage = snapshot.child("lastMessage").getValue().toString();
                        mLastTimeStamp = snapshot.child("lastTimeStamp").getValue().toString();
                        lastSeen = snapshot.child("lastSend").getValue().toString();
                    }
                    else{
                        mLastMessage = "Start Chatting one";
                        mLastTimeStamp = "";
                        lastSeen = "true";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getUserMatchId() {
        Query sortedMatchedByLastTimeStamp = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId)
                .child("connections").child("matched").orderByChild("lastTimeStamp");

        sortedMatchedByLastTimeStamp.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    for (DataSnapshot match : snapshot.getChildren())
                    {
                        FetchMatchInformation(match.getKey(), match.child("ChatId").toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    private void FetchMatchInformation(final String key, final String chatId)
    {

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        getLastMessageInfo(userDb);

        userDb.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                if(snapshot.exists()){
                    String userId = snapshot.getKey();
                    String name = "";
                    String profileImageUrl = "";
                    String occupation =  "";
                    final String lastMessage = "";
                    String lastTimeStamp = "";

                    if(snapshot.child("name").getValue() != null)
                    {
                        name = snapshot.child("name").getValue().toString();
                    }
                    if(snapshot.child("profileImageUrl").getValue() != null)
                    {
                        profileImageUrl = snapshot.child("profileImageUrl").getValue().toString();
                    }
                    if(snapshot.child("occupation").getValue() != null)
                    {
                        occupation = snapshot.child("occupation").getValue().toString();
                    }

                    String milliSec = mLastTimeStamp;
                    Long now;

                    try
                    {
                        now = Long.parseLong(milliSec);
                        lastTimeStamp = convertMilliToRelative(now);
                        String[] arrOfStr = lastTimeStamp.split(",");
                        mLastTimeStamp = arrOfStr[0];
                    }
                    catch (Exception e) {}

                    Matched obj = new Matched(userId, name, profileImageUrl, occupation, mLastMessage, mLastTimeStamp, chatId, lastMessage);

                    if(mList.containsKey(chatId)){
                        int key = mList.get(chatId);
                        resultsMatched.set(resultsMatched.size() - key, obj);
                    }
                    else
                    {
                        resultsMatched.add(0,obj);
                        mList.put(chatId, resultsMatched.size());
                    }

                    mMatchedAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }

    private String convertMilliToRelative(Long now)
    {
        String time = DateUtils.getRelativeDateTimeString(this, now, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
        return time;
    }

    private ArrayList<Matched> resultsMatched = new ArrayList<>();

    private List<Matched> getDataSetMatched()
    {
        return resultsMatched;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}




