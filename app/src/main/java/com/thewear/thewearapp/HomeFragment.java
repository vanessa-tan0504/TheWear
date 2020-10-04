package com.thewear.thewearapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ViewListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class HomeFragment extends Fragment {
    View v;
    androidx.appcompat.widget.SearchView searchView;
    CarouselView carouselView;
    String[] urlstring = new String[3];
    String[] descstring = new String[3];
    String[] titlestring = new String[3];
    LottieAnimationView loading_anim,loading_anim2;
    RecyclerView recyclerView;
    FirebaseFirestore db_carousel, db_rec, db_user;
    FirebaseUser user;
    private ArrayList<Clothes> clothesList;
    private RecRVAdapter recRVAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        loading_anim = v.findViewById(R.id.loading_anim);
        loading_anim.setAnimation(R.raw.circle_loading);
        loading_anim.setSpeed(1);
        loading_anim.playAnimation();

        loading_anim2 = v.findViewById(R.id.loading_anim2);
        loading_anim2.setAnimation(R.raw.circle_loading);
        loading_anim2.setSpeed(1);
        loading_anim2.playAnimation();

        //slide show slider, image and desc load from cloud firestore-----------------------------------------------
        carouselView = v.findViewById(R.id.carousel);

        db_carousel = FirebaseFirestore.getInstance();
        db_carousel.collection("images")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String url = document.getString("url");
                                String desc = document.getString("desc");
                                String title = document.getString("title");
                                urlstring[i] = url;
                                descstring[i] = desc;
                                titlestring[i] = title;
                                i++;
                            }
                            carouselView.setViewListener(new ViewListener() {
                                @Override
                                public View setViewForPosition(int position) {
                                    View view = getLayoutInflater().inflate(R.layout.carousel_layout, null);
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

                            carouselView.setImageClickListener(new ImageClickListener() {
                                @Override
                                public void onClick(int position) {

                                    final Dialog dialog = new Dialog(getContext());
                                    dialog.setContentView(R.layout.dialog_carousel);

                                    final ImageView carousel_img = dialog.findViewById(R.id.carousel_img);
                                    final TextView dialog_carousel_title = dialog.findViewById(R.id.dialog_carousel_title);
                                    final TextView dialog_carousel_content = dialog.findViewById(R.id.dialog_carousel_content);
                                    final LottieAnimationView dialog_loading = dialog.findViewById(R.id.dialog_loading);

                                    //switch (position){
                                         Glide.with(getContext()).load(urlstring[position]).listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                dialog_loading.setVisibility(View.GONE);
                                                return false;
                                            }
                                        }).into(carousel_img);
                                            dialog_carousel_title.setText(titlestring[position]);
                                            dialog_carousel_content.setText(descstring[position]);
                                           // break;
                                   // }
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); //remove white corners
                                    DisplayMetrics metrics = getResources().getDisplayMetrics(); //custom width and length of dialog box
                                    int width = metrics.widthPixels;
                                    int height = metrics.heightPixels;
                                    dialog.getWindow().setLayout((6 * width) / 7, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                                    dialog.show();
                                }
                            });
                            carouselView.setPageCount(3);
                        }
                    }
                });

        //slider end------------------------------------------------------------

        //staggered recycler view (recommandation)-----------------------------------------------

        //init rv and class list
        recyclerView = v.findViewById(R.id.rec_rv);
        clothesList = new ArrayList<>();
        recRVAdapter = new RecRVAdapter(getActivity(),clothesList);
        //init rv as staggered
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(recRVAdapter);

        db_rec=FirebaseFirestore.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();

        //async callback from readData - with user's gender info, show rec based on user's gender
        readData(new FirestoreCallback() {
            @Override
            public void onCallback(String tempgender) {

                if(tempgender==null){ //user first time register bug, cannot read fast from firestore, so need to refresh again
                    //refresh page without animation
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(HomeFragment.this).attach(HomeFragment.this).commit();
                    //Toast.makeText(getContext(), "please wait"+tempgender, Toast.LENGTH_SHORT).show();
                }else {

                    db_rec.collection("items").whereArrayContains("tag", tempgender + "").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            clothesList.clear();
                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : list) {
                                    Clothes c = d.toObject(Clothes.class);
                                    clothesList.add(c);
                                }
                            }
                            recRVAdapter.notifyDataSetChanged();
                            loading_anim2.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
        //staggered recycler view (recommandation) end-----------------------------------------------
        return v;
    }

    // async with user's gender-----------------------------------------------------------
    private void readData(final FirestoreCallback firestoreCallback){
        user = getInstance().getCurrentUser();
        db_user = FirebaseFirestore.getInstance();
        db_user.collection("users").document(user.getUid() + "")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String gen = document.getString("expectedGender");
                    firestoreCallback.onCallback(gen);

                }
            }
        });
    }

    private interface FirestoreCallback {
        void onCallback(String tempgender);
    }

//async with user's gender- ------------------------------

 }

