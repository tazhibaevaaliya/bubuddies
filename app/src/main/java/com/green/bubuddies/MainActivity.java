package com.green.bubuddies;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/** This is the user's dashboard. They may edit profile, or logout here. It also displays their information. */
public class MainActivity extends AppCompatActivity implements BottomMenu.BtmMenuActivity{
    Button logout, editProfile;
    TextView aboutMe, welcome, classes;
    FirebaseAuth fAuth;
    String string_uid;
    ImageView your_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aboutMe = findViewById(R.id.text_aboutMe);
        welcome = findViewById(R.id.welcomeUser);
        classes = findViewById(R.id.text_myClasses);
        your_image = findViewById(R.id.your_image);
        logout = findViewById(R.id.button_logout);
        editProfile = findViewById(R.id.button_editProfile);
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        Log.e("TESTING", fAuth.toString());
        Log.e("TESTING", currentUser.toString());
        string_uid = currentUser.getUid();


        // Grab user data.
        DatabaseReference username = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        username.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                welcome.setText(dataSnapshot.child("username").getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TESTING", "onCancelled", databaseError.toException());
            }
        });
        DatabaseReference getPFP = FirebaseDatabase.getInstance().getReference("Profiles").child(currentUser.getUid());
        getPFP.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.with(getApplicationContext()).load(snapshot.child("picture").getValue().toString()).transform(new CircleTransform()).into(your_image);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        DatabaseReference getAboutMe = FirebaseDatabase.getInstance().getReference("Profiles").child(currentUser.getUid());
        getAboutMe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aboutMe.setText(snapshot.child("aboutMe").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        DatabaseReference getClasses = FirebaseDatabase.getInstance().getReference("Profiles").child(currentUser.getUid()).child("classes");
        getClasses.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        if (dataSnapshot.exists()) {
                            List<String> classList = new ArrayList<String>();
                            for(DataSnapshot d : dataSnapshot.getChildren()) {
                                classList.add(d.getKey());
                            }
                            Log.e("TESTING", classList.toString());
                            classes.setText(String.join(", ", classList));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });



        // Log the user out.
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        });

        // Edit profile.
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
            }
        });

    }

    // Below is for functionality of the bottom hotbar.
    @Override
    public void updateClickableButtons(){
        BottomMenu fragment = (BottomMenu) getSupportFragmentManager().findFragmentById(R.id.btmFragmentMain);
        fragment.disableClick(BottomMenu.PROFILE);
    }

    @Override
    public void changeActivity(int nextAct) {
        switch(nextAct) {
            case (BottomMenu.PROFILE):
                startActivity(new Intent(MainActivity.this, MainActivity.class)); // can remove this line
                break;
            case (BottomMenu.PAIR):
                Log.d("Pairing", "Starting pair Activity");
                Intent i = new Intent(MainActivity.this, Pair.class);
                i.putExtra("UID", string_uid);
                startActivity(i);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
            case (BottomMenu.MESSAGE):
                Log.d("Messaging", "Starting message Activity");
                i = new Intent(MainActivity.this, Users.class);
                i.putExtra("UID",string_uid);
                startActivity(i);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
            case (BottomMenu.STORE):
                Log.d("Store", "Starting store Activity");
                i = new Intent(MainActivity.this, StoreActivity.class);
                i.putExtra("UID", string_uid);
                startActivity(i);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
        }
    }
}