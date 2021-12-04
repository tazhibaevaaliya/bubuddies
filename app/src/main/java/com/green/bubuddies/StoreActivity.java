package com.green.bubuddies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class StoreActivity extends AppCompatActivity {


    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button btnPost, btnCancel, btnUpload;
    private Button btnClose, btnCheckOut;
    private TextInputLayout txtListedBookName;
    private TextInputLayout txtListedBookPrice;
    private TextView txtListedBookDescription;
    private TextView idPopUpBookPrice, idPopUpBookTitle, idPopUpBookDescription;
    //private ActivityStoreBinding binding;
    private RecyclerView listView;
    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<String> prices = new ArrayList<String>();
    ArrayList<String> ids = new ArrayList<String>();
    ArrayList<String> imageURIs = new ArrayList<String>();
    ArrayList<String> listingDescriptions = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    int clickedRow = -1;
    private RecyclerView listingRV;
    FirebaseAuth fAuth;
    String curr_user;
    StorageReference storageReference;
    String imageURI;
    LinearLayoutManager linearLayoutManager;
    ListingAdapter listingAdapter;
    final String default_picture = "https://firebasestorage.googleapis.com/v0/b/bubuddies-3272b.appspot.com/o/books_default.gif?alt=media&token=29717e83-fa20-49f3-8da1-8a082a14c7cc"; //default picture for each listing

    // Arraylist for storing data
    private ArrayList<ListingModel> listingModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        // here we have created new array list and added data to it.
        listingModelArrayList = new ArrayList<>();

        Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            curr_user = extras.getString("UID");
        } else {
            curr_user = UserDetails.uid;
        }

        getData();



        fAuth = FirebaseAuth.getInstance(); //getting user UID
        FirebaseUser currentUser = fAuth.getCurrentUser();

        listingRV = (RecyclerView) findViewById(R.id.idRVListing);
        listingRV.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), listingRV ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        createListingWindow(position);
                        Toast.makeText(getBaseContext(),"Clicked row is" + String.valueOf(position), Toast.LENGTH_SHORT).show();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,titles);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                clickedRow = position;
//            }
//        });

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
    //reading data from Firebase and saving it into listings----------------------------------------------------------------------------------
    //getting a database reference
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("listings");

    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {


            //Load Data (title, price and image)
            for (DataSnapshot child : snapshot.getChildren()) {
                String title = child.child("title").getValue().toString(); //getting the title of each listing
                String price = child.child("price").getValue().toString(); //getting the price of each listing
                if(!child.child("picture").exists()){
                    imageURIs.add(default_picture);
                }
                else{
                    imageURIs.add(child.child("picture").getValue().toString()); //getting the image URI String
                }
                if(!child.child("description").exists() | child.child("description").getValue().equals("")){
                    listingDescriptions.add("Sorry! There is not any description");
                }else{
                    listingDescriptions.add(child.child("picture").getValue().toString());
                }

                ids.add(child.getKey().toString()); //getting ids of each listing
                titles.add(title);
                prices.add(price);

            }

            for(int i=0; i<titles.size(); i++){
                listingModelArrayList.add(new ListingModel(titles.get(i),prices.get(i),imageURIs.get(i),getBaseContext()));
            }

            // we are initializing our adapter class and passing our arraylist to it.
            listingAdapter = new ListingAdapter(getBaseContext(), listingModelArrayList);

            // below line is for setting a layout manager for our recycler view.
            // here we are creating vertical list so we will provide orientation as vertical
            linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);


            // in below two lines we are setting layoutmanager and adapter to our recycler view.
            listingRV.setLayoutManager(linearLayoutManager);
            listingRV.setAdapter(listingAdapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            return;
        }
    });
}

    public void createListingWindow(int position){
        dialogBuilder = new AlertDialog.Builder(StoreActivity.this);
        final View listingPopupWindow = getLayoutInflater().inflate(R.layout.listing_popup,null);

        //creating reference to the Views
        btnClose = (Button) listingPopupWindow.findViewById(R.id.btnClose);
        btnCheckOut = (Button) listingPopupWindow.findViewById(R.id.btnCheckOut);

        imageURI = null; //initializing URI for book image

        idPopUpBookTitle = (TextView) listingPopupWindow.findViewById(R.id.idPopUpBookTitle);
        idPopUpBookPrice = (TextView) listingPopupWindow.findViewById(R.id.idPopUpBookPrice);
        idPopUpBookDescription = (TextView) listingPopupWindow.findViewById(R.id.idPopUpBookDescription);

        //populating the title, price and description of the listing
        idPopUpBookPrice.setText(prices.get(position));
        idPopUpBookTitle.setText(titles.get(position));
        idPopUpBookDescription.setText(listingDescriptions.get(position));

        dialogBuilder.setView(listingPopupWindow);
        dialog = dialogBuilder.create();
        dialog.show();

        //binding an event:
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedRow = position;
                //Toast.makeText(getBaseContext(),"Item was successfully checked out!",Toast.LENGTH_LONG).show();

                Intent intent = new Intent(StoreActivity.this,CheckoutActivity.class);
                intent.putExtra("Listing ID", ids.get(clickedRow));
                startActivity(intent);
                dialog.dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
//            case R.id.shopping_cart:
//
//                break;
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
        final View listingPopupWindow = getLayoutInflater().inflate(R.layout.new_listing_popup,null);

        //creating reference to the Views
        btnPost = (Button) listingPopupWindow.findViewById(R.id.btnPost);
        btnCancel = (Button) listingPopupWindow.findViewById(R.id.btnCancel);
        btnUpload = (Button) listingPopupWindow.findViewById(R.id.btnUpload);

        imageURI = null; //initializing URI for book image

        txtListedBookName = (TextInputLayout) listingPopupWindow.findViewById(R.id.txtListedBookName);
        txtListedBookPrice = (TextInputLayout) listingPopupWindow.findViewById(R.id.txtListedBookPrice);
        txtListedBookDescription = (TextView) listingPopupWindow.findViewById(R.id.txtListedBookDescription);

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

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 100);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100) {
            if(resultCode == Activity.RESULT_OK) {
                imageURI = data.getData().toString();
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        StorageReference fileRef = storageReference.child(curr_user + ".jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Toast.makeText(StoreActivity.this, "Picture Updated.", Toast.LENGTH_SHORT).show();
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

    private void submitPost(){

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();

        if(txtListedBookName.getEditText().getText().equals("")){
            txtListedBookName.setError("REQUIRED");
            return;
        }

        if(txtListedBookPrice.getEditText().getText().equals("")){
            txtListedBookPrice.setError("REQUIRED");
            return;
        }

        if(txtListedBookDescription.getText().toString().equals("")){
            txtListedBookName.setError("REQUIRED");
            return;
        }
        final String title = txtListedBookName.getEditText().getText().toString().trim();
        final int price = Integer.parseInt(txtListedBookPrice.getEditText().getText().toString().trim());
        final String description = txtListedBookDescription.getText().toString();

        if(imageURI==null){
            imageURI = default_picture; //if there is no image close Menu window pop-up
        }

        //getting a database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        //creating a new instance of listing object
        Listing listing = new Listing(title,price,curr_user,imageURI,description);
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

