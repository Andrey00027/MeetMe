package com.example.meetme.Matched;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meetme.ChatRoom.ChatActivity;
import com.example.meetme.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MatchedViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  {

    public TextView mMatchId, mMatchName, mMatchOccupation, mLastTimeStamp, mLastMessage, mProfile;
    public ImageView mNotificationDot, mMatchProfileImage;



    public MatchedViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId = itemView.findViewById(R.id.MatchId);
        mMatchName = itemView.findViewById(R.id.MatchUserName);
        mMatchOccupation = itemView.findViewById(R.id.MatchUserOccupation);
        mLastTimeStamp = itemView.findViewById(R.id.MatchLastTime);
        mLastMessage = itemView.findViewById(R.id.MatchUserLastMessage);
        mNotificationDot = itemView.findViewById(R.id.MatchNotificationDot);
        mMatchProfileImage = itemView.findViewById(R.id.MatchUserImage);
        mProfile = itemView.findViewById(R.id.MatchProfileId);



    }

    @Override
    public void onClick(View v)
    {

        Intent intent = new Intent(v.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchId", mMatchId.getText().toString());
        b.putString("matchName", mMatchName.getText().toString());
        b.putString("matchOccupation", mMatchOccupation.getText().toString());
        b.putString("matchlastTimeStamp", mLastTimeStamp.getText().toString());
        b.putString("matchLastMessage", mLastMessage.getText().toString());
        b.putString("matchProfile", mProfile.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);

    }

}
