package com.thewear.thewearapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class HisRVAdapter extends RecyclerView.Adapter<HisRVAdapter.ViewHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Order> orderList = new ArrayList<Order>();
    Context context;

    public HisRVAdapter(Context context, ArrayList<Order> orderList) {
        this.orderList = orderList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView id,title,size,color,qty,price;
        ImageView img;
        ConstraintLayout bg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            id= itemView.findViewById(R.id.his_id);
            title = itemView.findViewById(R.id.his_title);
            size = itemView.findViewById(R.id.his_size);
            color = itemView.findViewById(R.id.his_color);
            qty = itemView.findViewById(R.id.his_qty);
            price = itemView.findViewById(R.id.his_price);
            img=itemView.findViewById(R.id.his_image);
            bg=itemView.findViewById(R.id.his_bg);
        }
    }

    @NonNull
    @Override
    public HisRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);

        HisRVAdapter.ViewHolder vh = new HisRVAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HisRVAdapter.ViewHolder holder, int position) {
        final Order order = orderList.get(position);
        holder.id.setText("Order ID: "+order.getOrderID());
        holder.title.setText(order.getTitle());
        holder.price.setText("RM "+order.getTotalprice()+"");
        holder.size.setText("Size: "+order.getSize()+"");
        holder.color.setText("Color: "+order.getColor()+"");
        holder.qty.setText("Quantity: "+order.getQty());

        Glide.with(context).load(order.getItemURL())
                .transition(DrawableTransitionOptions.withCrossFade()).centerCrop().into(holder.img);

        if (position % 2 == 0) {
            holder.bg.setBackgroundColor(Color.LTGRAY);

        }

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
