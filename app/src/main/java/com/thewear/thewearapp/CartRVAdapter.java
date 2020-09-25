package com.thewear.thewearapp;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

public class CartRVAdapter extends RecyclerView.Adapter<CartRVAdapter.ViewHolder>{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Order> orderList = new ArrayList<Order>();
    Context context;

    public CartRVAdapter(Context context, ArrayList<Order> orderList) {
        this.orderList = orderList;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,size,color,qty,price;
        ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.cart_title);
            size = itemView.findViewById(R.id.cart_size);
            color = itemView.findViewById(R.id.cart_color);
            qty = itemView.findViewById(R.id.cart_qty);
            price = itemView.findViewById(R.id.cart_price);
            img=itemView.findViewById(R.id.cart_image);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);

        CartRVAdapter.ViewHolder vh = new CartRVAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Order order = orderList.get(position);
        holder.title.setText(order.getTitle());
        holder.price.setText("RM "+order.getTotalprice()+"");
        holder.size.setText("Size: "+order.getSize()+"");
        holder.color.setText("Color: "+order.getColor()+"");
        holder.qty.setText("Quantity: "+order.getQty());

        Glide.with(context).load(order.getItemURL())
                .transition(DrawableTransitionOptions.withCrossFade()).centerCrop().into(holder.img);

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


}
