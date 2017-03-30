package com.accloud.ac_service_android_demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;
import com.accloud.cloudservice.AC;

/**
 * 个人中心页面
 * <p/>
 * Created by sudongsheng on 2015/1/27.
 */
public class MenuActivity extends Activity implements View.OnClickListener {
    private RelativeLayout about;
    private RelativeLayout logout;
    private RelativeLayout config;
    private TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        about = (RelativeLayout) findViewById(R.id.about);
        logout = (RelativeLayout) findViewById(R.id.logout);
        config = (RelativeLayout) findViewById(R.id.config_detail);
        back = (TextView) findViewById(R.id.menu_back);
        config.setOnClickListener(this);
        back.setOnClickListener(this);
        about.setOnClickListener(this);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.config_detail:
                startActivity(new Intent(this, ConfigDetailActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.menu_back:
                finish();
                break;
            case R.id.logout:
                //退出登录
                AC.accountMgr().logout();
                finish();
                break;
        }
    }
}
