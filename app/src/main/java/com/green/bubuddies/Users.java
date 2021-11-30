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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Users extends AppCompatActivity {
    ListView usersList;
    ArrayList<String> display_names = new ArrayList<>();
    Map<String,String> display_msgs = new HashMap<>();
    Map<String,String> display_imgs = new HashMap<>();
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        usersList = findViewById(R.id.usersList);


        pd = new ProgressDialog(Users.this);
        pd.setMessage("Loading...");
        pd.show();
        MyAdapter adapter= new MyAdapter(this.getBaseContext(),display_names,display_msgs,display_imgs);
        usersList.setAdapter(adapter);

        //change the path when integrate
        // Get the contact of current user
        FirebaseDatabase.getInstance().getReference().child("users").child(UserDetails.username).child("contacts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                display_names.clear();
                display_msgs.clear();
                display_imgs.clear();
                for (DataSnapshot data:snapshot.getChildren()){
                    String chatwith = data.getValue().toString();
                    display_names.add(chatwith);
                    Log.e("name",display_names.toString());

                    //Get the profile pics from the contacts
                    FirebaseDatabase.getInstance().getReference().child("users").child(chatwith).child("img").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            display_imgs.put(chatwith,snapshot.getValue().toString());
                            Log.e("url",display_imgs.toString());
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });

                    //Get the latest messages between the current user and his/her contacts
                    FirebaseDatabase.getInstance().getReference().child("messages").child(com.green.bubuddies.UserDetails.username+"_"+chatwith).limitToLast(1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            display_msgs.put(chatwith,snapshot.child("message").getValue().toString());

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
                UserDetails.contacts = display_names;
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        usersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserDetails.chatWith = display_names.get(position);
                startActivity(new Intent(Users.this, Chat.class));
            }
        });
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


