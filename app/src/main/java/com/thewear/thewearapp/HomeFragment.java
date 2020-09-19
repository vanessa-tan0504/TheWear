package com.thewear.thewearapp;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class HomeFragment extends Fragment {
    View v;
    androidx.appcompat.widget.SearchView searchView;
    CarouselView carouselView;
    String [] urlstring= new String [3];
    String[] descstring = new String [3];
    String[] titlestring = new String[3];
    LottieAnimationView loading_anim;
    RecyclerView staggeredRv;
    FirestoreRecyclerAdapter adapter_rec;
    StaggeredGridLayoutManager manager;
    DatabaseReference mDatbaseRef;
    FirebaseFirestore db_carousel,db_rec;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        loading_anim=v.findViewById(R.id.loading_anim);
        loading_anim.setAnimation(R.raw.circle_loading);
        loading_anim.setSpeed(2);
        loading_anim.playAnimation();


        //search bar start---------------------------------------------------------------------------------------------
        searchView = v.findViewById(R.id.search_view);
        //search bar icon layout on right
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            searchView.setLayoutParams(new Toolbar.LayoutParams(Gravity.RIGHT));
        }

        //search bar input text settings
        EditText txtSearch = ((EditText)searchView.findViewById(androidx.appcompat.R.id.search_src_text));
        txtSearch.setHintTextColor(Color.WHITE);
        txtSearch.setHint("Search for");
        txtSearch.setTextColor(Color.WHITE);

        //search bar listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getContext(), "onQueryTextSubmit", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getContext(), "onQueryTextChange", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        //end of search bar methods----------------------------------------------------------------------------

        //slide show slider, image and desc load from cloud firestore-----------------------------------------------
        carouselView=v.findViewById(R.id.carousel);

        db_carousel = FirebaseFirestore.getInstance();
        db_carousel.collection("images")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            int i=0;
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String url = document.getString("url");
                                String desc = document.getString("desc");
                                String title = document.getString("title");
                                urlstring[i]=url;
                                descstring[i]=desc;
                                titlestring[i]=title;
                                i++;
                            }
                            carouselView.setViewListener(new ViewListener() {
                                @Override
                                public View setViewForPosition(int position) {
                                   View view = getLayoutInflater().inflate(R.layout.carousel_layout,null);
                                   TextView desc = view.findViewById(R.id.carousel_desc);
                                   TextView title = view.findViewById(R.id.carousel_title);
                                   ImageView img = view.findViewById(R.id.carousel_image);
                                    desc.setText(descstring[position]); //load description from firestore
                                    title.setText(titlestring[position]);

                                    Glide.with(getContext()).load(urlstring[position]) //use glide to load image from firestore
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    return false;
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    loading_anim.setVisibility(View.GONE);
                                                    return false;
                                                }
                                            })
                                            .into(img);
                                    return view;
                                }
                            });
                            carouselView.setPageCount(3);
                        }
                    }
                });

        //slider end------------------------------------------------------------

        //staggered recycler view (recommandation)-----------------------------------------------

        //init rv as staggered
        staggeredRv= v.findViewById(R.id.staggered_rv);
        manager= new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        staggeredRv.setLayoutManager(manager);

        //use firebase recycyler adapter
        db_rec = FirebaseFirestore.getInstance();
        Query query = db_rec.collection("images").whereEqualTo("title","LATEST TREND"); //filter based on gender
        FirestoreRecyclerOptions<Clothes> response = new FirestoreRecyclerOptions.Builder<Clothes>()
                .setQuery(query,Clothes.class)
                .build();

       adapter_rec= new FirestoreRecyclerAdapter<Clothes, rowHolder>(response) {
           @Override
           protected void onBindViewHolder(@NonNull rowHolder holder, int position, @NonNull Clothes model) {

              // holder.textName.setText(model.getName());
               Glide.with(getContext()).load(model.getUrl()).into(holder.img);

               holder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                   }
               });

           }

           @NonNull
           @Override
           public rowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);

               return new rowHolder(view);
           }

           @Override
           public void onError(@NonNull FirebaseFirestoreException e) {
               Toast.makeText(getContext(), "error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
           }
       };

       adapter_rec.notifyDataSetChanged();
       staggeredRv.setAdapter(adapter_rec);

        //staggered recycler view (recommandation) end-----------------------------------------------

        return v;
    }

    public class rowHolder extends  RecyclerView.ViewHolder{
        ImageView img ;

        public rowHolder(@NonNull View itemView) {
            super(itemView);
            img=itemView.findViewById(R.id.row_img);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter_rec.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter_rec.stopListening();
    }


}