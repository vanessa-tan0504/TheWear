package com.thewear.thewearapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class CartRVAdapter extends RecyclerView.Adapter<CartRVAdapter.ViewHolder>{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Clothes> clothesList = new ArrayList<Clothes>();
    Context context;


    public CartRVAdapter(Context context,
                         ArrayList<Clothes> clothesList) {
        this.clothesList = clothesList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rec_item, parent, false);

        CartRVAdapter.ViewHolder vh = new CartRVAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Clothes clothes = clothesList.get(position);

        if (position % 2 != 0) { //odd position - left
                    holder.img.setMaxHeight(300);

                    //set card size
                    holder.card.setLayoutParams(new CardView.LayoutParams(500, 910));
                    ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(500, 720);
                    holder.img.setLayoutParams(layoutParams);

                    //set card margin
                    ViewGroup.MarginLayoutParams cardMargin = (ViewGroup.MarginLayoutParams) holder.card.getLayoutParams();
                    cardMargin.setMargins(15, 0, 0, 40);
                    holder.card.requestLayout();
        }

        holder.title.setText(clothes.getTitle());
                holder.price.setText(String.format("RM%.2f", clothes.getPrice()));

                Glide.with(context).load(clothes.getCoverURL()).into(holder.img);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "clicked" + position, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return clothesList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img ;
        CardView card;
        ConstraintLayout constraintLayout;
        TextView title,price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.row_img);
            card= itemView.findViewById(R.id.rec_card);
            title=itemView.findViewById(R.id.clothes_title);
            price=itemView.findViewById(R.id.clothes_price);
            constraintLayout= itemView.findViewById(R.id.rec_constraint);
        }
    }
}
