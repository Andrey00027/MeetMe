package com.example.meetme.Matched;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.meetme.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MatchedAdapter extends RecyclerView.Adapter<MatchedViewHolder>
{

    private List<Matched> matchedList;
    private Context context;

    public MatchedAdapter(List<Matched> matchedList, Context context)
    {
        this.matchedList = matchedList;
        this.context = context;
    }



    @NonNull
    @Override
    public MatchedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchedViewHolder rcv = new MatchedViewHolder(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchedViewHolder holder, int position)
    {

        holder.mMatchId.setText(matchedList.get(position).getUserId());
        holder.mMatchName.setText(matchedList.get(position).getName());
        holder.mMatchOccupation.setText(matchedList.get(position).getOccupation());
        holder.mLastTimeStamp.setText(matchedList.get(position).getLastTimeStamp());
        holder.mLastMessage.setText(matchedList.get(position).getLastMessage());
        holder.mProfile.setText(matchedList.get(position).getChildId());
        String lastSeen = matchedList.get(position).getLastSeen();

        if(lastSeen.equals("true"))
            holder.mNotificationDot.setVisibility(View.VISIBLE);
        else
            holder.mNotificationDot.setVisibility(View.GONE);
        if(!matchedList.get(position).getProfileImageUrl().equals("default")){
            Glide.with(context).load(matchedList.get(position).getProfileImageUrl()).into(holder.mMatchProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return this.matchedList.size();
    }
}