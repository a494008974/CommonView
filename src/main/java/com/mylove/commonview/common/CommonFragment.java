package com.mylove.commonview.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jess.arms.base.BaseFragment;
import com.jess.arms.di.component.AppComponent;
import com.mylove.commonview.R;

import me.jessyan.armscomponent.commonres.download.DownloadCallback;
import me.jessyan.armscomponent.commonres.download.DownloadRecord;
import me.jessyan.armscomponent.commonres.download.DownloadRequest;
import me.jessyan.armscomponent.commonres.download.DownloadUtil;
import me.jessyan.armscomponent.commonres.focus.FocusBorder;
import me.jessyan.armscomponent.commonres.utils.AppUtils;
import me.jessyan.armscomponent.commonres.utils.Contanst;
import me.jessyan.armscomponent.commonres.utils.SystemUtils;
import me.jessyan.armscomponent.commonres.view.GCircleProgress;
import me.jessyan.armscomponent.commonsdk.utils.FileUtils;
import me.jessyan.armscomponent.commonservice.dao.DaoHelper;
import me.jessyan.armscomponent.commonservice.dao.InfoBean;

public abstract class CommonFragment extends BaseFragment {

    public ViewGroup viewGroup;
    protected FocusBorder mFocusBorder;

    public View previousView,nextView;
    public int pageIndex;

    private void initFocusBorder(ViewGroup viewGroup) {
        if(null == mFocusBorder) {
            mFocusBorder = new FocusBorder.Builder()
                    .asColor()
                    .borderColor(getResources().getColor(R.color.public_white))
                    .borderWidth(TypedValue.COMPLEX_UNIT_DIP, 3)
                    .shadowColor(getResources().getColor(R.color.public_white))
                    .shadowWidth(18)
                    .animDuration(180L)
                    .noShimmer()
                    .build(viewGroup);
            mFocusBorder.setVisible(true);
        }
    }


    protected void onMoveFocusBorder(View focusedView, float scale, float roundRadius) {
        if(null != mFocusBorder) {
            mFocusBorder.onFocus(focusedView, FocusBorder.OptionsFactory.get(scale, scale, roundRadius));
        }
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        if(viewGroup != null){
            initFocusBorder(viewGroup);
        }
    }

    public abstract int initLayout();

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(initLayout(), container, false);
        return viewGroup;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public void setData(@Nullable Object data) {

    }

    public boolean initOpen(View v){
        String tag = (String)v.getTag();
        InfoBean infoBean = DaoHelper.fetchInfoBean(String.valueOf(tag));
        if(infoBean != null){
            if(AppUtils.isAppInstalled(getActivity(),infoBean.getPkg())){
                //打开
                SystemUtils.openApk(getActivity(),infoBean.getPkg());
            }else{
                //下载
                if(v == null)return false;
                GCircleProgress progress = (GCircleProgress)v.findViewById(R.id.gcircle_progress);
                progress.setVisibility(View.VISIBLE);
                String url = "";
                if("1".equals(infoBean.getIs_link())){
                    url = infoBean.getLink();
                }else{
                    url = String.format(Contanst.downmain,infoBean.getUrl());
                }
                download(getActivity(),url,progress);
            }
        }
        return true;
    }


    public void download(Context context, String url, final GCircleProgress progress){
        DownloadRequest request = DownloadRequest.newBuilder()
                .downloadUrl(url)
                .downloadDir(Contanst.path)
                .downloadName(DownloadUtil.getMD5(url))
                .downloadListener(new DownloadCallback(){
                    @Override
                    public void onProgress(DownloadRecord record) {
                        super.onProgress(record);
                        progress.setProgress(record.getProgress());
                    }

                    @Override
                    public void onFailed(DownloadRecord record, String errMsg) {
                        super.onFailed(record, errMsg);
                        DownloadUtil.get().taskRemove(record.getId());
                    }

                    @Override
                    public void onFinish(DownloadRecord record) {
                        super.onFinish(record);
                        progress.setVisibility(View.GONE);
                        String path = record.getRequest().getFilePath();
                        AppUtils.install(getActivity(),path);
                        DownloadUtil.get().taskRemove(record.getId());
                    }
                })
                .build();
        DownloadUtil.get().registerListener(context);
        if(FileUtils.isFileExists(request.getFilePath())){
            progress.setVisibility(View.GONE);
            AppUtils.install(context,request.getFilePath());
        }else {
            DownloadUtil.get().enqueue(request);
        }
    }
}
