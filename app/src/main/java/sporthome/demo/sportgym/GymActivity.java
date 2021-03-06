/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package sporthome.demo.sportgym;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import sporthome.demo.Global;
import sporthome.demo.R;
import sporthome.demo.searchroute.MassTransitRouteDemo;

public class GymActivity extends AppCompatActivity {

    private String url;
    private Gym gym;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym);

        final Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        get_gym_info(id);

        final TextView gym_loc = findViewById(R.id.gym_loc);
        gym_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nintent = new Intent(GymActivity.this, MassTransitRouteDemo.class);
                nintent.putExtra("lat", intent.getDoubleExtra("lat", 0));
                nintent.putExtra("id", intent.getStringExtra("id"));
                nintent.putExtra("lng", intent.getDoubleExtra("lng", 0));
                GymActivity.this.startActivity(nintent);
            }

        });
    }
    private void get_gym_info(int id) {
        String URL = Global.HOST+"/gym/gym?"+"id=%d";
        url = String.format(URL, id);
        new Thread(networkTask).start();

        URL = Global.HOST+"/media/media?"+"id=%d";
        url = String.format(URL,id);
        new Thread(networkTask2).start();
    }

    /**
     * ??????????????????????????????
     */
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            // TODO
            // ??????????????? http request.????????????????????????
            String response = doGet(url);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("gyms", response);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    /**
     * ??????????????????????????????
     */
    Runnable networkTask2 = new Runnable() {
        @Override
        public void run() {
            // TODO
            // ??????????????? http request.????????????????????????
            String response = doGet(url);
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("figs", response);
            msg.setData(data);
            myhandler.sendMessage(msg);
        }
    };

    /**
     * get?????????http??????
     *
     * @param httpUrl ????????????
     * @return ????????????
     */
    public static String doGet(String httpUrl) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        String result = null;// ?????????????????????
        try {
            // ????????????url????????????
            URL url = new URL(httpUrl);
            // ????????????url??????????????????????????????????????????httpURLConnection???
            connection = (HttpURLConnection) url.openConnection();
            // ?????????????????????get
            connection.setRequestMethod("GET");
            // ?????????????????????????????????????????????15000??????
            connection.setConnectTimeout(15000);
            // ??????????????????????????????????????????60000??????
            connection.setReadTimeout(60000);
            // ????????????
            System.out.println(httpUrl);
            connection.connect();
            // ??????connection????????????????????????
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                // ????????????????????????????????????
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                // ????????????
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
            // ????????????
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
                connection.disconnect();// ??????????????????
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
            Log.i("mylog", "???????????????-->" + val);
            gym = JSON.parseObject(val, Gym.class);
            TextView gymName = findViewById(R.id.gym_name);
            gymName.setText(gym.getName());
            TextView gymLoc = findViewById(R.id.gym_loc);
            gymLoc.setText(gym.getLocatdisc());
            TextView gymRoute = findViewById(R.id.gym_route);
            gymRoute.setText(gym.getRoutedisc());
            TextView gymTag = findViewById(R.id.gym_tag);
            gymTag.setText("???????????????  "+gym.getTag());
            TextView gymInfo = findViewById(R.id.gym_info);
            NumberFormat nf = new DecimalFormat("00");
            gymInfo.setText("???????????????  "+nf.format(gym.getStarthour())+":"+nf.format(gym.getStartmin())+"~"+nf.format(gym.getEndhour())+":"+nf.format(gym.getEndmin()));
        }
    };

    @SuppressLint("HandlerLeak")
    Handler myhandler = new Handler() {
        @Override
        public void handleMessage(@NotNull Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("figs");
            //gymid = data.getInt("id", -1);
            Log.i("myfig", "???????????????-->" + val);
            List<String> figs= JSON.parseArray(val, String.class);
            if (figs.size() != 0) {
                ImageView imageView = findViewById(R.id.imageView);
                JSONObject jsonObject = JSONObject.parseObject(figs.get(0));
                Glide.with(getApplicationContext()).load(Global.HOST+"/api/file/"+jsonObject.getString("url")).into(imageView);
            }

        }
    };
}