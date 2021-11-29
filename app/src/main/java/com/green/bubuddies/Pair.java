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

    //Databse values
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("Profiles");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pair);

        if(savedInstanceState != null){
            curr_user = savedInstanceState.getString("UID");
        } else {
            curr_user = "0srmamAQZ6NkALd7JzqBRickByF3"; //hard coded user for testing
        }
        Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            curr_user = extras.getString("UID");
        } else {
            curr_user = "0srmamAQZ6NkALd7JzqBRickByF3";
        }

        UserDetails.uid = curr_user;

        //initialize references to views
        txt_name = findViewById(R.id.txt_name);
        txt_bio = findViewById(R.id.txt_bio);
        txt_classes = findViewById(R.id.txt_classes);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.child(curr_user).child("classes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    String data = child.getKey();
                    classList.add(data);
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
        Toast.makeText(Pair.this, "No other users share a class with you, showing all users.", Toast.LENGTH_LONG).show();
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
                txt_name.setText(snapshot.child("name").getValue(String.class));
                txt_bio.setText("About me: " + snapshot.child("aboutMe").getValue(String.class) +
                        "\nMajor: " + snapshot.child("major").getValue(String.class) +
                        "\nClass year: " + snapshot.child("graduationYear").getValue(String.class)
                    );
                btn_next.setClickable(true);
                String img = snapshot.child("picture").getValue(String.class);
                Picasso.with(Pair.this).load(img).into(img_pfp);
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
                startActivity(new Intent(Pair.this, MainActivity.class));
                break;
            case (BottomMenu.PAIR):
                startActivity(new Intent(Pair.this, MainActivity.class)); // can remove this line
                break;
            case (BottomMenu.MESSAGE):
                startActivity(new Intent(Pair.this, Users.class));
                break;
            case (BottomMenu.STORE):
                startActivity(new Intent(Pair.this, MainActivity.class));
                break;
        }
    }

    @Override
    public void newConversation(){
        //code here to add user as a friend
        Intent i = new Intent(Pair.this,Chat.class);
//        i.putExtra("uid",curr_user);
        i.putExtra("chatwithid",pair_user);
//        Log.e("Passing uid",curr_user);
        Log.e("Passing chatwithid", pair_user);
        startActivity(i); // change to messaging tab.
    }

}