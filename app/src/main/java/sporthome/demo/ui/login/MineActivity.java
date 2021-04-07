/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package sporthome.demo.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import sporthome.demo.R;
import sporthome.demo.data.Preferences;
import sporthome.demo.data.model.LoggedInUser;

public class MineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);

        LoggedInUser user = Preferences.getAccountUser();
        TextView phone = findViewById(R.id.ch_phone);
        phone.setText(user != null ? user.getDisplayName(): "");
        TextView name = findViewById(R.id.ch_name);
        name.setText(user != null ? "uid"+user.getUserId(): "请登录");
        Button logout = findViewById(R.id.logout);
        if (user == null) {
            logout.setText("用户登录");
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Preferences.userClear();
                    finish();
                    Intent intent = new Intent(MineActivity.this, LoginActivity.class);
                    startActivity(intent);

                }
            });
        } else {
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Preferences.userClear();
                    finish();
                    Intent intent = new Intent(MineActivity.this, MineActivity.class);
                    startActivity(intent);
                }
            });
        }



    }
}