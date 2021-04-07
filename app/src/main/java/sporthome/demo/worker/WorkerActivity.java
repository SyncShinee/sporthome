/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package sporthome.demo.worker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.decoration.GridSpacingItemDecoration;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.ScreenUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import sporthome.demo.R;
import me.leefeng.citypicker.CityPicker;
import me.leefeng.citypicker.CityPickerListener;

import static com.baidu.vi.VIContext.getContext;
import sporthome.demo.Global;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class WorkerActivity extends AppCompatActivity implements CityPickerListener {

    private double lat;
    private double lng;
    private CityPicker cityPicker;
    private TextView choseCity = null;
    private GridImageAdapter mAdapter;
    private EditText gymNameText;
    private EditText gymLocText;
    private EditText gymRouteText;
    private CheckBox basketballCheck;
    private CheckBox footballCheck;
    private CheckBox pingpongCheck;
    private CheckBox wusuCheck;
    private CheckBox swimCheck;
    private CheckBox yogaCheck;
    private CheckBox slideCheck;
    private CheckBox badmintonCheck;
    private CheckBox shootCheck;
    private CheckBox bodyCheck;
    private CheckBox monCheck;
    private CheckBox tueCheck;
    private CheckBox wedCheck;
    private CheckBox tusCheck;
    private CheckBox friCheck;
    private CheckBox satCheck;
    private CheckBox sunCheck;
    private String url;

    private Spinner startHourSpinner;
    private Spinner startMinSpinner;
    private Spinner endHourSpinner;
    private Spinner endMinSpinner;
    private int gymid = -1;

    private  List<LocalMedia> selectList;

    private OkHttpClient ok;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        Button btn_pickLoc = findViewById(R.id.buttonLatLng);

        gymNameText = findViewById(R.id.editTextGymName);
        gymLocText = findViewById(R.id.editTextGymLoc);
        gymRouteText = findViewById(R.id.editTextGymRoute);
        basketballCheck = findViewById(R.id.checkBoxBasketball);
        footballCheck = findViewById(R.id.checkBoxFootball);
        pingpongCheck = findViewById(R.id.checkBoxPingpong);
        wusuCheck = findViewById(R.id.checkBoxWusu);
        swimCheck = findViewById(R.id.checkBoxSwim);
        yogaCheck = findViewById(R.id.checkBoxYoga);
        slideCheck = findViewById(R.id.checkBoxSlide);
        badmintonCheck = findViewById(R.id.checkBoxBadminton);
        shootCheck = findViewById(R.id.checkBoxshoot);
        bodyCheck = findViewById(R.id.checkBoxBody);
        monCheck = findViewById(R.id.checkBox1);
        tueCheck = findViewById(R.id.checkBox2);
        wedCheck = findViewById(R.id.checkBox3);
        tusCheck = findViewById(R.id.checkBox4);
        friCheck = findViewById(R.id.checkBox5);
        satCheck = findViewById(R.id.checkBox6);
        sunCheck = findViewById(R.id.checkBox7);

        startHourSpinner = findViewById(R.id.spinnerStartHour);
        endHourSpinner = findViewById(R.id.spinnerEndHour);
        startMinSpinner = findViewById(R.id.spinnerStartMin);
        endMinSpinner = findViewById(R.id.spinnerEndMin);


        btn_pickLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WorkerActivity.this, PickActivity.class);
                startActivityForResult(intent, 1);

            }
        });

        cityPicker = new CityPicker(WorkerActivity.this, this);
        Button choseCityBtn = findViewById(R.id.chose_city);
        choseCity = findViewById(R.id.locate_text);
        choseCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityPicker.show();
            }
        });

        ProgressBar pb = findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);


        RecyclerView mRecyclerView = findViewById(R.id.recycler);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4,
                ScreenUtils.dip2px(this, 8), false));
        mAdapter = new GridImageAdapter(getContext(), onAddPicClickListener);
        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("selectorList") != null) {
            mAdapter.setList(savedInstanceState.getParcelableArrayList("selectorList"));
        }

        mAdapter.setSelectMax(9);
        mRecyclerView.setAdapter(mAdapter);

        Button finish = findViewById(R.id.button_finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (choseCity.getText().equals("请选择所在城市")) {
                    Toast.makeText(WorkerActivity.this, "请选择场馆所属城市", Toast.LENGTH_SHORT).show();
                    return;
                }
                ProgressBar pb = findViewById(R.id.progressBar);
                pb.setVisibility(View.VISIBLE);
//                Thread t = new Thread(new Runnable() {
//                    @Override
//
//                    public void run() {
//                        try {
//                            Thread.sleep(10000);//让他显示10秒后，取消ProgressDialog
//
//                        } catch (InterruptedException e) {
//
//                            e.printStackTrace();
//
//                        }
//                        Looper.prepare();
//                        Toast.makeText(getApplicationContext(), "服务器状态异常", Toast.LENGTH_LONG).show();
//                        pb.setVisibility(View.INVISIBLE);
//                        Looper.loop();
                String URL = Global.HOST+"/gym/save?"+"name=%s&lat=%f&lng=%f&locatdisc=%s&routedisc=%s&mon=%d&tue=%d&wed=%d&tus=%d&fri=%d&sat=%d&sun=%d&starthour=%d&startmin=%d&endhour=%d&endmin=%d&provence=%s&city=%s&district=%s&tag=%s";
                String name = gymNameText.getText().toString();
                String locatdisc = gymLocText.getText().toString();
                String routedisc = gymRouteText.getText().toString();
                int mon = monCheck.isChecked() ? 1 : 0;
                int tue = tueCheck.isChecked() ? 1 : 0;
                int wed = wedCheck.isChecked() ? 1 : 0;
                int tus = tusCheck.isChecked() ? 1 : 0;
                int fri = friCheck.isChecked() ? 1 : 0;
                int sat = satCheck.isChecked() ? 1 : 0;
                int sun = sunCheck.isChecked() ? 1 : 0;
                int starthour =  Integer.parseInt(startHourSpinner.getSelectedItem().toString());
                int startmin = Integer.parseInt(startMinSpinner.getSelectedItem().toString());
                int endhour =  Integer.parseInt(endHourSpinner.getSelectedItem().toString());
                int endmin = Integer.parseInt(endMinSpinner.getSelectedItem().toString());
                String provence = choseCity.getText().toString().split(" ")[0];
                String city = choseCity.getText().toString().split(" ")[1];
                String district = choseCity.getText().toString().split(" ")[2];
                String tag = "";
                if (basketballCheck.isChecked()) {
                    tag += " "+basketballCheck.getText();
                }
                if (footballCheck.isChecked()) {
                    tag += " "+footballCheck.getText();
                }
                if (pingpongCheck.isChecked()) {
                    tag += " "+pingpongCheck.getText();
                }
                if (wusuCheck.isChecked()) {
                    tag += " "+wusuCheck.getText();
                }
                if (swimCheck.isChecked()) {
                    tag += " "+swimCheck.getText();
                }
                if (yogaCheck.isChecked()) {
                    tag += " "+yogaCheck.getText();
                }
                if (slideCheck.isChecked()) {
                    tag += " "+slideCheck.getText();
                }
                if (badmintonCheck.isChecked()) {
                    tag += " "+badmintonCheck.getText();
                }
                if (shootCheck.isChecked()) {
                    tag += " "+shootCheck.getText();
                }
                if (bodyCheck.isChecked()) {
                    tag += " "+bodyCheck.getText();
                }
                Log.e("tag", tag);
                try {
                    url = String.format(URL, URLEncoder.encode(name, "utf-8"), lat, lng, URLEncoder.encode(locatdisc, "utf-8"), URLEncoder.encode(routedisc, "utf-8"), mon, tue, wed, tus, fri, sat, sun, starthour, startmin, endhour, endmin, URLEncoder.encode(provence, "utf-8"), URLEncoder.encode(city, "utf-8"),  URLEncoder.encode(district, "utf-8"),URLEncoder.encode(tag, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i("mylog", url);
                new Thread(networkTask).start();


                        //new Thread(networkTask).start();

//                    }
//                });
//                t.start();
            }
        });

        Button btn_upload = findViewById(R.id.button_upload);
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (gymid == -1) {
                    Toast.makeText(WorkerActivity.this, "请先上传场馆信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Log.e("img file name", selectList.get(0).getRealPath());
                int i = 1;
                for (LocalMedia lm : selectList) {
                    uploadMedia(lm, i);
                    i += 1;
                }
            }
        });

    }

    void uploadMedia(LocalMedia lm, int i) {
        File file = new File(lm.getRealPath());
        if (!file.exists()) {
            Toast.makeText(WorkerActivity.this, "图片不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", file.getName())
                .addFormDataPart("id", "111")
                .addFormDataPart("type", "2")
                .addFormDataPart("file", file.getName(),RequestBody.Companion.create(file, MediaType.parse("multipart/form-data")))
                .build();
        Request request = new Request.Builder()
                .url(Global.UPLOAD_URL).post(requestBody)
                .addHeader("user-agent", String.valueOf(gymid))
                .addHeader("x-userid", "752332")// 添加x-userid请求头
                .addHeader("x-sessionkey", "kjhsfjkaskfashfuiwf")// 添加x-sessionkey请求头
                .addHeader("x-tonce", Long.valueOf(System.currentTimeMillis()).toString())// 添加x-tonce请求头
                .addHeader("x-timestamp", Long.valueOf(System.currentTimeMillis()).toString())// 添加x-timestamp请求头
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        MyHandler myHandler = new MyHandler();
        final Message msg = myHandler.obtainMessage();
        okHttpClient.newCall(request).enqueue(new Callback(){
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                msg.what =0;
                myHandler.sendMessage(msg);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String result = response.body().string();
                Log.i("上传图片结果：", result);
                if (!response.isSuccessful()) {
                    Log.i("响应失败：", response.code() + "");
                    msg.what =1;
                    return;
                }
                msg.what = 3;
                msg.arg1 = i;
                myHandler.sendMessage(msg);
            }
        });
    }
    private class MyHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;

                case 2:
                    Toast.makeText(getApplicationContext(),"获取服务端数据为空",Toast.LENGTH_SHORT).show();
                    break;

                case 3:
                    Toast.makeText(getApplicationContext(), "图片"+msg.arg1+"上传成功", Toast.LENGTH_SHORT).show();

                    break;
            }

        }
    }




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

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            String response = doGet(url);
            JSONObject jsonObject = null;
            int error_code = -1;
            int gymid = -1;
            try {
                jsonObject = new JSONObject(response);
                error_code = jsonObject.getInt("status");
                gymid = jsonObject.getInt("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("status", error_code);
            data.putInt("id", gymid);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NotNull Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            int val = data.getInt("status", -1);
            gymid = data.getInt("id", -1);
            Log.i("mylog", "请求结果为-->" + val);
            // TODO
            if (val == 1) {
                Toast.makeText(getApplicationContext(), "场馆信息上传成功", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "服务器状态异常", Toast.LENGTH_SHORT).show();
            }
            ProgressBar pb = findViewById(R.id.progressBar);
            pb.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PickActivity.RESULT_CODE) {
            if (requestCode == 1) {
                lat = data.getDoubleExtra("lat", 0);
                lng = data.getDoubleExtra("lng", 0);
                TextView result = findViewById(R.id.textLatLng);
                result.setText("("+lat+","+lng+")");
            }
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    selectList = PictureSelector.obtainMultipleResult(data);
                    mAdapter.setList(selectList);
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            PictureSelector.create(WorkerActivity.this)
                    .openGallery(PictureMimeType.ofAll())
                    .loadImageEngine(GlideEngine.createGlideEngine()) // Please refer to the Demo GlideEngine.java
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        }
    };

    @Override
    public void getCity(String s) {
        choseCity.setText(s);
    }
}