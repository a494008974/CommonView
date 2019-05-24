package com.mylove.commonview.t_one;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mylove.commonview.common.CommonFragment;
import com.mylove.commonview.R;
import com.mylove.commonview.widget.ContentView;

import org.simple.eventbus.EventBus;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends CommonFragment implements View.OnFocusChangeListener,View.OnKeyListener{

    ViewGroup contentMain;

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
    }

    @Override
    public int initLayout() {
        return R.layout.fragment_home;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        contentMain = viewGroup.findViewById(R.id.content_main);
        setListener();
    }

    public void setListener() {
        for (int i=0; i<contentMain.getChildCount(); i++){
            View view = contentMain.getChildAt(i);
            if(view instanceof ContentView){
                ContentView contentView = (ContentView) view;
                int n = i+1;
                if(n/10 == 0){
                    contentView.setTag(pageIndex+"0"+n);
                }else{
                    contentView.setTag(pageIndex+n);
                }

                contentView.setOnKeyListener(this);
                contentView.setOnFocusChangeListener(this);
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            onMoveFocusBorder(v,1.05f,10);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch (keyCode){
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    nextView = v;
                    EventBus.getDefault().post("next","turnPage");
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if(v.getId() == R.id.one_6){
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:

                    Toast.makeText(getActivity(), (String)v.getTag(),Toast.LENGTH_LONG).show();
                    break;
            }
        }
        return false;
    }
}
