package com.green.bubuddies;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ContactListAdapter extends RecyclerView.Adapter{

    private
    Context mContext;
    ArrayList<Contact> ContactsList;

    public ContactListAdapter(Context context, ArrayList<Contact> contacts) {
        mContext = context;
        ContactsList = contacts;
    }

    @Override
    public int getItemCount() {
        return ContactsList.size();
    }

    // Inflates the layout.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlist_row,parent,false);
        return new ContactHolder(view);
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Contact contact = (Contact) ContactsList.get(position);
        ((ContactHolder) holder).bind(contact);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDetails.chatwithid = contact.getId();
                UserDetails.chatwithname = contact.getName();
                mContext.startActivity(new Intent(mContext, Chat.class));
            }
        });
    }

    private class ContactHolder extends RecyclerView.ViewHolder {
        TextView messageText, nameText, timeText;
        ImageView profileImage;

        ContactHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.latest_msg);
            nameText = (TextView) itemView.findViewById(R.id.user_name);
            timeText = (TextView) itemView.findViewById(R.id.msg_time);
            profileImage = (ImageView) itemView.findViewById(R.id.user_pic);
        }

        void bind(Contact contact) {
            messageText.setText(contact.getMsg());
            nameText.setText(contact.getName());

            // Display the time of the latest msg

            try {

                Date temp = new Date(contact.getMsg_time());
                Long diff = new Date().getTime() - temp.getTime();
                long day_diff = TimeUnit.DAYS.convert(diff,TimeUnit.MILLISECONDS);

                if(day_diff < 1){
                    SimpleDateFormat formatter = new SimpleDateFormat("kk:mm");
                    String dateString = formatter.format(new Date(contact.getMsg_time()));
                    timeText.setText(dateString);
                }
                else{
                    SimpleDateFormat formatter = new SimpleDateFormat("MM-d");
                    String dateString = formatter.format(new Date(contact.getMsg_time()));
                    timeText.setText(dateString);
                }
            } catch(Exception e){
                Log.e("time","Null");
            }

            // Insert the profile image from the URL into the ImageView.
            Picasso.with(mContext).load(contact.getPic()).transform(new CircleTransform()).into(profileImage);
        }
    }
}
