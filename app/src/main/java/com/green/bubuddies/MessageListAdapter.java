package com.green.bubuddies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_SENT_BEFORE_TODAY = 2;

    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 3;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED_BEFORE_TODAY = 4;

    private Date last;


    private
    Context mContext;
    ArrayList<Message> mMessageList;

    public MessageListAdapter(Context context, ArrayList<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = mMessageList.get(position);

        if(position == 0){
            last = new Date(message.getTimestamp());
            if(message.getUser().equals(UserDetails.uid)){
                return VIEW_TYPE_MESSAGE_SENT_BEFORE_TODAY;
            }
            else{
                return VIEW_TYPE_MESSAGE_RECEIVED_BEFORE_TODAY;
            }
        }
        else{
            SimpleDateFormat formatter = new SimpleDateFormat("MM-d");
            String prev = formatter.format(last);
            String cur = formatter.format(new Date(message.getTimestamp()));
            last = new Date(message.getTimestamp());
            if(!prev.equals(cur)){
                if(message.getUser().equals(UserDetails.uid)){
                    return VIEW_TYPE_MESSAGE_SENT_BEFORE_TODAY;
                }
                else{
                    return VIEW_TYPE_MESSAGE_RECEIVED_BEFORE_TODAY;
                }
            }
            else{
                if(message.getUser().equals(UserDetails.uid)){
                    return VIEW_TYPE_MESSAGE_SENT;
                }
                else{
                    return VIEW_TYPE_MESSAGE_RECEIVED;
                }
            }
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.msg_me_today, parent, false);
            return new SentMessageTodayHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_SENT_BEFORE_TODAY) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.msg_me, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.msg_other_today, parent, false);
            return new ReceivedMessageTodayHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED_BEFORE_TODAY) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.msg_other, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = (Message) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageTodayHolder) holder).bind(message);
                ((SentMessageTodayHolder) holder).profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupProfile(message.getUser());
                    }
                });
                break;
            case VIEW_TYPE_MESSAGE_SENT_BEFORE_TODAY:
                ((SentMessageHolder) holder).bind(message);
                ((SentMessageHolder) holder).profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupProfile(message.getUser());
                    }
                });
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageTodayHolder) holder).bind(message);
                ((ReceivedMessageTodayHolder) holder).profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupProfile(message.getUser());
                    }
                });
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED_BEFORE_TODAY:
                ((ReceivedMessageHolder) holder).bind(message);
                ((ReceivedMessageHolder) holder).profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popupProfile(message.getUser());
                    }
                });
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, dateText;
        ImageView profileImage;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.msgtext_me);
            dateText = (TextView) itemView.findViewById(R.id.msg_date);
            timeText = (TextView) itemView.findViewById(R.id.time_me);
            profileImage = (ImageView) itemView.findViewById(R.id.pic_me);
        }

        void bind(Message message) {
            messageText.setText(message.getMsg());

            // Format the stored timestamp into a readable String using method.
            SimpleDateFormat formatter = new SimpleDateFormat("kk:mm");
            String dateString = formatter.format(new Date(message.getTimestamp()));
            timeText.setText(dateString);

            formatter = new SimpleDateFormat("EEE, MMM d");
            dateString = formatter.format(new Date(message.getTimestamp()));
            dateText.setText(dateString);

            // Insert the profile image from the URL into the ImageView.
            Picasso.with(mContext).load(UserDetails.selfpic).transform(new CircleTransform()).into(profileImage);
        }
    }

    private class SentMessageTodayHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView profileImage;

        SentMessageTodayHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.msgtext_me);
            timeText = (TextView) itemView.findViewById(R.id.time_me);
            profileImage = (ImageView) itemView.findViewById(R.id.pic_me);
        }

        void bind(Message message) {
            messageText.setText(message.getMsg());

            // Format the stored timestamp into a readable String using method.
            SimpleDateFormat formatter = new SimpleDateFormat("kk:mm");
            String dateString = formatter.format(new Date(message.getTimestamp()));
            timeText.setText(dateString);

            // Insert the profile image from the URL into the ImageView.
            Picasso.with(mContext).load(UserDetails.selfpic).transform(new CircleTransform()).into(profileImage);
        }
    }


    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, dateText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.latest_msg);
            timeText = (TextView) itemView.findViewById(R.id.time_other);
            dateText = (TextView) itemView.findViewById(R.id.msg_date);
            profileImage = (ImageView) itemView.findViewById(R.id.user_pic);
        }

        void bind(Message message) {
            messageText.setText(message.getMsg());

            // Format the stored timestamp into a readable String using method.
            SimpleDateFormat formatter = new SimpleDateFormat("kk:mm");
            String dateString = formatter.format(new Date(message.getTimestamp()));
            timeText.setText(dateString);

            formatter = new SimpleDateFormat("EEE, MMM d");
            dateString = formatter.format(new Date(message.getTimestamp()));
            dateText.setText(dateString);

            // Insert the profile image from the URL into the ImageView.
            Picasso.with(mContext).load(UserDetails.chatwithpic).transform(new CircleTransform()).into(profileImage);
        }
    }

    private class ReceivedMessageTodayHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView profileImage;

        ReceivedMessageTodayHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.latest_msg);
            timeText = (TextView) itemView.findViewById(R.id.time_other);
            profileImage = (ImageView) itemView.findViewById(R.id.user_pic);
        }

        void bind(Message message) {
            messageText.setText(message.getMsg());

            // Format the stored timestamp into a readable String using method.
            SimpleDateFormat formatter = new SimpleDateFormat("kk:mm");
            String dateString = formatter.format(new Date(message.getTimestamp()));
            timeText.setText(dateString);

            // Insert the profile image from the URL into the ImageView.
            Picasso.with(mContext).load(UserDetails.chatwithpic).transform(new CircleTransform()).into(profileImage);
        }
    }

    public void popupProfile(String uid){
        AlertDialog.Builder popup_builder = new AlertDialog.Builder(mContext,R.style.CustomAlertDialog);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View popupView = inflater.inflate(R.layout.profile_popup,null);


        TextView name = popupView.findViewById(R.id.profile_name);

        TextView major = popupView.findViewById(R.id.profile_major);
        TextView gradtime = popupView.findViewById(R.id.profile_gradtime);
        TextView classes = popupView.findViewById(R.id.profile_class);
        TextView aboutme = popupView.findViewById(R.id.profile_aboutme);
        ImageView img = popupView.findViewById(R.id.profile_img);

        FirebaseDatabase.getInstance().getReference().child("Profiles").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("name").getValue().toString());
                major.setText(snapshot.child("major").getValue().toString());
                aboutme.setText(snapshot.child("aboutMe").getValue().toString());
                gradtime.setText("class of "+snapshot.child("graduationYear").getValue().toString());
                Picasso.with(mContext).load(snapshot.child("picture").getValue().toString()).transform(new CircleTransform()).into(img);

                String classlist = "";
                Iterator<DataSnapshot> courses = snapshot.child("classes").getChildren().iterator();
                while(courses.hasNext()){
                    classlist += courses.next().getKey();
                    if (courses.hasNext())
                        classlist += ", ";
                }
                classes.setText(classlist);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        popup_builder.setView(popupView);
        AlertDialog popup = popup_builder.create();
        popup.show();

    }
}

