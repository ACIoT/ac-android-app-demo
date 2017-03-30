package com.accloud.ac_service_android_demo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.accloud.ac_service_android_demo.R;
import com.accloud.ac_service_android_demo.application.MainApplication;
import com.accloud.ac_service_android_demo.config.Config;
import com.accloud.ac_service_android_demo.utils.Pop;
import com.accloud.cloudservice.AC;
import com.accloud.cloudservice.PayloadCallback;
import com.accloud.service.ACDeviceMsg;
import com.accloud.service.ACException;
import com.accloud.utils.PreferencesUtils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义二进制格式发送到设备
 * <p/>
 * Created by Administrator on 2015/7/13.
 */
public class CustomBinaryActivity extends Activity implements View.OnClickListener {
    private TextView back;
    private EditText sendTime;
    private Button sendStart;
    private TextView sendHistory;
    private TextView sendCount;
    private TextView receiveCount;
    private EditText reqCode;
    private EditText reqPayload;
    private TextView respCode;
    private TextView respPayload;

    private int code;
    private double duration;
    private String hexString;

    private String subDomain;
    private long deviceId;
    private String physicalDeviceId;
    private int send_count = 0;
    private int receive_count = 0;
    Timer timer;
    private boolean isStart = false;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ACException e = (ACException) msg.obj;
                    Pop.popToast(CustomBinaryActivity.this, e.getErrorCode() + "-->" + e.getMessage());
                    break;
                case 1:
                    receiveCount.setText("get:" + (++receive_count));
                    ACDeviceMsg deviceMsg = (ACDeviceMsg) msg.obj;
                    respCode.setText(deviceMsg.getCode() + "");
                    if (deviceMsg.getContent() != null)
                        respPayload.setText(byteToHexString(deviceMsg.getContent()));
                    break;
            }
        }
    };

    private void assignViews() {
        back = (TextView) findViewById(R.id.send_back);
        sendTime = (EditText) findViewById(R.id.send_time);
        sendStart = (Button) findViewById(R.id.send_start);
        sendCount = (TextView) findViewById(R.id.send_count);
        receiveCount = (TextView) findViewById(R.id.receive_count);
        sendHistory = (TextView) findViewById(R.id.send_history);
        reqCode = (EditText) findViewById(R.id.req_code);
        reqPayload = (EditText) findViewById(R.id.req_payload);
        respCode = (TextView) findViewById(R.id.resp_code);
        respPayload = (TextView) findViewById(R.id.resp_payload);
        back.setOnClickListener(this);
        sendHistory.setOnClickListener(this);
        sendStart.setOnClickListener(this);
        reqPayload.setText(PreferencesUtils.getString(this, deviceId + "hexString", ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        deviceId = getIntent().getLongExtra("deviceId", 0);
        physicalDeviceId = getIntent().getStringExtra("physicalDeviceId");
        super.onCreate(savedInstanceState);
        subDomain = PreferencesUtils.getString(MainApplication.getInstance(), "subDomain", Config.SUBDOMAIN);
        setContentView(R.layout.activity_cus_binary);
        assignViews();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_back:
                finish();
                break;
            case R.id.send_history:
                Intent intent = new Intent(CustomBinaryActivity.this, HistoryRecordActivity.class);
                intent.putExtra("deviceId", deviceId);
                intent.putExtra("physicalDeviceId", physicalDeviceId);
                startActivity(intent);
                break;
            case R.id.send_start:
                try {
                    if (isStart) {
                        sendStart.setText(R.string.cus_binary_aty_starttext);
                        isStart = false;
                        if (timer != null)
                            timer.cancel();
                    } else {
                        sendStart.setText(R.string.cus_binary_aty_stoptext);
                        isStart = true;
                        if (reqCode.getText().toString().length() == 0)
                            code = 68;
                        else
                            code = Integer.parseInt(reqCode.getText().toString());
                        if (sendTime.getText().toString().length() == 0)
                            duration = 5;
                        else
                            duration = Double.parseDouble(sendTime.getText().toString());
                        hexString = reqPayload.getText().toString();
                        PreferencesUtils.putString(this, deviceId + "hexString", hexString);
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendCount.setText("send:" + (++send_count));
                                        ACDeviceMsg deviceMsg = new ACDeviceMsg(code, getBytes());
                                        AC.bindMgr().sendToDeviceWithOption(subDomain, physicalDeviceId, deviceMsg, AC.ONLY_LOCAL, new PayloadCallback<ACDeviceMsg>() {
                                            @Override
                                            public void success(ACDeviceMsg deviceMsg) {
                                                Message msg = new Message();
                                                msg.what = 1;
                                                msg.obj = deviceMsg;
                                                handler.sendMessage(msg);
                                            }

                                            @Override
                                            public void error(ACException e) {
                                                Message msg = new Message();
                                                msg.what = 0;
                                                msg.obj = e;
                                                handler.sendMessage(msg);
                                            }
                                        });
                                    }
                                });
                            }
                        }, 0, (int) (duration * 1000));
                    }
                } catch (Exception e) {
                    Pop.popToast(CustomBinaryActivity.this, getString(R.string.cus_binary_aty_please_input_valid_hexstr));
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null)
            timer.cancel();
    }

    public byte[] getBytes() {
        Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(hexString);
        hexString = m.replaceAll("");
        hexString = hexString.replace(",", "");

        int length = hexString.length() / 2;
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = (byte) (Short.decode("0x" + hexString.substring(i * 2, i * 2 + 1) + hexString.substring(i * 2 + 1, i * 2 + 2)).shortValue());
        }
        return bytes;
    }

    public String byteToHexString(byte[] bArray) {
        StringBuilder sb = new StringBuilder();
        String sTemp;
        for (byte b : bArray) {
            sTemp = Integer.toHexString(0xFF & b);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase();
    }
}
