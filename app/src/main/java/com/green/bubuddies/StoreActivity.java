package com.green.bubuddies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class StoreActivity extends AppCompatActivity implements BottomMenu.BtmMenuActivity {


    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private Button btnPost, btnCancel, btnUpload;
    private Button btnClose, btnCheckOut;
    private TextInputLayout txtListedBookName;
    private TextInputLayout txtListedBookPrice;
    private TextView txtListedBookDescription;
    private TextView idPopUpBookPrice, idPopUpBookTitle, idPopUpBookDescription;
    private ImageView idPopUpBookPic, ListedBookPic;
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

//        // Disable back icon in top left and hide app name.

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(false);

        Bundle extras = getIntent().getExtras();
        if(extras!= null) {
            curr_user = extras.getString("UID");
        } else {
            curr_user = UserDetails.uid;
        }

        UserDetails.uid = curr_user;

        getData();



        fAuth = FirebaseAuth.getInstance(); //getting user UID
        FirebaseUser currentUser = fAuth.getCurrentUser();

        listingRV = (RecyclerView) findViewById(R.id.idRVListing);
        listingRV.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), listingRV ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        createListingWindow(position);
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
                if(child.child("owner").getValue().toString().equals(curr_user)){
                    continue;
                }
                String title = child.child("title").getValue().toString(); //getting the title of each listing
                String price = child.child("price").getValue().toString(); //getting the price of each listing
                if(!child.child("imageURI").exists()){
                    imageURIs.add(default_picture);
                }
                else{
                    imageURIs.add(child.child("imageURI").getValue().toString()); //getting the image URI String
                }
                if(!child.child("description").exists()){
                    listingDescriptions.add("Sorry! There is not any description");
                }
                else if(child.child("description").getValue().equals("")){
                    listingDescriptions.add("Empty Description! Nothing to report");
                }
                else{
                    listingDescriptions.add(child.child("description").getValue().toString());
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
        dialogBuilder = new AlertDialog.Builder(StoreActivity.this,R.style.CustomAlertDialog);
        final View listingPopupWindow = getLayoutInflater().inflate(R.layout.listing_popup,null);

        //creating reference to the Views
        btnClose = (Button) listingPopupWindow.findViewById(R.id.btnClose);
        btnCheckOut = (Button) listingPopupWindow.findViewById(R.id.btnCheckOut);

        imageURI = imageURIs.get(position); //initializing URI for book image

        idPopUpBookTitle = (TextView) listingPopupWindow.findViewById(R.id.idPopUpBookTitle);
        idPopUpBookPrice = (TextView) listingPopupWindow.findViewById(R.id.idPopUpBookPrice);
        idPopUpBookDescription = (TextView) listingPopupWindow.findViewById(R.id.idPopUpBookDescription);
        idPopUpBookPic = (ImageView) listingPopupWindow.findViewById(R.id.idIVBookImage);


        //populating the title, price and description of the listing
        idPopUpBookPrice.setText("$" + prices.get(position));
        idPopUpBookTitle.setText(titles.get(position));
        idPopUpBookDescription.setText(listingDescriptions.get(position));
        Picasso.with(getBaseContext()).load(imageURI).into(idPopUpBookPic);


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
                intent.putExtra("UID",curr_user);
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
                filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //handle item selection:
        switch(item.getItemId()){

            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;

            case R.id.comparePrice:
                Intent intent1 = new Intent(StoreActivity.this,PriceComparison.class);
                startActivity(intent1);
                break;

            //createNewListingWindow();

        }

        return super.onOptionsItemSelected(item);
    }

    private void filter(String text) {
        // creating a new array list to filter our data.
        ArrayList<ListingModel> filteredlist = new ArrayList<>();

        // running a for loop to compare elements.
        for (ListingModel item : listingModelArrayList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredlist.add(item);
            }
        }
        if (filteredlist.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            // at last we are passing that filtered
            // list to our adapter class.
            listingAdapter.filterList(filteredlist);
            listingAdapter.notifyDataSetChanged();
        }
    }

    public void createNewListingWindow(){
        dialogBuilder = new AlertDialog.Builder(StoreActivity.this, R.style.CustomAlertDialog);
        final View listingPopupWindow = getLayoutInflater().inflate(R.layout.new_listing_popup,null);

        //creating reference to the Views
        btnPost = (Button) listingPopupWindow.findViewById(R.id.btnPost);
        btnCancel = (Button) listingPopupWindow.findViewById(R.id.btnCancel);
        btnUpload = (Button) listingPopupWindow.findViewById(R.id.btnUpload);

        imageURI = default_picture; //initializing URI for book image

        txtListedBookName = (TextInputLayout) listingPopupWindow.findViewById(R.id.txtListedBookName);
        txtListedBookPrice = (TextInputLayout) listingPopupWindow.findViewById(R.id.txtListedBookPrice);
        txtListedBookDescription = (EditText) listingPopupWindow.findViewById(R.id.txtListedBookDescription);
        ListedBookPic = (ImageView) listingPopupWindow.findViewById(R.id.imageView2);
        Picasso.with(getBaseContext()).load(imageURI).into(ListedBookPic);


        dialogBuilder.setView(listingPopupWindow);
        dialog = dialogBuilder.create();
        dialog.show();

        //binding an event:
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
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
                uploadImage(data.getData());
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        String key = FirebaseDatabase.getInstance().getReference().child("books").push().getKey();
        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("books/"+key);
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageURI = String.valueOf(uri);
                        Picasso.with(getBaseContext()).load(imageURI).into(ListedBookPic);
                    }
                });
            }
        });
    }

    private void submitPost(){

        fAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = fAuth.getCurrentUser();

        if(txtListedBookName.getEditText().getText().toString().equals("") |  txtListedBookPrice.getEditText().getText().toString().equals("") | txtListedBookDescription.getText().toString().equals("")){
            Toast.makeText(getBaseContext(),"Empty Field(s)! Need an input", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
        else{
            final String title = txtListedBookName.getEditText().getText().toString().trim();
            final double price = Double.parseDouble(txtListedBookPrice.getEditText().getText().toString());
            String description = txtListedBookDescription.getText().toString();

            if(imageURI==null){
                imageURI = default_picture; //if there is no image close Menu window pop-up
            }

            //getting a database reference
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();

            //creating a new instance of listing object

            Listing listing = new Listing(title,price,curr_user,imageURI,description);
            Log.e("description", listing.getDescription());
            Log.e("description", listing.getPrice().toString());
            Log.e("description", listing.getOwner());
            Log.e("description", listing.getTitle());

//            String key = myRef.child("listings").push().getKey();
            myRef.child("listings").push().setValue(listing); //writing to the database
        }
        btnPost.setEnabled(false); //enabling the button

    }

    @Override
    public void updateClickableButtons() {
        BottomMenu fragment = (BottomMenu) getSupportFragmentManager().findFragmentById(R.id.btmFragmentStore);
        fragment.disableClick(BottomMenu.STORE);
    }

    @Override
    public void changeActivity(int nextAct) {
        //switch cases to correct activity in final implementation.
        switch(nextAct) {
            case (BottomMenu.PROFILE):
                Intent i = new Intent(StoreActivity.this, MainActivity.class);
                i.putExtra("UID", curr_user);
                startActivity(i);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
            case (BottomMenu.PAIR):
                i = new Intent(StoreActivity.this, Pair.class);
                i.putExtra("UID", curr_user);
                startActivity(i);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
            case (BottomMenu.MESSAGE):
                i = new Intent(StoreActivity.this, Users.class);
                i.putExtra("UID",curr_user);
                startActivity(i);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
            case (BottomMenu.STORE):
                i = new Intent(StoreActivity.this, StoreActivity.class);
                i.putExtra("UID", curr_user);
                startActivity(i);
                overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
        }

    }


}

