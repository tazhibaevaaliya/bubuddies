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

/** Page for logging into the app.  */
public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    TextView forgotPass, register;
    Button login;
    FirebaseAuth fAuth;





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







        // Reset password button. Sends an email to the name of email inputted.
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

                // Check if parameters entered are valid.
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
                // Attempts  to sign in via fAuth. if there is an account, let them in, otherwise, deny.
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
}