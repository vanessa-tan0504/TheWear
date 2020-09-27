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
import android.widget.Toast;
import android.widget.Toolbar;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_search, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
                if(query.equals("")){
                    searchView.clearFocus(); //when press submit, hide keyboard
                    showAll();
                }else {
                    searchData(query);
                    searchView.clearFocus(); //when press submit, hide keyboard
                    Toast.makeText(getContext(), "query " + query, Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //call when we type
                if(newText.equals("")){
                    searchView.clearFocus(); //when query is empty, hide keyboard
                    showAll();
                }
                else {
                    searchData(newText);
                    Toast.makeText(getContext(), "newText " + newText, Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus){
//                    searchView.onActionViewCollapsed();
//                    searchView.setQuery("",false);
//                }
//            }
//        });
        //end of search bar methods----------------------------------------------------------------------------

        //setup adapter and recyclerview
        recyclerView=v.findViewById(R.id.browse_rv);
        clothesList = new ArrayList<>();
        browseRVAdapter = new RecRVAdapter(getActivity(),clothesList);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(browseRVAdapter);

        return v;
    }

    //show all items
    private void showAll(){
        db_browse= FirebaseFirestore.getInstance();
        db_browse.collection("items").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        Clothes c = d.toObject(Clothes.class);
                        clothesList.add(c);
                    }
                }
                browseRVAdapter.notifyDataSetChanged();
            }
        });
    }

    //filter data
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
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "error2"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
