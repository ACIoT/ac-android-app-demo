package com.accloud.ac_service_android_demo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;

/**
 * 关于页面
 * <p>
 * Created by sudongsheng on 2015/1/27.
 */
public class AboutActivity extends Activity {
    TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        back = (TextView) findViewById(R.id.about_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutActivity.this.finish();
            }
        });
    }
}
