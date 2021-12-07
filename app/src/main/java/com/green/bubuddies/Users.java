package com.green.bubuddies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class Users extends AppCompatActivity implements BottomMenu.BtmMenuActivity{
    private
    RecyclerView mContactRecycler;
    ContactListAdapter mContactAdapter;
    ArrayList<Contact> contacts = new ArrayList<>();
    ArrayList<String> contacts_id = new ArrayList<>();
    ProgressDialog pd;
    String curr_user, read_key;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mContactRecycler = findViewById(R.id.users_list);
        mContactAdapter = new ContactListAdapter(this, contacts);
        mContactRecycler.setLayoutManager(new LinearLayoutManager(this));
        mContactRecycler.setAdapter(mContactAdapter);

        pd = new ProgressDialog(Users.this);
        pd.setMessage("Loading...");
        pd.show();

        Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            curr_user = extras.getString("UID");
        } else {
            curr_user = UserDetails.uid;
        }


        // Get the name of current user

        Log.e("username",curr_user);
        UserDetails.uid = curr_user;

        Query query = FirebaseDatabase.getInstance().getReference().child("Users").child(UserDetails.uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("username",snapshot.child("username").getValue().toString());
                UserDetails.username = snapshot.child("username").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Get the contacts of the current user
        FirebaseDatabase.getInstance().getReference().child("Users").child(UserDetails.uid).child("Contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contacts_id.clear();
                contacts.clear();
                // for each contact of the current user, find profile pics and the latest message
                for (DataSnapshot data:snapshot.getChildren()){
                    // get the contacts id
                    String chatwithid = data.getValue().toString();
                    contacts_id.add(chatwithid);
                    Contact user = new Contact();
                    user.setId(chatwithid);
                    contacts.add(user);

                    // Get the name of the contact
                    Query query = FirebaseDatabase.getInstance().getReference().child("Users").child(chatwithid);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String chatwith = snapshot.child("username").getValue().toString();
                            user.setName(chatwith);
                            Log.e("chatwith",chatwith);
                            mContactAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    //Get the profile pics from the contacts
                    FirebaseDatabase.getInstance().getReference().child("Profiles").child(chatwithid).child("picture").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user.setPic(snapshot.getValue().toString());
                            mContactAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });

                    //Get the latest messages between the current user and his/her contacts
                    FirebaseDatabase.getInstance().getReference().child("Messages").child(UserDetails.uid+"_"+chatwithid).limitToLast(1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            user.setMsg(snapshot.child("message").getValue().toString());
                            user.setMsg_time((Long)snapshot.child("timestamp").getValue());
                            Collections.sort(contacts,Contact.contactsComparator);
                            mContactAdapter.notifyDataSetChanged();
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

                    // Get the number of unread msg

                    // Check for the key for already read msg
                    FirebaseDatabase.getInstance().getReference().child("Track").child(UserDetails.uid+"_"+chatwithid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            try {
                                // If exist, then get the msg count after the already read msg
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    read_key = data.getValue().toString();
                                    Log.e("read", read_key);
                                }

                                FirebaseDatabase.getInstance().getReference().child("Messages").child(UserDetails.uid+"_"+chatwithid).orderByKey().startAfter(read_key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        user.setUnread_count((int) snapshot.getChildrenCount());
                                        Log.e("set unread count", String.valueOf(snapshot.getChildrenCount()));
                                        mContactAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            catch (Exception e){
                                //no msg are read
                                Log.e("msg","no read msg");
                                FirebaseDatabase.getInstance().getReference().child("Messages").child(UserDetails.uid+"_"+chatwithid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        user.setUnread_count((int)snapshot.getChildrenCount());
                                        Log.e("set unread count", String.valueOf(snapshot.getChildrenCount()));

                                        mContactAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                UserDetails.contacts = contacts_id;
                pd.dismiss();
                mContactAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(extras!=null){
        if(extras.getString("from")!=null) {
            if (extras.getString("from").equals("pair")) {
                Intent i = new Intent(Users.this, Chat.class);
                i.putExtra("chatwithid", extras.getString("chatwithid"));
                i.putExtra("from", "pair");
                startActivity(i);
            } else {
                Intent i = new Intent(Users.this, Chat.class);
                i.putExtra("chatwithid", extras.getString("chatwithid"));
                i.putExtra("from", "checkout");
                startActivity(i);
            }
        }}
    }


    @Override
    public void updateClickableButtons(){
        BottomMenu fragment = (BottomMenu) getSupportFragmentManager().findFragmentById(R.id.users_btmFrag);
        fragment.disableClick(BottomMenu.MESSAGE);
    }

    @Override
    public void changeActivity(int nextAct) {
        switch(nextAct) {
            case (BottomMenu.PROFILE):
                startActivity(new Intent(Users.this, MainActivity.class)); // can remove this line
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
            case (BottomMenu.PAIR):
                Log.d("Pairing", "Starting pair Activity");
                Intent i = new Intent(Users.this, Pair.class);
                i.putExtra("UID", curr_user);
                startActivity(i);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
            case (BottomMenu.MESSAGE):
                Log.d("Messaging", "Starting message Activity");
                i = new Intent(Users.this, Users.class);
                i.putExtra("UID",curr_user);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                startActivity(i);
                break;
            case (BottomMenu.STORE):
                startActivity(new Intent(Users.this, StoreActivity.class));
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
        }
    }
}



