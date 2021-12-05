package com.green.bubuddies;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomeActivity extends AppCompatActivity {
    Button logout, resend, editProfile;
    TextView uid, welcome, email, warningMessage;
    FirebaseAuth fAuth;
    String string_uid;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uid = findViewById(R.id.text_userID);
        welcome = findViewById(R.id.welcomeUser);
        email = findViewById(R.id.text_userEmail);
        logout = findViewById(R.id.button_logout);
        editProfile = findViewById(R.id.button_editProfile);
        fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        Log.e("TESTING", fAuth.toString());
        Log.e("TESTING", currentUser.toString());


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
                            Toast.makeText(getBaseContext(), "A verification link has been sent to your email!", Toast.LENGTH_SHORT).show();
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
}
