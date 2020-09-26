package com.thewear.thewearapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class CartFragment extends Fragment {
    private FirebaseAuth auth;
    private RecyclerView rv_cart;
    private ArrayList<Order> orderList;
    private CartRVAdapter cartRVAdapter;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseFirestore db;
    private String userUID;
    private FirebaseUser currentUser;
    private TextView total_qty, total_price,grand_price,emptytv;
    private LottieAnimationView loading_anim;
    private Button btn_checkout;
    private Spinner spinner;
    private double deliveryfee;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_cart, container, false);
        Button btn = v.findViewById(R.id.signout);

        total_qty = v.findViewById(R.id.cart_qty);
        total_price = v.findViewById(R.id.total);
        grand_price= v.findViewById(R.id.grand);
        //delivery_fee = v.findViewById(R.id.tv_fee);
        btn_checkout = v.findViewById(R.id.btn_checkout);
        spinner = v.findViewById(R.id.cart_deliveryspin);
        emptytv=v.findViewById(R.id.empty_carttv);
        loading_anim=v.findViewById(R.id.loading_anim);
        rv_cart=v.findViewById(R.id.rv_cart);
        linearLayoutManager= new LinearLayoutManager(getActivity());

        //loading anim
        loading_anim.setAnimation(R.raw.circle_loading);
        loading_anim.setSpeed(1);
        loading_anim.playAnimation();

        //content in cart list
        orderList = new ArrayList<>();
        cartRVAdapter = new CartRVAdapter(getActivity(),orderList);

        //set layout manager and adapter
        rv_cart.setLayoutManager(linearLayoutManager);
        rv_cart.setAdapter(cartRVAdapter);

        //set firebase instance
        db= FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();

        //get current user and id
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        userUID=currentUser.getUid();

        emptytv.setVisibility(View.GONE);




        //get total price amount after load from firebase
        readData(new DropdownCallback() {
            @Override
            public void onCallback(final double allprice) {
                //set spinner adapter and grand total
                ArrayAdapter<CharSequence>adapter = ArrayAdapter.createFromResource(getContext(),R.array.delivery,android.R.layout.simple_spinner_dropdown_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position){
                            case 0 : deliveryfee=5;
                                //dropdownCallback.onCallback(deliveryfee);
                                grand_price.setText(String.format("RM %.2f",(allprice+deliveryfee)));
                                break;
                            case 1:deliveryfee=10;
                                //dropdownCallback.onCallback(deliveryfee);
                                grand_price.setText(String.format("RM %.2f",(allprice+deliveryfee)));
                                break;
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

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


    // async with delivery data-----------------------------------------------------------
    private void readData(final CartFragment.DropdownCallback dropdownCallback){
        //retrieve order data from firebase
        db.collection("orders").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){ // if firebase db hv data
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    int all_qty=0;
                    double all_price=0;

                    for(DocumentSnapshot d : list){
                        Order order = d.toObject(Order.class);

                        //get specific account's order and paid status is false
                        if(order.getUser().equals(userUID)&&(!order.getPaid())){
                            orderList.add(order);
                            all_qty+=order.getQty();
                            all_price+=order.getTotalprice();
                        }
                    }
                    total_qty.setText(all_qty+"");
                    total_price.setText(String.format("RM %.2f",all_price));
                    spinner.setClickable(true);
                    loading_anim.setVisibility(View.GONE);
                    btn_checkout.setEnabled(true);
                    dropdownCallback.onCallback(all_price);
                    cartRVAdapter.notifyDataSetChanged();

                    //set swipehelper
                    SwipeHelper swipeHelper = new SwipeHelper(getContext()) {
                        @Override
                        public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                            underlayButtons.add(new SwipeHelper.UnderlayButton("Delete", 0, Color.parseColor("#FF3C30"),
                                    new UnderlayButtonClickListener() {
                                        @Override
                                        public void onClick(int pos) {
                                            final Order order_del = cartRVAdapter.getData().get(pos);
                                            cartRVAdapter.removeItem(pos);
                                            db.collection("orders").document(order_del.getOrderID()+"").delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                            ft.detach(CartFragment.this).attach(CartFragment.this).commit();
                                                            Snackbar.make(getView(),"Item has been deleted",Snackbar.LENGTH_LONG).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                            );
                        }
                    };
                    swipeHelper.attachToRecyclerView(rv_cart);



                    if(orderList.isEmpty()){ //if specific user no order anything
                        total_qty.setText("0");
                        total_price.setText("RM 0.00");
                        grand_price.setText("RM0.00");
                        emptytv.setVisibility(View.VISIBLE);
                        spinner.setClickable(false);
                        loading_anim.setVisibility(View.GONE);
                        btn_checkout.setEnabled(false); //disable sent order button if no data shown
                    }

                }
                else{ //if firebase don't have data from order
                    total_qty.setText("0");
                    total_price.setText("RM 0.00");
                    grand_price.setText("RM0.00");
                    emptytv.setVisibility(View.VISIBLE);
                    spinner.setClickable(false);
                    loading_anim.setVisibility(View.GONE);
                    btn_checkout.setEnabled(false);
                }
            }
        });

        btn_checkout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    btn_checkout.setBackgroundResource(R.drawable.rounded_btn_grey);
                    btn_checkout.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    //when button released
                    btn_checkout.setBackgroundResource(R.drawable.rounded_btn_black);
                    btn_checkout.setTextColor(Color.parseColor("#FFFFFF"));
                }
                return false;
            }
        });

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), RecognizeActivity.class);
////                intent.putExtra("intentfrom","cart");
////                startActivity(intent);
                Intent intent = new Intent(getContext(), CheckoutActivity.class);
                //intent.putExtra("intentfrom","cart");
                startActivity(intent);
            }
        });
    }

    private interface DropdownCallback {
        void onCallback(double allprice);
    }

}