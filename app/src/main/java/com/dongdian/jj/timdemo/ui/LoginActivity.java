package com.dongdian.jj.timdemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dongdian.jj.timdemo.MainActivity;
import com.dongdian.jj.timdemo.R;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.qcloud.presentation.business.InitBusiness;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.tlslibrary.service.TlsBusiness;

/**
 * Created by jj on 2018/3/18.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText etAccount,etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //这个初始化一般放在启动页，demo里有点随意
        initTencentIm();
        findView();
        initListener();
    }

    private void initListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account=etAccount.getText().toString();
                String userSig=etPassword.getText().toString();
                login(account,userSig);
            }
        });
    }

    private void initTencentIm() {
        //初始化IMSDK
        InitBusiness.start(getApplicationContext(), TIMLogLevel.DEBUG.ordinal());
        //初始化TLS
        TlsBusiness.init(getApplicationContext());
        Log.d("tencentim","初始化腾讯云Im");
    }

    private void findView() {
        etAccount=findViewById(R.id.et_account);
        etPassword=findViewById(R.id.et_password);
        btnLogin=findViewById(R.id.btn_login);


    }


    private void login(String account,String userSig){
        LoginBusiness.loginIm(account, userSig, new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess() {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });
    }
}
