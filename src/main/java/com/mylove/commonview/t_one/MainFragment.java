package com.mylove.commonview.t_one;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.mylove.commonview.R;
import com.mylove.commonview.common.CommonFragment;
import com.mylove.commonview.common.MoreFragment;

import org.simple.eventbus.Subscriber;

import me.kaelaela.verticalviewpager.VerticalViewPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends CommonFragment{

    VerticalViewPager viewPager;
    ContentFragmentAdapter contentFragmentAdapter;
    public static MainFragment newInstance() {
        MainFragment homeFragment = new MainFragment();
        return homeFragment;
    }

    @Override
    public int initLayout() {
        return R.layout.fragment_main;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        viewPager = (VerticalViewPager) viewGroup.findViewById(R.id.vertical_viewpager);
        contentFragmentAdapter = new ContentFragmentAdapter.Holder(getFragmentManager())
                .add(HomeFragment.newInstance())
                .add(MoreFragment.newInstance())
                .set();
        viewPager.setAdapter(contentFragmentAdapter);
        viewPager.setOffscreenPageLimit(contentFragmentAdapter.getCount());
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Subscriber(tag = "turnPage")
    public void turnPage(String pageEvent){
        if (viewPager == null) return;
        int index = viewPager.getCurrentItem();
        CommonFragment commonFragment;
        if("previous".equals(pageEvent)){
            viewPager.setCurrentItem(index-1);
            Fragment fragment = contentFragmentAdapter.getItem(index-1);
            if(fragment instanceof CommonFragment){
                commonFragment = (CommonFragment)fragment;
                if(commonFragment.nextView != null){
                    commonFragment.nextView.requestFocus();
                }
            }
        }else if("next".equals(pageEvent)){
            viewPager.setCurrentItem(index+1);
            Fragment fragment = contentFragmentAdapter.getItem(index+1);
            if(fragment instanceof CommonFragment){
                commonFragment = (CommonFragment)fragment;
                if(commonFragment.previousView != null){
                    commonFragment.previousView.requestFocus();
                }
            }
        }
    }

}
