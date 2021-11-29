package com.green.bubuddies;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class StoreActivity extends AppCompatActivity {


    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button btnPost, btnCancel;
    private TextView txtListedBookName;
    private TextView txtListedBookPrice;
    //private ActivityStoreBinding binding;
    private ListView listView;
    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<String> prices = new ArrayList<String>();
    ArrayList<String> ids = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    int clickedRow = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        listView = (ListView) findViewById(R.id.listItems);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,titles);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedRow = position;
            }
        });

        //reading data from Firebase and saving it into listings
        getData();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewListingWindow();
            }
        });
//        FloatingActionButton fab = binding.fab;
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                createNewListingWindow();
//            }
//        });

    }

    public void getData(){

        //getting a database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("listings");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Log.d("type", myRef.toString());


                //Looping through each child to get title and price of the book
                for (DataSnapshot child : snapshot.getChildren()) {
                    ids.add(child.getKey().toString()); //getting ids of each listing
                    titles.add(child.child("title").getValue().toString());
                    prices.add(child.child("price").getValue().toString());
                }

                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                return;
            }
        });
    }




    //Menu creation
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextual_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        //searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //handle item selection:
        switch(item.getItemId()){

            case R.id.comparePrice:
                Intent intent1 = new Intent(StoreActivity.this,PriceComparison.class);
                startActivity(intent1);
                break;
            case R.id.shopping_cart:
                if(clickedRow==-1){
                    Toast.makeText(StoreActivity.this, "Please Select an Item first!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(StoreActivity.this,StoreActivity.class);
                    intent.putExtra("Listing ID", ids.get(clickedRow));
                    startActivity(intent);
                }
                break;
//            case R.id.new_listing:
//                //calling a new intent for listing form
//                Intent intent = new Intent(MainActivity.this, ItemListing.class);
//                startActivity(intent);
//                break;

                //createNewListingWindow();


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

//    public void createNewListingWindow(){
//        dialogBuilder = new AlertDialog.Builder(MainActivity.this);
//        final View listingPopupWindow = getLayoutInflater().inflate(R.layout.listing_popup, null);
//
//        //creating reference to the Views
//        btnPost = (Button) findViewById(R.id.btnPost);
//        btnCancel = (Button) findViewById(R.id.btnCancel);
//
//        txtListedBookName = (TextView) findViewById(R.id.txtListedBookName);
//        txtListedBookPrice = (TextView) findViewById(R.id.txtListedBookPrice);
//
//        dialogBuilder.setView(listingPopupWindow);
//        dialog = dialogBuilder.create();
//        dialog.show();
//
//        //binding an event:
//        btnPost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                submitPost();
//            }
//        });
//
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//    }


}





