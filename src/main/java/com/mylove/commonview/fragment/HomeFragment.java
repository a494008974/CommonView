package com.mylove.commonview.fragment;


import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mylove.commonview.common.CommonFragment;
import com.mylove.commonview.R;
import com.mylove.commonview.widget.ContentView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import me.jessyan.armscomponent.commonres.utils.SystemUtils;
import me.jessyan.armscomponent.commonres.view.GMarqueeTextView;
import me.jessyan.armscomponent.commonservice.entity.AdResponse;

import static me.jessyan.armscomponent.commonres.utils.Contanst.MSG_WHAT_LAUNCHER_DATA;
import static me.jessyan.armscomponent.commonres.utils.Contanst.MSG_WHAT_LAUNCHER_MSG;
import static me.jessyan.armscomponent.commonres.utils.Contanst.MSG_WHAT_LAUNCHER_NET;
import static me.jessyan.armscomponent.commonres.utils.Contanst.MSG_WHAT_LAUNCHER_TIME;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends CommonFragment implements View.OnFocusChangeListener,View.OnKeyListener{

    ViewGroup contentMain;

    private Typeface typeface;

    ImageView mLauncherNet;
    TextView mLauncherTime;
    TextView mLauncherStatu;
    TextView mLauncherDate;
    TextView mLauncherWeek;

    GMarqueeTextView gMarqueeTextView;

    TextView mTvFocus;


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
        mLauncherNet = (ImageView)viewGroup.findViewById(R.id.launcher_net);
        mLauncherTime = (TextView)viewGroup.findViewById(R.id.launcher_time);
        mLauncherStatu = (TextView)viewGroup.findViewById(R.id.launcher_statu);
        mLauncherDate = (TextView)viewGroup.findViewById(R.id.launcher_date);
        mLauncherWeek = (TextView)viewGroup.findViewById(R.id.launcher_week);
        mTvFocus = (TextView)viewGroup.findViewById(R.id.tv_focus);
        gMarqueeTextView = (GMarqueeTextView)viewGroup.findViewById(R.id.launcher_msg);
        showTime();
        showNet();
        setListener();
    }


    public void setListener() {
        int n = 1;
        for (int i=0; i<contentMain.getChildCount(); i++){
            View view = contentMain.getChildAt(i);
            if(view instanceof ContentView){
                ContentView contentView = (ContentView) view;
                if(n<10){
                    contentView.setTag(pageIndex+"0"+n);
                }else{
                    contentView.setTag(pageIndex+""+n);
                }
                n++;
                contentView.setOnKeyListener(this);
                contentView.setOnFocusChangeListener(this);
                contentView.initView();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(gMarqueeTextView != null){
            gMarqueeTextView.stopScroll();
            gMarqueeTextView.startScroll();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            onMoveFocusBorder(v,1.0f,5);
            String tag = String.valueOf(v.getTag());
            if("107".equals(tag) || "108".equals(tag) || "109".equals(tag) || "110".equals(tag) || "111".equals(tag)){
               mTvFocus.setX(v.getX());
               if(v instanceof ContentView){
                   ContentView contentView = (ContentView) v;
                   mTvFocus.setText(contentView.getTitle());
               }
               mTvFocus.setVisibility(View.VISIBLE);
            }else {
                mTvFocus.setVisibility(View.INVISIBLE);
            }
        }else {
            mTvFocus.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch (keyCode){
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    nextView = v;
                    if(v.getId() == R.id.one_7 || v.getId() == R.id.one_8 || v.getId() == R.id.one_9 || v.getId() == R.id.one_10 || v.getId() == R.id.one_11){
                        EventBus.getDefault().post("next","turnPage");
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if(v.getId() == R.id.one_4 || v.getId() == R.id.one_5 || v.getId() == R.id.one_6){
                        contentMain.findViewById(R.id.one_1).requestFocus();
                        return true;
                    }else if( v.getId() == R.id.one_11){
                        contentMain.findViewById(R.id.one_7).requestFocus();
                        return true;
                    }else if( v.getId() == R.id.one_3){
                        contentMain.findViewById(R.id.one_4).requestFocus();
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    if("104".equals((String) v.getTag())){
                        if(SystemUtils.openApk(getActivity(),"com.android.settings")){
                            return true;
                        }
                        if(SystemUtils.openApk(getActivity(),"com.android.tv.settings")){
                            return true;
                        }
                    }else{
                        initOpen(v);
                    }
                    break;
            }
        }
        return false;
    }



    public void showNet(){

        switch (SystemUtils.isNetworkAvailable(getActivity())){
            case 1: //WIFI
                mLauncherNet.setImageResource(R.drawable.launcher_iv_wifi);
                break;
            case 2: //ETH
                mLauncherNet.setImageResource(R.drawable.launcher_iv_net);
                break;
            case 3: //NOT NET
                mLauncherNet.setImageResource(R.drawable.launcher_iv_unnet);
                break;
        }

    }

    public void showTime(){
        typeface = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Thin.ttf");
        mLauncherTime.setTypeface(typeface);
        mLauncherStatu.setTypeface(typeface);
        mLauncherDate.setTypeface(typeface);
        mLauncherWeek.setTypeface(typeface);

        mLauncherTime.setText(SystemUtils.getTime(getActivity()));
        mLauncherStatu.setText(SystemUtils.getStatu());
        mLauncherDate.setText(SystemUtils.getDate());
        mLauncherWeek.setText(SystemUtils.getWeek());
    }

    @Subscriber
    public void fetchData(Message msg){
        switch (msg.what){
            case MSG_WHAT_LAUNCHER_DATA: //桌面数据
                for (int i=0; i<contentMain.getChildCount(); i++){
                    View view = contentMain.getChildAt(i);
                    if(view instanceof ContentView){
                        ContentView contentView = (ContentView) view;
                        contentView.initView();
                    }
                }
                break;
            case MSG_WHAT_LAUNCHER_TIME: //时间
                showTime();
                break;
            case MSG_WHAT_LAUNCHER_NET: //网络
                switch (msg.arg1){
                    case 1: //WIFI
                        mLauncherNet.setImageResource(R.drawable.launcher_iv_wifi);
                        break;
                    case 2: //ETH
                        mLauncherNet.setImageResource(R.drawable.launcher_iv_net);
                        break;
                    case 3: //NOT NET
                        mLauncherNet.setImageResource(R.drawable.launcher_iv_unnet);
                        break;
                }
                break;
            case MSG_WHAT_LAUNCHER_MSG: //消息
                if(gMarqueeTextView != null){
                    AdResponse adResponse = (AdResponse) msg.obj;
                    if(adResponse != null && adResponse.getInfo() != null){
                        if ("1".equals(adResponse.getInfo().getDisplay())) {
                            gMarqueeTextView.setVisibility(View.VISIBLE);
                            gMarqueeTextView.setText(adResponse.getInfo().getRemark());
                            gMarqueeTextView.stopScroll();
                            gMarqueeTextView.startScroll();
                        } else {
                            gMarqueeTextView.stopScroll();
                            gMarqueeTextView.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                break;
        }
    }
}
