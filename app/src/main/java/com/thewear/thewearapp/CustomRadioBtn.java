package com.thewear.thewearapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import androidx.appcompat.widget.AppCompatRadioButton;

public class CustomRadioBtn extends AppCompatRadioButton {

    private OnCheckedChangeListener onCheckedChangeListener;
    public CustomRadioBtn(Context context) {
        super(context);
    }

    public CustomRadioBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRadioBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setOwnOnCheckedChangeListener();
        setButtonDrawable(null);//lets remove the default drawable to create our own
    }

    public void setOwnOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    private void setOwnOnCheckedChangeListener(){
        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(onCheckedChangeListener!=null){
                    //this is called when we have set our listener
                    onCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
                }
            }
        });
    }
}
