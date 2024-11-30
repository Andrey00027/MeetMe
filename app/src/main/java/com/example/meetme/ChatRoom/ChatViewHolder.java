package com.example.meetme.ChatRoom;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.meetme.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{

    public TextView mMessage;
    public LinearLayout mContainer;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
//        mMessage = itemView.findViewById(R.id.message);
//        mContainer = itemView.findViewById(R.id.container);
    }

    @Override
    public void onClick(View v) {

    }
}
