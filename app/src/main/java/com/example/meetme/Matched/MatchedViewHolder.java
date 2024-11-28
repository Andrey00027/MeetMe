package com.example.meetme.Matched;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.meetme.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MatchedViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  {

    public TextView mMatchId, mMatchName, mMatchPhone, mMatchOccupation, mLastTimeStamp, mLastMessage;
    public ImageView mNotificationDot, mMatchProfileImage;



    public MatchedViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        mMatchId = itemView.findViewById(R.id.MatchId);
    }

    @Override
    public void onClick(View v) {

    }

}
