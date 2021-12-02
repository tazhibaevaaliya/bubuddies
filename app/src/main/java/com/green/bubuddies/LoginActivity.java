package com.green.bubuddies;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/** Page for logging into the app. Users can alternatively use Google Sign-in to enter here. */
public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    TextView forgotPass, register;
    Button login;
    FirebaseAuth fAuth;
    GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 120;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        login = findViewById(R.id.button_login);
        register = findViewById(R.id.button_register);
        forgotPass = findViewById(R.id.button_forgotPass);
        fAuth = FirebaseAuth.getInstance();

        // If the user is already logged in, go straight to dashboard.
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        // Configure Google Sign In.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // For some reason, default_web_client_id will not work. Have to hardcode idtoken as result from values.xml
                .requestIdToken("935525663116-k8n9tckl0u39bdkq073m2oiqv876enme.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.button_googSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TESTING", "google sign in button clicked");
                signIn();
            }
        });







        // Reset password button.
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetPass = new EditText(view.getContext());
                AlertDialog.Builder passwordReset = new AlertDialog.Builder(view.getContext());
                passwordReset.setTitle("Reset password?");
                passwordReset.setMessage("Enter your email here.");
                passwordReset.setView(resetPass);

                passwordReset.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String email = resetPass.getText().toString();
                        fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(LoginActivity.this, "A link to reset your password has been sent to your email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, "Invalid email! Try Again.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                passwordReset.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                passwordReset.create().show();
            }
        });





        // Register Button.
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                finish();
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
            }
        });





        // Login Button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TESTING", "login button clicked");
                String i_email, i_pass;
                i_email = email.getText().toString().trim();
                i_pass = password.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(i_email).matches()) {
                    email.setError("Email must be valid.");
                    email.requestFocus();
                    return;
                }

                if (i_pass.length() < 6) {
                    password.setError("Password must be 6 or more characters.");
                    password.requestFocus();
                    return;
                }

                Log.e("TESTING", "attempting login...");
                fAuth.signInWithEmailAndPassword(i_email, i_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.e("TESTING", "login successful");
                            Toast.makeText(LoginActivity.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Log.e("TESTING", "login failed");
                            //Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("TESTING", task.getException().getMessage());
                            email.setError("Your email or password was incorrect.");
                            password.setError("Your email or password was incorrect.");
                        }
                    }
                });
            }
        });
    }







    // Google sign-in related methods
    private void signIn() {
        Log.d("TESTING", "inside google sign in");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task.isSuccessful()) {
                Log.d("TESTING", "task successful");
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d("TESTING", "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.d("TESTING", "Google sign in failed", e);
                }
            }

        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Log.e("TESTING", "FIREBASEAUTH " + credential.toString());
        Log.e("TESTING", "FIREBASEAUTH " + idToken);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TESTING", "signInWithCredential: success");

                            // Is this google account new? If so, require them to make a username first.
                            Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("uid").equalTo(fAuth.getCurrentUser().getUid());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    // They were not found in the database.
                                    if (snapshot.getChildrenCount() < 1) {
                                        Log.d("TESTING", fAuth.getCurrentUser().getUid().toString());
                                        Log.d("TESTING", snapshot.toString());
                                        Log.d("TESTING", "There isn't an entry for the google account yet.");

                                        // Pop-up alert for entering a username.
                                        EditText enterUser = new EditText(LoginActivity.this);
                                        AlertDialog.Builder createUser = new AlertDialog.Builder(LoginActivity.this);

                                        createUser.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                            @Override
                                            public void onCancel(DialogInterface dialogInterface) {
                                                cancelLogin();
                                            }
                                        });

                                        createUser.setTitle("Please create a username for yourself.");
                                        createUser.setMessage("Enter username here.");
                                        createUser.setView(enterUser);

                                        createUser.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String c_username = enterUser.getText().toString();
                                                // Check if the username is taken already
                                                Query query = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").equalTo(c_username);
                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.getChildrenCount() > 0) {
                                                            Toast.makeText(LoginActivity.this, "This username is already taken. Please try another one.", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                            finish();

                                                            // Put the Google user into the database
                                                            User user = new User(c_username, fAuth.getCurrentUser().getEmail(), fAuth.getCurrentUser().getUid());

                                                            FirebaseDatabase.getInstance().getReference("Users").child(fAuth.getCurrentUser().getUid())
                                                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    Toast.makeText(LoginActivity.this, "User Successfully Created!", Toast.LENGTH_SHORT).show();


                                                                    // Make a profile for the user if one does not exist.
                                                                    Query query = FirebaseDatabase.getInstance().getReference().child("Profiles").orderByChild("uid").equalTo(fAuth.getCurrentUser().getUid());
                                                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                            if (snapshot.getChildrenCount() < 1) {
                                                                                Log.d("TESTING", "No profile found. Creating new one.");
                                                                                Profile profile = new Profile(fAuth.getCurrentUser().getUid());
                                                                                FirebaseDatabase.getInstance().getReference("Profiles").child(fAuth.getCurrentUser().getUid())
                                                                                        .setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                                                                        i.putExtra("ID_TOKEN", idToken);
                                                                                        Log.d("TESTING", "PUTEXTRA " + idToken);
                                                                                        startActivity(i);
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
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        });

                                        createUser.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Log.d("TESTING", "PROCESS CANCELLED");
                                                cancelLogin();
                                            }
                                        });
                                        createUser.create().show();
                                    } else {
                                        Intent i = new Intent(getApplicationContext(),MainActivity.class);
                                        i.putExtra("ID_TOKEN", idToken);
                                        Log.d("TESTING", "PUTEXTRA " + idToken);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TESTING", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    // In case user app crashes or clicks out of the pop-up alert for creating username in Google account, to prevent bugs.
    private void cancelLogin() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        fAuth.signOut();
                    }
                });
    }

    protected void onStop() {
        super.onStop();
        cancelLogin();
    }

    protected void onDestroy() {
        super.onDestroy();
        cancelLogin();
    }
}