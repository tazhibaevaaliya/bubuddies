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

public class ContactListAdapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_NO_UNREAD = 1;
    private static final int VIEW_TYPE = 2;

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

    // determine the view type by number of unread msgs
    @Override
    public int getItemViewType(int position) {
        Contact contact = ContactsList.get(position);
        if(contact.getUnread_count() <= 0){
            return VIEW_TYPE_NO_UNREAD;
        }
        else{
            return VIEW_TYPE;
        }
    }

    // Inflates the layout.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(viewType == VIEW_TYPE_NO_UNREAD){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlist_no_unread,parent,false);
            return new ContactHolder(view);
        }
        else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlist_row,parent,false);
            return new ContactWithUnreadHolder(view);
        }
    }

    // Passes the contact object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Contact contact = (Contact) ContactsList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE:
                ((ContactListAdapter.ContactWithUnreadHolder) holder).bind(contact);
                break;
            case VIEW_TYPE_NO_UNREAD:
                ((ContactListAdapter.ContactHolder) holder).bind(contact);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserDetails.chatwithid = contact.getId();
                UserDetails.chatwithname = contact.getName();
                mContext.startActivity(new Intent(mContext, Chat.class));
            }
        });
    }

    private class ContactWithUnreadHolder extends RecyclerView.ViewHolder {
        TextView messageText, nameText, timeText, unreadText;
        ImageView profileImage;

        ContactWithUnreadHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.latest_msg);
            nameText = (TextView) itemView.findViewById(R.id.user_name);
            timeText = (TextView) itemView.findViewById(R.id.msg_time);
            unreadText = (TextView) itemView.findViewById(R.id.unread_count);
            profileImage = (ImageView) itemView.findViewById(R.id.user_pic);
        }

        void bind(Contact contact) {
            messageText.setText(contact.getMsg());
            nameText.setText(contact.getName());
            String unread = String.valueOf(contact.getUnread_count());
            unreadText.setText(unread);

            // Display the time of the latest msg
            // If before today (access date), show the date
            // else (within one day) show the hours

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("MM-d");
                String cur = formatter.format(new Date(contact.getMsg_time()));
                String now = formatter.format(new Date());

                if(cur.equals(now)){
                    formatter = new SimpleDateFormat("HH:mm");
                    String hour = formatter.format(new Date(contact.getMsg_time()));
                    timeText.setText(hour);
                }
                else{
                    timeText.setText(cur);
                }
            } catch(Exception e){
                Log.e("time","Null");
            }

            // Insert the profile image from the URL into the ImageView.
            Picasso.with(mContext).load(contact.getPic()).transform(new CircleTransform()).into(profileImage);
        }
    }

    private class ContactHolder extends RecyclerView.ViewHolder {
        TextView messageText, nameText, timeText, unreadText;
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
            // If before today (access date), show the date
            // else (within one day) show the hours

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("MM-d");
                String cur = formatter.format(new Date(contact.getMsg_time()));
                String now = formatter.format(new Date());

                if(cur.equals(now)){
                    formatter = new SimpleDateFormat("HH:mm");
                    String hour = formatter.format(new Date(contact.getMsg_time()));
                    timeText.setText(hour);
                }
                else{
                    timeText.setText(cur);
                }
            } catch(Exception e){
                Log.e("time","Null");
            }

            // Insert the profile image from the URL into the ImageView.
            Picasso.with(mContext).load(contact.getPic()).transform(new CircleTransform()).into(profileImage);
        }
    }
}
