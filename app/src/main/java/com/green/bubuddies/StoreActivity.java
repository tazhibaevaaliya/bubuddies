package com.green.bubuddies;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.green.bubuddies.databinding.ActivityStoreBinding;
import com.green.bubuddies.ui.main.SectionsPagerAdapter;

public class StoreActivity extends AppCompatActivity {

    private ActivityStoreBinding binding;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button btnPost, btnCancel;
    private TextView txtListedBookName;
    private TextView txtListedBookPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewListingWindow();
            }
        });
        Toast.makeText(StoreActivity.this, "check out intent starting in 5 seconds", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(StoreActivity.this, "check out intent starts", Toast.LENGTH_SHORT).show();
                Intent i=new Intent(StoreActivity.this,CheckoutActivity.class);
                String listId = "-MpOoxYKfvW1Kk3HphAt"; //example
                i.putExtra("ListingID", listId);
                startActivity(i);
            }
        }, 5000);

    }

    //Menu creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextual_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //handle item selection:
        switch(item.getItemId()){
            case R.id.shopping_cart:
                Toast.makeText(StoreActivity.this, "check out intent starts", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(StoreActivity.this, CheckoutActivity.class);
                String listId = "-MpOoxYKfvW1Kk3HphAt"; //example
                i.putExtra("ListingID", listId);
                startActivity(i);
                break;
            case R.id.new_listing:
                //calling a new intent for listing form
                createNewListingWindow();
                break;
            case R.id.profile:
                //start new intent to show profile information
//                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
//                startActivity(intent);
                Toast.makeText(StoreActivity.this,"Profile Activity Opened", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void createNewListingWindow(){
        dialogBuilder = new AlertDialog.Builder(StoreActivity.this);
        final View listingPopupWindow = getLayoutInflater().inflate(R.layout.listing_popup,null);

        //creating reference to the Views
        btnPost = (Button) listingPopupWindow.findViewById(R.id.btnPost);
        btnCancel = (Button) listingPopupWindow.findViewById(R.id.btnCancel);

        txtListedBookName = (TextView) listingPopupWindow.findViewById(R.id.txtListedBookName);
        txtListedBookPrice = (TextView) listingPopupWindow.findViewById(R.id.txtListedBookPrice);

        dialogBuilder.setView(listingPopupWindow);
        dialog = dialogBuilder.create();
        dialog.show();

        //binding an event:
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
                Toast.makeText(getBaseContext(),"Item was successfully posted!",Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    private void submitPost(){

        if(txtListedBookName.getText().toString().equals("")){
            txtListedBookName.setError("REQUIRED");
            return;
        }

        final String title = txtListedBookName.getText().toString();
        final int price = Integer.parseInt(txtListedBookPrice.getText().toString());

        //getting a database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        //creating a new instance of listing object
        Listing listing = new Listing(title,price);
        myRef.child("listings").push().setValue(listing); //writing to the database

        btnPost.setEnabled(false); //enabling the button

    }
}

