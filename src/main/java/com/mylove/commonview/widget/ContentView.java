package com.mylove.commonview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jess.arms.http.imageloader.ImageLoader;
import com.mylove.commonview.R;

import me.jessyan.armscomponent.commonres.utils.Contanst;
import me.jessyan.armscomponent.commonres.utils.SystemUtils;
import me.jessyan.armscomponent.commonservice.dao.DaoHelper;
import me.jessyan.armscomponent.commonservice.dao.InfoBean;


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

    public String getTitle(){
        if(tvContent != null){
            return tvContent.getText().toString();
        }
        return "";
    }

    public void initView(){
        String tag = String.valueOf(this.getTag());
        InfoBean infoBean = DaoHelper.fetchInfoBean(tag);

        if(String.valueOf(Contanst.KEY_LIVE).equals(tag)){
            SystemUtils.setProp("bsw.live.package", infoBean.getPkg());
        }else if(String.valueOf(Contanst.KEY_VOD).equals(tag)){
            SystemUtils.setProp("bsw.vod.package", infoBean.getPkg());
        }

        if(tvContent != null){
            tvContent.setText(infoBean.getTitle());
        }

        if(imgContent != null){
            Glide.with(mContext).load(String.format(Contanst.downmain,infoBean.getImg_url())).into(imgContent);
        }

        if("101".equals(tag) || "102".equals(tag) || "103".equals(tag)){
            if(tvContent != null){
                tvContent.setVisibility(View.VISIBLE);
            }
        }
    }

}
