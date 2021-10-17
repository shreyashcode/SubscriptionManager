package com.example.ott;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CardRecyclerViewAdapter extends RecyclerView.Adapter<CardRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Subscription> localDataSet;
    ItemClickListener itemClickListener;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //private final TextView textView;

        private TextView nameTvCard, costTvCard, nextPay;
        private ImageView logo;
        private CardView global;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            global = view.findViewById(R.id.global);
            nameTvCard = view.findViewById(R.id.ottNameCard);
            costTvCard = view.findViewById(R.id.ottCostCard);
            nextPay = view.findViewById(R.id.ottNextPaymentCard);
            logo = view.findViewById(R.id.ottLogoCard);
            //textView = (TextView) view.findViewById(R.id.textView);
            global.setOnClickListener(this);
        }

        public TextView getNameTvCard() {
            return nameTvCard;
        }

        public TextView getCostTvCard() {
            return costTvCard;
        }

        public TextView getNextPay() {
            return nextPay;
        }

        public ImageView getLogo() {
            return logo;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.itemClicked(getAdapterPosition());
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public CardRecyclerViewAdapter(ArrayList<Subscription> dataSet, ItemClickListener itemClickListener, Context context) {
        localDataSet = dataSet;
        this.itemClickListener = itemClickListener;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.subs_item, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        Subscription currentSub = localDataSet.get(position);
        viewHolder.getNameTvCard().setText(currentSub.getName());
        viewHolder.getCostTvCard().setText(MessageFormat.format("Rs. {0} /month", currentSub.getCost()));
        viewHolder.getNextPay().setText("next Payment "+convertTime(currentSub.getRenewDate()));
        String url = "https://i.postimg.cc/KcpJ4yyz/icons8-netflix-desktop-app-480.png";
        if(Common.map.containsKey(currentSub.getName())){
            url = Common.map.get(currentSub.getName());
        }

        Glide.with(context)
                .load(url)
                .into(viewHolder.logo);
//        viewHolder.getT
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd-MM-yy");
        return format.format(date);
    }
}
