package com.mylove.commonview;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.component.DaggerAppComponent;
import com.mylove.slideanimation.anim.Anim;
import com.mylove.slideanimation.anim.AnimManager;
import com.mylove.slideanimation.anim.EnterAnimLayout;

import org.simple.eventbus.Subscriber;

import java.lang.reflect.Constructor;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SlideFragment extends BaseFragment {
    EnterAnimLayout enterAnimOut;
    ImageView imgAnimOut;

    EnterAnimLayout enterAnimIn;
    ImageView imgAnimIn;

    private int index = 0;
    private int animIndex = 0;

    private static final int ENTERANIM = 0x1001;
    private int delay = 6000;
    private int animTime = 2000;
    RequestOptions requestOptions = new RequestOptions();


    private String[] urls;
    private String type;

    private static final String URL = "URLS";
    private static final String TYPE = "TYPE";
    private static SlideFragment slideFragment;

    public static SlideFragment newInstance(String[] urls,String type){
        slideFragment = new SlideFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray(URL,urls);
        bundle.putString(TYPE,type);
        slideFragment.setArguments(bundle);
        return slideFragment;
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case ENTERANIM:
                    Class clazzIn = AnimManager.getAnimIn(animIndex);
                    Class clazzOut = AnimManager.getAnimOut(0);

                    if(!"FadeInOut".equals(type)){
                        animIndex++;
                    }

                    try {

                        Constructor constructorOut = clazzOut.getConstructor(EnterAnimLayout.class);
                        Anim animOut =  (Anim)constructorOut.newInstance(enterAnimOut);
                        animOut.startAnimation(animTime);

                        Glide.with(SlideFragment.this.getActivity())
                                .load(getUrl(index-1))
                                .apply(requestOptions)
                                .into(imgAnimOut);

                        Constructor constructorIn = clazzIn.getConstructor(EnterAnimLayout.class);
                        Anim animIn =  (Anim)constructorIn.newInstance(enterAnimIn);
                        animIn.startAnimation(animTime);

                        Glide.with(SlideFragment.this.getActivity())
                                .load(getUrl(index))
                                .apply(requestOptions)
                                .into(imgAnimIn);

                        index++;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mHandler.removeMessages(ENTERANIM);
                    mHandler.sendEmptyMessageDelayed(ENTERANIM,delay);
                    break;
            }
            return false;
        }
    });

    public String getUrl(int n){
        if(n < 0){
            n = urls.length - 1;
        }
        return urls[n % urls.length];
    }

    public void preload(){
        mHandler.removeMessages(ENTERANIM);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        for(int i=0; i<urls.length; i++){
            Glide.with(SlideFragment.this.getActivity())
                    .load(urls[i])
                    .apply(requestOptions)
                    .preload();
        }
        mHandler.sendEmptyMessage(ENTERANIM);
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slide, container, false);
        enterAnimOut =(EnterAnimLayout)view.findViewById(R.id.enter_anim_out);
        imgAnimOut = (ImageView)view.findViewById(R.id.img_anim_out);
        enterAnimIn = (EnterAnimLayout)view.findViewById(R.id.enter_anim_in);
        imgAnimIn = (ImageView) view.findViewById(R.id.img_anim_in);
        return view;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(bundle != null){
            type = bundle.getString(TYPE);
            urls = bundle.getStringArray(URL);
            preload();
        }
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mHandler != null){
            mHandler.removeMessages(ENTERANIM);
        }
    }

    @Subscriber(tag = "urls")
    public void changeUrls(String[] urls){
        System.out.println("zhouyajun ......");
        this.urls = urls;
        preload();
    }
}
