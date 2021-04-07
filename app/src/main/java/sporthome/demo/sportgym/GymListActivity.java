package sporthome.demo.sportgym;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import sporthome.demo.Global;
import sporthome.demo.R;
import sporthome.demo.util.DemoListAdapter;
import me.leefeng.citypicker.CityPicker;
import me.leefeng.citypicker.CityPickerListener;

public class GymListActivity extends AppCompatActivity implements CityPickerListener {

    private CityPicker cityPicker = null;
    private TextView choseCity = null;
    private String url;
    private ListView demoList;
    private List<Gym> gyms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menulist);
        demoList = (ListView) findViewById(R.id.mapList);
        // 添加ListItem，设置事件响应
        //demoList.setAdapter(new DemoListAdapter(GeometryList.this, DEMOS));
        demoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {
                onListItemClick(index);
            }
        });

        choseCity = findViewById(R.id.locate_text);
        choseCity.setText("北京市 北京市 海淀区");
        query_gyms();


        Button button = findViewById(R.id.button_map);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(GymListActivity.this, GymMapListActivity.class);
                GymListActivity.this.startActivity(intent);
            }
        });


        cityPicker = new CityPicker(GymListActivity.this, this);
        Button choseCityBtn = findViewById(R.id.chose_city);

        choseCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityPicker.show();
            }
        });
    }

    void query_gyms() {
        String URL = Global.HOST+"/gym/search?"+"provence=%s&city=%s&district=%s";
        String provence = choseCity.getText().toString().split(" ")[0];
        String city = choseCity.getText().toString().split(" ")[1];
        String district = choseCity.getText().toString().split(" ")[2];
        try {
            url = String.format(URL, URLEncoder.encode(provence, "utf-8"), URLEncoder.encode(city, "utf-8"),  URLEncoder.encode(district, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        new Thread(networkTask).start();
    }

    void onListItemClick(int index) {
        Intent intent;
        intent = new Intent(GymListActivity.this, GymActivity.class);
        intent.putExtra("id", gyms.get(index).getId());
        intent.putExtra("lat", 39.963175);
        intent.putExtra("lng", 116.400244);
        this.startActivity(intent);
    }



    @Override
    public void getCity(String s) {
        choseCity.setText(s);
        query_gyms();
    }

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            String response = doGet(url);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("gyms", response);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    /**
     * get方式的http请求
     *
     * @param httpUrl 请求地址
     * @return 返回结果
     */
    public static String doGet(String httpUrl) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            System.out.println(httpUrl);
            connection.connect();
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                // 封装输入流，并指定字符集
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                // 存放数据
                StringBuilder sbf = new StringBuilder();
                String temp;
                while ((temp = bufferedReader.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append(System.getProperty("line.separator"));
                }
                result = sbf.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();// 关闭远程连接
            }
        }
        return result;
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NotNull Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("gyms");
            //gymid = data.getInt("id", -1);
            Log.i("mylog", "请求结果为-->" + val);
            gyms = JSON.parseArray(val, Gym.class);
            demoList.setAdapter(new DemoListAdapter(GymListActivity.this, gyms));
        }
    };
}

