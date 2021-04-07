/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package sporthome.demo.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import sporthome.demo.R;
import sporthome.demo.data.Preferences;
import sporthome.demo.data.model.LoggedInUser;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private int verint = 0;
    public static final String KEY = "f49c3d2d88c976fc829a1ca4b2ddf450";
    public static final String TPL_ID = "230908";
    public static String url = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final Button verifyButton = findViewById(R.id.verify);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadingProgressBar.setVisibility(View.VISIBLE);

                if (passwordEditText.getText().toString().equals(String.valueOf(verint))) {
                    Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_LONG).show();
                    LoggedInUser user = new LoggedInUser();
                    user.setDisplayName(usernameEditText.getText().toString());
                    user.setUserId((int)((Math.random()*9+1)*10000));
                    Preferences.setAccountUser(user, "123");
                    Intent intent;
                    intent = new Intent(LoginActivity.this, MineActivity.class);
                    LoginActivity.this.startActivity(intent);
                    finish();
                    //loginViewModel.login(usernameEditText.getText().toString(),
                    //        passwordEditText.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "验证码错误", Toast.LENGTH_LONG).show();
                    //loadingProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verint = (int)((Math.random()*9+1)*100000);

                String variable = "#code#="+verint;
                String URL = "http://v.juhe.cn/sms/send?mobile=%s&tpl_id=%s&tpl_value=%s&key=%s";
                String mobile = usernameEditText.getText().toString();
                try {
                    url = String.format(URL, mobile, TPL_ID, URLEncoder.encode(variable, "utf-8"), KEY);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                new Thread(networkTask).start();
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
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
            try {
                jsonObject = new JSONObject(response);
                error_code = jsonObject.getInt("error_code");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putInt("value", error_code);
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
            int val = data.getInt("value", -1);
            Log.i("mylog", "请求结果为-->" + val);
            // TODO
            if (val == 0) {
                Toast.makeText(getApplicationContext(), "验证码已发送", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getApplicationContext(), "调用接口失败", Toast.LENGTH_LONG).show();
            }
        }
    };

}