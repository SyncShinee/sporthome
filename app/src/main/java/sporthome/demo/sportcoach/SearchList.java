package sporthome.demo.sportcoach;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import sporthome.demo.R;
//import baidumapsdk.demo.util.CoachListAdapter;


public class SearchList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coachlist);


        TabHost tabHost = findViewById(R.id.tabhost);

        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("教练", null).setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("陪练", null).setContent(R.id.tab2));

        ListView demoList = (ListView) findViewById(R.id.coachList);
        // 添加ListItem，设置事件响应
        demoList.setAdapter(new CoachListAdapter(DEMOS));

        ListView demo2List = (ListView) findViewById(R.id.trailList);
        // 添加ListItem，设置事件响应
        demo2List.setAdapter(new CoachListAdapter(DEMOS2));

        demoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
                onListItemClick(index, DEMOS);
            }
        });

        demo2List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
                onListItemClick(index, DEMOS2);
            }
        });

        Button btn = findViewById(R.id.coachapply);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(SearchList.this, SearchRouteList.class);
                SearchList.this.startActivity(intent);
            }
        });
    }

    void onListItemClick(int index, DemoInfo[] demos) {
        Intent intent;
        intent = new Intent(SearchList.this, CoachActivity.class);
        intent.putExtra("id", demos[index].getTitle());
        intent.putExtra("loc", demos[index].getDesc());
        this.startActivity(intent);
    }

    private static final DemoInfo[] DEMOS = {
            new DemoInfo(R.drawable.coach, R.string.coach4, R.string.fangshan, PoiSugSearchDemo.class),
            new DemoInfo(R.drawable.coach, R.string.coach5, R.string.dongcheng, PoiSugSearchDemo.class),
            new DemoInfo(R.drawable.coach, R.string.coach6, R.string.haidian, PoiSugSearchDemo.class),
            new DemoInfo(R.drawable.coach, R.string.coach9, R.string.fangshan,
                    PoiSugSearchDemo.class),
            new DemoInfo(R.drawable.coach, R.string.coach10, R.string.haidian, PoiSugSearchDemo.class),
    };

    private static final DemoInfo[] DEMOS2 = {
            new DemoInfo(R.drawable.coach, R.string.coach3, R.string.haidian,PoiSugSearchDemo.class),
            new DemoInfo(R.drawable.coach, R.string.coach4, R.string.fangshan, PoiSugSearchDemo.class),
            new DemoInfo(R.drawable.coach, R.string.coach8, R.string.fangshan, PoiSugSearchDemo.class),
            new DemoInfo(R.drawable.coach, R.string.coach9, R.string.fangshan,
                    PoiSugSearchDemo.class),
            new DemoInfo(R.drawable.coach, R.string.coach10, R.string.haidian, PoiSugSearchDemo.class),

    };

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

        public int getTitle() {
            return title;
        }

        public int getDesc() {
            return desc;
        }
    }

    private class CoachListAdapter extends BaseAdapter {

        private DemoInfo[] demos;
        private CoachListAdapter(DemoInfo[] demos) {
            super();
            this.demos = demos;
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = View.inflate(SearchList.this, R.layout.demo_item, null);
            }
            ImageView imageView =(ImageView)convertView.findViewById(R.id.image);
            TextView title = (TextView) convertView.findViewById(R.id.title);
            TextView desc = (TextView) convertView.findViewById(R.id.desc);
            imageView.setBackgroundResource(demos[index].image);
            title.setText(demos[index].title);
            desc.setText(demos[index].desc);
            return convertView;
        }

        @Override
        public int getCount() {
            return demos.length;
        }

        @Override
        public Object getItem(int index) {
            return demos[index];
        }

        @Override
        public long getItemId(int id) {
            return id;
        }
    }
}

