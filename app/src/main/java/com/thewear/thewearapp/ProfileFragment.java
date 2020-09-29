package com.thewear.thewearapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private FirebaseFirestore db;
    private String userUID;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private ArrayList<Order> orderList;
    private HisRVAdapter hisRVAdapter;
    private RecyclerView rv_his;
    private LinearLayoutManager linearLayoutManager;
    private TextView tvname;
    private Button edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View v = inflater.inflate(R.layout.fragment_profile, container, false);

        rv_his=v.findViewById(R.id.rv_latest);
        linearLayoutManager= new LinearLayoutManager(getActivity());
        tvname=v.findViewById(R.id.tv_name);
        edit=v.findViewById(R.id.editprofile);

        //set firebase instance
        db= FirebaseFirestore.getInstance();
        auth= FirebaseAuth.getInstance();

        //get current user and id
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        userUID=currentUser.getUid();

        //content in cart list
        orderList = new ArrayList<>();
        hisRVAdapter = new HisRVAdapter(getActivity(),orderList);

        //set layout manager and adapter
        rv_his.setLayoutManager(linearLayoutManager);
        rv_his.setAdapter(hisRVAdapter);

        //show user name
        tvname.setText(currentUser.getDisplayName()+"");

        edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    edit.setBackgroundResource(R.drawable.rounded_btn_grey);
                    edit.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    //when button released
                    edit.setBackgroundResource(R.drawable.rounded_btn_black);
                    edit.setTextColor(Color.parseColor("#FFFFFF"));
                }
                return false;
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),EditActivity.class);
                startActivity(intent);
            }
        });

        //history recyclerview
        db.collection("orders")
                .whereEqualTo("user",userUID+"")
                .orderBy("orderID", Query.Direction.DESCENDING)
                .limit(5)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orderList.clear();
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for(DocumentSnapshot d : list){
                        Order order = d.toObject(Order.class);

                        //if paid status is true
                        if(order.getPaid()){
                            orderList.add(order);
                        }
                    }
                    hisRVAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("History",e.getMessage());
            }
        });
        return v;
    }
}