package com.green.bubuddies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class Pair extends AppCompatActivity implements BottomMenu.BtmMenuActivity, NewMsg.newMsgActivity {

    //references to views
    private TextView txt_name;
    private TextView txt_bio;
    private TextView txt_classes;
    private ImageView img_pfp;
    private Button btn_next;

    //UID variables
    private String curr_user;
    private String pair_user;
    private ArrayList<String> potentialMates;
    private ArrayList<String> deniedMates;

    //Current User info
    private ArrayList<String> classList;
    private String gradYear;

    //Firebase values
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Profiles");
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = fAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair);

        if(savedInstanceState != null){
            curr_user = savedInstanceState.getString("UID");
        } else {
            curr_user = "2ax0y5TP9gRs9JV31d3JKSRjNz52"; //hard coded user for testing
        }
        Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            curr_user = extras.getString("UID");
        } else {
            curr_user = "2ax0y5TP9gRs9JV31d3JKSRjNz52";
        }

        UserDetails.uid = curr_user;

        //initialize references to views
        txt_name = findViewById(R.id.txt_name);
        txt_name.setClipToOutline(true);
        txt_bio = findViewById(R.id.txt_bio);
        txt_bio.setClipToOutline(true);
        txt_classes = findViewById(R.id.txt_classes);
        txt_classes.setClipToOutline(true);
        img_pfp = findViewById(R.id.img_pfp);
        btn_next = findViewById(R.id.btn_next);
        btn_next.setClickable(false);

        classList = new ArrayList<String>();
        potentialMates = new ArrayList<String>();
        findPair();

        deniedMates = new ArrayList<String>();
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_next.setClickable(false);
                deniedMates.add(pair_user);
                potentialMates.remove(0);
                if (potentialMates.size() < 1){
                    findPair();
                } else {
                    pair_user = potentialMates.get(0);
                    updateGUI(potentialMates.get(0));
                }
            }
        });



    }

    public void findPair() {
        txt_bio.setText("finding pair");
        ref.child(curr_user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gradYear = snapshot.child("graduationYear").getValue(String.class);
                for (DataSnapshot child: snapshot.child("classes").getChildren()){
                    String data = child.getKey();
                    classList.add(data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        database.getReference("Users").child(curr_user).child("Contacts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    deniedMates.add(child.getValue(String.class));
                }
                findPair2();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
    public void findPair2() {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String search_user = child.child("uid").getValue(String.class);
                    if ((!search_user.equals(curr_user)) && (!deniedMates.contains(search_user))) {
                        //Toast.makeText(Pair.this, "valid uid", Toast.LENGTH_LONG);
                        Iterator<DataSnapshot> courses = child.child("classes").getChildren().iterator();
                        while(courses.hasNext()){
                            //Toast.makeText(Pair.this,"searching through users", Toast.LENGTH_LONG).show();
                            if (classList.contains(courses.next().getKey())) {
                                potentialMates.add(search_user);
                                break;
                            }
                        }
                    }
                }
                if(potentialMates.size() == 0) {
                    findPair3();
                } else  {
                    pair_user = potentialMates.get(0);
                    updateGUI(pair_user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void findPair3(){
        Toast.makeText(Pair.this, "No other users share a class with you, showing all users.", Toast.LENGTH_SHORT).show();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String search_user = child.child("uid").getValue(String.class);
                    if (!search_user.equals(curr_user)) {
                        potentialMates.add(search_user);
                    }
                }
                pair_user = potentialMates.get(0);
                updateGUI(pair_user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateGUI(String pair_uid){
        ref.child(pair_uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DatabaseReference username = FirebaseDatabase.getInstance().getReference("Users").child(snapshot.child("uid").getValue().toString());
                username.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        txt_name.setText(dataSnapshot.child("username").getValue().toString());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("TESTING", "onCancelled", databaseError.toException());
                    }
                });

                txt_bio.setText("About Me: " + snapshot.child("aboutMe").getValue(String.class) +
                        "\nMajor: " + snapshot.child("major").getValue(String.class) +
                        "\nGraduation Year: " + snapshot.child("graduationYear").getValue(String.class)
                    );
                btn_next.setClickable(true);
                String img = snapshot.child("picture").getValue(String.class);
                Picasso.with(Pair.this).load(img).transform(new CircleTransform()).into(img_pfp);
                String classes = "Current BU Courses: ";
                Iterator<DataSnapshot> courses = snapshot.child("classes").getChildren().iterator();
                while(courses.hasNext()){
                    classes += courses.next().getKey();
                    if (courses.hasNext())
                    classes += ", ";
                }
                txt_classes.setText(classes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void updateClickableButtons(){
        BottomMenu fragment = (BottomMenu) getSupportFragmentManager().findFragmentById(R.id.btmFragmentPair);
        fragment.disableClick(BottomMenu.PAIR);
    }

    @Override
    public void changeActivity(int nextAct) {

        //switch cases to correct activity in final implementation.
        switch(nextAct) {
            case (BottomMenu.PROFILE):
                Intent i = new Intent(Pair.this, MainActivity.class);
                i.putExtra("UID", curr_user);
                startActivity(i);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
            case (BottomMenu.PAIR):
                i = new Intent(Pair.this, Pair.class);
                i.putExtra("UID", curr_user);
                startActivity(i);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
            case (BottomMenu.MESSAGE):
                i = new Intent(Pair.this, Users.class);
                i.putExtra("UID",curr_user);
                startActivity(i);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
            case (BottomMenu.STORE):
                i = new Intent(Pair.this, StoreActivity.class);
                i.putExtra("UID", curr_user);
                startActivity(i);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
        }
    }

    @Override
    public void newConversation(){
        //code here to add user as a friend
        Intent i = new Intent(Pair.this,Chat.class);
//        i.putExtra("uid",curr_user);
        i.putExtra("chatwithid",pair_user);
        i.putExtra("from","pair");
//        Log.e("Passing uid",curr_user);
        Log.e("Passing chatwithid", pair_user);
        startActivity(i); // change to messaging tab.
    }

}