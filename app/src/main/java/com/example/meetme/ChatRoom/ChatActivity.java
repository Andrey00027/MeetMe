package com.example.meetme.ChatRoom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.meetme.Matched.MatchedActivity;
import com.example.meetme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity
{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private EditText mSendEditText;
    private ImageButton mBack;

    private ImageButton mSendButton;
    private String notification;
    private String currentUserId, matchId, chatId;
    private String matchName, matchOccupation, matchProfile;
    private String lastMessage, lastTimeStamp;
    private String message, createdByUser, isSeen, messageId, currentUserName;
    private Boolean currentUserBoolean;
    ValueEventListener seenListener;
    DatabaseReference mDatabaseUser, mDatabaseChat;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        matchId = getIntent().getStringExtra("matchId");
        matchName = getIntent().getStringExtra("matchName");
        matchOccupation = getIntent().getStringExtra("occupation");
        matchProfile = getIntent().getStringExtra("profile");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("connections")
                .child("matched").child(matchId).child("chatId");
        mDatabaseChat = FirebaseDatabase.getInstance().getReference().child("Chat");

        getChatId();

        mRecyclerView = findViewById(R.id.recyclerViewChat);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetChat(), this);
        mRecyclerView.setAdapter(mChatAdapter);

        mSendEditText = findViewById(R.id.etMessage);
        mBack = findViewById(R.id.imbBackChat);

        mSendButton = findViewById(R.id.btnSend);

        mSendButton.setOnClickListener(v -> {
            sendMessage();
        });

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.smoothScrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });

        mBack.setOnClickListener(v -> {
            Intent i = new Intent(ChatActivity.this, MatchedActivity.class);
            startActivity(i);
            finish();
        });

        Toolbar toolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
        Map onChat = new HashMap();
        onChat.put("onChat", matchId);
        reference.updateChildren(onChat);

        DatabaseReference current = FirebaseDatabase.getInstance().getReference("Users").child(matchId)
                .child("connections").child("matched").child(currentUserId);
        Map lastSeen = new HashMap();
        lastSeen.put("lastSeen", "false");
        current.updateChildren(lastSeen);

    }

    @Override
    protected void onPause() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
        Map onChat = new HashMap();
        onChat.put("onChat", "None");
        reference.updateChildren(onChat);
        super.onPause();
    }

    @Override
    protected void onStop() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);
        Map onChat = new HashMap();
        onChat.put("onChat", "None");
        reference.updateChildren(onChat);
        super.onStop();
    }

    private void seenMessage(final String text) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(matchId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("onChat").exists()){
                        if (snapshot.child("notificationKey").exists()){
                            notification = snapshot.child("notificationKey").getValue().toString();
                        }
                        else{
                            notification = "";

                            if(!snapshot.child("onChat").getValue().toString().equals(currentUserId)){
                                new SendNotification(text, "New message from: " + currentUserName, notification, "activityToBeOpened", "MatchedActivity");
                            }
                            else {
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId)
                                        .child("connections").child("matched").child(matchId);
                                Map seenInfo = new HashMap();
                                seenInfo.put("lastSend", false);
                                reference.updateChildren(seenInfo);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        TextView mMatchNameTextView = findViewById(R.id.chatToolbar);
        mMatchNameTextView.setText(matchName);
        return true;
    }

    public void showProfile(View v){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.item_profile, null);

        TextView name = popupView.findViewById(R.id.name);
        ImageView image = popupView.findViewById(R.id.image);
        TextView occupation = popupView.findViewById(R.id.occupation);
        TextView age = popupView.findViewById(R.id.age);


        name.setText(matchName);
        occupation.setText(matchOccupation);

        switch (matchProfile){
            case "default":
                Glide.with(popupView.getContext()).load(R.drawable.no_image).into(image);
                break;
            default:
                //Glide.clear(image);
                Glide.with(popupView.getContext()).load(matchProfile).into(image);
                break;
        }

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        hideSoftKeyBoard();

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });

    }

    private void hideSoftKeyBoard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if(imm.isAcceptingText()){
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.unmatch){
            new AlertDialog.Builder(ChatActivity.this)
                    .setTitle("Unmatch")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Unmatch", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteMatch(matchId);
                            Intent i = new Intent(ChatActivity.this, MatchedActivity.class);
                            startActivity(i);
                            finish();
                            Toast.makeText(ChatActivity.this, "Unmatched", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
        else if(item.getItemId() == R.id.viewProfile){
            showProfile(findViewById(R.id.content));
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteMatch(String matchId)
    {

        DatabaseReference matchId_in_UserId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserId).child("connections").child("matches").child(matchId);

        DatabaseReference userId_in_matchId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(matchId).child("connections").child("matches").child(currentUserId);

        DatabaseReference yeps_in_matchId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(matchId).child("connections").child("yeps").child(currentUserId);

        DatabaseReference yeps_in_UserId_dbReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserId).child("connections").child("yeps").child(matchId);

        DatabaseReference matchId_chat_dbReference = FirebaseDatabase.getInstance().getReference().child("Chat").child(chatId);

        matchId_chat_dbReference.removeValue();
        matchId_in_UserId_dbReference.removeValue();
        userId_in_matchId_dbReference.removeValue();
        yeps_in_matchId_dbReference.removeValue();
        yeps_in_UserId_dbReference.removeValue();

    }

    private void sendMessage(){
        final String sendMessageText = mSendEditText.getText().toString();
        long now = System.currentTimeMillis();
        String timeStamp = Long.toString(now);

        if(!sendMessageText.isEmpty()){
            DatabaseReference newMessageDb = mDatabaseChat.push();

            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserId);
            newMessage.put("text", sendMessageText);
            newMessage.put("timeStamp", timeStamp);
            newMessage.put("seen", "false");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(snapshot.child("name").exists()){
                            currentUserName = snapshot.child("name").getValue().toString();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            lastMessage = sendMessageText;
            lastTimeStamp = timeStamp;
            updateLastMessage();
            seenMessage(sendMessageText);
            newMessageDb.setValue(newMessage);

        }

        mSendEditText.setText(null);
    }

    private void updateLastMessage()
    {

        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId)
                .child("connections").child("matched").child(matchId);
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId)
                .child("connections").child("matched").child(currentUserId);

        Map lastMessageMap = new HashMap();
        lastMessageMap.put("lastMessage", lastMessage);
        Map lastTimeStampMap = new HashMap();
        lastTimeStampMap.put("lastTimeStamp", lastTimeStamp);

        Map lastSeen = new HashMap();
        lastSeen.put("lastSeen", "true");
        currentUserDb.updateChildren(lastSeen);
        currentUserDb.updateChildren(lastMessageMap);
        currentUserDb.updateChildren(lastTimeStampMap);

        matchDb.updateChildren(lastMessageMap);
        matchDb.updateChildren(lastTimeStampMap);

    }



    private void getChatId()
    {

        mDatabaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists()){
                    chatId = snapshot.getValue().toString();
                    mDatabaseChat = mDatabaseChat.child(chatId);
                    getChatMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getChatMessage()
    {

        mDatabaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    messageId = null;
                    message = null;
                    createdByUser = null;
                    isSeen = null;

                    if(snapshot.child("text").getValue() != null){
                        message = snapshot.child("text").getValue().toString();
                    }
                    if(snapshot.child("createdByUser").getValue() != null){
                        createdByUser = snapshot.child("createdByUser").getValue().toString();
                    }
                    if(snapshot.child("seen").getValue() != null){
                        isSeen = snapshot.child("seen").getValue().toString();
                    }
                    else {
                        isSeen = "true";
                    }

                    messageId = snapshot.getKey().toString();
                    if(message != null && createdByUser != null)
                    {
                        currentUserBoolean = false;
                        if(createdByUser.equals(currentUserId))
                        {
                            currentUserBoolean = true;
                        }

                        Chat newMessage = null;
                        if(isSeen.equals("false"))
                        {
                            if(!currentUserBoolean)
                            {
                                isSeen = "true";

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Chat")
                                        .child(matchId).child(messageId);

                                Map seenInfo = new HashMap();
                                seenInfo.put("seen", "true");
                                reference.updateChildren(seenInfo);

                                newMessage = new Chat(message, currentUserBoolean, true);
                            }
                            else {
                                newMessage = new Chat(message, currentUserBoolean, false);
                            }
                        }
                        else {
                            newMessage = new Chat(message, currentUserBoolean, true);
                        }

                        DatabaseReference usersInChat = FirebaseDatabase.getInstance().getReference().child("Chat").child(matchId);
                        resultsChat.add(newMessage);
                        mChatAdapter.notifyDataSetChanged();

                        if(mRecyclerView.getAdapter() != null && resultsChat.size() > 0){
                            mRecyclerView.smoothScrollToPosition(resultsChat.size() - 1 );
                        }
                        else {
                            Toast.makeText(ChatActivity.this, "Chat Empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private ArrayList<Chat> resultsChat = new ArrayList<>();
    private List<Chat> getDataSetChat(){
        return resultsChat;
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}