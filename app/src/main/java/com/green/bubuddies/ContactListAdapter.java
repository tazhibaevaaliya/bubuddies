package com.green.bubuddies;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

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

    // Inflates the layout according.
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
        TextView messageText, nameText;
        ImageView profileImage;

        ContactHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.latest_msg);
            nameText = (TextView) itemView.findViewById(R.id.user_name);
            profileImage = (ImageView) itemView.findViewById(R.id.user_pic);
        }

        void bind(Contact contact) {
            messageText.setText(contact.getMsg());
            nameText.setText(contact.getName());

            // Insert the profile image from the URL into the ImageView.
            Picasso.with(mContext).load(contact.getPic()).transform(new CircleTransform()).into(profileImage);
        }
    }
}
