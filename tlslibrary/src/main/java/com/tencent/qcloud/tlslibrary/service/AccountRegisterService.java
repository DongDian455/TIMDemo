package com.tencent.qcloud.tlslibrary.service;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.qcloud.tlslibrary.activity.IndependentLoginActivity;
import com.tencent.qcloud.tlslibrary.helper.Util;

import tencent.tls.platform.TLSErrInfo;
import tencent.tls.platform.TLSStrAccRegListener;
import tencent.tls.platform.TLSUserInfo;

/**
 * Created by dgy on 15/8/13.
 */
public class AccountRegisterService {

    private final static String TAG = "AccountRegisterService";

    private Context context;
    private EditText txt_username;
    private EditText txt_password;
    private EditText txt_repassword;
    private Button btn_register;

    private TLSService tlsService;
    private StrAccRegListener strAccRegListener;

    private String username;
    private String password;

    public AccountRegisterService(Context context,
                               EditText txt_username,
                               EditText txt_password,
                               EditText txt_repassword,
                               Button btn_register) {
        this.context = context;
        this.txt_username = txt_username;
        this.txt_password = txt_password;
        this.txt_repassword = txt_repassword;
        this.btn_register = btn_register;

        tlsService = TLSService.getInstance();
        strAccRegListener = new StrAccRegListener();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = AccountRegisterService.this.txt_username.getText().toString();
                password = AccountRegisterService.this.txt_password.getText().toString();
                String tmp = AccountRegisterService.this.txt_repassword.getText().toString();

                if (username.length() == 0 || password.length() == 0 || tmp.length() == 0) {
                    Util.showToast(AccountRegisterService.this.context, "用户名密码不能为空");
                    return;
                }

                if (!password.equals(tmp)) {
                    Util.showToast(AccountRegisterService.this.context, "两次输入的密码不一致");
                    return;
                }

                if (password.length() < 8) {
                    Util.showToast(AccountRegisterService.this.context, "密码的长度不能小于8个字符");
                }

                int result = tlsService.TLSStrAccReg(username, password, strAccRegListener);
                if (result == TLSErrInfo.INPUT_INVALID) {
                    Util.showToast(AccountRegisterService.this.context, "帐号不合法");
                }
            }
        });
    }

    class StrAccRegListener implements TLSStrAccRegListener {
        @Override
        public void OnStrAccRegSuccess(TLSUserInfo userInfo) {
            Util.showToast(context, "成功注册 " + userInfo.identifier);
            TLSService.getInstance().setLastErrno(0);
            StrAccountLogin login = new StrAccountLogin(context);
            login.doStrAccountLogin(username, password);
        }

        @Override
        public void OnStrAccRegFail(TLSErrInfo errInfo) {
            Util.notOK(context, errInfo);
        }

        @Override
        public void OnStrAccRegTimeout(TLSErrInfo errInfo) {
            Util.notOK(context, errInfo);
        }
    }
}
