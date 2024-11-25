package com.example.meetme.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.meetme.R;

import java.util.List;

import androidx.annotation.NonNull;

public class ArrayAdapter extends android.widget.ArrayAdapter<Card>
{
    private final Context context;

    public ArrayAdapter(Context context, int resourceId, List<Card> cards)
    {
        super(context, resourceId, cards);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        // Get the data item for this position
        Card card = getItem(position);

        // Check if an existing view is being reused, otherwise inflate a new view
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        }

        ImageView image = convertView.findViewById(R.id.imvCardImage);
        TextView name = convertView.findViewById(R.id.txvNameCard);
        TextView age = convertView.findViewById(R.id.txvAgeCard);
        TextView phone = convertView.findViewById(R.id.txvPhoneCard);
        TextView hobbies = convertView.findViewById(R.id.txvHobbiesCard);
        TextView ocupation = convertView.findViewById(R.id.txvOccupationCard);
        TextView socialStatus = convertView.findViewById(R.id.txvSocialStatusCard);

        name.setText(card.getName());
        age.setText(String.valueOf(card.getAge()));
        phone.setText(String.valueOf(card.getPhone()));
        hobbies.setText(card.getHobbies());
        ocupation.setText(card.getOcupation());
        socialStatus.setText(card.getSocialStatus());

        switch ( card.getProfileUrl())
        {
            case "default":
                Glide.with(convertView.getContext()).load(R.drawable.no_image).into(image);
                break;
            default:
                Glide.with(convertView.getContext()).load(card.getProfileUrl()).into(image);
                break;
        }

        return convertView;

    }
}
