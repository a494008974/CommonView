package com.mylove.commonview.common;

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

import me.jessyan.armscomponent.commonres.focus.FocusBorder;

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

}
