package com.green.bubuddies;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {
    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
//    ListView msgList;
    ScrollView scrollView;
    String selfimg,img;
    ArrayList<Message> messages = new ArrayList<>();
//    FirebaseAuth fauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = findViewById(R.id.layout1);
        scrollView = findViewById(R.id.scrollView);

        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);

//        msgList = findViewById(R.id.msgList);

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
//        fauth = FirebaseAuth.getInstance();
//        UserDetails.uid = fauth.getCurrentUser().getUid();

//        DatabaseReference getName = FirebaseDatabase.getInstance().getReference("Profiles").child(UserDetails.uid);
//        getName.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                UserDetails.username = snapshot.child("name").getValue().toString();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.uid);
                    // put the info of user and message to the database

                    if(!UserDetails.contacts.contains(UserDetails.chatwithid)) {
                        reference.child("Users").child(UserDetails.uid).child("Contacts").push().setValue(UserDetails.chatwithid);
                    }
                    reference.child("Messages").child(UserDetails.uid + "_" + UserDetails.chatwithid).push().setValue(map);
                    reference.child("Messages").child(UserDetails.chatwithid + "_" + UserDetails.uid).push().setValue(map);
                    InputMethodManager imm = (InputMethodManager) Chat.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                    messageArea.setText("");
                }
            }
        });

        reference.child("Profiles").child(UserDetails.uid).child("picture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                selfimg = snapshot.getValue().toString();
                Log.e("selfimg",selfimg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.child("Profiles").child(UserDetails.chatwithid).child("picture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                img = snapshot.getValue().toString();
                Log.e("img",img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        msgAdapter adapter= new msgAdapter(this.getBaseContext(),messages,selfimg,img);
//        msgList.setAdapter(adapter);

        // Get the previous msgs
        reference.child("Messages").child(UserDetails.uid + "_" + UserDetails.chatwithid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String message = snapshot.child("message").getValue().toString();
                String userName = snapshot.child("user").getValue().toString();
//                messages.add(new Message(snapshot.child("user").getValue().toString(),snapshot.child("message").getValue().toString()));
//                Log.e("msg",snapshot.child("message").getValue().toString());
                Log.e("msg",messages.toString());
//                adapter.notifyDataSetChanged();
                if(userName.equals(UserDetails.uid)){
                    addMessageBox("You: " + message, 1);
                }
                else{
                    addMessageBox(UserDetails.chatwithname + ": " + message, 2);
                }
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


    // Determine the background of the message box
    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        textView.setLayoutParams(lp);

        // Check if the message was sent by the user or the other people
        if(type == 1) {
            textView.setBackgroundResource(R.drawable.rounded_corner1);
        }
        else{
            textView.setBackgroundResource(R.drawable.rounded_corner2);
        }

        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}

class msgAdapter extends BaseAdapter {

    private
    ArrayList<Message> msgs;
    String self,other;

    Context context;   //Creating a reference to our context object, so we only have to get it once.  Context enables access to application specific resources.


    // grab the context, we will need it later, the callback gets it as a parm.
    // load the strings and images into object references.
    public msgAdapter(Context aContext,ArrayList messages,String selfimg, String img) {
//initializing our data in the constructor.
        context = aContext;  //saving the context we'll need it again.
        msgs = messages;
        self = selfimg;
        other = img;
    }

    // ListView uses this to determine how many rows to render.
    @Override
    public int getCount() { return msgs.size(); }

    //Override getItem/getItemId, we aren't using these, but we must override anyway.
    @Override
    public Object getItem(int position) {
        return msgs.get(position);
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
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//// Let's optimize a bit by checking to see if we need to inflate, or if it's already been inflated...
        if (convertView == null){  //indicates this is the first time we are creating this row.
            Log.i("TESTING", "convertView " + convertView);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //Inflater's are awesome, they convert xml to Java Objects!
            Log.i("TESTING", "inflater " + inflater.toString());
            if(msgs.get(position).getUser().equals(UserDetails.username)){
                Log.i("user",msgs.get(position).getUser());
                row = inflater.inflate(R.layout.msg_row,parent,false);
            }
            else{
                Log.i("user",msgs.get(position).getUser());
                row = inflater.inflate(R.layout.msg_other_row,parent,false);
            }
//            row = inflater.inflate(R.layout.msg_row, parent, false);
        }
        else
        {
            row = convertView;
        }

        ImageView profile_pic;
        TextView msg;
        if(msgs.get(position).getUser().equals(UserDetails.username)){
            profile_pic = (ImageView) row.findViewById(R.id.img_mine);  //Q: Notice we prefixed findViewByID with row, why?  A: Row, is the container.
            msg = (TextView) row.findViewById(R.id.msy_mine);
            msg.setText(msgs.get(position).getMsg());
            Log.i("msg",msg.getText().toString());

            String img_url = self;
            Picasso.with(context).load(img_url).into(profile_pic);
        }
        else{
            profile_pic = (ImageView) row.findViewById(R.id.img_other);  //Q: Notice we prefixed findViewByID with row, why?  A: Row, is the container.
            msg = (TextView) row.findViewById(R.id.msg_other);
            msg.setText(msgs.get(position).getMsg());
            Log.i("msg",msg.getText().toString());

            String img_url = other;
            Picasso.with(context).load(img_url).into(profile_pic);

        }
//Now that we have a valid row instance, we need to get references to the views within that row and fill with the appropriate text and images.
//        ImageView profile_pic = row.findViewById(R.id.msglist_img);  //Q: Notice we prefixed findViewByID with row, why?  A: Row, is the container.
//        TextView msg = row.findViewById(R.id.msglist_msg);

//        msg.setText(msgs.get(position).getMsg());
//        Log.i("msg",msg.getText().toString());
//        if(msgs.get(position).getUser().equals(UserDetails.username)){
//            String img_url = self;
//            Picasso.with(context).load(img_url).into(profile_pic);
//
//        }
//        else{
//            String img_url = other;
//            Picasso.with(context).load(img_url).into(profile_pic);
//
//        }
//        String img_url = imgs.get(users.get(position));
//        Picasso.with(context).load(img_url).into(profile_pic);

        Log.i("TESTING", "row " + row.toString());
        return row;
//        return convertView;
    }}