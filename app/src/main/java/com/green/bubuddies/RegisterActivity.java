package com.green.bubuddies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


/** Register a new account on the app. */
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "TESTING";
    EditText name, email, password, confirmPass;
    Button register;
    TextView login;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize stuff from .xml
        name = findViewById(R.id.reg_name);
        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_pass);
        confirmPass = findViewById(R.id.reg_confirmPass);
        login = findViewById(R.id.regButton_login);
        register = findViewById(R.id.regButton_reg);
        fAuth = FirebaseAuth.getInstance();

        // Attempt to register user given the credentials.
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String i_name, i_email, i_pass, i_confirmpass;
                i_name = name.getText().toString().trim();
                i_email = email.getText().toString().trim();
                i_pass = password.getText().toString().trim();
                i_confirmpass = confirmPass.getText().toString().trim();

                // Name guidelines; It cannot be empty
                if (i_name.length() < 1) {
                    name.setError("Your name cannot be null!");
                    name.requestFocus();
                    return;
                }
                // Is email valid ?
                if (!Patterns.EMAIL_ADDRESS.matcher(i_email).matches()) {
                    email.setError("Email must be valid.");
                    email.requestFocus();
                    return;
                }
                // Is email unique ?
                Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("email").equalTo(i_email);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() > 0) {
                            email.setError("This email is already in use.");
                            email.requestFocus();
                        } else {
                            // Is password valid ?
                            if (i_pass.length() < 6) {
                                password.setError("Your password should be at least 6 characters!");
                                password.requestFocus();
                                return;
                            }
                            if (!i_pass.equals(i_confirmpass)) {
                                confirmPass.setError("Your passwords must match!");
                                confirmPass.requestFocus();
                                return;
                            }


                            // Create a new user in the firebase auth and database.
                            fAuth.createUserWithEmailAndPassword(i_email, i_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Verification link for email
                                        FirebaseUser fUser = fAuth.getCurrentUser();

                                        // Create the user using a User object and put them into database.
                                        User user = new User(i_name, i_email, fUser.getUid());
                                        FirebaseDatabase.getInstance().getReference("Users").child(fAuth.getCurrentUser().getUid())
                                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(RegisterActivity.this, "User Successfully Created!", Toast.LENGTH_SHORT).show();


                                                // Make a profile for the user. Check in case it exists already. (bug testing)
                                                Query query = FirebaseDatabase.getInstance().getReference().child("Profiles").orderByChild("uid").equalTo(fAuth.getCurrentUser().getUid());
                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.getChildrenCount() < 1) {
                                                            Log.d("TESTING", "No profile found. Creating new one.");
                                                            Profile profile = new Profile(fAuth.getCurrentUser().getUid(), i_name);
                                                            FirebaseDatabase.getInstance().getReference("Profiles").child(fAuth.getCurrentUser().getUid())
                                                                    .setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                                                    finish();
                                                                }
                                                            });
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });
                                            }
                                        });
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });



        // Redirect to login page
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
            }
        });
    }
}