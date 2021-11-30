package com.green.bubuddies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Users extends AppCompatActivity implements BottomMenu.BtmMenuActivity{
    ListView usersList;
    ArrayList<String> display_names = new ArrayList<>();
    ArrayList<String> contacts_id = new ArrayList<>();
    Map<String,String> display_msgs = new HashMap<>();
    Map<String,String> display_imgs = new HashMap<>();
    ProgressDialog pd;
    private String curr_user;
    private String pair_user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        usersList = findViewById(R.id.usersList);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,display_names);


        pd = new ProgressDialog(Users.this);
        pd.setMessage("Loading...");
        pd.show();
//        MyAdapter adapter= new MyAdapter(this.getBaseContext(),display_names,display_msgs,display_imgs);
        usersList.setAdapter(adapter);

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
                display_names.clear();
                display_msgs.clear();
                display_imgs.clear();
                // for each contact of the current user, find profile pics and the latest message
                for (DataSnapshot data:snapshot.getChildren()){
                    // get the contacts id
                    String chatwithid = data.getValue().toString();
                    contacts_id.add(chatwithid);

                    // Get the name of the contact
                    Query query = FirebaseDatabase.getInstance().getReference().child("Users").child(chatwithid);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String chatwith = snapshot.child("username").getValue().toString();
                            Log.e("chatwith",chatwith);
                            display_names.add(chatwith);
                            Log.e("chatwith",display_names.toString());
                            Log.e("list", String.valueOf(display_names.size()));
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Log.e("name",display_names.toString());

                    //Get the profile pics from the contacts
                    FirebaseDatabase.getInstance().getReference().child("Profiles").child(chatwithid).child("picture").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            display_imgs.put(chatwithid,snapshot.getValue().toString());
                            Log.e("url",display_imgs.toString());
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });

                    //Get the latest messages between the current user and his/her contacts
                    FirebaseDatabase.getInstance().getReference().child("Messages").child(UserDetails.uid+"_"+chatwithid).limitToLast(1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            display_msgs.put(chatwithid,snapshot.child("message").getValue().toString());

                            Log.e("msg",display_msgs.toString());
                            adapter.notifyDataSetChanged();
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
                UserDetails.contacts = contacts_id;
                pd.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.e("list", String.valueOf(display_names.size()));

        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatwithid = contacts_id.get(position);
                UserDetails.chatwithname = display_names.get(position);
                startActivity(new Intent(Users.this, Chat.class));
            }
        });
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
                break;
            case (BottomMenu.PAIR):
                Log.d("Pairing", "Starting pair Activity");
                Intent i = new Intent(Users.this, Pair.class);
                i.putExtra("UID", curr_user);
                startActivity(i);
                break;
            case (BottomMenu.MESSAGE):
                Log.d("Messaging", "Starting message Activity");
                i = new Intent(Users.this, Users.class);
                i.putExtra("UID",curr_user);
                startActivity(i);
                break;
            case (BottomMenu.STORE):
                startActivity(new Intent(Users.this, StoreActivity.class));
                break;
        }
    }
}


class MyAdapter extends BaseAdapter {

    private
    ArrayList<String> users;
    Map<String,String> imgs;
    Map<String,String> msgs;

    Context context;   //Creating a reference to our context object, so we only have to get it once.  Context enables access to application specific resources.


    // grab the context, we will need it later, the callback gets it as a parm.
    // load the strings and images into object references.
    public MyAdapter(Context aContext,ArrayList display_names, Map display_msgs, Map display_imgs) {
//initializing our data in the constructor.
        context = aContext;  //saving the context we'll need it again.
        users = display_names;
        msgs = display_msgs;
        imgs = display_imgs;
    }

// ListView uses this to determine how many rows to render.
    @Override
    public int getCount() {
        return Math.min(Math.min(users.size(),msgs.size()),imgs.size());
    }

    //Override getItem/getItemId, we aren't using these, but we must override anyway.
    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //THIS IS WHERE THE ACTION HAPPENS.  getView(..) is how each row gets rendered.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //position is the index of the row being rendered.
        //convertView represents the Row (it may be null),
        // parent is the layout that has the row Views.

//Inflate the listview row based on the xml.
        View row;  //this will refer to the row to be inflated or displayed if it's already been displayed. (listview_row.xml)
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService

//// Let's optimize a bit by checking to see if we need to inflate, or if it's already been inflated...
        if (convertView == null){  //indicates this is the first time we are creating this row.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //Inflater's are awesome, they convert xml to Java Objects!
            row = inflater.inflate(R.layout.userlist_row, parent, false);
        }
        else
        {
            row = convertView;
        }

//Now that we have a valid row instance, we need to get references to the views within that row and fill with the appropriate text and images.
        ImageView profile_pic = row.findViewById(R.id.userlist_img);  //Q: Notice we prefixed findViewByID with row, why?  A: Row, is the container.
        TextView latest_msg = row.findViewById(R.id.userlist_msg);
        TextView name = row.findViewById(R.id.userlist_name);

        name.setText(users.get(position));
        latest_msg.setText(msgs.get(users.get(position)));
        String img_url = imgs.get(users.get(position));
        Picasso.with(context).load(img_url).into(profile_pic);


        return row;

    }}


