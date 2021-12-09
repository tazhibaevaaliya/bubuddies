package com.green.bubuddies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//Activity for message page between two users
public class Chat extends AppCompatActivity {

    private
    RecyclerView mMessageRecycler;
    MessageListAdapter mMessageAdapter;
    Toolbar mToolbar;
    ImageButton deleteContact;
    ImageView sendButton;
    EditText messageArea;
    TextView title;
    String selfimg,img;
    ValueEventListener listener;
    ArrayList<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setSupportActionBar(mToolbar);

        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        deleteContact = findViewById(R.id.delete);
        title = findViewById(R.id.bar_title);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mMessageRecycler = findViewById(R.id.recycler_chat);
        mMessageAdapter = new MessageListAdapter(this, messages);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        //Check for if the page is directed from pairing page or checkout page
        Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            //get the name of user who sends the msg
            UserDetails.chatwithid = extras.getString("chatwithid");
            Log.e("chatwithid",UserDetails.chatwithid);
            Query query = reference.child("Users").child(UserDetails.chatwithid);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserDetails.chatwithname = snapshot.child("username").getValue().toString();
                    //Set the message title to be the user's name
                    title.setText(UserDetails.chatwithname);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            // if this page is directed from contacts page, then the other user's information is already retrieved
            title.setText(UserDetails.chatwithname);
        }

        // Back button, check which activity initiates the message page and direct back to the activity
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Messages").child(UserDetails.uid + "_" + UserDetails.chatwithid).removeEventListener(listener);
                if(extras!= null) {
                    Log.e("from",extras.getString("from"));

                    if(extras.getString("from").equals("pair")){
                        Intent i = new Intent(Chat.this, Pair.class);
                        i.putExtra("UID",UserDetails.uid);
                        startActivity(i);
                    }
                    else{
                        Intent i = new Intent(Chat.this, StoreActivity.class);
                        i.putExtra("UID",UserDetails.uid);
                        startActivity(i);
                    }
                }
                else{
                startActivity(new Intent(Chat.this,Users.class));}
            }
        });

        // Profile popup window for clicking on the title
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupProfile(UserDetails.chatwithid);
            }
        });

        // Delete popup window for delete button
        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup();
            }
        });

        // sending the msg
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();
                // if not empty, store the data in the firebase
                if(!messageText.equals("")){
                    Map<String, Object> map = new HashMap<>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.uid);
                    map.put("timestamp",ServerValue.TIMESTAMP);

                    // If the msg is sent by the user who blocked the current user but wants to re-pair with the current user
                    // Remove sender's uid from current user's blocked by list
                    reference.child("Users").child(UserDetails.chatwithid).child("BlockedBy").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data:snapshot.getChildren()){
                                if(data.getValue().toString().equals(UserDetails.uid)){
                                    data.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    // Update users' contact list
                    if(!UserDetails.contacts.contains(UserDetails.chatwithid)) {
                        reference.child("Users").child(UserDetails.uid).child("Contacts").push().setValue(UserDetails.chatwithid);
                        reference.child("Users").child(UserDetails.chatwithid).child("Contacts").push().setValue(UserDetails.uid);

                    }
                    String read_key = reference.child("Messages").child(UserDetails.uid + "_" + UserDetails.chatwithid).push().getKey();
                    reference.child("Track").child(UserDetails.uid + "_" + UserDetails.chatwithid).child("read").setValue(read_key);
                    reference.child("Messages").child(UserDetails.uid + "_" + UserDetails.chatwithid).child(read_key).setValue(map);
                    reference.child("Messages").child(UserDetails.chatwithid + "_" + UserDetails.uid).push().setValue(map);
                    InputMethodManager imm = (InputMethodManager) Chat.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                    messageArea.setText("");
                }
            }
        });

        // Get profile pic for current user
        reference.child("Profiles").child(UserDetails.uid).child("picture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                selfimg = snapshot.getValue(String.class);
                if(selfimg != null) Log.e("selfimg",selfimg); UserDetails.selfpic = selfimg;
                mMessageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Get profile pic for the other user
        reference.child("Profiles").child(UserDetails.chatwithid).child("picture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                img = snapshot.getValue(String.class);
                if(img!=null) Log.e("img",img); UserDetails.chatwithpic = img;
                mMessageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Get the msgs, automatically scroll down to the position of latest msg on the recyclerview
        reference.child("Messages").child(UserDetails.uid + "_" + UserDetails.chatwithid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                messages.add(new Message(snapshot.child("user").getValue().toString(),snapshot.child("message").getValue().toString(), (Long) snapshot.child("timestamp").getValue()));
                Log.e("msg",snapshot.child("message").getValue().toString());
                mMessageRecycler.smoothScrollToPosition(messages.size()-1);
                mMessageAdapter.notifyDataSetChanged();
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

        // Get the latest read msg key and store the info in firebase
        listener = new ValueEventListener() {
            String read_key;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    read_key = data.getKey();
                    Log.e("latest read msg time", data.getKey());
                }
                reference.child("Track").child(UserDetails.uid + "_" + UserDetails.chatwithid).child("read").setValue(read_key);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.child("Messages").child(UserDetails.uid + "_" + UserDetails.chatwithid).limitToLast(1).addValueEventListener(listener);
    }

    public void deleteContact(){
        // Delete previous messages in the firebase
        FirebaseDatabase.getInstance().getReference().child("Messages").child(UserDetails.uid + "_" + UserDetails.chatwithid).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Messages").child(UserDetails.chatwithid + "_" + UserDetails.uid).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Track").child(UserDetails.uid + "_" + UserDetails.chatwithid).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Track").child(UserDetails.chatwithid + "_" + UserDetails.uid).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Users").child(UserDetails.chatwithid).child("BlockedBy").push().setValue(UserDetails.uid);
        // Update contact list
        UserDetails.contacts.remove(UserDetails.chatwithid);
        FirebaseDatabase.getInstance().getReference().child("Users").child(UserDetails.uid).child("Contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data:snapshot.getChildren()){
                    if(data.getValue().toString().equals(UserDetails.chatwithid)){
                        data.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(UserDetails.chatwithid).child("Contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data:snapshot.getChildren()){
                    if(data.getValue().toString().equals(UserDetails.uid)){
                        data.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // jump to the contact page
        startActivity(new Intent(Chat.this,Users.class));
    }

    // pop up window for deleting user from contacts
    public void popup(){
        AlertDialog.Builder popup_builder = new AlertDialog.Builder(this,R.style.CustomAlertDialog);
        final View popupView = getLayoutInflater().inflate(R.layout.delete_popup,null);

        TextView warn = popupView.findViewById(R.id.warn_msg);
        Button confirm = popupView.findViewById(R.id.confirm_btn);
        Button cancel = popupView.findViewById(R.id.cancel_btn);

        popup_builder.setView(popupView);
        AlertDialog popup = popup_builder.create();
        popup.show();

        warn.setText("DELETE "+ UserDetails.chatwithname+ " from contacts\n Note: "+UserDetails.chatwithname+" will be blocked until you send new message to "+UserDetails.chatwithname);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteContact();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.hide();
            }
        });
    }

    // pop up window for displaying user profile
    public void popupProfile(String uid){
        AlertDialog.Builder popup_builder = new AlertDialog.Builder(this,R.style.CustomAlertDialog);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        final View popupView = inflater.inflate(R.layout.profile_popup,null);


        TextView name = popupView.findViewById(R.id.profile_name);

        TextView major = popupView.findViewById(R.id.profile_major);
        TextView gradtime = popupView.findViewById(R.id.profile_gradtime);
        TextView classes = popupView.findViewById(R.id.profile_class);
        TextView aboutme = popupView.findViewById(R.id.profile_aboutme);
        ImageView img = popupView.findViewById(R.id.profile_img);

        // Get Profile info for the specified user
        FirebaseDatabase.getInstance().getReference().child("Profiles").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText(snapshot.child("name").getValue().toString());

                major.setText(snapshot.child("major").getValue().toString());
                aboutme.setText(snapshot.child("aboutMe").getValue().toString());
                gradtime.setText("class of "+snapshot.child("graduationYear").getValue().toString());

                if(snapshot.child("major").getValue().toString().equals("")){
                    major.setText("no major info");
                }
                if(snapshot.child("graduationYear").getValue().toString().equals("")){
                    Log.e("goy","empty");
                    gradtime.setText("no graduation date info");
                }
                Picasso.with(getApplicationContext()).load(snapshot.child("picture").getValue().toString()).transform(new CircleTransform()).into(img);

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

    @Override
    protected void onStop() {
        super.onStop();
    }
}