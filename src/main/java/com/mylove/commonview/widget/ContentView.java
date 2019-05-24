package com.mylove.commonview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.http.imageloader.ImageLoader;
import com.mylove.commonview.R;


/**
 * TODO: document your custom view class.
 */
public class ContentView extends FrameLayout {
    private Context mContext;
    private ImageView imgContent;
    private TextView tvContent;

    public ContentView(Context context) {
        this(context,null);
    }

    public ContentView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ContentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        View view = inflate(context,R.layout.content_view,this);
        imgContent = (ImageView)view.findViewById(R.id.content_img);
        tvContent = (TextView) view.findViewById(R.id.content_tv);
    }

    public void initView(){

    }

    public void updateView(ImageLoader mImageLoader){
//        String tag = String.valueOf(this.getTag());
//        InfoBean infoBean = DaoHelper.fetchInfoBean(tag);
//        if(infoBean == null){ return;}
//
//        if(String.valueOf(Contanst.KEY_LIVE).equals(tag)){
//            SystemUtils.setProp("bsw.live.package", infoBean.getPkg());
//        }else if(String.valueOf(Contanst.KEY_VOD).equals(tag)){
//            SystemUtils.setProp("bsw.vod.package", infoBean.getPkg());
//        }
//
//        if(imgContent != null){
//            mImageLoader.loadImage(mContext,
//                    CommonImageConfigImpl
//                            .builder()
//                            .setDecodeFormate(DecodeFormat.PREFER_ARGB_8888)
//                            .url(String.format(Contanst.downmain,infoBean.getImg_url()))
//                            .imageView(imgContent)
//                            .build());
//        }


    }
}
