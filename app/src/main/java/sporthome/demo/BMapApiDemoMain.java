/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */
package sporthome.demo;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;

import java.util.ArrayList;

import sporthome.demo.data.Preferences;
import sporthome.demo.sportgym.GymListActivity;
import sporthome.demo.sportcoach.SearchList;
import sporthome.demo.worker.WorkerActivity;


public class BMapApiDemoMain extends AppCompatActivity {
    private static final String LTAG = BMapApiDemoMain.class.getSimpleName();

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (TextUtils.isEmpty(action)) {
//                return;
//            }
//            TextView text = (TextView) findViewById(R.id.text_Info);
//            text.setTextColor(Color.RED);
//            if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
//                text.setText("key 验证出错! 错误码 :" + intent.getIntExtra
//                        (SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE, 0)
//                        +  " ; 请在 AndroidManifest.xml 文件中检查 key 设置");
//            } else if (action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
//                text.setText("key 验证成功! 功能可以正常使用");
//                text.setTextColor(Color.GREEN);
//            } else if (action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
//                text.setText("网络出错");
//            }
        }
    }

    private SDKReceiver mReceiver;
    private boolean isPermissionRequested;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //TextView text = (TextView) findViewById(R.id.text_Info);
        //text.setTextColor(Color.GREEN);
        //text.setText("欢迎使用百度地图Android SDK v" + VersionInfo.getApiVersion());
        setTitle("运动之家");
//        ListView mListView = (ListView) findViewById(R.id.listView);
//        // 添加ListItem，设置事件响应
//        mListView.setAdapter(new DemoListAdapter());
//        mListView.setOnItemClickListener(new OnItemClickListener() {
//            public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
//                onListItemClick(index);
//            }
//        });

        Button button_room = findViewById(R.id.button_room);
        button_room.setText("运动场所");
        button_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListItemClick(0);
            }
        });
        Button button_coach = findViewById(R.id.button_coach);
        button_coach.setText("运动教陪");
        button_coach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListItemClick(1);
            }
        });
        Button button_org = findViewById(R.id.button_org);
        button_org.setText("信息录入");
        button_org.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(BMapApiDemoMain.this, WorkerActivity.class);
                BMapApiDemoMain.this.startActivity(intent);
            }
        });




        // 申请动态权限


        requestPermission();

        // 注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        registerReceiver(mReceiver, iFilter);


        //login
        Preferences.init(this);
        Button btn_login = findViewById(R.id.button_mine);
        btn_login.setText("我的");
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!Preferences.isLogin()) {
//                    Intent intent;
//                    intent = new Intent(BMapApiDemoMain.this, LoginActivity.class);
//                    BMapApiDemoMain.this.startActivity(intent);
//                } else {
//                    Intent intent;
//                    intent = new Intent(BMapApiDemoMain.this, MineActivity.class);
//                    BMapApiDemoMain.this.startActivity(intent);
//                }
            }
        });
    }

    void onListItemClick(int index) {
        Intent intent;
        intent = new Intent(BMapApiDemoMain.this, DEMOS[index].demoClass);
        this.startActivity(intent);
    }

    private static final DemoInfo[] DEMOS = {
            new DemoInfo(R.drawable.draw, R.string.demo_title_drawlist, R.string.demo_desc_drawlist, GymListActivity.class),
            new DemoInfo(R.drawable.search, R.string.demo_title_drawlist, R.string.demo_desc_searchlist, SearchList.class),
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消监听 SDK 广播
        unregisterReceiver(mReceiver);
    }

    /**
     * Android6.0之后需要动态申请权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
            isPermissionRequested = true;
            ArrayList<String> permissionsList = new ArrayList<>();
            String[] permissions = {
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
            };

            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(perm)) {
                    permissionsList.add(perm);
                    // 进入到这里代表没有权限.
                }
            }

            if (!permissionsList.isEmpty()) {
                String[] strings = new String[permissionsList.size()];
                requestPermissions(permissionsList.toArray(strings), 0);
            }
        }
    }

    private class DemoListAdapter extends BaseAdapter {
        private DemoListAdapter() {
            super();
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = View.inflate(BMapApiDemoMain.this, R.layout.demo_item, null);
            }
            ImageView imageView =(ImageView)convertView.findViewById(R.id.image);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView desc = (TextView) convertView.findViewById(R.id.desc);
            imageView.setBackgroundResource(DEMOS[index].image);
            title.setText(DEMOS[index].title);
            desc.setText(DEMOS[index].desc);
            return convertView;
        }

        @Override
        public int getCount() {
            return DEMOS.length;
        }

        @Override
        public Object getItem(int index) {
            return DEMOS[index];
        }

        @Override
        public long getItemId(int id) {
            return id;
        }
    }

    private static class DemoInfo {
        private final int image;
        private final int title;
        private final int desc;
        private final Class<? extends Activity> demoClass;

        private DemoInfo(int image,int title, int desc, Class<? extends Activity> demoClass) {
            this.image = image;
            this.title = title;
            this.desc = desc;
            this.demoClass = demoClass;
        }
    }
}