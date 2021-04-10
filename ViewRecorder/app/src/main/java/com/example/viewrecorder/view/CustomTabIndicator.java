package com.example.viewrecorder.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.viewrecorder.R;

public class CustomTabIndicator extends RelativeLayout {

    private final Context mContext;
    private TextView tvTabTitle;
    private AppCompatImageView ivTabImage;
    private TabClickListener tabClickListener;
    private Drawable mSelectedDrawable,mUnSelectedDrawable;
    private String tabTitleText = "";
    private View view;

    public void setTabClickListener(final TabClickListener tabClickListener){
        this.tabClickListener = tabClickListener;
    }

    public CustomTabIndicator(Context context){
        this(context,null);
    }

    public CustomTabIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray ar = null;
        try {
//            ar = context.obtainStyledAttributes(attrs, R.styleable.CustomTabIndicator);
//            mSelectedDrawable = ar.getDrawable(R.styleable.CustomTabIndicator_selected_image);
//            mUnSelectedDrawable = ar.getDrawable(R.styleable.CustomTabIndicator_un_selected_image);
//            tabTitleText = ar.getString(R.styleable.CustomTabIndicator_tab_title);
        }catch (Exception e){

        }finally {
            ar.recycle();
        }
        init();
    }

    private void init(){
        view = inflate(mContext,R.layout.custom_indicator_view,this);
        tvTabTitle = view.findViewById(R.id.tab_title);
        ivTabImage = view.findViewById(R.id.tab_image);
        tvTabTitle.setText(tabTitleText);
        ivTabImage.setImageDrawable(mUnSelectedDrawable);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tabClickListener!=null){
                    tabClickListener.onClick(v);
                    setSelected(true);
                }
            }
        });
    }

    public void setActive(final Boolean selected){
        tvTabTitle.setActivated(selected);
        ivTabImage.setImageDrawable(selected?mSelectedDrawable:mUnSelectedDrawable);
    }

    public interface TabClickListener{
        void onClick(View view);
    }
}

