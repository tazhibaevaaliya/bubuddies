package com.green.bubuddies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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


        Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            UserDetails.chatwithid = extras.getString("chatwithid");
            Log.e("chatwithid",UserDetails.chatwithid);
            Query query = reference.child("Users").child(UserDetails.chatwithid);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserDetails.chatwithname = snapshot.child("username").getValue().toString();
                    title.setText(UserDetails.chatwithname);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            title.setText(UserDetails.chatwithname);
        }


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Chat.this,Users.class));
            }
        });

        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup();
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, Object> map = new HashMap<>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.uid);
                    map.put("timestamp",ServerValue.TIMESTAMP);
                    // put the info of user and message to the database

                    if(!UserDetails.contacts.contains(UserDetails.chatwithid)) {
                        reference.child("Users").child(UserDetails.uid).child("Contacts").push().setValue(UserDetails.chatwithid);
                        reference.child("Users").child(UserDetails.chatwithid).child("Contacts").push().setValue(UserDetails.uid);

                    }
                    reference.child("Messages").child(UserDetails.uid + "_" + UserDetails.chatwithid).push().setValue(map);
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

        // Get the previous msgs
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
    }

    public void deleteContact(){
        // Delete previous messages in the firebase
        FirebaseDatabase.getInstance().getReference().child("Messages").child(UserDetails.uid + "_" + UserDetails.chatwithid).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Messages").child(UserDetails.chatwithid + "_" + UserDetails.uid).removeValue();
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
        AlertDialog.Builder popup_builder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.delete_popup,null);

        TextView warn = popupView.findViewById(R.id.warn_msg);
        Button confirm = popupView.findViewById(R.id.confirm_btn);
        Button cancel = popupView.findViewById(R.id.cancel_btn);

        popup_builder.setView(popupView);
        AlertDialog popup = popup_builder.create();
        popup.show();

        warn.setText("DELETE "+ UserDetails.chatwithname+ " from contacts");
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
}