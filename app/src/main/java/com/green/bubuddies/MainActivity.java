package com.green.bubuddies;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/** This is the user's dashboard. They may edit profile, or logout here. It also displays their specific information. */
public class MainActivity extends AppCompatActivity implements BottomMenu.BtmMenuActivity{
    Button logout, resend, editProfile;
    TextView uid, welcome, email, warningMessage;
    //GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth fAuth;
    String string_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        String googToken;
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            googToken = extras.getString("ID_TOKEN");
//        } else {
//            googToken = "";
//        }
//        Log.e("TESTING", "ID_TOKEN " + googToken);
        // Configure Google Sign In.
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                // For some reason, default_web_client_id will not work. Have to hardcode idtoken as result from values.xml
//                .requestIdToken("935525663116-k8n9tckl0u39bdkq073m2oiqv876enme.apps.googleusercontent.com")
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        uid = findViewById(R.id.text_userID);
        welcome = findViewById(R.id.welcomeUser);
        email = findViewById(R.id.text_userEmail);
        logout = findViewById(R.id.button_logout);
        editProfile = findViewById(R.id.button_editProfile);
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        Log.e("TESTING", fAuth.toString());
        Log.e("TESTING", currentUser.toString());
        Log.e("TESTING", Boolean.toString(isSignedIn()));





        resend = findViewById(R.id.resendVerify);
        warningMessage = findViewById(R.id.userNotVerified);
        // Is the user email verified? If so, don't show this message. Otherwise show it.
        if(!currentUser.isEmailVerified()) {
            Log.e("TESTING", Boolean.toString(currentUser.isEmailVerified()));
            resend.setVisibility(View.VISIBLE);
            warningMessage.setVisibility(View.VISIBLE);
            resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this, "A verification link has been sent to your email!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            resend.setVisibility(View.INVISIBLE);
            warningMessage.setVisibility(View.INVISIBLE);
        }






        string_uid = currentUser.getUid();
        uid.setText("Your User ID is:\n " + string_uid);
        email.setText("Your Email is:\n " + currentUser.getEmail());
        DatabaseReference username = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        username.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text = "Welcome, " + dataSnapshot.child("username").getValue().toString();
                welcome.setText(text);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TESTING", "onCancelled", databaseError.toException());
            }
        });



        // Log the user out.
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
//                mGoogleSignInClient.signOut()
//                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                // Have to do this too to sign user out of app completely.
//
//                            }
//                        });
            }
        });

        // Reminder in the future after finished debugging, make this button for verified users only.
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
            }
        });

    }

    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(getApplicationContext()) != null;
    }

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
                break;
            case (BottomMenu.MESSAGE):
                Log.d("Messaging", "Starting message Activity");
                i = new Intent(MainActivity.this, Users.class);
                i.putExtra("UID",string_uid);
                startActivity(i);
                break;
            case (BottomMenu.STORE):
                Log.d("Store", "Starting store Activity");
                i = new Intent(MainActivity.this, StoreActivity.class);
                i.putExtra("UID", string_uid);
                startActivity(i);
                break;
        }
    }
}