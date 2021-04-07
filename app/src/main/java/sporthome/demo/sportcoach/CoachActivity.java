/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package sporthome.demo.sportcoach;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import sporthome.demo.R;

public class CoachActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach);

        Intent intent = getIntent();
        int s = intent.getIntExtra("id", 0);
        int s2 = intent.getIntExtra("loc", 0);
        TextView ch_name = findViewById(R.id.ch_name);
        ch_name.setText(s);
        TextView ch_loc = findViewById(R.id.ch_loc);
        ch_loc.setText(s2);
    }
}