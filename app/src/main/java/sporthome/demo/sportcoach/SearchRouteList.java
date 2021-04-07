/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package sporthome.demo.sportcoach;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wildma.pictureselector.PictureBean;
import com.wildma.pictureselector.PictureSelector;

import sporthome.demo.R;
import me.leefeng.citypicker.CityPicker;
import me.leefeng.citypicker.CityPickerListener;

public class SearchRouteList extends AppCompatActivity implements CityPickerListener {

    private TextView textView = null;
    private CityPicker cityPicker = null;
    private ImageView img = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coachapply);

        Button btn = findViewById(R.id.btn_apply);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(SearchRouteList.this
                        , "申请已提交，请等待审核"
                        // 设置该Toast提示信息的持续时间
                        , Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        });

        cityPicker = new CityPicker(SearchRouteList.this, this);
        Button locate_btn = findViewById(R.id.locate_button);
        locate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cityPicker.show();
            }
        });

        textView = (TextView) findViewById(R.id.locate_text);
        //findViewById(R.id.butto).setOnClickListener(new View.OnClickListener() {
       //     @Override
        //    public void onClick(View view) {
        //        cityPicker.close();
        //    }
       // });


        Button btn_fig = findViewById(R.id.fig_btn);
        img = findViewById(R.id.fig_img);
        btn_fig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PictureSelector.create(SearchRouteList.this, PictureSelector.SELECT_REQUEST_CODE).selectPicture(true, 200, 200, 1, 1);
            }
        });
    }

    @Override
    public void getCity(final String s) {
        textView.setText(s);
    }

    @Override
    public void onBackPressed() {
        if (cityPicker.isShow()){
            cityPicker.close();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*结果回调*/
        if (requestCode == PictureSelector.SELECT_REQUEST_CODE) {
            if (data != null) {
                PictureBean pictureBean = data.getParcelableExtra(PictureSelector.PICTURE_RESULT);
                if (pictureBean.isCut()) {
                    img.setImageBitmap(BitmapFactory.decodeFile(pictureBean.getPath()));
                } else {
                    img.setImageURI(pictureBean.getUri());
                }

                //使用 Glide 加载图片
                /*Glide.with(this)
                        .load(pictureBean.isCut() ? pictureBean.getPath() : pictureBean.getUri())
                        .apply(RequestOptions.centerCropTransform()).into(mIvImage);*/
            }
        }
    }

    //    void onListItemClick(int index) {
//        Intent intent;
//        intent = new Intent(SearchRouteList.this, DEMOS[index].demoClass);
//        this.startActivity(intent);
//    }
//
//    private static final DemoInfo[] DEMOS = {
//            new DemoInfo(R.string.demo_title_driving_route, R.string.demo_desc_driving_route, DrivingRouteSearchDemo.class),
//            new DemoInfo(R.string.demo_title_walking_route, R.string.demo_desc_biking_route, WalkingRouteSearchDemo.class),
//            new DemoInfo(R.string.demo_title_biking_route, R.string.demo_desc_transit_route, BikingRouteSearchDemo.class),
//            new DemoInfo(R.string.demo_title_transit_route, R.string.demo_desc_walking_route, TransitRoutePlanDemo.class),
//            new DemoInfo(R.string.demo_title_mass_transit_route, R.string.demo_desc_mass_transit_route, MassTransitRouteDemo.class),
//            new DemoInfo(R.string.demo_title_indoorroute, R.string.demo_desc_indoorroute, IndoorRouteDemo.class),
//            new DemoInfo(R.string.demo_title_bus, R.string.demo_desc_bus, BusLineSearchDemo.class),
//    };
}


