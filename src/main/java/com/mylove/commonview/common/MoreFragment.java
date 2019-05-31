package com.mylove.commonview.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;

import com.mylove.commonview.R;
import com.owen.tvrecyclerview.widget.SimpleOnItemListener;
import com.owen.tvrecyclerview.widget.TvRecyclerView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.List;

import me.jessyan.armscomponent.commonres.adapter.CommonRecyclerViewAdapter;
import me.jessyan.armscomponent.commonres.adapter.CommonRecyclerViewHolder;
import me.jessyan.armscomponent.commonres.utils.AppUtils;
import me.jessyan.armscomponent.commonres.utils.SystemUtils;

import static me.jessyan.armscomponent.commonres.utils.Contanst.MSG_WHAT_RECEIVE_APP;


public class MoreFragment extends CommonFragment {

    private CommonRecyclerViewAdapter mAdapter;
    private PackageManager packageManager;

    private TvRecyclerView tvRecyclerView;

    private List<PackageInfo> packageInfos;

    public static final int DATAS = 0x111;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DATAS:
                    if(mAdapter != null && tvRecyclerView != null){
                        mAdapter.setDatas(packageInfos);
                        mAdapter.notifyDataSetChanged();
                        if(mAdapter.getItemCount() > 0){
                            tvRecyclerView.setSelection(0);
                        }
                    }
                    break;
            }
            return false;
        }
    });

    public static MoreFragment newInstance(){
        MoreFragment moreFragment = new MoreFragment();
        return moreFragment;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        tvRecyclerView = (TvRecyclerView) viewGroup.findViewById(R.id.more_recycle_view);
        tvRecyclerView.setSpacingWithMargins(10,10);
        packageManager = getActivity().getPackageManager();
        mAdapter = new CommonRecyclerViewAdapter<PackageInfo>(getActivity()) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.more_apps_item;
            }

            @Override
            public void onBindItemHolder(CommonRecyclerViewHolder helper, PackageInfo item, int position) {
                helper.getHolder().setImageDrawable(R.id.more_item_icon,packageManager.getApplicationIcon(item.applicationInfo));
                String name = (String) packageManager.getApplicationLabel(item.applicationInfo);
                helper.getHolder().setText(R.id.more_item_name,name);
            }
        };
        mAdapter.setDatas(packageInfos);
        tvRecyclerView.setAdapter(mAdapter);
        setListener();
        new Thread(){
            public void run() {
                if(packageInfos != null){
                    packageInfos.clear();
                    packageInfos.addAll(SystemUtils.getAllApps(getActivity(),3,false));
                }else{
                    packageInfos = SystemUtils.getAllApps(getActivity(),3,false);
                }
                mHandler.sendEmptyMessage(DATAS);
            };
        }.start();

        super.initData(savedInstanceState);
    }

    @Override
    public int initLayout() {
        return R.layout.fragment_more;
    }

    public void setListener() {
        tvRecyclerView.setOnItemListener(new SimpleOnItemListener() {
            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
                onMoveFocusBorder(itemView, 1.0f, 5);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                PackageInfo info = (PackageInfo)mAdapter.getItem(position);
                SystemUtils.openApk(getActivity(),info.applicationInfo.packageName);
            }
        });

        tvRecyclerView.setOnItemKeyListener(new TvRecyclerView.OnItemKeyListener() {
            @Override
            public boolean onItemKey(View v, int keyCode, KeyEvent event, int position) {
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    switch (keyCode){
                        case KeyEvent.KEYCODE_DPAD_UP:
                            if(position < 6){
                                previousView = v;
                                EventBus.getDefault().post("previous","turnPage");
                                return true;
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            if(position%6 == 0){
                                return true;
                            }
                            break;
                    }
                }
                return false;
            }
        });

        tvRecyclerView.setOnLongClickListener(new TvRecyclerView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(TvRecyclerView parent, View itemView, int position) {
                PackageInfo info = (PackageInfo)mAdapter.getItem(position);
                AppUtils.uninstall(getActivity(),info.applicationInfo.packageName);
                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Subscriber
    public void fetchReceiver(Message msg){
        switch (msg.what){
            case MSG_WHAT_RECEIVE_APP:
                Bundle bundle = msg.getData();

                if ("android.intent.action.PACKAGE_ADDED".equals(bundle.getString("action"))) {

                }else if("android.intent.action.PACKAGE_REMOVED".equals(bundle.getString("action"))){

                }

                if(packageInfos != null){
                    packageInfos.clear();
                }
                packageInfos = SystemUtils.getAllApps(getActivity(),3,false);
                mHandler.sendEmptyMessage(DATAS);
                break;
        }
    }

}
