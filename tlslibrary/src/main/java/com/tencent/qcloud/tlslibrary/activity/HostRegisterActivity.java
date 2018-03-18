package com.tencent.qcloud.tlslibrary.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.qcloud.tlslibrary.helper.MResource;
import com.tencent.qcloud.tlslibrary.helper.SmsContentObserver;
import com.tencent.qcloud.tlslibrary.service.Constants;
import com.tencent.qcloud.tlslibrary.service.TLSService;


public class HostRegisterActivity extends Activity {

    private TLSService tlsService;
    private SmsContentObserver smsContentObserver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(MResource.getIdByName(getApplication(), "layout", "tencent_tls_ui_activity_host_register"));

        tlsService = TLSService.getInstance();
        tlsService.initSmsRegisterService(this,
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "selectCountryCode_hostRegister")),
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "phoneNumber_hostRegister")),
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "checkCode_hostRegister")),
                (Button) findViewById(MResource.getIdByName(getApplication(), "id", "btn_requireCheckCode_hostRegister")),
                (Button) findViewById(MResource.getIdByName(getApplication(), "id", "btn_hostRegister"))
        );

        // 设置返回按钮
        findViewById(MResource.getIdByName(getApplication(), "id", "returnHostLoginActivity"))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HostRegisterActivity.this.onBackPressed();
                    }
                });

/*        smsContentObserver = new SmsContentObserver(new Handler(),
                this,
                (EditText) findViewById(MResource.getIdByName(getApplication(), "id", "checkCode_hostRegister")),
                Constants.SMS_REGISTER_SENDER);
        //注册短信变化监听
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, smsContentObserver);*/
    }

    protected void onDestroy() {
        super.onDestroy();
        if (smsContentObserver != null) {
            this.getContentResolver().unregisterContentObserver(smsContentObserver);
        }
    }
}
