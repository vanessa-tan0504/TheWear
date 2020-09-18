package com.thewear.thewearapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import androidx.appcompat.widget.SearchView;

public class HomeFragment extends Fragment {
    View v;
    androidx.appcompat.widget.SearchView searchView;
    CarouselView carouselView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_home, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
        //end of search bar methods

        //slider
        final int [] sampleimage={R.drawable.cross_icon,R.drawable.arrow_icon,R.drawable.tick_icon};

        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageResource(sampleimage[position]);

            }
        };
        carouselView=v.findViewById(R.id.carousel);
        carouselView.setPageCount(sampleimage.length);
        carouselView.setImageListener(imageListener);


        //slider end


        return v;
    }


}