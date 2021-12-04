package com.green.bubuddies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Chat extends AppCompatActivity {

    private
    RecyclerView mMessageRecycler;
    MessageListAdapter mMessageAdapter;
    Toolbar mToolbar;
    ImageButton deleteContact;
    ImageView sendButton;
    EditText messageArea;
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
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mMessageRecycler = findViewById(R.id.recycler_chat);
        mMessageAdapter = new MessageListAdapter(this, messages);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Chat.this,Users.class));
            }
        });

        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteContact();
            }
        });

        Log.e("uid before get",UserDetails.uid);
        Log.e("chatwithid before get", UserDetails.chatwithid);

        Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            UserDetails.chatwithid = extras.getString("chatwithid");
        } else {
        }

        Log.e("uid after get",UserDetails.uid);
        Log.e("chatwithid after get", UserDetails.chatwithid);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.uid);
                    // put the info of user and message to the database

//                    ArrayList<String> my_contacts = new ArrayList<>();
//                    ArrayList<String> other_contacts = new ArrayList<>();
//                    reference.child("Users").child(UserDetails.uid).child("Contacts").addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for (DataSnapshot data : snapshot.getChildren()) {
//                                my_contacts.add(data.getValue().toString());
//                            }
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//                    reference.child("Users").child(UserDetails.chatwithid).child("Contacts").addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for (DataSnapshot data:snapshot.getChildren()){
//                                other_contacts.add(data.getValue().toString());
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });

//                    if(!my_contacts.contains(UserDetails.chatwithid)){
//                        reference.child("Users").child(UserDetails.uid).child("Contacts").push().setValue(UserDetails.chatwithid);
//                    }
//
//                    if(!other_contacts.contains(UserDetails.uid)){
//                        reference.child("Users").child(UserDetails.chatwithid).child("Contacts").push().setValue(UserDetails.uid);
//                    }


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
                messages.add(new Message(snapshot.child("user").getValue().toString(),snapshot.child("message").getValue().toString()));
                Log.e("msg",snapshot.child("message").getValue().toString());
                Log.e("msg",messages.toString());
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
        FirebaseDatabase.getInstance().getReference().child("Messages").child(UserDetails.uid + "_" + UserDetails.chatwithid).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Messages").child(UserDetails.chatwithid + "_" + UserDetails.uid).removeValue();
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
        startActivity(new Intent(Chat.this,Users.class));
    }
}