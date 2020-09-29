package com.thewear.thewearapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditActivity extends AppCompatActivity {

    private ConstraintLayout expandableView,expandableView2;
    private CircularProgressButton btn_add,btn_chg_pw;
    private TextView desc,desc2;
    private EditText edit_oldpw,edit_add,edit_postal;
    private Spinner edit_country;
    private FirebaseAuth auth;
    private Bitmap bitmapTick,bitmapCross;
    private ScrollView settingview;
    private String country;
    private FirebaseFirestore db;
    private Button logout,delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        final Drawable black_btn = getResources().getDrawable(R.drawable.rounded_btn_black);
        Drawable tick = getResources().getDrawable(R.drawable.tick_icon);
        bitmapTick = ((BitmapDrawable)tick).getBitmap();
        Drawable cross = getResources().getDrawable(R.drawable.cross_icon);
        bitmapCross = ((BitmapDrawable)cross).getBitmap();
        expandableView = findViewById(R.id.expandableView);
        expandableView2 = findViewById(R.id.expandableView2);
        desc=findViewById(R.id.desc);
        desc2=findViewById(R.id.desc2);
        btn_add=findViewById(R.id.btn_add);
        btn_chg_pw=findViewById(R.id.btn_chg_pw);
        edit_oldpw=findViewById(R.id.input_pw);
        edit_add=findViewById(R.id.input_add);
        edit_postal=findViewById(R.id.input_postal);
        edit_country=findViewById(R.id.input_country_spin);
        settingview=findViewById(R.id.settingsview);
        logout=findViewById(R.id.logout);
        delete=findViewById(R.id.delete);


        //get firebase auth instance
        auth= FirebaseAuth.getInstance();
        final FirebaseUser user=auth.getCurrentUser();

        //expandable settings-----------------------------------------------------------------------------------------------------------
        // hide until its title is clicked (make expandable view)
        expandableView.setVisibility(View.GONE);
        expandableView2.setVisibility(View.GONE);

        desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //expandableView.setVisibility( expandableView.isShown() ? View.GONE : View.VISIBLE );
                if(expandableView.getVisibility()==View.VISIBLE){
                    expandableView.setVisibility(View.GONE);
                }
                else{
                    expandableView.setVisibility(View.VISIBLE);
                }

                if(expandableView2.getVisibility()==View.VISIBLE){
                    expandableView2.setVisibility(View.GONE);
                }
            }
        });

        desc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //expandableView.setVisibility( expandableView.isShown() ? View.GONE : View.VISIBLE );
                if(expandableView2.getVisibility()==View.VISIBLE){
                    expandableView2.setVisibility(View.GONE);
                }
                else{
                    expandableView2.setVisibility(View.VISIBLE);
                }

                if(expandableView.getVisibility()==View.VISIBLE){
                    expandableView.setVisibility(View.GONE);
                }
            }
        });
        //expandable settings-----------------------------------------------------------------------------------------------------------

        //chg pw------------------------------------------------------------------------------


        btn_chg_pw.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    btn_chg_pw.setBackgroundResource(R.drawable.rounded_btn_grey);
                    btn_chg_pw.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    //when button released
                    btn_chg_pw.setBackgroundResource(R.drawable.rounded_btn_black);
                    btn_chg_pw.setTextColor(Color.parseColor("#FFFFFF"));
                }
                return false;
            }
        });

        btn_chg_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWindowFocusChanged(true);
                btn_chg_pw.startAnimation();

                final String oldpassword = edit_oldpw.getText().toString();
                if (TextUtils.isEmpty(oldpassword)) {
                    edit_oldpw.setError("Enter old password");
                    delay_anim(black_btn,btn_chg_pw); //delay and revert anim
                    return;
                }
                else{
                    //final FirebaseUser user=auth.getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail()+"",oldpassword);
                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() { //if current password is correct, show dialog box
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                edit_oldpw.getText().clear(); //clear old pasword input
                                btn_chg_pw.doneLoadingAnimation(Color.GREEN,bitmapTick);
                                delay_anim(black_btn,btn_chg_pw); //delay and revert anim

                                Snackbar.make(settingview, "Reauthentication success", Snackbar.LENGTH_LONG).show();
                                final Dialog dialog = new Dialog(EditActivity.this);
                                dialog.setContentView(R.layout.dialog_chgpw);
                                final TextInputEditText inputNew= dialog.findViewById(R.id.newpw);
                                final TextInputEditText inputCfm= dialog.findViewById(R.id.cfmpw);
                                final CircularProgressButton btnNew=dialog.findViewById(R.id.btn_new);

                            btnNew.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if(event.getAction()== MotionEvent.ACTION_DOWN){
                                        btnNew.setBackgroundResource(R.drawable.rounded_btn_grey);
                                        btnNew.setTextColor(Color.parseColor("#FFFFFF"));
                                    }
                                    if(event.getAction()==MotionEvent.ACTION_UP){
                                        //when button released
                                        btnNew.setBackgroundResource(R.drawable.rounded_btn_black);
                                        btnNew.setTextColor(Color.parseColor("#FFFFFF"));
                                    }
                                    return false;
                                }
                            });

                            btnNew.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    onWindowFocusChanged(true);
                                    btnNew.startAnimation();
                                    String newpw = inputNew.getText().toString().trim();
                                    String cfmpw=inputCfm.getText().toString().trim();

                                    if (TextUtils.isEmpty(newpw)) {
                                        inputNew.setError("Enter new password");
                                        delay_anim(black_btn,btnNew); //delay and revert anim
                                        return;
                                    }
                                    if (TextUtils.isEmpty(cfmpw)) {
                                        inputCfm.setError("Enter comfirm password");
                                        delay_anim(black_btn,btnNew); //delay and revert anim
                                        return;
                                    }
                                    if (newpw.length() < 6) {
                                        inputNew.setError("Password too short");
                                        delay_anim(black_btn,btnNew); //delay and revert anim
                                        return;
                                    }
                                    if (!newpw.equals(cfmpw)) {
                                        inputCfm.setError("Confirm password not same with password");
                                        delay_anim(black_btn,btnNew); //delay and revert anim
                                        return;
                                    }

                                    //if both new pw and cfm pw are okay
                                    user.updatePassword(cfmpw).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                btn_chg_pw.doneLoadingAnimation(Color.GREEN,bitmapTick);
                                                delay_anim(black_btn,btn_chg_pw); //delay and revert anim
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog.cancel();
                                                        Snackbar.make(settingview, "Password updated successfully", Snackbar.LENGTH_LONG).show();
                                                    }
                                                }, 1500);
                                            }
                                            else{
                                                btn_chg_pw.doneLoadingAnimation(Color.RED,bitmapCross);
                                                delay_anim(black_btn,btn_chg_pw); //delay and revert anim
                                                Snackbar.make(settingview, "Password failed to updated ", Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //remove white corners
                            DisplayMetrics metrics = getResources().getDisplayMetrics(); //custom width and length of dialog box
                            int width = metrics.widthPixels;
                            int height = metrics.heightPixels;
                            dialog.getWindow().setLayout((6 * width)/7, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                            dialog.show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() { //if current password is wrong
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            btn_chg_pw.doneLoadingAnimation(Color.RED,bitmapCross);
                            delay_anim(black_btn,btn_chg_pw); //delay and revert anim
                            Snackbar.make(settingview, "Error: "+e.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        //end of chg password-----------------------------------------------------------------------------------------------------
//add address--------------------------------------------------------------------------------------------------------------------------
        //set firebase instance
        db= FirebaseFirestore.getInstance();

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditActivity.this,R.array.country,android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edit_country.setAdapter(adapter);
        edit_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Typeface font = ResourcesCompat.getFont(EditActivity.this, R.font.montserrat);
                        ((TextView) parent.getChildAt(0)).setTypeface(font);
                        ((TextView) parent.getChildAt(0)).setTextSize(15);

                switch (position){
                    case 0 : country=null; break;
                    case 1: country="Malaysia"; break;
                    case 2: country="Thailand"; break;
                    case 3: country="Philippine";break;
                    case 4: country="Korea"; break;
                    case 5: country="Japan"; break;
                    case 6: country="United Kingdom";break;
                    case 7: country="United State";break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_add.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    btn_add.setBackgroundResource(R.drawable.rounded_btn_grey);
                    btn_add.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    //when button released
                    btn_add.setBackgroundResource(R.drawable.rounded_btn_black);
                    btn_add.setTextColor(Color.parseColor("#FFFFFF"));
                }
                return false;
            }
        });

        //if user already got data in firestore, show the current address on the edit text, if no, edittext remain empty
        db.collection("address").document(user.getUid()+"").get()
        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                String add= document.getString("address");
                String pos=document.getString("postal");
                String country = document.getString("country");

                if(!task.isSuccessful()) {
                    edit_add.setText(null);
                    edit_postal.setText(null);
                    edit_country.setSelection(0);
                    return;
                }
                edit_add.setText(add);
                edit_postal.setText(pos);
                int spinnerPosition = adapter.getPosition(country);
                edit_country.setSelection(spinnerPosition);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                edit_add.setText(null);
                edit_postal.setText(null);
                edit_country.setSelection(0);
                Log.e("Settings",e.getMessage());
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWindowFocusChanged(true);
                btn_add.startAnimation();

                final String address = edit_add.getText().toString();
                final String postal = edit_postal.getText().toString();

                if (TextUtils.isEmpty(address)) {
                    edit_add.setError("Enter address");
                    delay_anim(black_btn,btn_add); //delay and revert anim
                    return;
                }
                if (TextUtils.isEmpty(postal)) {
                    edit_postal.setError("Enter postal code");
                    delay_anim(black_btn,btn_add); //delay and revert anim
                    return;
                }

                if(country==null){
                    TextView errorText = (TextView)edit_country.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Select country");//changes the selected item text to this
                    delay_anim(black_btn,btn_add); //delay and revert anim
                    return;
                }
                final Address new_address = new Address(address,postal,country,user.getUid()+"");
                db.collection("address").document(user.getUid()+"")
                        .set(new_address)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                btn_add.doneLoadingAnimation(Color.GREEN,bitmapTick);
                                delay_anim(black_btn,btn_add); //delay and revert anim
                                Toast.makeText(EditActivity.this, "data updated", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                btn_add.doneLoadingAnimation(Color.RED,bitmapCross);
                                delay_anim(black_btn,btn_add); //delay and revert anim
                                Snackbar.make(settingview, "Error: "+e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });

            }
        });
        //end of address-----------------------------------------------------------------------

        //logout
        logout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    logout.setBackgroundResource(R.drawable.rounded_btn_grey);
                    logout.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    //when button released
                    logout.setBackgroundResource(R.drawable.rounded_btn_black);
                    logout.setTextColor(Color.parseColor("#FFFFFF"));
                }
                return false;
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        auth.getInstance().signOut();
                        Intent intent = new Intent(EditActivity.this,MainActivity.class);
                        finish();
                        startActivity(intent);

            }
        });

        //delete account

        delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()== MotionEvent.ACTION_DOWN){
                    delete.setBackgroundResource(R.drawable.rounded_btn_grey);
                    delete.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    //when button released
                    delete.setBackgroundResource(R.drawable.rounded_btn_red);
                    delete.setTextColor(Color.parseColor("#FFFFFF"));
                }
                return false;
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


    }


    //hide status bar and below softkey
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }

    public void delay_anim(final Drawable draw_btn, final CircularProgressButton btn){
        new Handler().postDelayed(new Runnable() { //pause 3 second only resume to walkthru
            @Override
            public void run() {
                btn.revertAnimation();
                btn.setBackground(draw_btn);
            }
        }, 1000);
    }
}