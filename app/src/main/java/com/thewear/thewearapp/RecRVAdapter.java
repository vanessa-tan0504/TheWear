package com.thewear.thewearapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
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
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecRVAdapter extends RecyclerView.Adapter<RecRVAdapter.ViewHolder>{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Clothes> clothesList = new ArrayList<Clothes>();
    Context context;


    public RecRVAdapter(Context context,
                        ArrayList<Clothes> clothesList) {
        this.clothesList = clothesList;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rec_item, parent, false);

        RecRVAdapter.ViewHolder vh = new RecRVAdapter.ViewHolder(v);

        return vh;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Clothes clothes = clothesList.get(position);

        if (position % 2 != 0) { //odd position - left
            if(position==1){
                //set card margin
                holder.card.setLayoutParams(new CardView.LayoutParams(500, 900));
                ViewGroup.MarginLayoutParams cardMargin = (ViewGroup.MarginLayoutParams) holder.card.getLayoutParams();
                cardMargin.setMargins(15, 120, 0, 40);
                holder.card.requestLayout();
            }
            else{
                holder.card.setLayoutParams(new CardView.LayoutParams(500, 800));
                ViewGroup.MarginLayoutParams cardMargin = (ViewGroup.MarginLayoutParams) holder.card.getLayoutParams();
                cardMargin.setMargins(15, 0, 0, 40);
                holder.card.requestLayout();
            }
        }

        holder.title.setText(clothes.getTitle());
        holder.price.setText(String.format("RM%.2f", clothes.getPrice()));

                Glide.with(context).load(clothes.getCoverURL())
                        .transition(DrawableTransitionOptions.withCrossFade()).into(holder.img);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(),IdvItemActivity.class);
                        intent.putExtra("ItemID",clothes.getId()+"");
                        v.getContext().startActivity(intent);
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
            img=itemView.findViewById(R.id.item_img);
            card= itemView.findViewById(R.id.rec_card);
            title=itemView.findViewById(R.id.clothes_title);
            price=itemView.findViewById(R.id.clothes_price);
            constraintLayout= itemView.findViewById(R.id.rec_constraint);
        }
    }
}
