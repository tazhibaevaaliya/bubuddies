package com.green.bubuddies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    Button back, updateProfile, updatePicture;
    EditText name, major, yog, classes, about_me;
    FirebaseAuth fAuth;
    ImageView PFP;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        Log.e("TESTING", Boolean.toString(isSignedIn()));
        Log.e("TESTING", fAuth.toString());
        Log.e("TESTING", currentUser.toString());

        back = findViewById(R.id.button_back);
        updateProfile = findViewById(R.id.button_updateProfile);
        updatePicture = findViewById(R.id.button_updatePic);
        name = findViewById(R.id.edit_name);
        major = findViewById(R.id.edit_major);
        yog = findViewById(R.id.edit_gradYear);
        classes = findViewById(R.id.edit_classes);
        about_me = findViewById(R.id.edit_aboutMe);
        PFP = findViewById(R.id.profilePic);



        // Load user profile data.
        DatabaseReference getName = FirebaseDatabase.getInstance().getReference("Profiles").child(currentUser.getUid());
        getName.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               name.setText(snapshot.child("name").getValue().toString());
           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {
           }
        });
        DatabaseReference getMajor = FirebaseDatabase.getInstance().getReference("Profiles").child(currentUser.getUid());
        getMajor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                major.setText(snapshot.child("major").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        DatabaseReference getYog = FirebaseDatabase.getInstance().getReference("Profiles").child(currentUser.getUid());
        getYog.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                yog.setText(snapshot.child("graduationYear").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        DatabaseReference getAboutMe = FirebaseDatabase.getInstance().getReference("Profiles").child(currentUser.getUid());
        getAboutMe.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                about_me.setText(snapshot.child("aboutMe").getValue().toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        DatabaseReference getPFP = FirebaseDatabase.getInstance().getReference("Profiles").child(currentUser.getUid());
        getPFP.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.with(getApplicationContext()).load(snapshot.child("picture").getValue().toString()).fit().centerCrop().into(PFP);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        //Get datasnapshot at your "users" root node ; Grabs data of user's classes.
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






        // Goes back without saving changes.
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        updatePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 100);
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Profiles").child(fAuth.getCurrentUser().getUid());
                mData.child("aboutMe").setValue(about_me.getText().toString());
                mData.child("graduationYear").setValue(yog.getText().toString());
                mData.child("major").setValue(major.getText().toString());
                mData.child("name").setValue(name.getText().toString());
                Log.e("TESTING", classes.getText().toString());
                List<String> classUpdate = new ArrayList<String>(Arrays.asList(classes.getText().toString().replaceAll("\\s+","").split(",")));
                Log.e("TESTING", classUpdate.toString());
                mData.child("classes").removeValue();
                for (int i = 0; i < classUpdate.size(); i++) {
                    String data = classUpdate.get(i);
                    Log.e("TESTING", data);
                    mData.child("classes").child(data).setValue(true);
                }


                Toast.makeText(ProfileActivity.this, "Profile updated.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100) {
            if(resultCode == Activity.RESULT_OK) {
                Uri imageURI = data.getData();
                uploadImage(imageURI);
                // Set Image
                Picasso.with(getApplicationContext()).load(imageURI).fit().centerCrop().into(PFP);
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        StorageReference fileRef = storageReference.child(fAuth.getCurrentUser().getUid() + ".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Toast.makeText(ProfileActivity.this, "Picture Updated.", Toast.LENGTH_SHORT).show();
                        String url = uri.toString();
                        Log.e("TAG:", "the url is: " + url);

                        String ref = storageReference.getName();
                        Log.e("TAG:", "the ref is: " + ref);

                        DatabaseReference mData = FirebaseDatabase.getInstance().getReference("Profiles").child(fAuth.getCurrentUser().getUid());
                        mData.child("picture").setValue(url);
                    }
                });
            }
        });
    }

    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(getApplicationContext()) != null;
    }


}