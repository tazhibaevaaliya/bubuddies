package com.green.bubuddies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.Viewholder> implements View.OnClickListener {

    private Context context;
    private ArrayList<ListingModel> listingModelArrayList;

    // Constructor
    public ListingAdapter(Context context, ArrayList<ListingModel> courseModelArrayList) {
        this.context = context;
        this.listingModelArrayList = courseModelArrayList;
    }

    @NonNull
    @Override
    public ListingAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listing_card_layout, parent, false);
        return new Viewholder(view);

    }

    // method for filtering our recyclerview items.
    public void filterList(ArrayList<ListingModel> filterllist) {
        // below line is to add our filtered
        // list in our course array list.
        listingModelArrayList = filterllist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ListingAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        ListingModel model = listingModelArrayList.get(position);
        holder.courseNameTV.setText(model.getTitle());
        holder.courseRatingTV.setText("$"+model.getPrice());
        Glide.with(model.getContext()).load(model.getImage()).into(holder.courseIV);
        holder.itemView.setSelected(false);
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return listingModelArrayList.size();
    }

    @Override
    public void onClick(View v) {
        v.setSelected(true);
        //or new Handler().postDelayed(new Runnable...

        //your onClick action
    }

    // View holder class for initializing of
    // your views such as TextView and Imageview.
    public class Viewholder extends RecyclerView.ViewHolder {
        private ImageView courseIV;
        private TextView courseNameTV, courseRatingTV;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            itemView.setClickable(true);
            courseIV = itemView.findViewById(R.id.idIVBookImage);
            courseNameTV = itemView.findViewById(R.id.idPopUpBookTitle);
            courseRatingTV = itemView.findViewById(R.id.idPopUpBookPrice);
        }
    }
}

