package com.green.bubuddies;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PriceComparison extends AppCompatActivity {

    private Button btnFind;
    public String url;
    TextInputLayout txtBookName;
    static ArrayList<String> names;
    static ArrayList<String> prices;
    static ArrayList<String> images;
    private ListView lv_items;
    private ListAdapter lvAdapter;
    private RecyclerView listingRV;
    Toolbar mToolbar;
    // Arraylist for storing data
    private ArrayList<ListingModel> listingModelArrayList;
    final String default_picture = "https://lh3.googleusercontent.com/proxy/yS_DrYbvuDF-noQIDvVJk9Die3h8Gf1v3eYi0k16S_xxT_NoQTRkSaWF1BSWQZMO-MhLecz_eqyJtLcsPy9WBoBW"; //default picture for each listing


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_compare);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Boiler Plate: creating Views
        btnFind = (Button) findViewById(R.id.btnFindPrice);

        // here we have created new array list and added data to it.
        listingModelArrayList = new ArrayList<>();

        listingRV = (RecyclerView) findViewById(R.id.idRVListingeBay);
        //mToolbar = (Toolbar) findViewById(R.id.toolbarPriceCompare);
        txtBookName = (TextInputLayout) findViewById(R.id.txtBookName);

//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
//            getSupportActionBar().setTitle("");
//        }


//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(PriceComparison.this,StoreActivity.class));
//            }
//        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //url = "https://api.sandbox.ebay.com/buy/browse/v1/item_summary/search?q=" + txtBookName.getText().toString()+"&limit=5";
                //https://api.ebay.com/buy/browse/v1/item_summary/search?q=drone&limit=3
//                token = "AgAAAA**AQAAAA**aAAAAA**OmmcYQ**nY+sHZ2PrBmdj6wVnY+sEZ2PrA2dj6wFk4aiDZmKoQqdj6x9nY+seQ**R7YFAA**AAMAAA**yIdyYgpPLCGK2ybuko9Ykb8/eSIls1Oxc+f5F5qRNAw/m3xw4oJQAFX75v2uWnQsipBQmF7fAlb9zzsoVCzT1Q5Pm+tiod2raJh2gXX9USrwo4IB7YjwDishK21d8CKhu4DCXrgEFSIlq/+6KOcMF2nVnS8J6jci3/R9MzwyaJM6ydeBu/3zxRP+3FVoVgpMiDw73cV5xiJaWb2XZZxM7QpU5ae+WD8CXdCkO6/XB1VHrNSfQrJn+tl5Fv0kWtD/iwqxCcQ6ZEca2vHMuSRkvweiisUQGY5eRRNDLY6L1N2W1RYaOcCXmpi56GY6kGntVM7WlRJ2+tXDikSbpxiAE3bwuA9aGpS0f01KpyRE4k/kQoOiJxlP9embaVwBys2R4yP4VNHwiaXd7nluInunCXP+voSkeEe1rKGDLOafGBCSCeJm58rjaG+YpQy70n8Ou84ikRa5Yj6hNm3drssoOZ7I6oBvc9wKgwFE98RO3bXc19vH/EQ16qDmsEOP3NpHojR1IJLd2EyKDPnU0P5oSAY4EtgVPfeF8a7E7+56Bv9OzxOvC+//lSVgm6s3vJl4pnayiIhQS537IohuzfzrHhu/+YfyPQvSZ9/+X112PXarOdHp3BpA+VPS3lvGUwSDzBX+TebC5awjz4SxsrYYKcb+y0sjPH4xBQAkt6p5B/M1zOfyLB+l+BGFY0YGjXX1aiFkBCJ6tmXVLstcxg3viAsLONwTWkv7q5YuJMHwRNWyv+bWcDClaw11c7gjm18y";
                RequestQueue queue = Volley.newRequestQueue(getBaseContext()); // preparing the request object and requesting a queue using Volley library
                url = "https://ebay-search.p.rapidapi.com/search.php?query="+txtBookName.getEditText().getText().toString().trim(); // inserting the query parameter (name of object)
                // from user input to TextView "txtBookName"

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, // Request a string response from the provided URL.
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) { //catching JSONObject from API request

                                JSONArray items = null;

                                try {
                                    items = response.getJSONArray("items"); //parsing JSONObject to retrieve "item" JSONArray
                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }

                                parseData(items); //parse the data

                                if(names.size()!=0){
                                    //lvAdapter = new MyCustomAdapter(getBaseContext());  //instead of passing the boring default string adapter, let's pass our own, see class MyCustomAdapter below!
                                    //lv_items.setAdapter(lvAdapter);
                                    Toast.makeText(getBaseContext(),"Successful",Toast.LENGTH_SHORT).show(); //displaying a success message in the case of
                                }
                                else{
                                    Toast.makeText(getBaseContext(), "No items found! Try something else!", Toast.LENGTH_LONG).show();
                                }

                                // successful request of an API

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getBaseContext(),"It did not work!",Toast.LENGTH_SHORT).show(); //displaying an error message in the case of
                        //failure to get an access an API
                    }
                })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError { //defining the headers for the API request
                        Map<String, String>  params = new HashMap<String, String>();
                        //params.put("Authorization",token);
                        params.put("x-rapidapi-host", "ebay-search.p.rapidapi.com"); //defining the host
                        params.put("x-rapidapi-key", "0115038d54msh337444eec807d58p12d0c7jsn0efcb7dc5324"); //defining the API key provided from RAPIDAPI

                        return params;
                    }};

                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);

            }

        });
    }

    private void parseData(JSONArray items){
        JSONObject item = null;
        names = new ArrayList<String>();
        prices = new ArrayList<String>();
        images = new ArrayList<String>();

        //parsing through the data
        if(items.length()>0){
            for(int i=0;i<items.length();i++){  //looping through JSONArray to get JSONObject each time
                try {
                    item = items.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(item.isNull("title") | item.optString("title").equals("") | item.isNull("price") | item.optString("price").equals("")){
                    continue;
                }
                //name = item.optString("title"); //getting the field "title"
//                                        price = item.isNull("price")?"":item.optString("price"); //getting the field "price"
                names.add(item.optString("title")); //adding the title information to the ArrayList
                prices.add(item.optString("price")); //adding the price information to the ArrayList
                Log.d("Size", String.valueOf(prices.size()));
            }
        }

        if(names.size()!=0){
            for(int i=0; i<names.size(); i++){
                listingModelArrayList.add(new ListingModel(names.get(i),prices.get(i),default_picture,getBaseContext()));
            }
            // we are initializing our adapter class and passing our arraylist to it.
            ListingFromEbayAdapter courseAdapter = new ListingFromEbayAdapter(getBaseContext(), listingModelArrayList);

            // below line is for setting a layout manager for our recycler view.
            // here we are creating vertical list so we will provide orientation as vertical
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);

            // in below two lines we are setting layoutmanager and adapter to our recycler view.
            listingRV.setLayoutManager(linearLayoutManager);
            listingRV.setAdapter(courseAdapter);
        }
    }
}

class MyCustomAdapter extends BaseAdapter {

    private
    String[] titles = new String[PriceComparison.names.size()];             //Keeping it simple.  Using Parallel arrays is the introductory way to store the List data.
    String[]  prices = new String[PriceComparison.prices.size()];  //the "better" way is to encapsulate the list items into an object, then create an arraylist of objects.
    //     int episodeImages[];         //this approach is fine for now.
    ArrayList<Integer> episodeImages;  //Well, we can use one arrayList too...  Just mixing it up here, Arrays or Templated ArrayLists, you choose.

//    ArrayList<String> episodes;
//    ArrayList<String> episodeDescriptions;

    Button btnRandom;
    Context context;   //Creating a reference to our context object, so we only have to get it once.  Context enables access to application specific resources.
    // Eg, spawning & receiving intents, locating the various managers.

    //STEP 2: Override the Constructor, be sure to:
    // grab the context, we will need it later, the callback gets it as a parm.
    // load the strings and images into object references.
    public MyCustomAdapter(Context aContext) {
//initializing our data in the constructor.
        context = aContext;  //saving the context we'll need it again.
//        episodes =aContext.getResources().getStringArray(R.array.episodes);  //retrieving list of episodes predefined in strings-array "episodes" in strings.xml
//        episodeDescriptions = aContext.getResources().getStringArray(R.array.episode_descriptions);

        for(int i=0; i<PriceComparison.names.size();i++){
            titles[i] = PriceComparison.names.get(i);
            prices[i] = PriceComparison.prices.get(i);
        }

    }


    // ListView uses this to determine how many rows to render.
    @Override
    public int getCount() {
//        return episodes.size(); //all of the arrays are same length, so return length of any... ick!  But ok for now. :)
        return titles.length;   //all of the arrays are same length, so return length of any... ick!  But ok for now. :)
    }


    @Override
    public Object getItem(int position) {
//        return episodes.get(position);  //In Case you want to use an ArrayList
        return titles[position];        //really should be returning entire set of row data, but it's up to us, and we aren't using this call.
    }

    @Override
    public long getItemId(int position) {
        return position;  //Another call we aren't using, but have to do something since we had to implement (base is abstract).
    }

    // getView(..) is how each row gets rendered.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //position is the index of the row being rendered.
        //convertView represents the Row (it may be null),
        // parent is the layout that has the row Views.

//Inflating the listview row based on the xml.
        View row;  //this will refer to the row to be inflated or displayed if it's already been displayed. (listview_row.xml)

//// Let's optimize a bit by checking to see if we need to inflate, or if it's already been inflated...
        if (convertView == null){  //indicates this is the first time we are creating this row.
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  //Inflater's are awesome, they convert xml to Java Objects!
            row = inflater.inflate(R.layout.listview_row, parent, false);
        }
        else
        {
            row = convertView;
        }

// getting references to the views within that row and fill with the appropriate text and images.
        ImageView imgEpisode = (ImageView) row.findViewById(R.id.imgEpisode);  //Q: Notice we prefixed findViewByID with row, why?  A: Row, is the container.
        TextView bookTitle = (TextView) row.findViewById(R.id.bookTitle);
        TextView bookPrice = (TextView) row.findViewById(R.id.bookPrice);

        bookTitle.setText(titles[position]);
        bookPrice.setText(prices[position]);
        //imgEpisode.setImageResource(episodeImages.get(position).intValue());


//the row has been inflated and filled with data, return it.
        return row;  //once the row is fully constructed, return it.  Hey whatif we had buttons, can we target onClick Events within the rows, yep!
//return convertView;

    }

}
