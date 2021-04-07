package sporthome.demo.util;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import sporthome.demo.R;

public class ShareDemo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_demo);
    }

    public void startShareDemo(View view) {
        Intent intent = new Intent();
        intent.setClass(this, ShareDemoActivity.class);
        startActivity(intent);
    }
}
