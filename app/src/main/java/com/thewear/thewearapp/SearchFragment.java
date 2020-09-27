package com.thewear.thewearapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class SearchFragment extends Fragment {
    View v;
    androidx.appcompat.widget.SearchView searchView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Clothes> clothesList;
    private RecRVAdapter browseRVAdapter;
    FirebaseFirestore db_browse;
    private LottieAnimationView loading_anim;
    private TextView tv_empty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        recyclerView=v.findViewById(R.id.browse_rv);
        clothesList = new ArrayList<>();
        browseRVAdapter = new RecRVAdapter(getActivity(),clothesList);
        tv_empty=v.findViewById(R.id.empty_browse);

        //loading anim
        loading_anim=v.findViewById(R.id.loading_anim);
        loading_anim.setSpeed(1);
        loading_anim.playAnimation();

        //empty search tv
        tv_empty.setVisibility(View.GONE);

        //search bar start---------------------------------------------------------------------------------------------
        searchView = v.findViewById(R.id.search_view);

        //search bar input text settings
        EditText txtSearch = ((EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text));
        txtSearch.setHintTextColor(Color.BLACK);
        txtSearch.setHint("What are you looking for?");
        txtSearch.setTextColor(Color.BLACK);
        Typeface font = ResourcesCompat.getFont(getContext(), R.font.montserrat);
        txtSearch.setTypeface(font);

        //search bar listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //call when press search- when press submit, hide keyboard
                loading_anim.setVisibility(View.VISIBLE);
                if(!query.equals("")){
                    searchData(query);
                    searchView.clearFocus(); //when press submit, hide keyboard
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //call when we type
                if(newText.equals("")){
                    loading_anim.setVisibility(View.VISIBLE);
                    searchView.clearFocus(); //when query is empty, hide keyboard
                    showAll();
                }
                else{

                }
                return true;
            }
        });


        ImageView btnClose = searchView.findViewById(R.id.search_close_btn);
        btnClose.setEnabled(false);

        //end of search bar methods----------------------------------------------------------------------------

        //setup adapter and recyclerview to show result
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(browseRVAdapter);

        return v;
    }

    //show all items
    private void showAll(){
        db_browse= FirebaseFirestore.getInstance();
        db_browse.collection("items").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                browseRVAdapter.notifyDataSetChanged();
                loading_anim.setVisibility(View.GONE);

                if(clothesList.isEmpty()){ //if result list is empty
                    tv_empty.setVisibility(View.VISIBLE);
                                   }
                else{ //if result list is not empty
                    tv_empty.setVisibility(View.GONE);
                }
            }
        });
    }

    //show filtered items
    private void searchData(String query) {
        db_browse = FirebaseFirestore.getInstance();
        db_browse.collection("items").whereArrayContains("tag", query.toLowerCase())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                        browseRVAdapter.notifyDataSetChanged();
                        loading_anim.setVisibility(View.GONE);

                        if(clothesList.isEmpty()){ //if result list is empty
                            tv_empty.setVisibility(View.VISIBLE);
                        }
                        else{ //if result list is not empty
                            tv_empty.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "error2"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() { //reset searchview to empty after sliding
        super.onResume();
        if (searchView != null && !searchView.getQuery().toString().isEmpty()) {
            searchView.setQuery(null, false);
            searchView.setIconified(true);
            searchView.setIconified(true);
        }
    }
}
