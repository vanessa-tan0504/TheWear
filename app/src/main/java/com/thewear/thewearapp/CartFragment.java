package com.thewear.thewearapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private FirebaseAuth auth;
    private RecyclerView rv_cart;
    private ArrayList<Order> orderList;
    private CartRVAdapter cartRVAdapter;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseFirestore db;
    private String userUID;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_cart, container, false);
        Button btn = v.findViewById(R.id.signout);


        rv_cart=v.findViewById(R.id.rv_cart);
        linearLayoutManager= new LinearLayoutManager(getActivity());

        //content in cart list
        orderList = new ArrayList<>();
        cartRVAdapter = new CartRVAdapter(getActivity(),orderList);

        //set layout manager and adapter
        rv_cart.setLayoutManager(linearLayoutManager);
        rv_cart.setAdapter(cartRVAdapter);

        //set firebase instance
        db= FirebaseFirestore.getInstance();

        //get current user and id
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        userUID=currentUser.getUid();

        auth=FirebaseAuth.getInstance();

        //
//        v.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });

        //retrieve order data from firebase
        db.collection("orders").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){ // if firebase db hv data
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot d : list){
                        Order order = d.toObject(Order.class);

                        //get specific account's order and paid status is false
                        if(order.getUser().equals(userUID)&&(!order.getPaid())){
                            orderList.add(order);

                        }
                    }
                    cartRVAdapter.notifyDataSetChanged();

                }
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.getInstance().signOut();
                Intent intent = new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return v;
    }
}