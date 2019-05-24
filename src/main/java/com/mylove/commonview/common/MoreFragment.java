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

import java.util.List;

import me.jessyan.armscomponent.commonres.adapter.CommonRecyclerViewAdapter;
import me.jessyan.armscomponent.commonres.adapter.CommonRecyclerViewHolder;
import me.jessyan.armscomponent.commonres.utils.AppUtils;
import me.jessyan.armscomponent.commonres.utils.SystemUtils;


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
        register();

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
                onMoveFocusBorder(itemView, 1.05f, 10);
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
        unregister();

    }

    //=======================广播====================
    public void register() {
        mAppReceiver = new AppReceiver();
        IntentFilter filterAPP = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filterAPP.addDataScheme("package");
        filterAPP.addAction(Intent.ACTION_PACKAGE_REMOVED);
        getActivity().registerReceiver(mAppReceiver, filterAPP);
    }

    public void unregister() {
        if (mAppReceiver != null) {
            getActivity().unregisterReceiver(mAppReceiver);
        }
    }
    private AppReceiver mAppReceiver;
    public class AppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String packageName = intent.getDataString();
            packageName = packageName.split(":")[1];
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {

            }else if(intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")){

            }
            if(packageInfos != null){
                packageInfos.clear();
            }

            packageInfos = SystemUtils.getAllApps(getActivity(),3,false);
            mHandler.sendEmptyMessage(DATAS);
        }
    }
}
