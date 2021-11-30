package com.green.bubuddies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Listing_form extends Fragment {

    private Button btnPost;
    private TextView txtListedBookName;
    private TextView txtListedBookPrice;
    private String title = null;
    private int price = 0;

    //empty constructor
    public Listing_form(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listing_form, container, false);

        //creating reference to the Views
        btnPost = (Button) view.findViewById(R.id.btnPost);
        txtListedBookName = (TextView) view.findViewById(R.id.txtListedBookName);
        txtListedBookPrice = (TextView) view.findViewById(R.id.txtListedBookPrice);


        //binding an event:
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //setting values of TextViews to the variables values
//                try {
//                    title = txtListedBookName.getText().toString();
//                    price = Integer.parseInt(txtListedBookPrice.getText().toString());
//                } catch(Exception e) {
//                    Toast.makeText(getContext(),"The wrong inputs",Toast.LENGTH_SHORT).show();
//                }
                btnPost.setEnabled(false);
                submitPost();
            }
        });

        return view;
    }

//    // With the function
//    private fun modifyText(numberText: String) {
//        uiEditTextNumber.setText(numberText)
//        uiEditTextNumber.setSelection(numberText.length)
//    }

    private void submitPost(){

        if(txtListedBookName.getText().toString().equals("")){
            txtListedBookName.setError("REQUIRED");
            btnPost.setEnabled(true);
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


    public interface ListingFormListener{
        public void sendMessage(String title, int price);
    }

}

