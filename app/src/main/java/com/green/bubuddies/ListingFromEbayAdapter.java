package com.green.bubuddies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.ArrayList;

public class ListingFromEbayAdapter extends RecyclerView.Adapter<ListingFromEbayAdapter.Viewholder> {

    private Context context;
    private ArrayList<ListingModel> listingModelArrayList;

    // Constructor
    public ListingFromEbayAdapter(Context context, ArrayList<ListingModel> courseModelArrayList) {
        this.context = context;
        this.listingModelArrayList = courseModelArrayList;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_card_layout, parent, false);
        return new ListingFromEbayAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        ListingModel model = listingModelArrayList.get(position);
        holder.courseNameTV.setText(model.getTitle());
        holder.courseRatingTV.setText(model.getPrice());
        new DownloadImageFromInternet(holder.courseIV).execute(model.getImage());
        //Glide.with(model.getContext()).load(model.getImage()).into(holder.courseIV);

    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView=imageView;
            Toast.makeText(this.imageView.getContext(), "Please wait, it may take a few minute...",Toast.LENGTH_SHORT).show();
        }
        protected Bitmap doInBackground(String... urls) {
            String imageURL=urls[0];
            Bitmap bimage=null;
            try {
                InputStream in=new java.net.URL(imageURL).openStream();
                bimage= BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return listingModelArrayList.size();
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView courseIV;
        private TextView courseNameTV, courseRatingTV;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            courseIV = itemView.findViewById(R.id.idIVBookImage);
            courseNameTV = itemView.findViewById(R.id.idPopUpBookTitle);
            courseRatingTV = itemView.findViewById(R.id.idPopUpBookPrice);
        }
    }
}

